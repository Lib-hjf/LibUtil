package org.hjf.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class FileWriterTask implements Runnable {

    private String filePath;
    protected String content;
    private boolean append = false;

    public FileWriterTask(String filePath, String info) {
        super();
        this.filePath = filePath;
        this.content = info;
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
            writer.write(content);
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
