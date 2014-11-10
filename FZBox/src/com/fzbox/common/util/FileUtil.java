package com.fzbox.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.os.Environment;
import android.util.Log;

public class FileUtil {
    final private static String TAG = "FileUtil";

    private FileUtil() {}

    public static boolean forceDeleteFile(File file) {
        boolean result = false;
        int tryCount = 0;
        while (!result && tryCount++ < 10) {
            result = file.delete();
            if (!result) {
                try {
                    synchronized (file) {
                        file.wait(200);
                    }
                } catch (InterruptedException e) {
                    Log.e(TAG, e.getMessage() == null ? e.getClass().getName() : e.getMessage());
                }
            }
        }
        return result;
    }

    /**
     * 閫氳繃鎻愪緵鐨勬枃浠跺悕鍦ㄩ粯璁よ矾寰勪笅鐢熸垚鏂囦欢
     * 
     * @param fileName 鏂囦欢鐨勫悕绉�
     * @return 鐢熸垚鐨勬枃浠�
     * @throws IOException
     */
    public static File createFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }

    /**
     * 閫氳繃鎻愪緵鐨勬枃浠跺悕鍦ㄩ粯璁よ矾寰勪笅鐢熸垚鏂囦欢
     * 
     * @param fileName 鏂囦欢鐨勫悕绉�
     * @return 鐢熸垚鐨勬枃浠�
     * @throws IOException
     */
    public static void createFolder(String filePath) {
        filePath = filePath.replace("\\", "/");
        String folderPath = filePath.substring(0, filePath.lastIndexOf("/") + 1);
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    /**
     * 鍒犻櫎璺緞鎸囧悜鐨勬枃浠�
     * 
     * @param fileName 鏂囦欢鐨勫悕绉�
     * @return true鍒犻櫎鎴愬姛锛宖alse鍒犻櫎澶辫触
     */
    public static boolean deleteFile(final String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return file.delete();
        }
        return true;
    }

    /**
     * 灏嗕粠涓嬭浇绠＄悊閭ｉ噷鑾峰彇鏉ョ殑鏁版嵁娴佸啓鍏ユ枃浠朵腑
     * 
     * @param ops 浠庝笅杞界鐞嗛偅閲岃幏鍙栨潵鐨刬o娴�
     * @param fileName 闇�瀛樺偍鐨勬枃浠剁殑璺緞鍜屽悕绉�
     * @return 鎬诲叡瀛樺偍鎴愬姛鐨勫瓧鑺傛暟
     * @throws SDNotEnouchSpaceException
     * @throws SDUnavailableException
     * @throws IOException
     */
    public static void writeFile(byte[] bytes, String filePath) throws IOException {
        if (bytes != null) {
            RandomAccessFile file = null;
            try {
                file = new RandomAccessFile(createFile(filePath), "rw");
                file.seek(file.length());
                file.write(bytes);
            } catch (IOException e) {
                Log.w(TAG, e.getMessage(), e);
                throw e;
            } finally {
                try {
                    if (file != null) {
                        file.close();
                    }
                } catch (IOException e) {
                    Log.w(TAG, e.getMessage(), e);
                }
            }
        }
    }

    public static void copyFile(String ownerFilePath, String targetFilePath) throws Exception {
        File ownerFile = new File(ownerFilePath);
        File targetFile = new File(targetFilePath);
        if (ownerFile.isFile() && targetFile.createNewFile()) {
            FileInputStream input = new FileInputStream(ownerFile);
            FileOutputStream output = new FileOutputStream(targetFile);
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = input.read(b)) != -1) {
                output.write(b, 0, len);
            }
            output.flush();
            output.close();
            input.close();
        }
    }

    /**
     * 閫氳繃鎻愪緵鐨勬枃浠跺悕鍦ㄩ粯璁よ矾寰勪笅鐢熸垚鏂囦欢
     * 
     * @param fileName 鏂囦欢鐨勫悕绉�
     * @return 鐢熸垚鐨勬枃浠�
     * @throws IOException
     */
    public static File createNewFile(String filePath) throws IOException {
        String folderPath = filePath.substring(0, filePath.lastIndexOf("/"));
        File folder = getFileByPath(folderPath);
        folder.mkdirs();
        File file = getFileByPath(filePath);
        if (!file.exists()) {
            file.createNewFile();
        } else {
            return createFile(getNextPath(filePath));
        }
        return file;
    }

    private static String getNextPath(String path) {
        Pattern pattern = Pattern.compile("\\(\\d{1,}\\)\\.");
        Matcher matcher = pattern.matcher(path); // 闄や腑鏂囦笉鐢ㄥ锛屽叾浠栫殑閮借
        String str = null;
        while (matcher.find()) {
            str = matcher.group(matcher.groupCount());
            System.out.println("[" + str + "]");
        }
        if (str == null) {
            int index = path.lastIndexOf(".");
            path = path.substring(0, index) + "(1)" + path.substring(index);
        } else {
            int index = Integer.parseInt(str.replaceAll("[^\\d]*(\\d)[^\\d]*", "$1")) + 1;
            path = path.replace(str, "(" + index + ").");
        }
        return path;
    }

    public static File getFileByPath(String filePath) {
        filePath = filePath.replaceAll("\\\\", "/");
        boolean isSdcard = false;
        int subIndex = 0;
        if (filePath.indexOf("/sdcard") == 0) {
            isSdcard = true;
            subIndex = 7;
        } else if (filePath.indexOf("/mnt/sdcard") == 0) {
            isSdcard = true;
            subIndex = 11;
        }

        if (isSdcard) {
            if (isExistSdcard()) {
                File sdCardDir = Environment.getExternalStorageDirectory();// 鑾峰彇SDCard鐩綍,2.2鐨勬椂鍊欎负:/mnt/sdcard
                                                                           // 2.1鐨勬椂鍊欎负锛�sdcard锛屾墍浠ヤ娇鐢ㄩ潤鎬佹柟娉曞緱鍒拌矾寰勪細濂戒竴鐐广�
                String fileName = filePath.substring(subIndex);
                return new File(sdCardDir, fileName);
            } else if (isEmulator()) {
                File sdCardDir = Environment.getExternalStorageDirectory();
                String fileName = filePath.substring(subIndex);
                return new File(sdCardDir, fileName);
            }
            return null;
        } else {
            return new File(filePath);
        }
    }

    private static boolean isExistSdcard() {
        if (!isEmulator()) {
            return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        }
        return true;
    }

    private static boolean isEmulator() {
        return android.os.Build.MODEL.equals("sdk");
    }
}
