package com.gurella.engine.graphics.vector.svg.element;

public abstract class KnownElement extends Element {
	private ElementType elementType;
	
	public KnownElement(ElementType elementType) {
		this.elementType = elementType;
	}

	@Override
	public String getName() {
		return elementType.elementName;
	}
	
	public ElementType getElementType() {
		return elementType;
	}
}
