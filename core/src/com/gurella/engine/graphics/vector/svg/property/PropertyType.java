package com.gurella.engine.graphics.vector.svg.property;

import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.graphics.vector.AffineTransform;
import com.gurella.engine.graphics.vector.GradientSpread;
import com.gurella.engine.graphics.vector.LineCap;
import com.gurella.engine.graphics.vector.LineJoin;
import com.gurella.engine.graphics.vector.Path.UnmodifiablePath;
import com.gurella.engine.graphics.vector.svg.property.parser.ColorParser;
import com.gurella.engine.graphics.vector.svg.property.parser.DisplayParser;
import com.gurella.engine.graphics.vector.svg.property.parser.FillRuleParser;
import com.gurella.engine.graphics.vector.svg.property.parser.FloatArrayParser;
import com.gurella.engine.graphics.vector.svg.property.parser.FloatParser;
import com.gurella.engine.graphics.vector.svg.property.parser.LengthParser;
import com.gurella.engine.graphics.vector.svg.property.parser.LineCapParser;
import com.gurella.engine.graphics.vector.svg.property.parser.LineJoinParser;
import com.gurella.engine.graphics.vector.svg.property.parser.MarkerUnitsParser;
import com.gurella.engine.graphics.vector.svg.property.parser.OrientParser;
import com.gurella.engine.graphics.vector.svg.property.parser.OverflowParser;
import com.gurella.engine.graphics.vector.svg.property.parser.PaintParser;
import com.gurella.engine.graphics.vector.svg.property.parser.PathParser;
import com.gurella.engine.graphics.vector.svg.property.parser.PreserveAspectRatioParser;
import com.gurella.engine.graphics.vector.svg.property.parser.SpaceParser;
import com.gurella.engine.graphics.vector.svg.property.parser.SpreadParser;
import com.gurella.engine.graphics.vector.svg.property.parser.StdDeviationParser;
import com.gurella.engine.graphics.vector.svg.property.parser.TransformParser;
import com.gurella.engine.graphics.vector.svg.property.parser.ViewBoxParser;
import com.gurella.engine.graphics.vector.svg.property.parser.VisibilityParser;
import com.gurella.engine.graphics.vector.svg.property.value.Color;
import com.gurella.engine.graphics.vector.svg.property.value.Display;
import com.gurella.engine.graphics.vector.svg.property.value.FillRule;
import com.gurella.engine.graphics.vector.svg.property.value.Length;
import com.gurella.engine.graphics.vector.svg.property.value.MarkerUnits;
import com.gurella.engine.graphics.vector.svg.property.value.Overflow;
import com.gurella.engine.graphics.vector.svg.property.value.Paint;
import com.gurella.engine.graphics.vector.svg.property.value.PreserveAspectRatio;
import com.gurella.engine.graphics.vector.svg.property.value.Space;
import com.gurella.engine.graphics.vector.svg.property.value.StdDeviation;
import com.gurella.engine.graphics.vector.svg.property.value.Visibility;

