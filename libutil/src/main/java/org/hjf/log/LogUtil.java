package org.hjf.log;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * AbsLogger
 * support auto create Log.Tag
 * support level filter
 * support format: this.d("{0}，你好", "hjf")
 */
public final class LogUtil {

    private static final int JSON_INDENT = 2;
    /**
     * logger
     */
    private static Logcat logcat;
    private static FileLogger fileLogger;
    static DBLogger dbLogger;
    private static CrashLogger crashLogger;
    /**
     * context
     */
    static Context context;
    /**
     * format: this.d("{0}，你好", "hjf")
     */
    private static MessageFormat messageFormat = new MessageFormat("");
    /**
     * main thread id
     */
    private static long mainThreadId = -1;


    public static void v(String msg) {
        LogEntity logEntity = getLogEntity(Log.VERBOSE, msg);
        if (logEntity == null) {
            return;
        }
        if (LogUtil.logcat != null) {
            LogUtil.logcat.v(logEntity);
        }
        if (LogUtil.fileLogger != null) {
            LogUtil.fileLogger.v(logEntity);
        }
        if (LogUtil.dbLogger != null) {
            LogUtil.dbLogger.v(logEntity);
        }
    }

    public static void v(String pattern, Object... arguments) {
        LogUtil.messageFormat.applyPattern(pattern);
        String content = LogUtil.messageFormat.format(arguments);
        LogUtil.v(content);
    }

    public static void d(@Nullable LogEntity logEntity) {
        if (logEntity == null) {
            return;
        }
        if (LogUtil.logcat != null) {
            LogUtil.logcat.d(logEntity);
        }
        if (LogUtil.fileLogger != null) {
            LogUtil.fileLogger.d(logEntity);
        }
        if (LogUtil.dbLogger != null) {
            LogUtil.dbLogger.d(logEntity);
        }
    }

    public static void d(String msg) {
        LogEntity logEntity = getLogEntity(Log.DEBUG, msg);
        LogUtil.d(logEntity);
    }

    public static void d(String pattern, Object... arguments) {
        LogUtil.messageFormat.applyPattern(pattern);
        String content = LogUtil.messageFormat.format(arguments);
        LogUtil.d(content);
    }

    public static void d(Object object) {
        LogEntity logEntity = getLogEntity(Log.DEBUG, null);
        if (logEntity == null) {
            return;
        }
        logEntity.setArgument(object);
        LogUtil.d(logEntity);
    }

    public static void json(String json) {
        if (TextUtils.isEmpty(json)) {
            LogUtil.e("Empty/Null json content");
            return;
        }
        json = json.trim();
        Object argument = null;
        try {
            // json object
            if (json.startsWith("{")) {
                argument = new JSONObject(json);
            }
            // json array
            else if (json.startsWith("[")) {
                argument = new JSONArray(json);
            }
        } catch (JSONException e) {
            argument = null;
        }
        if (argument == null) {
            LogUtil.e("Invalid Json");
            return;
        }
        LogEntity logEntity = getLogEntity(Log.DEBUG, null);
        if (logEntity != null) {
            logEntity.setArgument(argument);
        }
        LogUtil.d(logEntity);
    }

    public static void i(String msg) {
        LogEntity logEntity = getLogEntity(Log.INFO, msg);
        if (logEntity == null) {
            return;
        }
        if (LogUtil.logcat != null) {
            LogUtil.logcat.i(logEntity);
        }
        if (LogUtil.fileLogger != null) {
            LogUtil.fileLogger.i(logEntity);
        }
        if (LogUtil.dbLogger != null) {
            LogUtil.dbLogger.i(logEntity);
        }
    }

    public static void i(String pattern, Object... arguments) {
        LogUtil.messageFormat.applyPattern(pattern);
        String content = LogUtil.messageFormat.format(arguments);
        LogUtil.i(content);
    }

