package com.werdnx;

import java.io.File;

public class DelFiles {
    public static void main(String[] args) {
        String path = "D:\\ocity\\maven-repository";
        String[] endWith = {".sha1", ".lastUpdated", "_remote.repositories"};
        File root = new File(path);
        removeFromFolder(root, endWith);
    }

    private static void removeFromFolder(File root, String[] endWith) {
        for (File file : root.listFiles()) {
            if (file.isDirectory()) {
                removeFromFolder(file, endWith);
            } else {
                checkToDeleteFile(endWith, file);
            }
        }

    }

    private static void checkToDeleteFile(String[] endWith, File file) {
        for (String s : endWith) {
            if (file.getName().endsWith(s)) {
                System.out.println("To delete " + file.getName());
//                        file.delete();
            }
        }
    }
}
