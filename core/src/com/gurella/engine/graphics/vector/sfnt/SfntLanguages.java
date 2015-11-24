package com.gurella.engine.graphics.vector.sfnt;

import com.badlogic.gdx.utils.IntMap;

public class SfntLanguages {
	private SfntLanguages() {
	}
	
	public interface LanguageId {
		int value();
		
		PlatformId getPlatformId();
	}
	
	public enum UnicodeLanguageId implements LanguageId {
		Unknown(-1), All(0);

		private static IntMap<UnicodeLanguageId> valuesById;

		public final int value;

		private UnicodeLanguageId(int value) {
			this.value = value;
			getValuesById().put(value, this);
		}

		private static IntMap<UnicodeLanguageId> getValuesById() {
			if (valuesById == null) {
				valuesById = new IntMap<UnicodeLanguageId>();
			}
			return valuesById;
		}

		@Override
		public int value() {
			return this.value;
		}

		public boolean equals(int value) {
			return value == this.value;
		}

		public static UnicodeLanguageId valueOf(int value) {
			return valuesById.get(value, Unknown);
		}
		
		@Override
		public PlatformId getPlatformId() {
			return PlatformId.Unicode;
		}
	}
	
	public enum MacintoshLanguageId implements LanguageId {
	    Unknown(-1),
	    English(0),
	    French(1),
	    German(2),
	    Italian(3),
	    Dutch(4),
	    Swedish(5),
	    Spanish(6),
	    Danish(7),
	    Portuguese(8),
	    Norwegian(9),
	    Hebrew(10),
	    Japanese(11),
	    Arabic(12),
	    Finnish(13),
	    Greek(14),
	    Icelandic(15),
	    Maltese(16),
	    Turkish(17),
	    Croatian(18),
	    Chinese_Traditional(19),
	    Urdu(20),
	    Hindi(21),
	    Thai(22),
	    Korean(23),
	    Lithuanian(24),
	    Polish(25),
	    Hungarian(26),
	    Estonian(27),
	    Latvian(28),
	    Sami(29),
	    Faroese(30),
	    FarsiPersian(31),
	    Russian(32),
	    Chinese_Simplified(33),
	    Flemish(34),
	    IrishGaelic(35),
	    Albanian(36),
	    Romanian(37),
	    Czech(38),
	    Slovak(39),
	    Slovenian(40),
	    Yiddish(41),
	    Serbian(42),
	    Macedonian(43),
	    Bulgarian(44),
	    Ukrainian(45),
	    Byelorussian(46),
	    Uzbek(47),
	    Kazakh(48),
	    Azerbaijani_Cyrillic(49),
	    Azerbaijani_Arabic(50),
	    Armenian(51),
	    Georgian(52),
	    Moldavian(53),
	    Kirghiz(54),
	    Tajiki(55),
	    Turkmen(56),
	    Mongolian_Mongolian(57),
	    Mongolian_Cyrillic(58),
	    Pashto(59),
	    Kurdish(60),
	    Kashmiri(61),
	    Sindhi(62),
	    Tibetan(63),
	    Nepali(64),
	    Sanskrit(65),
	    Marathi(66),
	    Bengali(67),
	    Assamese(68),
	    Gujarati(69),
	    Punjabi(70),
	    Oriya(71),
	    Malayalam(72),
	    Kannada(73),
	    Tamil(74),
	    Telugu(75),
	    Sinhalese(76),
	    Burmese(77),
	    Khmer(78),
	    Lao(79),
	    Vietnamese(80),
	    Indonesian(81),
	    Tagalong(82),
	    Malay_Roman(83),
	    Malay_Arabic(84),
	    Amharic(85),
	    Tigrinya(86),
	    Galla(87),
	    Somali(88),
	    Swahili(89),
	    KinyarwandaRuanda(90),
	    Rundi(91),
	    NyanjaChewa(92),
	    Malagasy(93),
	    Esperanto(94),
	    Welsh(128),
	    Basque(129),
	    Catalan(130),
	    Latin(131),
	    Quenchua(132),
	    Guarani(133),
	    Aymara(134),
	    Tatar(135),
	    Uighur(136),
	    Dzongkha(137),
	    Javanese_Roman(138),
	    Sundanese_Roman(139),
	    Galician(140),
	    Afrikaans(141),
	    Breton(142),
	    Inuktitut(143),
	    ScottishGaelic(144),
	    ManxGaelic(145),
	    IrishGaelic_WithDotAbove(146),
	    Tongan(147),
	    Greek_Polytonic(148),
	    Greenlandic(149),
	    Azerbaijani_Roman(150);
	    
