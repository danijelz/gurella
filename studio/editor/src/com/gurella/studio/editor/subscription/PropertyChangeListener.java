package com.gurella.studio.editor.subscription;

import com.gurella.engine.event.EventSubscription;
import com.gurella.engine.metatype.Property;

public interface PropertyChangeListener extends EventSubscription {
	void propertyChanged(Object instance, Property<?> property, Object newValue);
}
