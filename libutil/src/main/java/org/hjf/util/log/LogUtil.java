package org.hjf.util.log;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import org.hjf.util.DateUtils;

import java.text.MessageFormat;


/**
 * Log工具类
 * <p>
 * 详细功能介绍  ==> {@link LogMgr}
 *
 * @author huangjf
 */
public final class LogUtil {


    public static void v(String msg) {
        String[] output = null;
        if (LogMgr.getInstance().getLogCatLevel() <= Log.VERBOSE) {
            output = mergeDetailMessage(msg);
            if (output != null) {
                Log.v(output[0], output[1]);
            }
        }
        if (LogMgr.getInstance().getLogFileLevel() <= Log.VERBOSE) {
            if (output == null) {
                output = mergeDetailMessage(msg);
            }
            if (output != null) {
                LogUtil.logcat2File(output[0], output[1]);
            }
        }
    }

    public static void v(String pattern, Object ... arguments) {
        LogUtil.v(new MessageFormat(pattern).format(arguments));
    }

    public static void d(String msg) {
        String[] output = null;
        if (LogMgr.getInstance().getLogCatLevel() <= Log.DEBUG) {
            output = mergeDetailMessage(msg);
            if (output != null) {
                Log.d(output[0], output[1]);
            }
        }
        if (LogMgr.getInstance().getLogFileLevel() <= Log.DEBUG) {
            if (output == null) {
                output = mergeDetailMessage(msg);
            }
            if (output != null) {
                LogUtil.logcat2File(output[0], output[1]);
            }
        }
    }

    public static void d(String pattern, Object ... arguments) {
        LogUtil.d(new MessageFormat(pattern).format(arguments));
    }

    public static void i(String msg) {
        String[] output = null;
        if (LogMgr.getInstance().getLogCatLevel() <= Log.INFO) {
            output = mergeDetailMessage(msg);
            if (output != null) {
                Log.i(output[0], output[1]);
            }
        }
        if (LogMgr.getInstance().getLogFileLevel() <= Log.INFO) {
            if (output == null) {
                output = mergeDetailMessage(msg);
            }
            if (output != null) {
                LogUtil.logcat2File(output[0], output[1]);
            }
        }
    }

    public static void i(String pattern, Object ... arguments) {
        LogUtil.i(new MessageFormat(pattern).format(arguments));
    }

    public static void w(String msg) {
        String[] output = null;
        if (LogMgr.getInstance().getLogCatLevel() <= Log.WARN) {
            output = mergeDetailMessage(msg);
            if (output != null) {
                Log.w(output[0], output[1]);
            }
        }
        if (LogMgr.getInstance().getLogFileLevel() <= Log.WARN) {
            if (output == null) {
                output = mergeDetailMessage(msg);
            }
            if (output != null) {
                LogUtil.logcat2File(output[0], output[1]);
            }
        }
    }

    public static void w(String pattern, Object ... arguments) {
        LogUtil.w(new MessageFormat(pattern).format(arguments));
    }

    public static void e(String msg) {
        String[] output = null;
        if (LogMgr.getInstance().getLogCatLevel() <= Log.ERROR) {
            output = mergeDetailMessage(msg);
            if (output != null) {
                Log.e(output[0], output[1]);
            }
        }
        if (LogMgr.getInstance().getLogFileLevel() <= Log.ERROR) {
            if (output == null) {
                output = mergeDetailMessage(msg);
            }
            if (output != null) {
                LogUtil.logcat2File(output[0], output[1]);
            }
        }
    }

    public static void e(String pattern, Object ... arguments) {
        LogUtil.e(new MessageFormat(pattern).format(arguments));
    }


    /**
     * Log 日志信息 保存到本地磁盘。文件名：(日期 + logLevelTag)
     */
    private static void logcat2File(String logTag, String logText) {
        long currentTimeMillis = System.currentTimeMillis();

        // 选定文件名
        String fileName = DateUtils.getDate_YMD(LogMgr.getInstance().getContext(), currentTimeMillis) + ".txt";
        String filePath = LogMgr.getInstance().getDiskLogPath() + fileName;
        // time - tag - text
        String inputText = DateUtils.getDate_HMS(LogMgr.getInstance().getContext(), currentTimeMillis)
                + "[" + logTag + "]"
                + "  --  " + logText;
        // 开启任务写入，不覆盖以前内容
        FileWriterTask fileWriterTask = new FileWriterTask(filePath, inputText);
        fileWriterTask.setAppendable(true);
        LogThreadPoolExecutor.getInstance().execute(fileWriterTask);
    }

    /**
     * 合并完整的信息
     *
     * @param message 要显示的信息
     * @return 默认TAG【类名】以及信息详情,默认信息详情【 类名 + 方法名 + 行号 + message 】
     */
    @Nullable
    private static String[] mergeDetailMessage(String message) {

        // 拿到 Class 的完整路径：com.xx.xx.ClassName.xx
        String classPath = null;
        for (StackTraceElement element : new Throwable().getStackTrace()) {
            classPath = element.getClassName();
            // 栈顶肯定是 Log 这个类自己，下一个就是使用打印功能的类
            if (!LogUtil.class.getName().equals(classPath)) {
//                element.getMethodName(); // 方法名
//                element.getLineNumber(); // 行数
                break;
            }
        }

        // 非空检验
        if (TextUtils.isEmpty(classPath)) {
            return null;
        }

        // 是否需要答应 org.hjf.xx 内的 Class 调试信息
        if (!LogMgr.getInstance().needPrintHJFLog && classPath.startsWith(LogMgr.PACKAGE_HEAD)) {
            return null;
        }

        //最后一个点隔开的就是【类名】TAG
        int startIndex = classPath.lastIndexOf(".") + 1;
        int endIndex = classPath.lastIndexOf("$");
        String className;
        if (endIndex == -1) {
            className = classPath.substring(startIndex);
        } else {
            className = classPath.substring(startIndex, endIndex);
        }

        // 是否被加入 LogMgr 忽略名单
        if (LogMgr.getInstance().isIgnore(className)) {
            return null;
        }

        // 拼接显示内容
        return new String[]{className, message};
    }
}
