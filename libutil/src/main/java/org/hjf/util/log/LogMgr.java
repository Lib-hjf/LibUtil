package org.hjf.util.log;


import android.content.Context;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Log 功能配置：开启和关闭
 * <p>
 * 1. 自动拼接 LogCat 信息内容，默认 ClassName 当 Tag
 * 2. 同意控制 LogCat 、LogDisk、LogCloud 等级
 * 3. 支持：LogDisk ，将 Log 日志输出到手机磁盘的数据库中 TODO
 * 4. 支持：LogCloud，将重要 Log 信息上传到服务器上 TODO
 * 5. Class 黑名单，屏蔽无需关注的工具类的日志打印
 * 6. 支持占位符方式的 Log 日志打印:  LogUtil.d("{0}，你好", "hjf")
 *
 * @author huangjf
 */
public class LogMgr {

    static final String PACKAGE_HEAD = "org.hjf";
    private static final String ERR_LOG_MGR_NOT_INIT = "LogMgr not initialized\n.";
    private static final String ERR_LOG_MGR_HAS_INIT = "LogMgr already initialized\n.";

    private static LogMgr logManager = null;

    /**
     * 上下文对象
     */
    private Context context;

    /**
     * Log 日志的根目录
     */
    private String rootPath;

    /**
     * Log 日志打印忽略列表
     */
    private List<String> logIgnoreList = new ArrayList<>();

    /**
     * 配置输出到 LogCat 的 Log 日志的等级
     * <p>
     * [2] -> VERBOSE
     * [3] -> DEBUG
     * [4] -> INFO
     * [5] -> WARN
     * [6] -> ERROR
     * [7] -> ASSERT
     */
    private int logCatLevel = 0;

    /**
     * 配置输出到 Disk File 的 Log 日志的等级
     */
    private int logFileLevel = 0;
    /**
     * 配置输出到 Disk Database 的 Log 日志的等级
     */
    private int logDatabaseLevel = 0;

    /**
     * 配置输出到 Cloud 的 Log 日志的等级
     */
    private int logCloudLevel = 0;
    private int logCloudFocusLevel = 0; // 上传云端重点日志的等级，上传失败不能丢弃的那种
    private String cloudHost;
    private String cloudPort;
    private LogPacket logPacket; // 封装和 Cloud 通讯的数据格式

    /**
     * 是否输出 org.hjf.xx 包内的 Log 日志输出
     */
    boolean needPrintHJFLog = false;

    /**
     * xLog 初始化
     */
    public synchronized static void init(@NonNull Context context, String rootPath) {
        if (LogMgr.logManager != null) {
            throw new IllegalStateException(ERR_LOG_MGR_HAS_INIT);
        }
        LogMgr.logManager = new LogMgr(context, rootPath);
        // TODO 是否有权限使用此目录
    }

    /**
     * 销毁 LogManager 对象
     */
    public synchronized static void destory() {
        LogMgr.logManager.closeLogcat();
        LogMgr.logManager.closeDiskLog();
        LogMgr.logManager.closeCrashLog();
        LogThreadPoolExecutor.destroy();
        LogMgr.logManager = null;
    }

    /**
     * 构造方法
     */
    private LogMgr(@NonNull Context context, String rootPath) {
        this.context = context;
        this.rootPath = rootPath;
    }

    /**
     * 获取单例对象
     */
    public static LogMgr getInstance() {
        if (LogMgr.logManager == null) {
            throw new IllegalStateException(ERR_LOG_MGR_NOT_INIT);
        }
        return LogMgr.logManager;
    }

    /**
     * 获取上下文对象
     */
    @NonNull
    Context getContext() {
        return this.context;
    }

    /**
     * 添加 Class 黑名单
     */
    public void addBlackClass(String className) {
        if (!logIgnoreList.contains(className))
            logIgnoreList.add(className);
    }

    /**
     * 移除黑名单
     */
    public void removeBlackList(String className) {
        if (logIgnoreList.contains(className)) {
            logIgnoreList.remove(className);
        }
    }

    /**
     * 是否忽略
     */
    boolean isIgnore(String className) {
        return logIgnoreList != null && logIgnoreList.contains(className);
    }

    /**
     * 开启 LogCat 日志输出
     *
     * @param level 日志输出等级，如：{@link android.util.Log#DEBUG}
     */
    public void openLogcat(int level) {
        this.logCatLevel = level;
    }

    /**
     * 关闭 LogCat 日志输出
     */
    public void closeLogcat() {
        this.logCatLevel = 0;
    }


    /**
     * 开启 LogCat 输出到磁盘文件
     *
     * @param level 日志输出等级，如：{@link android.util.Log#DEBUG}
     */
    public void openDiskLog(int level) {
        this.logFileLevel = level;
    }

    /**
     * 关闭 LogCat 输出到磁盘
     */
    public void closeDiskLog() {
        this.logFileLevel = 0;
    }

    /**
     * 开启 LogCat 输出到磁盘数据库
     *
     * @param level 日志输出等级，如：{@link android.util.Log#DEBUG}
     */
    @Deprecated
    public void openDatabaseLog(int level) {
        this.logDatabaseLevel = level;
    }

    /**
     * 关闭 LogCat 输出到磁盘文件
     */
    @Deprecated
    public void closeDatabaseLog() {
        this.logDatabaseLevel = 0;
    }

    /**
     * 开启 LogCat 输出到磁盘文件
     *
     * @param level 日志输出等级，如：{@link android.util.Log#DEBUG}
     */
    @Deprecated
    public void openCloudLog(int level) {
        this.logCloudLevel = level;
    }

    /**
     * 设置上传云端重点关注信息等级
     *
     * @param level 日志输出等级，如：{@link android.util.Log#DEBUG}
     */
    @Deprecated
    public void setLogCloudFocusLevel(int level) {
        this.logCloudFocusLevel = level;
    }

    /**
     * 开启奔溃异常捕捉
     */
    public void openCrashLog() {
        CrashLog.getInstance().open();
    }

    /**
     * 关闭 LogCat 输出到磁盘文件
     */
    public void closeCloudLog() {
        this.logCloudLevel = 0;
    }

    /**
     * 开启 Debug 调试
     * 打印输出 org.hjf.xx 包内的 Log 日志信息
     */
    public void openHJFDebug(){
        this.needPrintHJFLog = true;
    }

    /**
     * 获取 LogCat 的级别
     */
    public int getLogCatLevel() {
        return logCatLevel;
    }

    /**
     * 获取 LogFile 的级别
     */
    public int getLogFileLevel() {
        return logFileLevel;
    }

    /**
     * 获取 LogCloud 的级别
     */
    public int getLogCloudLevel() {
        return logCloudLevel;
    }

    /**
     * 获取 CloudFocus 的级别
     */
    public int getLogCloudFocusLevel() {
        return logCloudFocusLevel;
    }

    /**
     * 获取 Database 的级别
     */
    public int getLogDatabaseLevel() {
        return logDatabaseLevel;
    }

    /**
     * 关闭奔溃异常捕捉
     */
    public void closeCrashLog() {
        CrashLog.getInstance().close();
    }

    /**
     * 获取磁盘输出的路径
     */
    public String getDiskLogPath() {
        return this.rootPath + "/log/";
    }

    /**
     * 获取磁盘输出的路径
     */
    public String getCrashLogPath() {
        return this.rootPath + "/crash/";
    }
}
