package com.gurella.engine.graphics.vector.sfnt.opentype;

import com.badlogic.gdx.utils.IntMap;
import com.gurella.engine.graphics.vector.sfnt.SfntTagUtils;

public enum ScriptTag {
	arab("Arabic"),
	armn("Armenian"),
	avst("Avestan"),
	bali("Balinese"),
	bamu("Bamum"),
	batk("Batak"),
	beng("Bengali"),
	bng2("Bengali v.2"),
	bopo("Bopomofo"),
	brai("Braille"),
	brah("Brahmi"),
	bugi("Buginese"),
	buhd("Buhid"),
	byzm("Byzantine Music"),
	cans("Canadian Syllabics"),
	cari("Carian"),
	cakm("Chakma"),
	cham("Cham"),
	cher("Cherokee"),
	hani("CJK Ideographic"),
	copt("Coptic"),
	cprt("Cypriot Syllabary"),
	cyrl("Cyrillic"),
	DFLT("Default"),
	dflt("Default - MS typo version"),
	dsrt("Deseret"),
	deva("Devanagari"),
	dev2("Devanagari v.2"),
	egyp("Egyptian heiroglyphs"),
	ethi("Ethiopic"),
	geor("Georgian"),
	glag("Glagolitic"),
	goth("Gothic"),
	grek("Greek"),
	gujr("Gujarati"),
	gjr2("Gujarati v.2"),
	guru("Gurmukhi"),
	gur2("Gurmukhi v.2"),
	hang("Hangul"),
	jamo("Hangul Jamo"),
	hano("Hanunoo"),
	hebr("Hebrew"),
	kana("Hiragana, Katakana"),
	armi("Imperial Aramaic"),
	phli("Inscriptional Pahlavi"),
	prti("Inscriptional Parthian"),
	java("Javanese"),
	kthi("Kaithi"),
	knda("Kannada"),
	knd2("Kannada v.2"),
	kali("Kayah Li"),
	khar("Kharosthi"),
	khmr("Khmer"),
	lao("Lao"),
	latn("Latin"),
	lepc("Lepcha"),
	limb("Limbu"),
	linb("Linear B"),
	lisu("Lisu (Fraser)"),
	lyci("Lycian"),
	lydi("Lydian"),
	mlym("Malayalam"),
	mlm2("Malayalam v.2"),
	mand("Mandaic, Mandaean"),
	math("Mathematical Alphanumeric Symbols"),
	mtei("Meitei Mayek (Meithei, Meetei)"),
	merc("Meroitic Cursive"),
	mero("Meroitic Hieroglyphs"),
	mong("Mongolian"),
	musc("Musical Symbols"),
	mymr("Myanmar"),
	mym2("Myanmar v.2"),
	talu("New Tai Lue"),
	nko("N'Ko"),
	ogam("Ogham"),
	olck("Ol Chiki"),
	ital("Old Italic"),
	xpeo("Old Persian Cuneiform"),
	sarb("Old South Arabian"),
	orkh("Old Turkic, Orkhon Runic"),
	orya("Odia (formerly Oriya)"),
	ory2("Odia v.2 (formerly Oriya v.2)"),
	osma("Osmanya"),
	phag("Phags-pa"),
	phnx("Phoenician"),
	rjng("Rejang"),
	runr("Runic"),
	samr("Samaritan"),
	saur("Saurashtra"),
	shrd("Sharada"),
	shaw("Shavian"),
	sinh("Sinhala"),
	sora("Sora Sompeng"),
	xsux("Sumero-Akkadian Cuneiform"),
	sund("Sundanese"),
	sylo("Syloti Nagri"),
	syrc("Syriac"),
	tglg("Tagalog"),
	tagb("Tagbanwa"),
	tale("Tai Le"),
	lana("Tai Tham (Lanna)"),
	tavt("Tai Viet"),
	takr("Takri"),
	taml("Tamil"),
	tml2("Tamil v.2"),
	telu("Telugu"),
	tel2("Telugu v.2"),
	thaa("Thaana"),
	thai("Thai"),
	tibt("Tibetan"),
	tfng("Tifinagh"),
	ugar("Ugaritic Cuneiform"),
	vai("Vai"),
	yi("Yi");

	private static IntMap<ScriptTag> tagMap;

	public final int tag;
	public final String description;

	private ScriptTag(String description) {
		this.tag = SfntTagUtils.tagToInt(name());
		this.description = description;
		getTagMap().put(tag, this);
	}

	public static IntMap<ScriptTag> getTagMap() {
		if (tagMap == null) {
			tagMap = new IntMap<ScriptTag>();
		}
		return tagMap;
	}

	public static ScriptTag fromTag(int value) {
		return tagMap.get(value);
	}
}