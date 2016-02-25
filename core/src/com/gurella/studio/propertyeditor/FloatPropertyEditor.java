package com.gurella.studio.propertyeditor;

import com.gurella.engine.resource.factory.ModelResourceFactory;
import com.gurella.engine.resource.model.ResourceModelProperty;
import com.gurella.engine.utils.Values;
import com.kotcrab.vis.ui.util.Validators;
import com.kotcrab.vis.ui.widget.VisValidatableTextField;

public class FloatPropertyEditor extends SimpleResourcePropertyEditor<VisValidatableTextField, Float> {
	public FloatPropertyEditor(ResourceModelProperty property, ModelResourceFactory<?> factory) {
		super(property, factory);
	}

	@Override
	public void present(Float value) {
		valueComponent.setText(value == null ? "0.0" : value.toString());
	}

	@Override
	protected VisValidatableTextField createValueComponent() {
		return new VisValidatableTextField(Validators.FLOATS);
	}

	@Override
	protected Float getComponentValue() {
		if (!valueComponent.isInputValid()) {
			throw new IllegalArgumentException("Invalid input for property: " + property.getName());
		}
		String value = valueComponent.getText();
		if (Values.isBlank(value)) {
			throw new IllegalArgumentException("Invalid input for property: " + property.getName());
		}

		return Float.valueOf(value);
	}
}
