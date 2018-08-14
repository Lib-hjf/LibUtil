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
final class Logcat extends AbsLogger {

    private static final String TAG = "MyLogger";
    /*  */private static final String LINE_START = "┌────────────────────────────────────────────\n";
    /**/private static final String LINE_CONTENT = "│   ";
    /* */private static final String LINE_MIDDLE = "├┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄\n";
    /*    */private static final String LINE_END = "└────────────────────────────────────────────\n";

    private StringBuilder stringBuilder = new StringBuilder();

    Logcat() {
    }

    @Override
    protected void onLog_VERBOSE(LogEntity logEntity) {
        Log.v(TAG, formatContent(logEntity));
    }

    @Override
    protected void onLog_Info(LogEntity logEntity) {
        Log.i(TAG, formatContent(logEntity));
    }

    @Override
    protected void onLog_Debug(LogEntity logEntity) {
        Log.d(TAG, formatContent(logEntity));
    }

    @Override
    protected void onLog_WARN(LogEntity logEntity) {
        Log.w(TAG, formatContent(logEntity));
    }

    @Override
    protected void onLog_ERROR(LogEntity logEntity) {
        Log.e(TAG, formatContent(logEntity));
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
            // {\n "json": "json"\n}
            // thread info
            if (logEntity.isMainThread()) {
                stringBuilder.append(LINE_CONTENT).append("Thread: main").append("\n");
                stringBuilder.append(LINE_MIDDLE);
            }
            // method info
            stringBuilder.append(LINE_CONTENT).append(logEntity.getClassPath()).append("#")
                    .append(logEntity.getMethodName()).append("\n");
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
