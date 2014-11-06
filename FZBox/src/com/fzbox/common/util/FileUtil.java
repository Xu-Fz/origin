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

	private FileUtil() {
	}

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
					Log.e(TAG, e.getMessage() == null ? e.getClass().getName()
							: e.getMessage());
				}
			}
		}
		return result;
	}

	/**
	 * 通过提供的文件名在默认路径下生成文件
	 * 
	 * @param fileName
	 *            文件的名称
	 * @return 生成的文件
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
	 * 通过提供的文件名在默认路径下生成文件
	 * 
	 * @param fileName
	 *            文件的名称
	 * @return 生成的文件
	 * @throws IOException
	 */
	public static void createFolder(String filePath) {
		filePath = filePath.replace("\\", "/");
		String folderPath = filePath
				.substring(0, filePath.lastIndexOf("/") + 1);
		File folder = new File(folderPath);
		if (!folder.exists()) {
			folder.mkdirs();
		}
	}

	/**
	 * 删除路径指向的文件
	 * 
	 * @param fileName
	 *            文件的名称
	 * @return true删除成功，false删除失败
	 */
	public static boolean deleteFile(final String filePath) {
		File file = new File(filePath);
		if (file.exists()) {
			return file.delete();
		}
		return true;
	}

	/**
	 * 将从下载管理那里获取来的数据流写入文件中
	 * 
	 * @param ops
	 *            从下载管理那里获取来的io流
	 * @param fileName
	 *            需要存储的文件的路径和名称
	 * @return 总共存储成功的字节数
	 * @throws SDNotEnouchSpaceException
	 * @throws SDUnavailableException
	 * @throws IOException
	 */
	public static void writeFile(byte[] bytes, String filePath)
			throws IOException {
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
					if(file != null){
						file.close();
					}
				} catch (IOException e) {
					Log.w(TAG, e.getMessage(), e);
				}
			}
		}
	}

	public static void copyFile(String ownerFilePath, String targetFilePath)
			throws Exception {
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
	 * 通过提供的文件名在默认路径下生成文件
	 * 
	 * @param fileName
	 *            文件的名称
	 * @return 生成的文件
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
		Matcher matcher = pattern.matcher(path); // 除中文不用外，其他的都要
		String str = null;
		while (matcher.find()) {
			str = matcher.group(matcher.groupCount());
			System.out.println("[" + str + "]");
		}
		if (str == null) {
			int index = path.lastIndexOf(".");
			path = path.substring(0, index) + "(1)" + path.substring(index);
		} else {
			int index = Integer.parseInt(str.replaceAll("[^\\d]*(\\d)[^\\d]*",
					"$1")) + 1;
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
				File sdCardDir = Environment.getExternalStorageDirectory();// 获取SDCard目录,2.2的时候为:/mnt/sdcard
																			// 2.1的时候为：/sdcard，所以使用静态方法得到路径会好一点。
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
			return Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED);
		}
		return true;
	}

	private static boolean isEmulator() {
		return android.os.Build.MODEL.equals("sdk");
	}
}
