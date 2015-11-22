package com.gurella.studio.propertyeditor;

import com.gurella.engine.audio.Pan;
import com.gurella.engine.resource.factory.ModelResourceFactory;
import com.gurella.engine.resource.model.ResourceModelProperty;
import com.gurella.studio.common.CompositeInputValidator;
import com.kotcrab.vis.ui.util.Validators;
import com.kotcrab.vis.ui.util.Validators.GreaterThanValidator;
import com.kotcrab.vis.ui.util.Validators.LesserThanValidator;
import com.kotcrab.vis.ui.util.form.SimpleFormValidator.EmptyInputValidator;

public class PanPropertyEditor extends SimpleObjectResourcePropertyEditor<Pan, Float> {
	public PanPropertyEditor(ResourceModelProperty property, ModelResourceFactory<?> factory) {
		super(property, factory);
	}

	@Override
	protected CompositeInputValidator createInputValidator() {
		return new CompositeInputValidator(new EmptyInputValidator(""), Validators.FLOATS, new GreaterThanValidator(-1,
				true), new LesserThanValidator(1, true));
	}

	@Override
	protected String getValuePropertyName() {
		return "pan";
	}

	@Override
	protected Class<Pan> getValueType() {
		return Pan.class;
	}

	@Override
	protected String getPropertyValueDescription(Float value) {
		return value == null
				? "0.0"
				: String.valueOf(value);
	}

	@Override
	protected Float convertFromPropertyValueDescription(String description) {
		return Float.valueOf(description);
	}
}
