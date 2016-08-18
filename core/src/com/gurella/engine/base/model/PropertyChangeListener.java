package com.gurella.engine.base.model;

import com.badlogic.gdx.utils.Pool.Poolable;

public interface PropertyChangeListener {
	void propertyChanged(PropertyChangeEvent event);

	public static class PropertyChangeEvent implements Poolable {
		public Object oldValue;
		public Object newValue;
		public String propertyName;

		@Override
		public void reset() {
			oldValue = null;
			newValue = null;
			propertyName = null;
		}
	}
}
