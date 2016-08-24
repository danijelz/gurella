package com.gurella.studio.editor.model.property;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.Property;
import com.gurella.engine.base.model.PropertyChangeListener;
import com.gurella.engine.utils.Values;
import com.gurella.studio.editor.model.ModelEditorContext;

public class PropertyEditorContext<M, P> extends ModelEditorContext<M> {
	public Property<P> property;
	public Supplier<P> valueGetter;
	public Consumer<P> valueSetter;

	public PropertyEditorContext(ModelEditorContext<M> parent, Property<P> property) {
		super(parent.sceneEditorContext, parent, parent.model, parent.modelInstance);
		this.property = property;
		valueGetter = this::defaultValueGetter;
		valueSetter = this::defaultValueSetter;
	}

	public PropertyEditorContext(ModelEditorContext<?> parent, M modelInstance, Property<P> property) {
		super(parent, modelInstance);
		this.property = property;
		valueGetter = this::defaultValueGetter;
		valueSetter = this::defaultValueSetter;
	}

	public PropertyEditorContext(ModelEditorContext<?> parent, Model<M> model, M modelInstance, Property<P> property) {
		super(parent.sceneEditorContext, parent, model, modelInstance);
		this.property = property;
		valueGetter = this::defaultValueGetter;
		valueSetter = this::defaultValueSetter;
	}

	protected P getValue() {
		return valueGetter.get();
	}

	private P defaultValueGetter() {
		return property.getValue(modelInstance);
	}

	protected void setValue(P newValue) {
		valueSetter.accept(newValue);
	}

	private void defaultValueSetter(P newValue) {
		P oldValue = getValue();
		if (!Values.isEqual(oldValue, newValue)) {
			property.setValue(modelInstance, newValue);
			propertyValueChanged(oldValue, newValue);
		}
	}

	public String getDescriptiveName() {
		return property.getDescriptiveName();
	}

	public void propertyValueChanged(Object oldValue, Object newValue) {
		signal.dispatch(new PropertyValueChangedEvent(model, property, modelInstance, oldValue, newValue));

		ModelEditorContext<?> temp = this;
		while (temp != null) {
			if (temp instanceof PropertyEditorContext) {
				PropertyEditorContext<?, ?> propertyEditorContext = (PropertyEditorContext<?, ?>) temp;
				if (temp.modelInstance instanceof PropertyChangeListener) {
					PropertyChangeListener listener = (PropertyChangeListener) temp.modelInstance;
					listener.propertyChanged(propertyEditorContext.property.getName(), oldValue, newValue);
				}
			}
			temp = temp.parent;
		}
	}

	public boolean isNullable() {
		return property.isNullable();
	}

	public boolean isFixedValue() {
		return getPropertyType().isPrimitive() || property.isFinal();
	}

	public Class<P> getPropertyType() {
		return property.getType();
	}
}
