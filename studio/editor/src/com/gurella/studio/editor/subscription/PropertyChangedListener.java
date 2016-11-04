package com.gurella.studio.editor.subscription;

import com.gurella.engine.base.model.Property;
import com.gurella.engine.event.EventSubscription;

public interface PropertyChangedListener extends EventSubscription {
	void propertyChanged(Object instance, Property<?> property, Object newValue);
}
