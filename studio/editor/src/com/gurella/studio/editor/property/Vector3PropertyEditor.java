package com.gurella.studio.editor.property;

import static com.gurella.studio.editor.property.PropertyEditorFactory.createEditor;

import java.util.Arrays;

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
import com.gurella.studio.GurellaStudioPlugin;

public class Vector3PropertyEditor extends SimplePropertyEditor<Vector3> {
	public Vector3PropertyEditor(Composite parent, PropertyEditorContext<?, Vector3> context) {
		super(parent, context);

		GridLayout layout = new GridLayout(3, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing = 2;
		layout.verticalSpacing = 0;
		body.setLayout(layout);

		buildUi();

		if (!context.isFixedValue()) {
			addMenuItem("New Vector3", () -> newInstance());
			if (context.isNullable()) {
				addMenuItem("Set to null", () -> setNull());
			}
		}
	}

	private void buildUi() {
		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		Vector3 value = getValue();
		if (value == null) {
			Label label = toolkit.createLabel(body, "null");
			label.setAlignment(SWT.CENTER);
			label.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 3, 1));
			label.addListener(SWT.MouseUp, (e) -> showMenu());
		} else {
			Model<Vector3> model = Models.getModel(Vector3.class);
			createEditorField(model, "x");
			createEditorField(model, "y");
			createEditorField(model, "z");
		}

		body.layout();
	}

	private void createEditorField(final Model<Vector3> model, String propertyName) {
		Property<Float> childProperty = model.getProperty(propertyName);
		PropertyEditorContext<Vector3, Float> propertyContext = new PropertyEditorContext<>(context, model, getValue(),
				childProperty);
		PropertyEditor<Float> editor = createEditor(body, propertyContext);
		editor.getComposite().setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		editor.getComposite().setToolTipText(propertyName);
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
