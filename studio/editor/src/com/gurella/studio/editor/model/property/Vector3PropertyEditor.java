package com.gurella.studio.editor.model.property;

import static com.gurella.studio.editor.model.PropertyEditorFactory.createEditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.badlogic.gdx.math.Vector3;
import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.Models;
import com.gurella.engine.base.model.Property;
import com.gurella.studio.editor.GurellaStudioPlugin;

public class Vector3PropertyEditor extends SimplePropertyEditor<Vector3> {
	public Vector3PropertyEditor(Composite parent, PropertyEditorContext<?, Vector3> context) {
		super(parent, context);

		GridLayout layout = new GridLayout(6, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		body.setLayout(layout);
		Model<Vector3> vector3Model = Models.getModel(Vector3.class);
		createEditorField(vector3Model, "x");
		createEditorField(vector3Model, "y");
		createEditorField(vector3Model, "z");
	}

	private PropertyEditor<Float> createEditorField(final Model<Vector3> vector3Model, String propertyName) {
		Property<Float> childProperty = vector3Model.getProperty(propertyName);
		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		Label label = toolkit.createLabel(body, childProperty.getDescriptiveName());
		label.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));
		return createEditor(body, new PropertyEditorContext<>(context, vector3Model, getValue(), childProperty));
	}
}
