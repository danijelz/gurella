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
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		body.setLayout(layout);
		Model<Vector3> vector3Model = Models.getModel(Vector3.class);
		createEditorField(vector3Model, "x");
		createEditorField(vector3Model, "y");
		createEditorField(vector3Model, "z");
	}

	private PropertyEditor<Float> createEditorField(final Model<Vector3> vector3Model, String propertyName) {
		Property<Float> childProperty = vector3Model.getProperty(propertyName);
		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		PropertyEditorContext<Vector3, Float> propertyContext = new PropertyEditorContext<>(context, vector3Model,
				getValue(), childProperty);
		PropertyEditor<Float> editor = createEditor(body, propertyContext);
		editor.getComposite().setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		Label label = toolkit.createLabel(body, childProperty.getDescriptiveName());
		label.setAlignment(SWT.LEFT);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		return editor;
	}
}
