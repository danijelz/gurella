package com.gurella.studio.editor.common.bean;

import java.util.Optional;

import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.Models;
import com.gurella.engine.base.model.Property;
import com.gurella.engine.event.Signal1;
import com.gurella.studio.editor.SceneEditorContext;

public class BeanEditorContext<T> {
	public final SceneEditorContext sceneContext;
	public final BeanEditorContext<?> parent;
	public final Model<T> model;
	public final T bean;
	public final Signal1<PropertyValueChangedEvent> propertiesSignal = new Signal1<>();

	public BeanEditorContext(SceneEditorContext sceneContext, T bean) {
		this(sceneContext, null, Models.getModel(bean), bean);
	}

	public BeanEditorContext(BeanEditorContext<?> parent, T bean) {
		this(parent.sceneContext, parent, Models.getModel(bean), bean);
	}

	public BeanEditorContext(SceneEditorContext sceneContext, BeanEditorContext<?> parent, Model<T> model, T bean) {
		this.sceneContext = sceneContext;
		this.parent = parent;
		this.model = model;
		this.bean = bean;
		Optional.ofNullable(parent).ifPresent(p -> propertiesSignal.addListener(p.propertiesSignal::dispatch));
	}

	public void propertyValueChanged(Property<?> property, Object oldValue, Object newValue) {
		propertiesSignal.dispatch(new PropertyValueChangedEvent(model, property, bean, oldValue, newValue));
	}
	
	public String getQualifiedName() {
		return model.getType().getName();
	}

	public static final class PropertyValueChangedEvent {
		public final Model<?> model;
		public final Property<?> property;
		public final Object modelInstance;
		public final Object oldValue;
		public final Object newValue;

		public PropertyValueChangedEvent(Model<?> model, Property<?> property, Object modelInstance, Object oldValue,
				Object newValue) {
			this.model = model;
			this.property = property;
			this.modelInstance = modelInstance;
			this.oldValue = oldValue;
			this.newValue = newValue;
		}
	}
}
