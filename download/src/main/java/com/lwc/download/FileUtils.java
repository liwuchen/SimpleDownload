package com.lwc.download;

import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * @Package: com.lwc.download;
 * @ClassName: FileUtils
 * @Description: 文件操作工具
 * @Author: liwuchen
 * @CreateDate: 2019/10/12
 */
public class FileUtils {

    private FileUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 检查外部存储是否可用
     * @return
     */
    private static boolean checkExternalStorageState(){
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 通过路径获取文件
     * @param filePath
     * @return
     */
    public static File getFileByPath(final String filePath) {
        return isSpace(filePath) ? null : new File(filePath);
    }

    /**
     * 文件是否存在
     * @param filePath 必须是完整路径
     * @return
     */
    public static boolean isFileExists(final String filePath) {
        return isFileExists(getFileByPath(filePath));
    }

    /**
     * 文件是否存在
     * @param file
     * @return
     */
    public static boolean isFileExists(final File file) {
        return file != null && file.exists();
    }

    /**
     * 是否是目录
     * @param dirPath
     * @return
     */
    public static boolean isDir(final String dirPath) {
        return isDir(getFileByPath(dirPath));
    }

    /**
     * 是否是目录
     * @param file
     * @return
     */
    public static boolean isDir(final File file) {
        return file != null && file.exists() && file.isDirectory();
    }

    /**
     * 是否是文件
     * @param filePath
     * @return
     */
    public static boolean isFile(final String filePath) {
        return isFile(getFileByPath(filePath));
    }

    /**
     * 是否是文件
     * @param file
     * @return
     */
    public static boolean isFile(final File file) {
        return file != null && file.exists() && file.isFile();
    }

    /**
     * 创建目录
     * @param dirPath
     * @return
     */
    public static boolean createDir(final String dirPath) {
        return createDir(getFileByPath(dirPath));
    }

    /**
     * 创建目录
     * @param file
     * @return
     */
    public static boolean createDir(final File file) {
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }

    /**
     * 创建文件
     * @param filePath
     * @return
     */
    public static boolean createFile(final String filePath) {
        return createFile(getFileByPath(filePath));
    }

    /**
     * 创建文件
     * @param file
     * @return
     */
    public static boolean createFile(final File file) {
        if (file == null) {
            return false;
        }
        if (file.exists()) {
            return file.isFile();
        }
        if (!createFile(file.getParentFile())) {
            return false;
        }
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 创建文件
     * @param filePath
     * @return
     */
    public static boolean createFileByDeleteOldFile(final String filePath) {
        return createFileByDeleteOldFile(getFileByPath(filePath));
    }

    /**
     * 创建文件
     * @param file
     * @return
     */
    public static boolean createFileByDeleteOldFile(final File file) {
        if (file == null) {
            return false;
        }
        if (file.exists() && !file.delete()) {
            return false;
        }
        if (!createDir(file.getParentFile())) {
            return false;
        }
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 拷贝目录
     * @param srcDirPath
     * @param destDirPath
     * @return
     */
    public static boolean copyDir(String srcDirPath, String destDirPath) {
        return copyDir(getFileByPath(srcDirPath), getFileByPath(destDirPath));
    }

    /**
     * 拷贝目录
     * @param srcDir
     * @param destDir
     * @return
     */
    public static boolean copyDir(File srcDir, File destDir) {
        return copyOrMoveDir(srcDir, destDir, false);
    }

    /**
     * 拷贝目录
     * @param srcDirPath
     * @param destDirPath
     * @param listener
     * @return
     */
    public static boolean copyDir(String srcDirPath, String destDirPath, OnReplaceListener listener) {
        return copyDir(getFileByPath(srcDirPath), getFileByPath(destDirPath), listener);
    }

    /**
     * 拷贝目录
     * @param srcDir
     * @param destDir
     * @param listener
     * @return
     */
    public static boolean copyDir(File srcDir, File destDir, OnReplaceListener listener) {
        return copyOrMoveDir(srcDir, destDir, listener, false);
    }

    /**
     * 拷贝文件
     * @param srcFilePath
     * @param destFilePath
     * @return
     */
    public static boolean copyFile(String srcFilePath, String destFilePath) {
        return copyFile(getFileByPath(srcFilePath), getFileByPath(destFilePath));
    }

    /**
     * 拷贝文件
     * @param srcFile
     * @param destFile
     * @return
     */
    public static boolean copyFile(File srcFile, File destFile) {
        return copyOrMoveFile(srcFile, destFile, false);
    }

    /**
     * 拷贝文件
     * @param srcFilePath
     * @param destFilePath
     * @param listener
     * @return
     */
    public static boolean copyFile(String srcFilePath, String destFilePath, OnReplaceListener listener) {
        return copyFile(getFileByPath(srcFilePath), getFileByPath(destFilePath), listener);
    }

    /**
     * 拷贝文件
     * @param srcFile
     * @param destFile
     * @param listener
     * @return
     */
    public static boolean copyFile(final File srcFile, File destFile, OnReplaceListener listener) {
        return copyOrMoveFile(srcFile, destFile, listener, false);
    }

    private static boolean copyOrMoveDir(File srcDir, File destDir, boolean isMove) {
        return copyOrMoveDir(srcDir, destDir, new OnReplaceListener() {
            @Override
            public boolean onReplace() {
                return true;
            }
        }, isMove);
    }

    private static boolean copyOrMoveDir(File srcDir, File destDir, OnReplaceListener listener, boolean isMove) {
        if (srcDir == null || destDir == null) {
            return false;
        }
        String srcPath = srcDir.getPath() + File.separator;
        String destPath = destDir.getPath() + File.separator;
        if (destPath.contains(srcPath)) {
            return false;
        }
        if (!srcDir.exists() || !srcDir.isDirectory()) {
            return false;
        }
        if (destDir.exists()) {
            if (listener == null || listener.onReplace()) {
                if (!deleteAllInDir(destDir)) {
                    return false;
                }
            } else {
                return true;
            }
        }
        if (!createDir(destDir)) {
            return false;
        }
        File[] files = srcDir.listFiles();
        for (File file : files) {
            File oneDestFile = new File(destPath + file.getName());
            if (file.isFile()) {
                if (!copyOrMoveFile(file, oneDestFile, listener, isMove)) return false;
            } else if (file.isDirectory()) {
                if (!copyOrMoveDir(file, oneDestFile, listener, isMove)) return false;
            }
        }
        return !isMove || deleteDir(srcDir);
    }

    private static boolean copyOrMoveFile(File srcFile, File destFile, boolean isMove) {
        return copyOrMoveFile(srcFile, destFile, new OnReplaceListener() {
            @Override
            public boolean onReplace() {
                return true;
            }
        }, isMove);
    }

    private static boolean copyOrMoveFile(File srcFile, File destFile, OnReplaceListener listener, boolean isMove) {
        if (srcFile == null || destFile == null) {
            return false;
        }
        if (srcFile.equals(destFile)) {
            return false;
        }
        if (!srcFile.exists() || !srcFile.isFile()) {
            return false;
        }
        if (destFile.exists()) {
            if (listener == null || listener.onReplace()) {
                if (!destFile.delete()) {
                    return false;
                }
            } else {
                return true;
            }
        }
        if (!createDir(destFile.getParentFile())) {
            return false;
        }
        try {
            return writeFileFromIS(destFile, new FileInputStream(srcFile))
                    && !(isMove && !deleteFile(srcFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     *
     * @param dirPath
     * @return
     */
    public static boolean deleteAllInDir(final String dirPath) {
        return deleteAllInDir(getFileByPath(dirPath));
    }

    /**
     * 删除目录（含过滤器）
     * @param dir
     * @return
     */
    public static boolean deleteAllInDir(File dir) {
        return deleteFilesInDirWithFilter(dir, new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return true;
            }
        });
    }

    /**
     * 删除目录（过滤器之内的文件）
     * @param dirPath
     * @param filter
     * @return
     */
    public static boolean deleteFilesInDirWithFilter(String dirPath, FileFilter filter) {
        return deleteFilesInDirWithFilter(getFileByPath(dirPath), filter);
    }

    /**
     * 删除目录（过滤器之内的文件）
     * @param dir
     * @param filter
     * @return
     */
    public static boolean deleteFilesInDirWithFilter(File dir, FileFilter filter) {
        if (dir == null) {
            return false;
        }
        if (!dir.exists()) {
            return true;
        }
        if (!dir.isDirectory()) {
            return false;
        }
        File[] files = dir.listFiles();
        if (files != null && files.length != 0) {
            for (File file : files) {
                if (filter.accept(file)) {
                    if (file.isFile()) {
                        if (!file.delete()) return false;
                    } else if (file.isDirectory()) {
                        if (!deleteDir(file)) return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * 删除目录
     * @param dir
     * @return
     */
    public static boolean deleteDir(File dir) {
        if (dir == null) {
            return false;
        }
        if (!dir.exists()) {
            return true;
        }
        if (!dir.isDirectory()) {
            return false;
        }
        File[] files = dir.listFiles();
        if (files != null && files.length != 0) {
            for (File file : files) {
                if (file.isFile()) {
                    if (!file.delete()) {
                        return false;
                    }
                } else if (file.isDirectory()) {
                    if (!deleteDir(file)) {
                        return false;
                    }
                }
            }
        }
        return dir.delete();
    }

    /**
     * 写文件（把流写进文件）
     * @param file
     * @param is
     * @return
     */
    public static boolean writeFileFromIS(File file, InputStream is) {
        OutputStream os = null;
        try {
            FileOutputStream fos = new FileOutputStream(file);
            os = new BufferedOutputStream(fos);
            byte data[] = new byte[8192];
            int len;
            while ((len = is.read(data, 0, 8192)) != -1) {
                os.write(data, 0, len);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 写文件（把流写进文件）
     * @param filePath
     * @param fileName
     * @param is
     * @return
     */
    public static boolean writeFileFromIS(String filePath, String fileName, InputStream is, boolean newFile) {
        File dirFolder = new File(filePath);
        if (!dirFolder.exists()) {
            dirFolder.mkdirs();
        }
        File file = new File(filePath + File.separator + fileName);
        OutputStream os = null;
        try {
            if (newFile) {
                file.createNewFile();
                os = new BufferedOutputStream(new FileOutputStream(file));
            } else {
                if (!file.exists()) {
                    file.createNewFile();
                }
                os = new BufferedOutputStream(new FileOutputStream(file, true));
            }
            byte data[] = new byte[8192];
            int len;
            while ((len = is.read(data, 0, 8192)) != -1) {
                os.write(data, 0, len);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 删除文件
     * @param srcFilePath
     * @param fileName
     * @return
     */
    public static boolean deleteFile(String srcFilePath, String fileName) {
        if (TextUtils.isEmpty(srcFilePath) || TextUtils.isEmpty(fileName)) {
            return false;
        }
        File file = new File(srcFilePath + File.separator + fileName);
        return deleteFile(file.getPath());
    }

    /**
     * 删除文件
     * @param srcFilePath
     * @return
     */
    public static boolean deleteFile(String srcFilePath) {
        return deleteFile(getFileByPath(srcFilePath));
    }

    /**
     * 删除文件
     * @param file
     * @return
     */
    public static boolean deleteFile(File file) {
        return file != null && (!file.exists() || file.isFile() && file.delete());
    }

    private static boolean isSpace(final String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }


    public interface OnReplaceListener {
        boolean onReplace();
    }



}
