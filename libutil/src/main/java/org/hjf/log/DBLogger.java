package org.hjf.log;

import org.hjf.util.DateUtils;

/**
 * database logger.
 * one day one database
 * database Maximum retention of 7
 */
final class DBLogger extends AbsLogger {

    DBlogHelper dblogHelper;

    DBLogger() {
        String dbName = "log-" + DateUtils.getDate_YMD(LogUtil.context, System.currentTimeMillis()) + ".db";
        this.dblogHelper = new DBlogHelper(LogUtil.context, dbName);
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
}
