package com.gurella.studio.propertyeditor;

import com.gurella.engine.resource.factory.ModelResourceFactory;
import com.gurella.engine.resource.model.ResourceModelProperty;

public abstract class AbstractResourcePropertyEditor<T> implements ResourcePropertyEditor<T> {
	protected final ResourceModelProperty property;
	protected final ModelResourceFactory<?> factory;
	
	public AbstractResourcePropertyEditor(ResourceModelProperty property, ModelResourceFactory<?> factory) {
		this.property = property;
		this.factory = factory;
	}
	
	public ResourceModelProperty getProperty() {
		return property;
	}
	
	public ModelResourceFactory<?> getFactory() {
		return factory;
	}
}
