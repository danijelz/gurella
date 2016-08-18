package com.gurella.engine.base.model;

public interface PropertyChangeListener {
	void propertyChanged(String propertyName, Object oldValue, Object newValue);
}
