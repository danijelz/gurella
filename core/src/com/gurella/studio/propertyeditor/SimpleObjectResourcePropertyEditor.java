package com.gurella.studio.propertyeditor;

import com.gurella.engine.resource.factory.ModelResourceFactory;
import com.gurella.engine.resource.model.ResourceModelProperty;
import com.gurella.studio.common.CompositeInputValidator;
import com.kotcrab.vis.ui.widget.VisValidatableTextField;

public abstract class SimpleObjectResourcePropertyEditor<T, V>
		extends SimpleResourcePropertyEditor<VisValidatableTextField, ModelResourceFactory<T>> {
	ModelResourceFactory<T> valueFactory;

	public SimpleObjectResourcePropertyEditor(ResourceModelProperty property, ModelResourceFactory<?> factory) {
		super(property, factory);
	}

	@Override
	public void present(ModelResourceFactory<T> aValueFactory) {
		this.valueFactory = aValueFactory;
		String propertyName = getValuePropertyName();

		if (valueFactory.containsPropertyValue(propertyName)) {
			valueComponent.setText(getPropertyValueDescription(valueFactory.<V> getPropertyValue(propertyName)));
		} else {
			Object defaultValue = valueFactory.getProperty(propertyName).getDefaultValue();
			if (defaultValue instanceof ModelResourceFactory) {
				ModelResourceFactory<?> defaultValueFactory = new ModelResourceFactory(
						(ModelResourceFactory) defaultValue);
				valueComponent.setText(getPropertyValueDescription((V) defaultValueFactory));
			} else {
				valueComponent.setText(getPropertyValueDescription((V) defaultValue));
			}
		}
	}

	protected abstract String getValuePropertyName();

	protected abstract Class<T> getValueType();

	protected abstract String getPropertyValueDescription(V value);

	protected abstract V convertFromPropertyValueDescription(String description);

	@Override
	protected VisValidatableTextField createValueComponent() {
		CompositeInputValidator inputValidator = createInputValidator();
		return inputValidator == null ? new VisValidatableTextField() : new VisValidatableTextField(inputValidator);
	}

	protected CompositeInputValidator createInputValidator() {
		return null;
	}

	@Override
	protected ModelResourceFactory<T> getComponentValue() {
		if (!valueComponent.isInputValid()) {
			throw new IllegalArgumentException("Invalid input for property: " + property.getName());
		}
		String description = valueComponent.getText();

		if (valueFactory == null) {
			valueFactory = new ModelResourceFactory<T>(getValueType());
		}

		valueFactory.setPropertyValue(getValuePropertyName(), convertFromPropertyValueDescription(description));

		return valueFactory;
	}
}
