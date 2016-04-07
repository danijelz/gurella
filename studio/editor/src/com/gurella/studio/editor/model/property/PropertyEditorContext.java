package com.gurella.studio.editor.model.property;

import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.Property;

public class PropertyEditorContext<M, P> extends ModelEditorContext<M> {
	public Property<P> property;

	public PropertyEditorContext(M modelInstance, Property<P> property) {
		super(modelInstance);
		this.property = property;
	}

	public PropertyEditorContext(Model<M> model, M modelInstance, Property<P> property) {
		super(model, modelInstance);
		this.property = property;
	}

	public PropertyEditorContext(ModelEditorContext<?> parent, M modelInstance, Property<P> property) {
		super(parent, modelInstance);
		this.property = property;
	}

	public PropertyEditorContext(ModelEditorContext<?> parent, Model<M> model, M modelInstance, Property<P> property) {
		super(parent, model, modelInstance);
		this.property = property;
	}

	public void propertyValueChanged(Object oldValue, Object newValue) {
		signal.dispatch(new PropertyValueChangedEvent(model, property, modelInstance, oldValue, newValue));
	}
}
