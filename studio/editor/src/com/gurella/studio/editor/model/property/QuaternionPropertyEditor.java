package com.gurella.studio.editor.model.property;

import static com.gurella.studio.GurellaStudioPlugin.createFont;
import static com.gurella.studio.editor.model.PropertyEditorFactory.createEditor;

import org.eclipse.jface.resource.FontDescriptor;
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
import com.gurella.studio.GurellaStudioPlugin;

public class QuaternionPropertyEditor extends SimplePropertyEditor<Quaternion> {
	public QuaternionPropertyEditor(Composite parent, PropertyEditorContext<?, Quaternion> context) {
		super(parent, context);

		GridLayout layout = new GridLayout(8, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing = 2;
		layout.verticalSpacing = 0;
		body.setLayout(layout);

		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		Label label = toolkit.createLabel(body, context.property.getDescriptiveName());
		label.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false, 8, 1));
		label.setFont(createFont(FontDescriptor.createFrom(label.getFont()).setStyle(SWT.BOLD)));

		Model<Quaternion> quaternionModel = Models.getModel(Quaternion.class);
		createEditorField(quaternionModel, "x", "\t");
		createEditorField(quaternionModel, "y", "");
		createEditorField(quaternionModel, "z", "");
		createEditorField(quaternionModel, "w", "");
	}

	private void createEditorField(final Model<Quaternion> vector2Model, String propertyName, String prefix) {
		Property<Float> childProperty = vector2Model.getProperty(propertyName);
		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();

		Label label = toolkit.createLabel(body, prefix + childProperty.getDescriptiveName() + ":");
		label.setAlignment(SWT.LEFT);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

		PropertyEditorContext<Quaternion, Float> propertyContext = new PropertyEditorContext<>(context, vector2Model,
				getValue(), childProperty);
		PropertyEditor<Float> editor = createEditor(body, propertyContext);
		editor.getComposite().setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
	}
}
