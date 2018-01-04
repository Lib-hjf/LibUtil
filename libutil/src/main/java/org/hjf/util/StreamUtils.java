package org.hjf.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 输入/输出工具类
 */
public class StreamUtils {

    /**
     * 读取文件为 String
     */
    public static String readFile2String(String filePath){
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
        }
        return StreamUtils.readFile2String(inputStream);
    }

    /**
     * 读取文件为 String
     */
    public static String readFile2String(InputStream inputStream){
        if (inputStream == null) {
            return "";
        }
        BufferedReader reader = null;
        StringBuilder stringBuilder = new StringBuilder();
        reader = new BufferedReader(new InputStreamReader(inputStream));
        String tempString;
        try {
            while ((tempString = reader.readLine()) != null){
                stringBuilder.append(tempString);
            }
        } catch (IOException e1) {
        } finally{
            if (reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }
        return stringBuilder.toString();
    }

}
