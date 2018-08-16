package org.hjf.log;

import android.util.Log;

abstract class AbsLogger {

    private static final int CLOSE = 9;
    /**
     * level filter
     * VERBOSE[2]
     * DEBUG[3]  for map、list、json、xml ....
     * INFO[4]
     * WARN[5]
     * ERROR[6]
     * ASSERT[7]
     */
    protected int logMIniLevel = AbsLogger.CLOSE;


    void v(LogEntity logEntity) {
        if (this.logMIniLevel > Log.VERBOSE) {
            return;
        }
        this.onLog_VERBOSE(logEntity);
    }

    void d(LogEntity logEntity) {
        if (this.logMIniLevel > Log.DEBUG) {
            return;
        }
        this.onLog_Debug(logEntity);
    }

    void i(LogEntity logEntity) {
        if (this.logMIniLevel > Log.INFO) {
            return;
        }
        this.onLog_Info(logEntity);
    }

    void w(LogEntity logEntity) {
        if (this.logMIniLevel > Log.WARN) {
            return;
        }
        this.onLog_WARN(logEntity);
    }

    void e(LogEntity logEntity) {
        if (this.logMIniLevel > Log.ERROR) {
            return;
        }
        this.onLog_ERROR(logEntity);
    }


    protected abstract void onLog_VERBOSE(LogEntity logEntity);

    protected abstract void onLog_Info(LogEntity logEntity);

    protected abstract void onLog_Debug(LogEntity logEntity);

    protected abstract void onLog_WARN(LogEntity logEntity);

    protected abstract void onLog_ERROR(LogEntity logEntity);

    /**
     * 是否打印此 LogLevel 日志
     *
     * @param logLevel log等级
     * @return true log
     */
    boolean isLog(int logLevel) {
        return logLevel >= this.logMIniLevel;
    }

    /**
     * set logLevel
     *
     * @param logLevel target log level
     */
    public void setLogLevel(int logLevel) {
        this.logMIniLevel = logLevel;
    }
}