    public static void w(String msg) {
        LogEntity logEntity = getLogEntity(Log.WARN, msg);
        if (logEntity == null) {
            return;
        }
        if (LogUtil.logcat != null) {
            LogUtil.logcat.w(logEntity);
        }
        if (LogUtil.fileLogger != null) {
            LogUtil.fileLogger.w(logEntity);
        }
        if (LogUtil.dbLogger != null) {
            LogUtil.dbLogger.w(logEntity);
        }
    }

    public static void w(String pattern, Object... arguments) {
        LogUtil.messageFormat.applyPattern(pattern);
        String content = LogUtil.messageFormat.format(arguments);
        LogUtil.w(content);
    }

    public static void e(String msg) {
        LogEntity logEntity = getLogEntity(Log.ERROR, msg);
        if (logEntity == null) {
            return;
        }
        if (LogUtil.logcat != null) {
            LogUtil.logcat.e(logEntity);
        }
        if (LogUtil.fileLogger != null) {
            LogUtil.fileLogger.e(logEntity);
        }
        if (LogUtil.dbLogger != null) {
            LogUtil.dbLogger.e(logEntity);
        }
    }

    public static void e(String pattern, Object... arguments) {
        LogUtil.messageFormat.applyPattern(pattern);
        String content = LogUtil.messageFormat.format(arguments);
        LogUtil.e(content);
    }

    /**
     * 获取LogEntity对象
     *
     * @param logLevel log等级
     * @param msg      信息
     * @return null 表示此log动作无效
     */
    @Nullable
    private static LogEntity getLogEntity(int logLevel, String msg) {
        if (LogUtil.context == null) {
            throw new RuntimeException("LogUtil has not init.");
        }
        if (LogUtil.logcat != null && LogUtil.logcat.isLog(logLevel)
                || (LogUtil.fileLogger != null && LogUtil.fileLogger.isLog(logLevel)
                || (LogUtil.dbLogger != null && LogUtil.dbLogger.isLog(logLevel)))) {
            return LogEntity.create(logLevel, msg);
        }
        return null;
    }


    /**
     * init
     */
    public static void init(@NonNull Context context) {
        LogUtil.context = context;
    }


    /**
     * 开启奔溃异常捕捉
     */
    public static void openCrashLog(String crashLogPath) {
        if (LogUtil.crashLogger == null) {
            LogUtil.crashLogger = new CrashLogger(crashLogPath);
        }
        LogUtil.crashLogger.open();
    }

    /**
     * 关闭奔溃异常捕捉
     */
    public static void closeCrashLog() {
        if (LogUtil.crashLogger != null) {
            LogUtil.crashLogger.close();
        }
    }

    /**
     * open logcat function
     */
    public static void openLogcat(int logLevel) {
        if (LogUtil.logcat == null) {
            LogUtil.logcat = new Logcat();
        }
        LogUtil.logcat.setLogLevel(logLevel);
    }

    /**
     * 开启文件log功能
     */
    public static void openDiskLog(String crashLogPath, int logLevel) {
        if (LogUtil.fileLogger == null) {
            LogUtil.fileLogger = new FileLogger(crashLogPath);
        }
        LogUtil.fileLogger.setLogLevel(logLevel);
    }

    /**
     * open Database log function
     */
    public static void openDatabaseLog(int logLevel) {
        if (LogUtil.dbLogger == null) {
            LogUtil.dbLogger = new DBLogger();
        }
        LogUtil.dbLogger.setLogLevel(logLevel);
    }

    public static void setMainThreadId(long mainThreadId) {
        LogUtil.mainThreadId = mainThreadId;
    }

    public static boolean isMainThread(long threadId) {
        return LogUtil.mainThreadId == threadId;
    }

    /**
     * into ui
     */
    public static void gotoDBView() {
        if (LogUtil.context == null) {
            throw new RuntimeException("LogUtil has not init.");
        }
        DBLogUIActivity.start(LogUtil.context);
    }
}
