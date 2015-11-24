package com.gurella.engine.graphics.vector.svg.element;

import com.gurella.engine.graphics.vector.svg.property.PropertyType;
import com.gurella.engine.graphics.vector.svg.property.value.Overflow;

public class SymbolElement extends KnownElement {
	public SymbolElement() {
		super(ElementType.symbol);
	}

	public Overflow getOverflow() {
		return getPropertyOrDefault(PropertyType.overflow);
	}
}
