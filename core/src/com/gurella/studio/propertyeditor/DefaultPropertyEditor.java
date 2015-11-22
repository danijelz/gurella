package com.gurella.studio.propertyeditor;

import com.gurella.engine.resource.factory.ModelResourceFactory;
import com.gurella.engine.resource.model.ResourceModelProperty;
import com.kotcrab.vis.ui.widget.VisLabel;

public class DefaultPropertyEditor extends SimpleResourcePropertyEditor<VisLabel, Object> {
	private Object cachedValue;

	public DefaultPropertyEditor(ResourceModelProperty property, ModelResourceFactory<?> factory) {
		super(property, factory);
	}

	@Override
	public void present(Object value) {
		this.cachedValue = value;
		valueComponent.setText(value == null
				? ""
				: value.toString());
	}

	@Override
	protected VisLabel createValueComponent() {
		return new VisLabel();
	}

	@Override
	protected Object getComponentValue() {
		return cachedValue;
	}
}
