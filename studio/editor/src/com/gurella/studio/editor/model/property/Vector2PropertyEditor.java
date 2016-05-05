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

import com.badlogic.gdx.math.Vector2;
import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.Models;
import com.gurella.engine.base.model.Property;
import com.gurella.studio.GurellaStudioPlugin;

public class Vector2PropertyEditor extends PropertyEditor<Vector2> {
	public Vector2PropertyEditor(Composite parent, PropertyEditorContext<?, Vector2> context) {
		super(parent, context);

		GridLayout layout = new GridLayout(4, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing = 2;
		layout.verticalSpacing = 0;
		body.setLayout(layout);

		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		Label label = toolkit.createLabel(body, context.property.getDescriptiveName());
		label.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false, 4, 1));
		label.setFont(createFont(FontDescriptor.createFrom(label.getFont()).setStyle(SWT.BOLD)));

		Model<Vector2> vector2Model = Models.getModel(Vector2.class);
		createEditorField(vector2Model, "x", "\t");
		createEditorField(vector2Model, "y", "");
	}

	private void createEditorField(final Model<Vector2> vector2Model, String propertyName, String prefix) {
		Property<Float> childProperty = vector2Model.getProperty(propertyName);
		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();

		Label label = toolkit.createLabel(body, prefix + childProperty.getDescriptiveName() + ":");
		label.setAlignment(SWT.LEFT);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

		PropertyEditorContext<Vector2, Float> propertyContext = new PropertyEditorContext<>(context, vector2Model,
				getValue(), childProperty);
		PropertyEditor<Float> editor = createEditor(body, propertyContext);
		editor.getComposite().setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
	}
}
