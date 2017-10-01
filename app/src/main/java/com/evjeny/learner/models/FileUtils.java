package com.evjeny.learner.models;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by Evjeny on 01.07.2017 18:23.
 */

public class FileUtils {
    public FileUtils() {}
    public void saveFile(File file, String content, String encoding) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(content.getBytes(encoding));
        fos.close();
    }

    public void saveFile(File file, String content) throws IOException {
        saveFile(file, content, "UTF-8");
    }

    public String readFile(File file, String encoding) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];
        fis.read(buffer);
        fis.close();
        String result = new String(buffer, Charset.forName(encoding));
        return result;
    }

    public String readFile(File file) throws IOException {
        return readFile(file, "UTF-8");
    }

    public File[] files(String[] from) {
        File[] result = new File[from.length];
        for (int i = 0; i < from.length; i++) {
            result[i] = new File(from[i]);
        }
        return result;
    }

    public String[] readFiles(File[] files, String encoding) throws IOException {
        String[] result = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            result[i] = readFile(files[i], encoding);
        }
        return result;
    }

    public String[] readFiles(File[] files) throws IOException {
        return readFiles(files, "UTF-8");
    }
}