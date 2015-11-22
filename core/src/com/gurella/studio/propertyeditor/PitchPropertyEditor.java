package com.gurella.studio.propertyeditor;

import com.gurella.engine.audio.Pitch;
import com.gurella.engine.resource.factory.ModelResourceFactory;
import com.gurella.engine.resource.model.ResourceModelProperty;
import com.gurella.studio.common.CompositeInputValidator;
import com.kotcrab.vis.ui.util.Validators;
import com.kotcrab.vis.ui.util.Validators.GreaterThanValidator;
import com.kotcrab.vis.ui.util.Validators.LesserThanValidator;
import com.kotcrab.vis.ui.util.form.SimpleFormValidator.EmptyInputValidator;

public class PitchPropertyEditor extends SimpleObjectResourcePropertyEditor<Pitch, Float> {
	public PitchPropertyEditor(ResourceModelProperty property, ModelResourceFactory<?> factory) {
		super(property, factory);
	}

	@Override
	protected CompositeInputValidator createInputValidator() {
		return new CompositeInputValidator(new EmptyInputValidator(""), Validators.FLOATS, new GreaterThanValidator(
				0.5f, true), new LesserThanValidator(2, true));
	}

	@Override
	protected String getValuePropertyName() {
		return "pitch";
	}

	@Override
	protected Class<Pitch> getValueType() {
		return Pitch.class;
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
