package com.werdnx;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CompareJson {

    public static void main(String[] args) throws IOException {
        File f1 = new File("D:\\temp\\1126\\json_notzip.json");
        File f2 = new File("D:\\temp\\1126\\json_zip.json");
        boolean b = equalFiles(f1, f2);
        System.out.println(b);
    }

    static boolean equalFiles(File f1, File f2) throws IOException {
        byte[] b1 = getBytesFromFile(f1);
        byte[] b2 = getBytesFromFile(f2);

        if (b1.length != b2.length) return false;
        for (int i = 0; i < b1.length; i++) {
            if (b1[i] != b2[i]) return false;
        }
        return true;
    }

    static int firstDiffBetween(File f1, File f2) throws IOException {
        byte[] b1 = getBytesFromFile(f1);
        byte[] b2 = getBytesFromFile(f2);

        int shortest = b1.length;
        if (b2.length < shortest) shortest = b2.length;
        for (int i = 0; i < shortest; i++) {
            if (b1[i] != b2[i]) return i;
        }
        return -1;
    }

    public static byte[] getBytesFromFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        long length = file.length();
        byte[] bytes = new byte[(int) length];
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }
        is.close();
        return bytes;
    }
}
