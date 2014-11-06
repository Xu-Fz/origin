package com.fzbox.common.util;

/**
 * One sentence description Detailed description
 * 
 * @author Administrator
 * @version [version, Jul 30, 2011]
 * @see [relevant class/method]
 * @since [product/module version]
 */
public class BytesUtil {
	public static int indexof(byte[] shortBytes, byte[] longBytes) {
		for (int i = 0; i < longBytes.length; i++) {
			if (longBytes[i] == shortBytes[0]
					&& longBytes.length - i >= shortBytes.length) {
				boolean isIndexof = true;
				for (int j = 1; j < shortBytes.length; j++) {
					if (shortBytes[j] != longBytes[i + j]) {
						isIndexof = false;
						continue;
					}
				}
				if (isIndexof) {
					return i;
				}
			}
		}
		return -1;
	}

	public static byte[] addBytes(byte[] src1, byte[] src2) {
		byte[] dest = new byte[src1.length + src2.length];
		System.arraycopy(src1, 0, dest, 0, src1.length);
		System.arraycopy(src2, 0, dest, src1.length, src2.length);
		return dest;
	}

	public static byte[] cutBytes(byte[] bytes, int pos, int length) {
		if (length < 0) {
			return null;
		}
		byte[] dest = new byte[length];
		System.arraycopy(bytes, pos, dest, 0, length);
		return dest;
	}

}
