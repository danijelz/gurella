package com.gurella.studio.editor.model.property;

import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.Property;
import com.gurella.engine.event.Signal1;

public class PropertyEditorContext<T> {
	public Model<?> model;
	public Property<T> property;
	public Object modelInstance;
	public final Signal1<PropertyChangedEvent> signal = new Signal1<>();

	public PropertyEditorContext(Model<?> model, Property<T> property, Object modelInstance) {
		this.model = model;
		this.property = property;
		this.modelInstance = modelInstance;
	}

	public static final class PropertyChangedEvent {
		public final Model<?> model;
		public final Property<?> property;
		public final Object modelInstance;
		public final Object oldValue;
		public final Object newValue;

		public PropertyChangedEvent(Model<?> model, Property<?> property, Object modelInstance, Object oldValue,
				Object newValue) {
			this.model = model;
			this.property = property;
			this.modelInstance = modelInstance;
			this.oldValue = oldValue;
			this.newValue = newValue;
		}
	}
}
