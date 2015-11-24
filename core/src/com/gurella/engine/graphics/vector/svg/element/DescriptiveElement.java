package com.gurella.engine.graphics.vector.svg.element;

import com.badlogic.gdx.utils.Array;

public class DescriptiveElement extends KnownElement {
	private String description;

	public DescriptiveElement(ElementType elementType) {
		super(elementType);
	}

	public String getDescription() {
		if (description == null) {
			StringBuilder builder = new StringBuilder();
			extractTextSequence(builder, this);
			description = builder.toString();
		}
		return description;
	}

	private void extractTextSequence(StringBuilder builder, Element parent) {
		Array<Element> children = parent.getChildren();
		for (int i = 0; i < children.size; i++) {
			Element child = children.get(i);
			if (child instanceof TextSequenceElement) {
				builder.append(((TextSequenceElement) child).getText());
			}
			extractTextSequence(builder, child);
		}
	}
}
