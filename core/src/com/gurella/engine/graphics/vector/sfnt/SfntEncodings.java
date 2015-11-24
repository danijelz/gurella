package com.gurella.engine.graphics.vector.sfnt;

import com.badlogic.gdx.utils.IntMap;

public class SfntEncodings {
	public static final String DEFAULT_ENCODING = "ISO-8859-1";
	
	private SfntEncodings() {
	}
	
	public static String getEncoding(int platformId, int encodingId) {
		String encoding = getPredefinedEncoding(platformId, encodingId);
		return encoding == null ? SfntEncodings.DEFAULT_ENCODING : encoding;
	}

	private static String getPredefinedEncoding(int platformId, int encodingId) {
		switch (PlatformId.valueOf(platformId)) {
		case Unicode:
			return UnicodeEncodingId.valueOf(encodingId).encodingName;
		case Macintosh:
			return MacintoshEncodingId.valueOf(encodingId).encodingName;
		case ISO:
			return null;
		case Windows:
			return WindowsEncodingId.valueOf(encodingId).encodingName;
		case Custom:
			return null;
		default:
			return null;
		}
	}
	
	public interface EncodingId {
		int value();
		
		String getEncodingName();
		
		PlatformId getPlatformId();
	}
	
	public enum UnicodeEncodingId implements EncodingId {
	    Unknown(-1, null),
	    Unicode1_0(0, "UTF-16BE"),//TODO is it OK that all encodings are same?
	    Unicode1_1(1, "UTF-16BE"),
	    ISO10646(2, "UTF-16BE"),
	    Unicode2_0_BMP(3, "UTF-16BE"),
	    Unicode2_0(4, "UTF-16BE"),
	    UnicodeVariationSequences(5, "UTF-16BE");
	    
		private static IntMap<UnicodeEncodingId> valuesById;

		public final int value;
		public final String encodingName;

		private UnicodeEncodingId(int value, String encodingName) {
			this.value = value;
			this.encodingName = encodingName;
			getValuesById().put(value, this);
		}

		private static IntMap<UnicodeEncodingId> getValuesById() {
			if (valuesById == null) {
				valuesById = new IntMap<UnicodeEncodingId>();
			}
			return valuesById;
		}

		@Override
		public int value() {
			return this.value;
		}
		
		@Override
		public String getEncodingName() {
			return encodingName;
		}

		public boolean equals(int value) {
			return value == this.value;
		}

		public static UnicodeEncodingId valueOf(int value) {
			return valuesById.get(value, Unknown);
		}
		
		@Override
		public PlatformId getPlatformId() {
			return PlatformId.Unicode;
		}
	}
	
	public enum WindowsEncodingId implements EncodingId {
	    Unknown(-1, null),
	    Symbol(0, "UTF-16BE"),
	    UnicodeUCS2(1, "UTF-16BE"),
	    ShiftJIS(2, "windows-933"),
	    PRC(3, "windows-936"),
	    Big5(4, "windows-950"),
	    Wansung(5, "windows-949"),
	    Johab(6, "ms1361"),
	    UnicodeUCS4(10, "UCS-4");
	    
	    private static IntMap<WindowsEncodingId> valuesById;

	    public final int value;
	    public final String encodingName;

		private WindowsEncodingId(int value, String encodingName) {
			this.value = value;
			this.encodingName = encodingName;
			getValuesById().put(value, this);
		}

		private static IntMap<WindowsEncodingId> getValuesById() {
			if (valuesById == null) {
				valuesById = new IntMap<WindowsEncodingId>();
			}
			return valuesById;
		}

		@Override
		public int value() {
			return this.value;
		}

		@Override
		public String getEncodingName() {
			return encodingName;
		}

		public boolean equals(int value) {
			return value == this.value;
		}

		public static WindowsEncodingId valueOf(int value) {
			return valuesById.get(value, Unknown);
		}
		
		@Override
		public PlatformId getPlatformId() {
			return PlatformId.Windows;
		}
	}

	public enum MacintoshEncodingId implements EncodingId {
	    Unknown(-1, null),
	    Roman(0, "MacRoman"),
	    Japanese(1, "Shift_JIS"),
	    ChineseTraditional(2, "Big5"),
	    Korean(3, "EUC-KR"),
	    Arabic(4, "MacArabic"),
	    Hebrew(5, "MacHebrew"),
	    Greek(6, "MacGreek"),
	    Russian(7, "MacCyrillic"),
	    RSymbol(8, "MacSymbol"),
	    Devanagari(9, null),
	    Gurmukhi(10, null),
	    Gujarati(11, null),
	    Oriya(12, null),
	    Bengali(13, null),
	    Tamil(14, null),
	    Telugu(15, null),
	    Kannada(16, null),
	    Malayalam(17, null),
	    Sinhalese(18, null),
	    Burmese(19, null),
	    Khmer(20, null),
	    Thai(21, "MacThai"),
	    Laotian(22, null),
	    Georgian(23, "MacCyrillic"),
	    Armenian(24, null),
	    ChineseSimplified(25, "EUC-CN"),
	    Tibetan(26, null),
	    Mongolian(27, "MacCyrillic"),
	    Geez(28, null),
	    Slavic(29, "MacCentralEurope"),
	    Vietnamese(30, null),
	    Sindhi(31, null),
	    Uninterpreted(32, null);

	    private static IntMap<MacintoshEncodingId> valuesById;

	    public final int value;
	    public final String encodingName;

		private MacintoshEncodingId(int value, String encodingName) {
			this.value = value;
			this.encodingName = encodingName;
			getValuesById().put(value, this);
		}

		private static IntMap<MacintoshEncodingId> getValuesById() {
			if (valuesById == null) {
				valuesById = new IntMap<MacintoshEncodingId>();
			}
			return valuesById;
		}

		@Override
		public int value() {
			return this.value;
		}
		
		@Override
		public String getEncodingName() {
			return encodingName;
		}

		public boolean equals(int value) {
			return value == this.value;
		}

		public static MacintoshEncodingId valueOf(int value) {
			return valuesById.get(value, Unknown);
		}
		
		@Override
		public PlatformId getPlatformId() {
			return PlatformId.Macintosh;
		}
	}
}
