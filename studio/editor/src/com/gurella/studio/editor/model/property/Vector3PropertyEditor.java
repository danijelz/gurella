package com.gurella.studio.editor.model.property;

import static com.gurella.studio.GurellaStudioPlugin.createFont;
import static com.gurella.studio.editor.model.PropertyEditorFactory.createEditor;

import java.util.Arrays;

import org.eclipse.jface.resource.FontDescriptor;
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
import com.gurella.engine.utils.Values;
import com.gurella.studio.GurellaStudioPlugin;

public class Vector3PropertyEditor extends PropertyEditor<Vector3> {
	public Vector3PropertyEditor(Composite parent, PropertyEditorContext<?, Vector3> context) {
		super(parent, context);

		GridLayout layout = new GridLayout(6, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing = 2;
		layout.verticalSpacing = 0;
		body.setLayout(layout);

		buildUi();

		if (!context.isFixedValue()) {
			addMenuItem("New instance", () -> newInstance());
			if (context.isNullable()) {
				addMenuItem("Set null", () -> setNull());
			}
		}
	}

	private void buildUi() {
		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		Vector3 value = getValue();
		if (value == null) {
			String descriptiveName = context.getDescriptiveName();
			boolean hasName = Values.isNotBlank(descriptiveName);
			if (hasName) {
				Label label = toolkit.createLabel(body, descriptiveName + ":");
				label.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false, 1, 1));
				label.setFont(createFont(FontDescriptor.createFrom(label.getFont()).setStyle(SWT.BOLD)));
			}

			Label label = toolkit.createLabel(body, "null");
			label.setAlignment(SWT.CENTER);
			label.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, hasName ? 5 : 6, 1));
		} else {
			String descriptiveName = context.getDescriptiveName();
			if (Values.isNotBlank(descriptiveName)) {
				Label label = toolkit.createLabel(body, descriptiveName);
				label.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false, 6, 1));
				label.setFont(createFont(FontDescriptor.createFrom(label.getFont()).setStyle(SWT.BOLD)));
			}

			Model<Vector3> model = Models.getModel(Vector3.class);
			createEditorField(model, "x", "");
			createEditorField(model, "y", "");
			createEditorField(model, "z", "");
		}

		body.layout();
	}

	private void createEditorField(final Model<Vector3> model, String propertyName, String prefix) {
		Property<Float> childProperty = model.getProperty(propertyName);
		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();

		Label label = toolkit.createLabel(body, prefix + childProperty.getDescriptiveName() + ":");
		label.setAlignment(SWT.LEFT);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

		PropertyEditorContext<Vector3, Float> propertyContext = new PropertyEditorContext<>(context, model, getValue(),
				childProperty);
		PropertyEditor<Float> editor = createEditor(body, propertyContext);
		editor.getComposite().setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
	}

	private void rebuildUi() {
		Arrays.stream(body.getChildren()).forEach(c -> c.dispose());
		buildUi();
	}

	private void setNull() {
		setValue(null);
		rebuildUi();
	}

	private void newInstance() {
		setValue(new Vector3());
		rebuildUi();
	}
}
