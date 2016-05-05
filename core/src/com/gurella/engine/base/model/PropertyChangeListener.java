package com.gurella.engine.base.model;

import com.badlogic.gdx.utils.Array;

public interface PropertyChangeListener {
	void propertyChanged(PropertyChangeEvent event);
	
	public static class PropertyChangeEvent {
		public Array<Object> propertyPath;
		public Object oldValue;
		public Object newValue;
	}
}
