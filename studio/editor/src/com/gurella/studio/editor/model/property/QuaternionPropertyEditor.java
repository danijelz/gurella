package com.gurella.studio.editor.model.property;

import static com.gurella.studio.editor.model.PropertyEditorFactory.createEditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.badlogic.gdx.math.Quaternion;
import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.Models;
import com.gurella.engine.base.model.Property;
import com.gurella.studio.editor.GurellaStudioPlugin;

public class QuaternionPropertyEditor extends SimplePropertyEditor<Quaternion> {
	public QuaternionPropertyEditor(Composite parent, PropertyEditorContext<?, Quaternion> context) {
		super(parent, context);

		GridLayout layout = new GridLayout(8, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		body.setLayout(layout);
		Model<Quaternion> quaternionModel = Models.getModel(Quaternion.class);
		createEditorField(quaternionModel, "x");
		createEditorField(quaternionModel, "y");
		createEditorField(quaternionModel, "z");
		createEditorField(quaternionModel, "w");
	}

	private void createEditorField(final Model<Quaternion> quaternionModel, String propertyName) {
		Property<Float> childProperty = quaternionModel.getProperty(propertyName);
		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		PropertyEditorContext<Quaternion, Float> propertyContext = new PropertyEditorContext<>(context, quaternionModel,
				getValue(), childProperty);
		PropertyEditor<Float> editor = createEditor(body, propertyContext);
		editor.getComposite().setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		Label label = toolkit.createLabel(body, childProperty.getDescriptiveName());
		label.setAlignment(SWT.LEFT);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
	}
}