		private static IntMap<MacintoshLanguageId> valuesById;

		public final int value;

		private MacintoshLanguageId(int value) {
			this.value = value;
			getValuesById().put(value, this);
		}

		private static IntMap<MacintoshLanguageId> getValuesById() {
			if (valuesById == null) {
				valuesById = new IntMap<MacintoshLanguageId>();
			}
			return valuesById;
		}

		@Override
		public int value() {
			return this.value;
		}

		public boolean equals(int value) {
			return value == this.value;
		}

		public static MacintoshLanguageId valueOf(int value) {
			return valuesById.get(value, Unknown);
		}
		
		@Override
		public PlatformId getPlatformId() {
			return PlatformId.Macintosh;
		}
	}
	
	public enum WindowsLanguageId implements LanguageId {
	    Unknown(-1),
	    Afrikaans_SouthAfrica(0x0436),
	    Albanian_Albania(0x041C),
	    Alsatian_France(0x0484),
	    Amharic_Ethiopia(0x045E),
	    Arabic_Algeria(0x1401),
	    Arabic_Bahrain(0x3C01),
	    Arabic_Egypt(0x0C01),
	    Arabic_Iraq(0x0801),
	    Arabic_Jordan(0x2C01),
	    Arabic_Kuwait(0x3401),
	    Arabic_Lebanon(0x3001),
	    Arabic_Libya(0x1001),
	    Arabic_Morocco(0x1801),
	    Arabic_Oman(0x2001),
	    Arabic_Qatar(0x4001),
	    Arabic_SaudiArabia(0x0401),
	    Arabic_Syria(0x2801),
	    Arabic_Tunisia(0x1C01),
	    Arabic_UAE(0x3801),
	    Arabic_Yemen(0x2401),
	    Armenian_Armenia(0x042B),
	    Assamese_India(0x044D),
	    Azeri_Cyrillic_Azerbaijan(0x082C),
	    Azeri_Latin_Azerbaijan(0x042C),
	    Bashkir_Russia(0x046D),
	    Basque_Basque(0x042D),
	    Belarusian_Belarus(0x0423),
	    Bengali_Bangladesh(0x0845),
	    Bengali_India(0x0445),
	    Bosnian_Cyrillic_BosniaAndHerzegovina(0x201A),
	    Bosnian_Latin_BosniaAndHerzegovina(0x141A),
	    Breton_France(0x047E),
	    Bulgarian_Bulgaria(0x0402),
	    Catalan_Catalan(0x0403),
	    Chinese_HongKongSAR(0x0C04),
	    Chinese_MacaoSAR(0x1404),
	    Chinese_PeoplesRepublicOfChina(0x0804),
	    Chinese_Singapore(0x1004),
	    Chinese_Taiwan(0x0404),
	    Corsican_France(0x0483),
	    Croatian_Croatia(0x041A),
	    Croatian_Latin_BosniaAndHerzegovina(0x101A),
	    Czech_CzechRepublic(0x0405),
	    Danish_Denmark(0x0406),
	    Dari_Afghanistan(0x048C),
	    Divehi_Maldives(0x0465),
	    Dutch_Belgium(0x0813),
	    Dutch_Netherlands(0x0413),
	    English_Australia(0x0C09),
	    English_Belize(0x2809),
	    English_Canada(0x1009),
	    English_Caribbean(0x2409),
	    English_India(0x4009),
	    English_Ireland(0x1809),
	    English_Jamaica(0x2009),
	    English_Malaysia(0x4409),
	    English_NewZealand(0x1409),
	    English_RepublicOfThePhilippines(0x3409),
	    English_Singapore(0x4809),
	    English_SouthAfrica(0x1C09),
	    English_TrinidadAndTobago(0x2C09),
	    English_UnitedKingdom(0x0809),
	    English_UnitedStates(0x0409),
	    English_Zimbabwe(0x3009),
	    Estonian_Estonia(0x0425),
	    Faroese_FaroeIslands(0x0438),
	    Filipino_Philippines(0x0464),
	    Finnish_Finland(0x040B),
	    French_Belgium(0x080C),
	    French_Canada(0x0C0C),
	    French_France(0x040C),
	    French_Luxembourg(0x140c),
	    French_PrincipalityOfMonoco(0x180C),
	    French_Switzerland(0x100C),
	    Frisian_Netherlands(0x0462),
	    Galician_Galician(0x0456),
	    Georgian_Georgia(0x0437),
	    German_Austria(0x0C07),
	    German_Germany(0x0407),
	    German_Liechtenstein(0x1407),
	    German_Luxembourg(0x1007),
	    German_Switzerland(0x0807),
	    Greek_Greece(0x0408),
	    Greenlandic_Greenland(0x046F),
	    Gujarati_India(0x0447),
	    Hausa_Latin_Nigeria(0x0468),
	    Hebrew_Israel(0x040D),
	    Hindi_India(0x0439),
	    Hungarian_Hungary(0x040E),
	    Icelandic_Iceland(0x040F),
	    Igbo_Nigeria(0x0470),
	    Indonesian_Indonesia(0x0421),
	    Inuktitut_Canada(0x045D),
	    Inuktitut_Latin_Canada(0x085D),
	    Irish_Ireland(0x083C),
	    isiXhosa_SouthAfrica(0x0434),
	    isiZulu_SouthAfrica(0x0435),
	    Italian_Italy(0x0410),
	    Italian_Switzerland(0x0810),
	    Japanese_Japan(0x0411),
	    Kannada_India(0x044B),
	    Kazakh_Kazakhstan(0x043F),
	    Khmer_Cambodia(0x0453),
	    Kiche_Guatemala(0x0486),
	    Kinyarwanda_Rwanda(0x0487),
	    Kiswahili_Kenya(0x0441),
	    Konkani_India(0x0457),
	    Korean_Korea(0x0412),
	    Kyrgyz_Kyrgyzstan(0x0440),
	    Lao_LaoPDR(0x0454),
	    Latvian_Latvia(0x0426),
	    Lithuanian_Lithuania(0x0427),
	    LowerSorbian_Germany(0x082E),
	    Luxembourgish_Luxembourg(0x046E),
	    Macedonian_FYROM_FormerYugoslavRepublicOfMacedonia(0x042F),
	    Malay_BruneiDarussalam(0x083E),
	    Malay_Malaysia(0x043E),
	    Malayalam_India(0x044C),
	    Maltese_Malta(0x043A),
	    Maori_NewZealand(0x0481),
	    Mapudungun_Chile(0x047A),
	    Marathi_India(0x044E),
	    Mohawk_Mohawk(0x047C),
	    Mongolian_Cyrillic_Mongolia(0x0450),
	    Mongolian_Traditional_PeoplesRepublicOfChina(0x0850),
	    Nepali_Nepal(0x0461),
	    Norwegian_Bokmal_Norway(0x0414),
	    Norwegian_Nynorsk_Norway(0x0814),
	    Occitan_France(0x0482),
	    Oriya_India(0x0448),
	    Pashto_Afghanistan(0x0463),
	    Polish_Poland(0x0415),
	    Portuguese_Brazil(0x0416),
	    Portuguese_Portugal(0x0816),
	    Punjabi_India(0x0446),
	    Quechua_Bolivia(0x046B),
	    Quechua_Ecuador(0x086B),
	    Quechua_Peru(0x0C6B),
	    Romanian_Romania(0x0418),
	    Romansh_Switzerland(0x0417),
	    Russian_Russia(0x0419),
	    Sami_Inari_Finland(0x243B),
	    Sami_Lule_Norway(0x103B),
	    Sami_Lule_Sweden(0x143B),
	    Sami_Northern_Finland(0x0C3B),
	    Sami_Northern_Norway(0x043B),
	    Sami_Northern_Sweden(0x083B),
	    Sami_Skolt_Finland(0x203B),
	    Sami_Southern_Norway(0x183B),
	    Sami_Southern_Sweden(0x1C3B),
	    Sanskrit_India(0x044F),
	    Serbian_Cyrillic_BosniaAndHerzegovina(0x1C1A),
	    Serbian_Cyrillic_Serbia(0x0C1A),
	    Serbian_Latin_BosniaAndHerzegovina(0x181A),
	    Serbian_Latin_Serbia(0x081A),
	    SesothoSaLeboa_SouthAfrica(0x046C),
	    Setswana_SouthAfrica(0x0432),
	    Sinhala_SriLanka(0x045B),
	    Slovak_Slovakia(0x041B),
	    Slovenian_Slovenia(0x0424),
	    Spanish_Argentina(0x2C0A),
	    Spanish_Bolivia(0x400A),
	    Spanish_Chile(0x340A),
	    Spanish_Colombia(0x240A),
	    Spanish_CostaRica(0x140A),
	    Spanish_DominicanRepublic(0x1C0A),
	    Spanish_Ecuador(0x300A),
	    Spanish_ElSalvador(0x440A),
	    Spanish_Guatemala(0x100A),
	    Spanish_Honduras(0x480A),
	    Spanish_Mexico(0x080A),
	    Spanish_Nicaragua(0x4C0A),
	    Spanish_Panama(0x180A),
	    Spanish_Paraguay(0x3C0A),
	    Spanish_Peru(0x280A),
	    Spanish_PuertoRico(0x500A),
	    Spanish_ModernSort_Spain(0x0C0A),
	    Spanish_TraditionalSort_Spain(0x040A),
	    Spanish_UnitedStates(0x540A),
	    Spanish_Uruguay(0x380A),
	    Spanish_Venezuela(0x200A),
	    Sweden_Finland(0x081D),
	    Swedish_Sweden(0x041D),
	    Syriac_Syria(0x045A),
	    Tajik_Cyrillic_Tajikistan(0x0428),
	    Tamazight_Latin_Algeria(0x085F),
	    Tamil_India(0x0449),
	    Tatar_Russia(0x0444),
	    Telugu_India(0x044A),
	    Thai_Thailand(0x041E),
	    Tibetan_PRC(0x0451),
	    Turkish_Turkey(0x041F),
	    Turkmen_Turkmenistan(0x0442),
	    Uighur_PRC(0x0480),
	    Ukrainian_Ukraine(0x0422),
	    UpperSorbian_Germany(0x042E),
	    Urdu_IslamicRepublicOfPakistan(0x0420),
	    Uzbek_Cyrillic_Uzbekistan(0x0843),
	    Uzbek_Latin_Uzbekistan(0x0443),
	    Vietnamese_Vietnam(0x042A),
	    Welsh_UnitedKingdom(0x0452),
	    Wolof_Senegal(0x0448),
	    Yakut_Russia(0x0485),
	    Yi_PRC(0x0478),
	    Yoruba_Nigeria(0x046A);
	    
	    private static IntMap<WindowsLanguageId> valuesById;

		public final int value;

		private WindowsLanguageId(int value) {
			this.value = value;
			getValuesById().put(value, this);
		}

		private static IntMap<WindowsLanguageId> getValuesById() {
			if (valuesById == null) {
				valuesById = new IntMap<WindowsLanguageId>();
			}
			return valuesById;
		}

		@Override
		public int value() {
			return this.value;
		}

		public boolean equals(int value) {
			return value == this.value;
		}

		public static WindowsLanguageId valueOf(int value) {
			return valuesById.get(value, Unknown);
		}
		
		@Override
		public PlatformId getPlatformId() {
			return PlatformId.Windows;
		}
	}
}
