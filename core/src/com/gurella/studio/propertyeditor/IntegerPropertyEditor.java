package com.gurella.studio.propertyeditor;

import com.gurella.engine.resource.factory.ModelResourceFactory;
import com.gurella.engine.resource.model.ResourceModelProperty;
import com.gurella.engine.utils.ValueUtils;
import com.kotcrab.vis.ui.util.Validators;
import com.kotcrab.vis.ui.util.Validators.GreaterThanValidator;
import com.kotcrab.vis.ui.util.Validators.LesserThanValidator;
import com.kotcrab.vis.ui.util.form.SimpleFormValidator.EmptyInputValidator;
import com.kotcrab.vis.ui.widget.VisValidatableTextField;

public class IntegerPropertyEditor extends SimpleResourcePropertyEditor<VisValidatableTextField, Integer> {
	public IntegerPropertyEditor(ResourceModelProperty property, ModelResourceFactory<?> factory) {
		super(property, factory);
	}

	@Override
	public void present(Integer value) {
		valueComponent.setText(value == null ? "0" : value.toString());
	}

	@Override
	protected VisValidatableTextField createValueComponent() {
		return new VisValidatableTextField(new EmptyInputValidator(""), Validators.INTEGERS,
				new GreaterThanValidator(Integer.MIN_VALUE, true), new LesserThanValidator(Integer.MAX_VALUE, true));
	}

	@Override
	protected Integer getComponentValue() {
		if (!valueComponent.isInputValid()) {
			throw new IllegalArgumentException("Invalid input for property: " + property.getName());
		}
		String value = valueComponent.getText();
		if (ValueUtils.isBlank(value)) {
			throw new IllegalArgumentException("Invalid input for property: " + property.getName());
		}

		return Integer.valueOf(value);
	}
}
