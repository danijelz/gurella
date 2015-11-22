package com.gurella.studio.propertyeditor;

import com.gurella.engine.resource.factory.ModelResourceFactory;
import com.gurella.engine.resource.model.ResourceModelProperty;
import com.kotcrab.vis.ui.widget.VisTextField;

public class StringPropertyEditor extends SimpleResourcePropertyEditor<VisTextField, String> {
	public StringPropertyEditor(ResourceModelProperty property, ModelResourceFactory<?> factory) {
		super(property, factory);
	}

	@Override
	public void present(String value) {
		valueComponent.setText(value == null
				? ""
				: value);
	}

	@Override
	protected VisTextField createValueComponent() {
		return new VisTextField();
	}

	@Override
	protected String getComponentValue() {
		return valueComponent.getText();
	}
}
