package org.hjf.log;

import org.hjf.util.DateUtils;

import java.io.File;
import java.text.ParseException;

/**
 * database logger.
 * one day one database
 * database Maximum retention of 7
 */
final class DBLogger extends AbsLogger {

    private final static int DB_FILE_SAVE_MAX_DAY_NUM = 7;

    DBLogHelper dblogHelper;

    DBLogger() {
        final String dbFileName = "log-" + DateUtils.getDate_YMD(LogUtil.context, System.currentTimeMillis()) + ".db";
        this.dblogHelper = new DBLogHelper(LogUtil.context, dbFileName);
        deleteObsoleteDatabase(dbFileName);
    }

    @Override
    protected void onLog_VERBOSE(LogEntity logEntity) {
        this.dblogHelper.insertCache(logEntity);
    }

    @Override
    protected void onLog_Info(LogEntity logEntity) {
        this.dblogHelper.insertCache(logEntity);
    }

    @Override
    protected void onLog_Debug(LogEntity logEntity) {
        this.dblogHelper.insertCache(logEntity);
    }

    @Override
    protected void onLog_WARN(LogEntity logEntity) {
        this.dblogHelper.insertCache(logEntity);
    }

    @Override
    protected void onLog_ERROR(LogEntity logEntity) {
        this.dblogHelper.insertCache(logEntity);
    }

    private void deleteObsoleteDatabase(final String dbFileNameToday) {
        LogThreadPoolExecutor.getInstance().submit(new Runnable() {
            @Override
            public void run() {
                File dir = LogUtil.context.getDatabasePath(dbFileNameToday);
                // /data/data/com.hjf.test/databases/...
                for (File item : dir.getParentFile().listFiles()) {
                    String fileName = item.getName();
                    // log-2018-08-15.db、log-2018-08-15.db-journal
                    if (!fileName.matches("^(log-)[0-9]{4}(-[0-9]{2}){2}[.](db|db-journal)$")) {
                        return;
                    }
                    long time = System.currentTimeMillis();
                    try {
                        time = DateUtils.getTime_YMD(fileName.substring(4, 14));
                    } catch (ParseException ignored) {
                    }
                    if (System.currentTimeMillis() - time > 24 * 60 * 60 * 1000 * (DB_FILE_SAVE_MAX_DAY_NUM - 1)) {
                        item.delete();
                    }
                }
            }
        });
    }

}
