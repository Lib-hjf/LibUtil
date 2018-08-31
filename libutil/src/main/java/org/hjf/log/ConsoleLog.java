package org.hjf.log;

import android.util.Log;

/**
 * ┌────────────────────────────────────────────
 * │
 * ├┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄
 * │
 * ├┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄
 * │
 * └────────────────────────────────────────────
 * ┌────────────────────────────────────────────
 * │
 * │
 * └────────────────────────────────────────────
 */
final class ConsoleLog extends AbsLogger {

    private String tag = "HJFLogger";
    /*  */private static final String LINE_START = "┌────────────────────────────────────────────\n";
    /**/private static final String LINE_CONTENT = "│   ";
    /* */private static final String LINE_MIDDLE = "├┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄\n";
    /*    */private static final String LINE_END = "└────────────────────────────────────────────\n";

    private StringBuilder stringBuilder = new StringBuilder();

    ConsoleLog() {
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    protected void onLog_VERBOSE(LogEntity logEntity) {
        Log.println(Log.VERBOSE, tag, formatContent(logEntity));
    }

    @Override
    protected void onLog_Info(LogEntity logEntity) {
        Log.println(Log.INFO, tag, formatContent(logEntity));
    }

    @Override
    protected void onLog_Debug(LogEntity logEntity) {
        Log.println(Log.DEBUG, tag, formatContent(logEntity));
    }

    @Override
    protected void onLog_WARN(LogEntity logEntity) {
        Log.println(Log.WARN, tag, formatContent(logEntity));
    }

    @Override
    protected void onLog_ERROR(LogEntity logEntity) {
        Log.println(Log.ERROR, tag, formatContent(logEntity));
    }

    /**
     * 调整显示内容格式
     */
    private String formatContent(LogEntity logEntity) {
        stringBuilder.setLength(0);
        stringBuilder.append(logEntity.getTag()).append("\n");
        stringBuilder.append(LINE_START);
        // list、array、map、json ...
        if (!logEntity.hasArgument()) {
            // thread & method info  -->  "UI Thread, com.hjf.MainActivity.onCreate(MainActivity.java:35)\n"
            stringBuilder.append(LINE_CONTENT);
            stringBuilder.append(logEntity.isMainThread() ? "UI" : "BG").append(" Thread, ");
            stringBuilder.append(logEntity.getClassPath()).append(".").append(logEntity.getMethodName()).append("(");
            stringBuilder.append(logEntity.getClassNames()[0]).append(".java:").append(logEntity.getLineNumber()).append(")").append("\n");
            stringBuilder.append(LINE_MIDDLE);
        }
        // content
        String content = logEntity.getContent();
        String content1 = content.replaceAll("[\n]", "\n" + LINE_CONTENT);
        stringBuilder.append(LINE_CONTENT).append(content1).append("\n");
        stringBuilder.append(LINE_END);
        return stringBuilder.toString();
    }
}
