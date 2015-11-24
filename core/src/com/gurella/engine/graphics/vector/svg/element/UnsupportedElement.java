package com.gurella.engine.graphics.vector.svg.element;

public class UnsupportedElement extends Element {
	private String name;

	public UnsupportedElement(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}
}
