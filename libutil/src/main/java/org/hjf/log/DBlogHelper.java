package org.hjf.log;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.WorkerThread;
import android.util.SparseArray;

import org.hjf.util.R;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * db helper
 * support create table
 * support insertCache、delete、query
 */
final class DBlogHelper extends SQLiteOpenHelper {

    private static final String LOG_TABLE = "log";
    private static final String LOG_TAG_TABLE = "log_tag";
    private static final int DB_SUBMIT_TIME_INTERVAL = 2000;
    static final int QUERY_DATA_NUM = 30;
    /**
     * submit data list runnable
     */
    private static final int MESSAGE_SUBMIT_DB = R.integer.handler_message_submit_db_log;

    /**
     * log entity data cache
     */
    private final LinkedList<LogEntity> logEntityCache = new LinkedList<>();

    /**
     * tagId <--> tagStr mapCache
     */
    private TagIdSparseArray tagIdCache = new TagIdSparseArray();


    private Handler submitHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            final LinkedList<LogEntity> clone = (LinkedList<LogEntity>) logEntityCache.clone();
            logEntityCache.clear();
            insertDB(clone);
        }
    };

    DBlogHelper(Context context, String name) {
        super(context, name, null, 28);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + LOG_TABLE;
        db.execSQL(sql);
        onCreate(db);
    }

    private void createTable(SQLiteDatabase db) {
        StringBuilder sqlBuilder = new StringBuilder();
        // 1. create table log
        sqlBuilder.append("CREATE TABLE ").append(LOG_TABLE).append("(");
        sqlBuilder.append("TimeStamp integer, ");// id notnull
        sqlBuilder.append("TagId integer, ");// tag id 连表
        sqlBuilder.append("Level integer, ");// tag id 连表
        sqlBuilder.append("ClassPath text, ");
        sqlBuilder.append("Method text, ");
        sqlBuilder.append("LineNumber integer, ");
        sqlBuilder.append("Content text,");
        sqlBuilder.append("isMainThread integer default 0");
        sqlBuilder.append(");");
        db.execSQL(sqlBuilder.toString());

        // 1. create table log_tag
        sqlBuilder.setLength(0);
        sqlBuilder.append("CREATE TABLE ").append(LOG_TAG_TABLE).append("(");
        sqlBuilder.append("TagId integer primary key AUTOINCREMENT , ");// id notnull
        sqlBuilder.append("TagStr text");
        sqlBuilder.append(");");
        db.execSQL(sqlBuilder.toString());
    }


    /**
     * 多线程访问，刚打开App时log工作十分繁重
     * 此处需要线程安全，不能进行耗时操作
     * 将任务添入缓存池中，开启事务添入
     */
    void insertCache(LogEntity entity) {
        logEntityCache.add(entity);
        submitHandler.removeMessages(MESSAGE_SUBMIT_DB);
        submitHandler.sendEmptyMessageDelayed(MESSAGE_SUBMIT_DB, DB_SUBMIT_TIME_INTERVAL);
    }

    /**
     * insert data list
     * use transaction
     *
     * @param logEntities log entity list
     */
    private void insertDB(final LinkedList<LogEntity> logEntities) {
        LogThreadPoolExecutor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                // 开启事务处理多个插入任务
                SQLiteDatabase db = getWritableDatabase();
                db.beginTransaction();
                try {
                    for (LogEntity entity : logEntities) {
                        int tagId = findTagIdByTagStr(entity.getTag());
                        String sql = "insert into " + LOG_TABLE +
                                " (TimeStamp, TagId, Level, ClassPath, Method, LineNumber, Content, isMainThread) " +
                                " values(?,?,?,?,?,?,?,?);";
                        db.execSQL(sql, new Object[]{
                                entity.getTimeStamp(),
                                tagId,
                                entity.getLogLevel(),
                                entity.getClassPath(),
                                entity.getMethodName(),
                                entity.getLineNumber(),
                                entity.getContent(),
                                entity.isMainThread() ? 1 : 0
                        });
                    }
                }
                // 不处理
                catch (Exception e) {
                    e.printStackTrace();
                }
                // 标记事务成功并结束，进行commit处理
                // 结束时不标记成功会进行rollback处理
                finally {
                    db.setTransactionSuccessful();
                    db.endTransaction();
                }
            }
        });
    }


    /**
     * find log_tag into table {@link #LOG_TAG_TABLE}
     *
     * @param tagStr log tag string
     * @return TagId
     */
    int findTagIdByTagStr(String tagStr) {
        int tagId = -1;

        // find tagId from cache map
        int index = tagIdCache.indexOfValue(tagStr);
        if (index != -1) {
            tagId = tagIdCache.keyAt(index);
            return tagId;
        }

        // find tagId from db
        Cursor cursor = getReadableDatabase().query(LOG_TAG_TABLE,
                new String[]{"TagId"},
                "TagStr = ? ",
                new String[]{tagStr}, null, null, null);

        // find tagId ok
        if (cursor.moveToFirst()) {
            tagId = cursor.getInt(0);
        }
        cursor.close();
        if (tagId != -1) {
            tagIdCache.put(tagId, tagStr);
            return tagId;
        }

        // not find tagId. to do insert
        SQLiteDatabase db = getWritableDatabase();
        String sql = "insert into " + LOG_TAG_TABLE + "(TagStr) values(?);";
        db.execSQL(sql, new Object[]{tagStr});
        cursor = db.rawQuery("select last_insert_rowid() from " + LOG_TAG_TABLE, null);
        if (cursor.moveToFirst()) {
            tagId = cursor.getInt(0);
        }
        tagIdCache.put(tagId, tagStr);
        return tagId;
    }

    /**
     * query "TimeStamp, ClassPath, Content, Method" from table
     * query num = 30
     *
     * @param selectedTagStrList selected tag string list
     * @param logLevel           log level
     * @param offset             query start index
     * @return entity list
     */
    @WorkerThread
    List<LogEntity> queryLogEntityInDB(List<String> selectedTagStrList, int logLevel, int offset) {

        StringBuilder selectionBuilder = new StringBuilder();
        // log level
        selectionBuilder.append("Level >= ? and ");
        String[] keys = new String[selectedTagStrList.size() + 1];
        keys[0] = String.valueOf(logLevel);
        if (!selectedTagStrList.isEmpty()) {
            selectionBuilder.append("(");
        }
        // log tag id
        for (int i = 0; i < selectedTagStrList.size(); i++) {
            selectionBuilder.append("TagId = ? or ");
            keys[i + 1] = selectedTagStrList.get(i);
        }
        // delete selection string last two char " or "
        selectionBuilder.setLength(selectionBuilder.length() == 0 ? 0 : selectionBuilder.length() - 4);
        if (!selectedTagStrList.isEmpty()) {
            selectionBuilder.append(")");
        }
        // or rawQuery
        // limit 1,2;   <==>   limit 2 offset 1;
        Cursor cursor = getReadableDatabase().query(LOG_TABLE,
                new String[]{"TimeStamp, Level, ClassPath, Method, LineNumber, Content, isMainThread"},
                selectionBuilder.toString(),
                keys,
                null, null, "TimeStamp DESC", offset + "," + QUERY_DATA_NUM);


        List<LogEntity> logEntityList = new ArrayList<>();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                LogEntity entity = new LogEntity();
                entity.timeStamp = cursor.getLong(0);
                entity.logLevel = cursor.getInt(1);
                entity.setClassPathAndTag(cursor.getString(2));
                entity.methodName = cursor.getString(3);
                entity.lineNumber = cursor.getInt(4);
                entity.content = cursor.getString(5);
                entity.isMainThread = cursor.getInt(6) == 1;
                logEntityList.add(entity);
            }
        }
        return logEntityList;
    }


    /**
     * query all tag string from table {@link #LOG_TAG_TABLE}, and update cache {@link #tagIdCache}
     *
     * @return all tag string
     */
    List<String> queryAllTagStringDB() {

        Cursor cursor = getReadableDatabase().query(LOG_TAG_TABLE,
                new String[]{"TagId, TagStr"},
                null, null, null, null, null);

        tagIdCache.clear();
        ArrayList<String> tagStrList = new ArrayList<>();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                int tagId = cursor.getInt(0);
                String tagStr = cursor.getString(1);
                tagIdCache.put(tagId, tagStr);
                tagStrList.add(tagStr);
            }
        }
        return tagStrList;
    }


    private static final class TagIdSparseArray extends SparseArray<String> {

        @Override
        public int indexOfValue(String value) {
            for (int i = 0; i < size(); i++) {
                if (valueAt(i).equals(value)) {
                    return i;
                }
            }
            return -1;
        }
    }
}
