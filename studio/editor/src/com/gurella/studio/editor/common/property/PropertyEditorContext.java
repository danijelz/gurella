package com.gurella.studio.editor.common.property;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.gurella.engine.metatype.MetaType;
import com.gurella.engine.metatype.Property;
import com.gurella.engine.metatype.PropertyChangeListener;
import com.gurella.engine.utils.Values;
import com.gurella.studio.editor.common.bean.BeanEditorContext;

public class PropertyEditorContext<M, P> extends BeanEditorContext<M> {
	public Property<P> property;
	public Supplier<P> valueGetter;
	public Consumer<P> valueSetter;

	public PropertyEditorContext(BeanEditorContext<M> parent, Property<P> property) {
		super(parent.sceneContext, parent, parent.metaType, parent.bean);
		this.property = property;
		valueGetter = this::defaultValueGetter;
		valueSetter = this::defaultValueSetter;
	}

	public PropertyEditorContext(BeanEditorContext<?> parent, M bean, Property<P> property) {
		super(parent, bean);
		this.property = property;
		valueGetter = this::defaultValueGetter;
		valueSetter = this::defaultValueSetter;
	}

	public PropertyEditorContext(BeanEditorContext<?> parent, MetaType<M> metaType, M bean, Property<P> property) {
		super(parent.sceneContext, parent, metaType, bean);
		this.property = property;
		valueGetter = this::defaultValueGetter;
		valueSetter = this::defaultValueSetter;
	}

	public P getValue() {
		return valueGetter.get();
	}

	private P defaultValueGetter() {
		return property.getValue(bean);
	}

	public void setValue(P newValue) {
		valueSetter.accept(newValue);
	}

	private void defaultValueSetter(P newValue) {
		P oldValue = getValue();
		if (!Values.isEqual(oldValue, newValue)) {
			property.setValue(bean, newValue);
			propertyValueChanged(oldValue, newValue);
		}
	}

	public String getDescriptiveName() {
		return PropertyEditorData.getDescriptiveName(this);
	}

	public void propertyValueChanged(Object oldValue, Object newValue) {
		propertiesSignal.dispatch(new PropertyValueChangedEvent(metaType, property, bean, oldValue, newValue));

		BeanEditorContext<?> temp = this;
		while (temp != null) {
			if (temp instanceof PropertyEditorContext) {
				PropertyEditorContext<?, ?> propertyEditorContext = (PropertyEditorContext<?, ?>) temp;
				if (temp.bean instanceof PropertyChangeListener) {
					PropertyChangeListener listener = (PropertyChangeListener) temp.bean;
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

	@Override
	public String getQualifiedName() {
		return parent.getQualifiedName() + "." + property.getName();
	}
}
