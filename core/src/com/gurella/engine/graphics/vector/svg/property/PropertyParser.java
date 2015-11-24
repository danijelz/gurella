package com.gurella.engine.graphics.vector.svg.property;

public interface PropertyParser<T> {
	T parse(String strValue);
}