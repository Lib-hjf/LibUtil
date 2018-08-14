package org.hjf.log;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

import org.hjf.util.DateUtils;
import org.hjf.util.EnvironUtils;
import org.hjf.util.FileWriterTask;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.concurrent.TimeUnit;


/**
 * App 异常扑捉
 *
 * @author huangjf
 */
final class CrashLogger implements Thread.UncaughtExceptionHandler {

    private boolean isOpenCrashCatch = false;
    private Thread.UncaughtExceptionHandler systemDefaultHandler;
    private String mCrashLogPath;

    CrashLogger(String crashLogPath) {
        this.mCrashLogPath = crashLogPath;
    }

    void open() {
        if (this.isOpenCrashCatch) {
            return;
        }
        this.isOpenCrashCatch = true;
        this.systemDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    void close() {
        if (!this.isOpenCrashCatch) {
            return;
        }
        this.isOpenCrashCatch = false;
        Thread.setDefaultUncaughtExceptionHandler(this.systemDefaultHandler);
        this.systemDefaultHandler = null;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

        LogUtil.e("收到系统错误");
        //不关心的异常让系统默认的异常处理器来处理
        if (!handlerException(ex) && this.systemDefaultHandler != null) {
            this.systemDefaultHandler.uncaughtException(thread, ex);
        }
        // 处理异常完后的动作
        else {

            toastHint();

            LogUtil.e("异常处理完毕，系统睡眠两秒");
            // 崩溃后返回到系统的时间延时
            try {
                TimeUnit.MILLISECONDS.sleep(2000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 重启程序代码
//			Intent intent = new Intent(mContext, MainActivity.class);
//			PendingIntent restartIntent = PendingIntent.getActivity(mContext, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
//			AlarmManager mgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
//			mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000L, restartIntent);


            // 利用系统的处理方法，会显示xxx停止已运行，点击确定
//			systemDefaultHandler.uncaughtException(thread, ex);

            // 立即释放 Activity 占有资源，清理后台缓存的本进程
            // 操作系统中结束进程，进程中的 JVM 虚拟机也没了
//			android.os.Process.killProcess(android.os.Process.myPid());

            // 操作系统中结束进程，进程中的 JVM 虚拟机也没了
            // 退出 JVM 虚拟机。非 0 值都为异常退出
            System.exit(1);
        }
    }


    /**
     * 提醒用户程序异常退出
     */
    private void toastHint() {
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(LogUtil.context, "很抱歉，程序出现异常，即将退出。", Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();
    }


    /**
     * 保存报错信息
     */
    private boolean handlerException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        // 1. 获取运行环境信息：硬件、OS、软件
        StringBuilder stringBuilder = getSystemInfo(LogUtil.context);

        // 2. 收集异常信息
        PrintWriter printWriter = null;
        String exceptionInfo = "";
        try {
            Writer writer = new StringWriter();
            printWriter = new PrintWriter(writer);
            ex.printStackTrace(printWriter);
            Throwable cause = ex.getCause();
            while (cause != null) {
                cause.printStackTrace(printWriter);
                cause = cause.getCause();
            }
            exceptionInfo = writer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (printWriter != null) {
                printWriter.close();
            }
        }

        stringBuilder.append("\n");
        stringBuilder.append("#-------AndroidRuntime-------\n");
        stringBuilder.append(exceptionInfo);
        stringBuilder.append("\n");
        stringBuilder.append("#end");

        // 3. 开启线程去写入到本地磁盘
        String fileName = DateUtils.getDate_YMD_HMS(LogUtil.context, System.currentTimeMillis()) + ".txt";
        String filePath = this.mCrashLogPath + fileName;

        LogUtil.e("组成完毕参数，提交线程去执行任务");
        FileWriterTask fileWriterTask = new FileWriterTask(filePath, stringBuilder.toString());
        LogThreadPoolExecutor.getInstance().execute(fileWriterTask);
        return true;
    }


    /**
     * 获取手机环境信息：硬件、OS、软件
     *
     * @return 自定义的系统信息
     */
    private static StringBuilder getSystemInfo(Context context) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("\n");
        buffer.append("#-------system content-------");
        buffer.append("\n");
        buffer.append("version-name:");
        buffer.append(EnvironUtils.getAppVersionName(context));
        buffer.append("\n");
        buffer.append("version-code:");
        buffer.append(EnvironUtils.getAppVersionCode(context));
        buffer.append("\n");
        buffer.append("model:");
        buffer.append(EnvironUtils.getModel());
        buffer.append("\n");
        buffer.append("system-version:");
        buffer.append(EnvironUtils.getSystemVersion());
        buffer.append("\n");
        buffer.append("density:");
        buffer.append(EnvironUtils.getScreenDensity(context));
        buffer.append("\n");
        buffer.append("screen-height:");
        buffer.append(EnvironUtils.getScreenHeight(context));
        buffer.append("\n");
        buffer.append("screen-width:");
        buffer.append(EnvironUtils.getScreenWidth(context));
        buffer.append("\n");
        buffer.append("imei:");
        buffer.append(EnvironUtils.getIMEI(context));
        buffer.append("\n");
        buffer.append("imsi:");
        buffer.append(EnvironUtils.getIMSI(context));
        buffer.append("\n");
        buffer.append("msisdn:");
        buffer.append(EnvironUtils.getMSISDN(context));
        buffer.append("\n");
        buffer.append("mac-addr:");
        buffer.append(EnvironUtils.getMacAddress(context));
        buffer.append("\n");
        buffer.append("isWifiOpen:");
        buffer.append(EnvironUtils.isWifiOpen(context));
        buffer.append("\n");
        buffer.append("\n");
        return buffer;
    }
}
