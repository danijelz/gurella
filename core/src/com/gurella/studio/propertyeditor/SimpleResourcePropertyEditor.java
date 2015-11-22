package com.gurella.studio.propertyeditor;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.gurella.engine.resource.factory.ModelResourceFactory;
import com.gurella.engine.resource.model.ResourceModelProperty;
import com.kotcrab.vis.ui.widget.VisLabel;

public abstract class SimpleResourcePropertyEditor<A extends Actor, T> extends AbstractResourcePropertyEditor<T> {
	protected final VisLabel propertyNameLabel = new VisLabel();
	protected final A valueComponent;

	private Array<Actor> uiComponents = new Array<Actor>();

	public SimpleResourcePropertyEditor(ResourceModelProperty property, ModelResourceFactory<?> factory) {
		super(property, factory);
		propertyNameLabel.setText(property.getDescriptiveName() + ": ");
		valueComponent = createValueComponent();
		uiComponents.add(propertyNameLabel);
		uiComponents.add(valueComponent);

		String propertyName = property.getName();
		if (factory.containsPropertyValue(propertyName)) {
			present(factory.<T> getPropertyValue(propertyName));
		} else {
			Object defaultValue = property.getDefaultValue();
			if (defaultValue instanceof ModelResourceFactory) {
				ModelResourceFactory<?> defaultValueFactory = new ModelResourceFactory(
						(ModelResourceFactory) defaultValue);
				present((T) defaultValueFactory);
			} else {
				present((T) defaultValue);
			}
		}
	}

	protected abstract A createValueComponent();

	@Override
	public Array<Actor> getUiComponents() {
		return uiComponents;
	}

	@Override
	public int getCellspan(int componentIndex) {
		return 1;
	}

	@Override
	public int getRowspan(int componentIndex) {
		return 1;
	}

	@Override
	public void save() {
		factory.setPropertyValue(property.getName(), getComponentValue());
	}

	@Override
	public Object getValue() {
		return getComponentValue();
	}

	protected abstract T getComponentValue();
}
