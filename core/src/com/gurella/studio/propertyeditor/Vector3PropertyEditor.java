package com.gurella.studio.propertyeditor;

import com.badlogic.gdx.math.Vector3;
import com.gurella.engine.resource.factory.ModelResourceFactory;
import com.gurella.engine.resource.model.ResourceModelProperty;
import com.kotcrab.vis.ui.util.Validators;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisValidatableTextField;

public class Vector3PropertyEditor extends SimpleResourcePropertyEditor<VisTable, ModelResourceFactory<Vector3>> {
	private VisValidatableTextField xField;
	private VisValidatableTextField yField;
	private VisValidatableTextField zField;

	private ModelResourceFactory<Vector3> valueFactory;

	public Vector3PropertyEditor(ResourceModelProperty property, ModelResourceFactory<?> factory) {
		super(property, factory);
	}

	@Override
	protected VisTable createValueComponent() {
		xField = new VisValidatableTextField(Validators.FLOATS);
		yField = new VisValidatableTextField(Validators.FLOATS);
		zField = new VisValidatableTextField(Validators.FLOATS);

		VisTable content = new VisTable();

		content.add("X").pad(2);
		content.add(xField).pad(2);

		content.add("Y").pad(2);
		content.add(yField).pad(2);

		content.add("Z").pad(2);
		content.add(zField).pad(2);

		return content;
	}

	@Override
	public void present(ModelResourceFactory<Vector3> aValueFactory) {
		this.valueFactory = aValueFactory;
		if (valueFactory == null) {
			xField.setText("0.0");
			yField.setText("0.0");
			zField.setText("0.0");
		} else {
			xField.setText(String.valueOf(valueFactory.<Float> getPropertyValue("x", 0.0f)));
			yField.setText(String.valueOf(valueFactory.<Float> getPropertyValue("y", 0.0f)));
			zField.setText(String.valueOf(valueFactory.<Float> getPropertyValue("z", 0.0f)));
		}
	}

	@Override
	protected ModelResourceFactory<Vector3> getComponentValue() {
		if (!xField.isInputValid() || !yField.isInputValid() || !zField.isInputValid()) {
			throw new IllegalArgumentException("Invalid input for property: " + property.getName());
		}

		if (valueFactory == null) {
			valueFactory = new ModelResourceFactory<Vector3>(Vector3.class);
		}

		valueFactory.setPropertyValue("x", Float.valueOf(xField.getText()));
		valueFactory.setPropertyValue("y", Float.valueOf(yField.getText()));
		valueFactory.setPropertyValue("z", Float.valueOf(zField.getText()));

		return valueFactory;
	}
}
