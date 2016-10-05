package com.gurella.studio.editor.property;

import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.badlogic.gdx.math.Vector3;
import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.Models;
import com.gurella.engine.base.model.Property;
import com.gurella.engine.utils.Values;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.utils.UiUtils;

public class Vector3PropertyEditor extends SimplePropertyEditor<Vector3> {
	public Vector3PropertyEditor(Composite parent, PropertyEditorContext<?, Vector3> context) {
		super(parent, context);

		GridLayout layout = new GridLayout(3, false);
		layout.marginWidth = 1;
		layout.marginHeight = 2;
		layout.horizontalSpacing = 4;
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
			label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 3, 1));
			label.addListener(SWT.MouseUp, (e) -> showMenu());
		} else {
			Model<Vector3> model = Models.getModel(Vector3.class);
			createEditorField(model, "x");
			createEditorField(model, "y");
			createEditorField(model, "z");
			UiUtils.paintBordersFor(body);
		}

		body.layout();
	}

	private void createEditorField(final Model<Vector3> model, String propertyName) {
		Property<Float> childProperty = model.getProperty(propertyName);

		Text text = UiUtils.createFloatWidget(body);
		GridData layoutData = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
		layoutData.widthHint = 60;
		layoutData.heightHint = 16;
		text.setLayoutData(layoutData);
		text.setToolTipText(propertyName);

		Vector3 value = getValue();
		if (value != null) {
			text.setText(childProperty.getValue(value).toString());
		}

		text.addModifyListener(e -> valueChanged(childProperty, text.getText()));
	}

	private void valueChanged(Property<Float> childProperty, String txtValue) {
		Vector3 value = getValue();
		Vector3 oldValue = new Vector3(value);
		childProperty.setValue(value, Values.isBlank(txtValue) ? Float.valueOf(0) : Float.valueOf(txtValue));
		context.propertyValueChanged(oldValue, value);
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
