package com.fzbox.common.util;

import java.io.File;
import android.os.Environment;
import android.os.StatFs;

/**
 * Collection of methods for operate memory.
 * 
 */
public class MemoryStatus {

	static final int ERROR = -1;

	private static final long RESERVED_SIZE = 2097152;

	static public boolean externalMemoryAvailable() {
		return android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
	}

	static public long getAvailableInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return availableBlocks * blockSize;
	}

	static public long getTotalInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return totalBlocks * blockSize;
	}

	static public long getAvailableExternalMemorySize() {
		if (externalMemoryAvailable()) {
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long availableBlocks = stat.getAvailableBlocks();
			return availableBlocks * blockSize;
		} else {
			return ERROR;
		}
	}

	static public long getTotalExternalMemorySize() {
		if (externalMemoryAvailable()) {
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long totalBlocks = stat.getBlockCount();
			return totalBlocks * blockSize;
		} else {
			return ERROR;
		}
	}

	static public String formatSize(long size) {
		String suffix = "B";

		if (size >= 1024) {
			suffix = "KiB";
			size /= 1024;
			if (size >= 1024) {
				suffix = "MiB";
				size /= 1024;
			}
		}

		StringBuilder resultBuffer = new StringBuilder(Long.toString(size));

		int commaOffset = resultBuffer.length() - 3;
		while (commaOffset > 0) {
			resultBuffer.insert(commaOffset, ',');
			commaOffset -= 3;
		}

		if (suffix != null) {
			resultBuffer.append(suffix);
		}
		return resultBuffer.toString();
	}

	static public boolean isExternalMemoryAvailable(long size) {
		long availableMemory = getAvailableExternalMemorySize();
		return !(size > availableMemory);
	}

	static public boolean isInternalMemoryAvailable(long size) {
		long availableMemory = getAvailableInternalMemorySize();
		return !(size > availableMemory);
	}

	static public boolean isMemoryAvailable(long size) {
		size += RESERVED_SIZE;
		if (externalMemoryAvailable()) {
			return isExternalMemoryAvailable(size);
		}
		return isInternalMemoryAvailable(size);
	}

	static public long getSpecificMemoryAvaliable(String path) {
		StatFs stat = new StatFs(path);
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return availableBlocks * blockSize;
	}

	static public boolean isSpecificMemoryAvailable(long size, String path) {
		long availableMemory = getSpecificMemoryAvaliable(path);
		return !(size > availableMemory);
	}
}
