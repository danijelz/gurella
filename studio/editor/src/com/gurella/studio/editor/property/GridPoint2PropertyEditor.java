package com.gurella.studio.editor.property;

import static com.gurella.studio.GurellaStudioPlugin.createFont;
import static com.gurella.studio.editor.property.PropertyEditorFactory.createEditor;

import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.badlogic.gdx.math.GridPoint2;
import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.Models;
import com.gurella.engine.base.model.Property;
import com.gurella.studio.GurellaStudioPlugin;

public class GridPoint2PropertyEditor extends PropertyEditor<GridPoint2> {
	public GridPoint2PropertyEditor(Composite parent, PropertyEditorContext<?, GridPoint2> context) {
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
		
		Model<GridPoint2> model = Models.getModel(GridPoint2.class);
		createEditorField(model, "x", "");
		createEditorField(model, "y", "");
	}

	private void createEditorField(final Model<GridPoint2> model, String propertyName, String prefix) {
		Property<Float> childProperty = model.getProperty(propertyName);
		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();

		Label label = toolkit.createLabel(body, prefix + childProperty.getDescriptiveName() + ":");
		label.setAlignment(SWT.LEFT);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

		PropertyEditorContext<GridPoint2, Float> propertyContext = new PropertyEditorContext<>(context, model,
				getValue(), childProperty);
		PropertyEditor<Float> editor = createEditor(body, propertyContext);
		editor.getComposite().setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
	}
}
