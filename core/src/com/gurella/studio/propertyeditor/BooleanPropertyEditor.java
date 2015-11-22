package com.gurella.studio.propertyeditor;

import com.gurella.engine.resource.factory.ModelResourceFactory;
import com.gurella.engine.resource.model.ResourceModelProperty;
import com.kotcrab.vis.ui.widget.VisCheckBox;

public class BooleanPropertyEditor extends SimpleResourcePropertyEditor<VisCheckBox, Boolean> {
	public BooleanPropertyEditor(ResourceModelProperty property, ModelResourceFactory<?> factory) {
		super(property, factory);
	}

	@Override
	public void present(Boolean value) {
		valueComponent.setChecked(value != null && value.booleanValue());
	}

	@Override
	protected VisCheckBox createValueComponent() {
		return new VisCheckBox("");
	}

	@Override
	protected Boolean getComponentValue() {
		return Boolean.valueOf(valueComponent.isChecked());
	}
}
