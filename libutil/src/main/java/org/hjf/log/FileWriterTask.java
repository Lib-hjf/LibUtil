package org.hjf.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * 任务：文件写入到磁盘
 * Log 调试时 不能使用 LogUtil，避免循环建立 LogCat 写入本地文件线程任务
 */
class FileWriterTask implements Runnable {

    private String filePath;
    private String info;
    private boolean append = false;

    FileWriterTask(String filePath, String info) {
        super();
        this.filePath = filePath;
        this.info = info;
    }

    public void setAppendable(boolean append) {
        this.append = append;
    }

    @Override
    public void run() {

        // 检测并创建目录
        File file = new File(filePath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        // 写出动作
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, this.append), "UTF-8"));
            writer.write(info);
            writer.newLine();
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 关闭流
        finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException ignored) {
            }
        }
    }
}
