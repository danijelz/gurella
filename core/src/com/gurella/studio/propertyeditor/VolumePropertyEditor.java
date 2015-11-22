package com.gurella.studio.propertyeditor;

import com.gurella.engine.audio.Volume;
import com.gurella.engine.resource.factory.ModelResourceFactory;
import com.gurella.engine.resource.model.ResourceModelProperty;
import com.gurella.studio.common.CompositeInputValidator;
import com.kotcrab.vis.ui.util.Validators;
import com.kotcrab.vis.ui.util.Validators.GreaterThanValidator;
import com.kotcrab.vis.ui.util.Validators.LesserThanValidator;
import com.kotcrab.vis.ui.util.form.SimpleFormValidator.EmptyInputValidator;

public class VolumePropertyEditor extends SimpleObjectResourcePropertyEditor<Volume, Float> {
	public VolumePropertyEditor(ResourceModelProperty property, ModelResourceFactory<?> factory) {
		super(property, factory);
	}

	@Override
	protected CompositeInputValidator createInputValidator() {
		return new CompositeInputValidator(new EmptyInputValidator(""), Validators.FLOATS, new GreaterThanValidator(0,
				true), new LesserThanValidator(1, true));
	}

	@Override
	protected String getValuePropertyName() {
		return "volume";
	}

	@Override
	protected Class<Volume> getValueType() {
		return Volume.class;
	}

	@Override
	protected String getPropertyValueDescription(Float value) {
		return value == null
				? "1.0"
				: String.valueOf(value);
	}

	@Override
	protected Float convertFromPropertyValueDescription(String description) {
		return Float.valueOf(description);
	}
}
