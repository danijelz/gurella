package com.gurella.engine.graphics.vector.svg.property.parser;

import com.gurella.engine.graphics.vector.svg.property.PropertyParser;
import com.gurella.engine.graphics.vector.svg.property.value.IriPaint;
import com.gurella.engine.graphics.vector.svg.property.value.Paint;

public class PaintParser implements PropertyParser<Paint>{
	public static final PaintParser instance = new PaintParser();

	private PaintParser() {
	}
	
	@Override
	public Paint parse(String strValue) {
		if("none".equals(strValue)) {
			return Paint.none;
		} else if("currentColor".equals(strValue)) {
			return Paint.currentColor;
		}
		
		String url = UrlParser.instance.parse(strValue);
		if (url != null) {
			return new IriPaint(url);
		}
		
		return ColorParser.instance.parse(strValue);
	}
}
