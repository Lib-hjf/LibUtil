package org.hjf.log;

import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

final class LogEntity {

    int lineNumber = -1;
    int logLevel = Log.VERBOSE;
    boolean isMainThread;
    long timeStamp = System.currentTimeMillis();
    private String classPath;
    private String[] classNames;
    String methodName;
    String content;
    /**
     * for map、list、json、xml ....
     * {@link LogEntity#content} is json,xml,array,collection
     */
    private Object argument;

    LogEntity() {
    }

    public int getLogLevel() {
        return logLevel;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public boolean isMainThread() {
        return isMainThread;
    }

    public String getClassPath() {
        return classPath;
    }

    public String getTag() {
        return classNames[0];
    }

    public String[] getClassNames() {
        return classNames;
    }

    /**
     * handle inner class and "className$1"
     * such as: Class$Adapter$1
     *
     * @param classPath class path
     */
    public void setClassPathAndTag(String classPath) {
        this.classPath = classPath;
        this.classNames = classPath.split("[$]");
        if (classNames.length > 1) {
            try {
                String intStr = classNames[classNames.length - 1];
                Integer.parseInt(intStr);
                this.classPath = classPath.replace("$" + intStr, "");
            } catch (NumberFormatException ignored) {
            }
        }
        int startIndex = this.classNames[0].lastIndexOf(".") + 1;
        this.classNames[0] = this.classNames[0].substring(startIndex);
    }

    public void setArgument(Object argument) {
        this.argument = argument;
    }

    public boolean hasArgument() {
        return this.argument != null;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getContent() {
        if (this.argument != null) {
            return getContentFromArgument(this.argument);
        }
        return content;
    }

    public static LogEntity create(int logLevel, String content) {
        LogEntity logEntity = new LogEntity();

        // 判断是否为主线程
        logEntity.isMainThread = LogUtil.isMainThread(Thread.currentThread().getId());
        logEntity.logLevel = logLevel;

        // 获取类路径、方法名、行数 org.hjf.log.LogEntity org.hjf.log.LogUtil
        for (StackTraceElement element : new Throwable().getStackTrace()) {
            String classPath = element.getClassName();
            // 栈顶肯定是 LogEntity，然后是 Log，在接着使用 LogUtil 的类
            if (!LogUtil.class.getName().equals(classPath) && !LogEntity.class.getName().equals(classPath)) {
                logEntity.classPath = classPath;
                logEntity.methodName = element.getMethodName();
                logEntity.lineNumber = element.getLineNumber();
                break;
            }
        }
        logEntity.setClassPathAndTag(logEntity.classPath);

        // 赋值日志
        logEntity.content = content;
        return logEntity;
    }

    private static String getContentFromArgument(@NonNull Object argument) {


        // JSONObject
        if (argument instanceof JSONObject) {
            try {
                return ((JSONObject) argument).toString(2);
            } catch (JSONException ignored) {
            }
        }
        // JSONArray
        else if (argument instanceof JSONArray) {
            try {
                return ((JSONArray) argument).toString(2);
            } catch (JSONException ignored) {
            }
        }
        // other
        else if (!argument.getClass().isArray()) {
            return argument.toString();
        }
        // array
        else if (argument instanceof boolean[]) {
            return Arrays.toString((boolean[]) argument);
        }
        //
        else if (argument instanceof byte[]) {
            return Arrays.toString((byte[]) argument);
        }
        //
        else if (argument instanceof char[]) {
            return Arrays.toString((char[]) argument);
        }
        //
        else if (argument instanceof short[]) {
            return Arrays.toString((short[]) argument);
        }
        //
        else if (argument instanceof int[]) {
            return Arrays.toString((int[]) argument);
        }
        //
        else if (argument instanceof long[]) {
            return Arrays.toString((long[]) argument);
        }
        //
        else if (argument instanceof float[]) {
            return Arrays.toString((float[]) argument);
        }
        //
        else if (argument instanceof double[]) {
            return Arrays.toString((double[]) argument);
        }
        //
        else if (argument instanceof Object[]) {
            return Arrays.deepToString((Object[]) argument);
        }

        return "Couldn't find a correct type for the object";
    }
}
