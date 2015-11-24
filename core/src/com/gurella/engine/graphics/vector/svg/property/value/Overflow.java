package com.gurella.engine.graphics.vector.svg.property.value;

public enum Overflow {
	visible, hidden, scroll, auto;

	public boolean isVisible() {
		return visible == this || auto == this;
	}
}