//TODO clipPathUnits, mask, vector-effect, viewport-fill, viewport-fill-opacity
public enum PropertyType {
	id("id", "xml:id", null, null),
	cssClass("class", null, null),
	style(true, null, null),
	overflow(Overflow.visible, OverflowParser.instance),
	display(Display.inline, DisplayParser.instance),
	visibility(true, Visibility.visible, VisibilityParser.instance),
	x(Length.zero, LengthParser.instance),
	y(Length.zero, LengthParser.instance),
	x1(Length.zero, LengthParser.instance),
	y1(Length.zero, LengthParser.instance),
	x2(Length.zero, LengthParser.instance),
	y2(Length.zero, LengthParser.instance),
	width(Length.zero, LengthParser.instance),
	height(Length.zero, LengthParser.instance),
	cx(Length.zero, LengthParser.instance),
	cy(Length.zero, LengthParser.instance),
	r(Length.zero, LengthParser.instance),
	rx(Length.zero, LengthParser.instance),
	ry(Length.zero, LengthParser.instance),
	color(true, Color.black, PaintParser.instance),
	opacity(true, Float.valueOf(1), FloatParser.instance),
	solidColor("solid-color", true, Color.black, PaintParser.instance),
	solidOpacity("solid-opacity", true, Float.valueOf(1), FloatParser.instance),
	fill(true, Color.black, PaintParser.instance),
	fillOpacity("fill-opacity", true, Float.valueOf(1), FloatParser.instance),
	fillRule("fill-rule", true, FillRule.nonzero, FillRuleParser.instance),
	stroke(true, Paint.none, PaintParser.instance),
	strokeWidth("stroke-width", true, Length.one, LengthParser.instance),
	strokeLinecap("stroke-linecap", true, LineCap.butt, LineCapParser.instance),
	strokeLinejoin("stroke-linejoin", true, LineJoin.miter, LineJoinParser.instance),
	strokeMiterlimit("stroke-miterlimit", true, Float.valueOf(4), FloatParser.instance),
	strokeDasharray("stroke-dasharray", true, null, FloatArrayParser.instance),
	strokeDashoffset("stroke-dashoffset", true, Float.valueOf(0), FloatParser.instance),
	strokeOpacity("stroke-opacity", true, Float.valueOf(1), FloatParser.instance),
	offset(null, FloatParser.instance),
	stopColor("stop-color", Color.black, ColorParser.instance),
	stopOpacity("stop-opacity", Float.valueOf(1), FloatParser.instance),
	href("xlink:href", null, null),
	fx(Length.one, LengthParser.instance),
	fy(Length.one, LengthParser.instance),
	points(new FloatArray(), FloatArrayParser.instance),
	d(new UnmodifiablePath(), PathParser.instance),
	transform(new AffineTransform(), TransformParser.instance),
	gradientTransform(new AffineTransform(), TransformParser.instance),
	spreadMethod(GradientSpread.pad, SpreadParser.instance),
	filter(null, null),
	clipPath("clip-path", true, null, null),
	clipRule("clip-rule", true, FillRule.nonzero, FillRuleParser.instance),
	stdDeviation(StdDeviation.zero, StdDeviationParser.instance),
	marker(true, null, null),
	markerStart("marker-start", true, null, null),
	markerMid("marker-mid", true, null, null),
	markerEnd("marker-end", true, null, null),
	orient(Float.valueOf(0), OrientParser.instance),
	viewBox(null, ViewBoxParser.instance),
	preserveAspectRatio(PreserveAspectRatio.LETTERBOX, PreserveAspectRatioParser.instance),
	markerUnits(MarkerUnits.strokeWidth, MarkerUnitsParser.instance),
	refX(Length.zero, LengthParser.instance),
	refY(Length.zero, LengthParser.instance),
	markerWidth(Length.three, LengthParser.instance),
	markerHeight(Length.three, LengthParser.instance),
	xmlSpace("xml:space", true, Space._default, SpaceParser.instance),
	;
	
	private static ObjectMap<String, PropertyType> propertiesByName = new ObjectMap<String, PropertyType>();
	static {
		PropertyType[] values = values();
		for (int i = 0; i < values.length; i++) {
			PropertyType value = values[i];
			propertiesByName.put(value.propertyName, value);
			
			if(value.propertyNameAlias != null) {
				propertiesByName.put(value.propertyNameAlias, value);
			}
		}
	}
	
	public final String propertyName;
	public final String propertyNameAlias;
	public final boolean inheritable;
	public final Object defaultValue;
	public final PropertyParser<?> transformer;
	
	private <T> PropertyType(T defaultValue, PropertyParser<T> transformer) {
		this.propertyName = name();
		this.propertyNameAlias = null;
		this.inheritable = false;
		this.defaultValue = defaultValue;
		this.transformer = transformer;
	}
	
	private <T> PropertyType(String name, T defaultValue, PropertyParser<T> transformer) {
		this.propertyName = name;
		this.propertyNameAlias = null;
		this.inheritable = false;
		this.defaultValue = defaultValue;
		this.transformer = transformer;
	}
	
	private <T> PropertyType(String name, String alias, T defaultValue, PropertyParser<T> transformer) {
		this.propertyName = name;
		this.propertyNameAlias = alias;
		this.inheritable = false;
		this.defaultValue = defaultValue;
		this.transformer = transformer;
	}
	
	private <T> PropertyType(boolean inheritable, T defaultValue, PropertyParser<T> transformer) {
		this.propertyName = name();
		this.propertyNameAlias = null;
		this.inheritable = inheritable;
		this.defaultValue = defaultValue;
		this.transformer = transformer;
	}
	
	private <T> PropertyType(String name, boolean inheritable, T defaultValue, PropertyParser<T> transformer) {
		this.propertyName = name;
		this.propertyNameAlias = null;
		this.inheritable = inheritable;
		this.defaultValue = defaultValue;
		this.transformer = transformer;
	}

	public <T> T transform(String strValue) {
		@SuppressWarnings("unchecked")
		T casted = (T)(transformer == null ? strValue : transformer.parse(strValue));
		return casted;
	}
	
	public static <T> T transform(String propertyName, String strValue) {
		if(strValue == null || strValue.length() == 0) {
			return null;
		}
		
		PropertyType property = getPropertyByName(propertyName);
		if(property == null) {
			@SuppressWarnings("unchecked")
			T casted = (T) strValue;
			return casted;
		} else {
			return property.transform(strValue);
		}
	}
	
	public static PropertyType getPropertyByName(String propertyName) {
		return propertyName == null ? null : (PropertyType) propertiesByName.get(propertyName.toLowerCase());
	}
}
