package com.gurella.engine.graphics.vector.svg.element;

import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pools;
import com.gurella.engine.graphics.vector.svg.Svg;

//TODO switch, animations, mask, pattern, text..., 
public enum ElementType {
	svg(SvgElement.class),
	a(AnchorElemet.class),
	defs(DefsElement.class),
	linearGradient(LinearGradientElement.class),
	radialGradient(RadialGradientElement.class),
	filter(FilterElement.class),
	feGaussianBlur(GaussianBlurElement.class),
	stop(StopElement.class),
	g(GroupElement.class),
	circle(CircleElement.class),
	ellipse(EllipseElement.class),
	line(LineElement.class),
	path(PathElement.class),
	polygon(PolygonElement.class),
	polyline(PolylineElement.class),
	rect(RectElement.class),
	style(StyleElement.class),
	pattern(PatternElement.class),
	clipPath(ClipPathElement.class),
	symbol(SymbolElement.class),
	use(UseElement.class),
	image(ImageElement.class),
	solidColor(SolidColorElement.class),
	view(ViewElement.class),
	text(TextElement.class),
	tspan(TspanElement.class),
	tref(TrefElement.class),
	textPath(TextPathElement.class),
	font("font", FontElement.class),
	fontFace("font-face", FontFaceElement.class),
	fontFaceSrc("font-face-src", FontFaceSrcElement.class),
	fontFaceUri("font-face-uri", FontFaceUriElement.class),
	fontFaceFormat("font-face-format", FontFaceFormatElement.class),
	fontFaceName("font-face-name", FontFaceNameElement.class),
	glyph(GlyphElement.class),
	missingGlyph("missing-glyph", MissingGlyphElement.class),
	altGlyph(AltGlyphElement.class),
	altGlyphDef(AltGlyphDefElement.class),
	altGlyphItem(AltGlyphItemElement.class),
	glyphRef(GlyphRefElement.class),
	marker(MarkerElement.class), 
	title(TitleElement.class),
	desc(DescElement.class),
	;

	private static ObjectMap<String, ElementType> tagsByName = new ObjectMap<String, ElementType>();
	static {
		ElementType[] values = values();
		for (int i = 0; i < values.length; i++) {
			ElementType value = values[i];
			tagsByName.put(value.elementName, value);

		}
	}

	public final String elementName;
	public final Class<? extends Element> elementClass;

	private ElementType(Class<? extends Element> elementClass) {
		this.elementName = name();
		this.elementClass = elementClass;
	}

	private ElementType(String elementName, Class<? extends Element> elementClass) {
		this.elementName = elementName;
		this.elementClass = elementClass;
	}

	private Element create() {
		return Pools.obtain(elementClass);
	}

	public static ElementType getTagByName(String tagName) {
		return tagName == null ? null : (ElementType) tagsByName.get(tagName.toLowerCase());
	}

	public static Element createElement(Svg svg, String elementName) {
		ElementType type = getTagByName(elementName);
		Element element = type == null ? new UnsupportedElement(elementName) : type.create();
		element.svg = svg;
		return element;
	}
}
