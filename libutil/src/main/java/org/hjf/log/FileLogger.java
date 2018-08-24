package org.hjf.log;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import org.hjf.util.DateUtils;
import org.hjf.util.FileWriterTask;
import org.hjf.util.R;

import java.util.LinkedList;

final class FileLogger extends AbsLogger {

    private static final int DB_SUBMIT_TIME_INTERVAL = 2000;
    /**
     * submit data list runnable
     */
    private static final int MESSAGE_SUBMIT_FILE = R.integer.handler_message_submit_file_log;

    private String mLogDiskPath;
    /**
     * log entity data cache
     */
    private final LinkedList<LogEntity> logEntityCache = new LinkedList<>();
    private Handler submitHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            synchronized (FileLogger.this) {
                final LinkedList<LogEntity> clone = (LinkedList<LogEntity>) logEntityCache.clone();
                logEntityCache.clear();
                logcat2File(mLogDiskPath, clone);
            }
        }
    };

    FileLogger(String logDiskPath) {
        this.mLogDiskPath = logDiskPath;
    }

    @Override
    protected void onLog_VERBOSE(LogEntity logEntity) {
        logcat2FileCache(logEntity);
    }

    @Override
    protected void onLog_Info(LogEntity logEntity) {
        logcat2FileCache(logEntity);
    }

    @Override
    protected void onLog_Debug(LogEntity logEntity) {
        logcat2FileCache(logEntity);
    }

    @Override
    protected void onLog_WARN(LogEntity logEntity) {
        logcat2FileCache(logEntity);
    }

    @Override
    protected void onLog_ERROR(LogEntity logEntity) {
        logcat2FileCache(logEntity);
    }

    /**
     * 缓存添加的 log entity
     * 以防短时间过多对文件 steam 进行过多的open、close操作
     *
     * @param entity log entity
     */
    private synchronized void logcat2FileCache(LogEntity entity) {
        logEntityCache.add(entity);
        submitHandler.removeMessages(MESSAGE_SUBMIT_FILE);
        submitHandler.sendEmptyMessageDelayed(MESSAGE_SUBMIT_FILE, DB_SUBMIT_TIME_INTERVAL);
    }


    /**
     * Log 日志信息 保存到本地磁盘。文件名：(日期 + logLevelTag)
     */
    private static void logcat2File(String logDiskPath, LinkedList<LogEntity> logEntityLinkedList) {
        long currentTimeMillis = System.currentTimeMillis();
        // 选定文件名
        String fileName = DateUtils.getDate_YMD(LogUtil.context, currentTimeMillis) + ".txt";
        String filePath = logDiskPath + fileName;
        // 开启任务写入，不覆盖以前内容
        FileWriterTask fileWriterTask = new LogEntityFileWriterTask(filePath, logEntityLinkedList);
        fileWriterTask.setAppendable(true);

        LogThreadPoolExecutor.getInstance().execute(fileWriterTask);
    }


    private static final class LogEntityFileWriterTask extends FileWriterTask {

        private LinkedList<LogEntity> datas;

        public LogEntityFileWriterTask(String filePath, LinkedList<LogEntity> datas) {
            super(filePath, "");
            this.datas = datas;
        }

        @Override
        public void run() {
            // Splicing Log Content
            StringBuilder stringBuilder = new StringBuilder();
            if (datas != null) {
                for (LogEntity entity : datas) {
                    stringBuilder.append(DateUtils.getDate_HMS(LogUtil.context, entity.getTimeStamp()))
                            .append("[").append(entity.getTag()).append("]")
                            .append(" -- ").append(entity.getContent()).append("\n");
                }
                super.content = stringBuilder.toString();
            }
            // data list is null
            else {
                super.content = "data is null";
            }

            super.run();
        }
    }
}
