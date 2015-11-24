package com.gurella.engine.graphics.vector.svg.element;

public class TextSequenceElement extends Element {
	private String text;

	public TextSequenceElement(String text) {
		this.text = text;
	}

	@Override
	public String getName() {
		return "_textSequence";
	}
	
	public String getText() {
		return text;
	}
}
