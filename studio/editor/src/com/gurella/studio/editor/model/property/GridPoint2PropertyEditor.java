package com.gurella.studio.editor.model.property;

import static com.gurella.studio.editor.model.PropertyEditorFactory.createEditor;

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

public class GridPoint2PropertyEditor extends SimplePropertyEditor<GridPoint2> {
	public GridPoint2PropertyEditor(Composite parent, PropertyEditorContext<?, GridPoint2> context) {
		super(parent, context);

		GridLayout layout = new GridLayout(4, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		body.setLayout(layout);
		Model<GridPoint2> model = Models.getModel(GridPoint2.class);
		createEditorField(model, "x");
		createEditorField(model, "y");
	}

	private void createEditorField(final Model<GridPoint2> model, String propertyName) {
		Property<Float> childProperty = model.getProperty(propertyName);
		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		PropertyEditorContext<GridPoint2, Float> propertyContext = new PropertyEditorContext<>(context, model,
				getValue(), childProperty);
		PropertyEditor<Float> editor = createEditor(body, propertyContext);
		editor.getComposite().setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		Label label = toolkit.createLabel(body, childProperty.getDescriptiveName());
		label.setAlignment(SWT.LEFT);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
	}
}
