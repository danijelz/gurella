package com.gurella.studio.editor.model.property;

import static com.gurella.studio.editor.model.PropertyEditorFactory.createEditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.badlogic.gdx.math.Vector2;
import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.Models;
import com.gurella.engine.base.model.Property;
import com.gurella.studio.editor.GurellaStudioPlugin;

public class Vector2PropertyEditor extends SimplePropertyEditor<Vector2> {
	public Vector2PropertyEditor(Composite parent, PropertyEditorContext<?, Vector2> context) {
		super(parent, context);

		GridLayout layout = new GridLayout(4, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		body.setLayout(layout);
		Model<Vector2> vector2Model = Models.getModel(Vector2.class);
		createEditorField(vector2Model, "x");
		createEditorField(vector2Model, "y");
	}

	private void createEditorField(final Model<Vector2> vector2Model, String propertyName) {
		Property<Float> childProperty = vector2Model.getProperty(propertyName);
		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		PropertyEditorContext<Vector2, Float> propertyContext = new PropertyEditorContext<>(context, vector2Model,
				getValue(), childProperty);
		PropertyEditor<Float> editor = createEditor(body, propertyContext);
		editor.getComposite().setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		Label label = toolkit.createLabel(body, childProperty.getDescriptiveName());
		label.setAlignment(SWT.LEFT);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
	}
}
