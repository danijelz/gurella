package com.gurella.engine.graphics.vector.sfnt;

import java.io.UnsupportedEncodingException;

public class SfntTagUtils {
	private SfntTagUtils() {
	}
	
	public static int tagToInt(byte... tag) {
	    return tag[0] << 24 | tag[1] << 16 | tag[2] << 8 | tag[3];
	}
	
	public static int tagToInt(String tag) {
	    return tag.charAt(0) << 24 | tag.charAt(1) << 16 | tag.charAt(2) << 8 | tag.charAt(3);
	}
	
	public static byte[] byteValue(int tag) {
		byte[] b = new byte[4];
		b[0] = (byte) (0xff & (tag >> 24));
		b[1] = (byte) (0xff & (tag >> 16));
		b[2] = (byte) (0xff & (tag >> 8));
		b[3] = (byte) (0xff & tag);
		return b;
	}
	
	public static String stringValue(int tag) {
		try {
			return new String(byteValue(tag), "US-ASCII");
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}
}
