package com.gurella.studio.editor.model.property;

import static com.gurella.studio.editor.model.PropertyEditorFactory.createEditor;

import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.badlogic.gdx.graphics.Color;
import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.Models;
import com.gurella.engine.base.model.Property;
import com.gurella.studio.editor.GurellaStudioPlugin;

public class ColorPropertyEditor extends SimplePropertyEditor<Color> {
	private ColorSelector selector;
	
	public ColorPropertyEditor(Composite parent, PropertyEditorContext<?, Color> context) {
		super(parent, context);

		GridLayout layout = new GridLayout(8, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		body.setLayout(layout);
		
		/*FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		selector = new ColorSelector(body);*/
		
		Model<Color> vector3Model = Models.getModel(Color.class);
		createEditorField(vector3Model, "r");
		createEditorField(vector3Model, "g");
		createEditorField(vector3Model, "b");
		createEditorField(vector3Model, "a");
	}

	private void createEditorField(final Model<Color> colorModel, String propertyName) {
		Property<Float> childProperty = colorModel.getProperty(propertyName);
		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		PropertyEditorContext<Color, Float> propertyContext = new PropertyEditorContext<>(context, colorModel,
				getValue(), childProperty);
		PropertyEditor<Float> editor = createEditor(body, propertyContext);
		editor.getComposite().setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		Label label = toolkit.createLabel(body, childProperty.getDescriptiveName());
		label.setAlignment(SWT.LEFT);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
	}
}
