package com.gurella.engine.base.model;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.utils.ArrayExt;

public interface PropertyChangeListener {
	void propertyChanged(PropertyChangeEvent event);

	public static class PropertyChangeEvent implements Poolable {
		public Object oldValue;
		public Object newValue;
		public final ArrayExt<Object> propertyPath = new ArrayExt<Object>();

		@Override
		public void reset() {
			oldValue = null;
			newValue = null;
			propertyPath.reset();
		}
	}
}
