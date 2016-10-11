package com.gurella.studio.editor.property;

import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.badlogic.gdx.math.Vector2;
import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.Models;
import com.gurella.engine.base.model.Property;
import com.gurella.engine.utils.Values;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.utils.UiUtils;

public class Vector2PropertyEditor extends SimplePropertyEditor<Vector2> {
	public Vector2PropertyEditor(Composite parent, PropertyEditorContext<?, Vector2> context) {
		super(parent, context);

		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 1;
		layout.marginHeight = 2;
		layout.horizontalSpacing = 4;
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
		Vector2 value = getValue();
		if (value == null) {
			Label label = toolkit.createLabel(body, "null");
			label.setAlignment(SWT.CENTER);
			label.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 1));
			label.addListener(SWT.MouseUp, (e) -> showMenu());
		} else {
			Model<Vector2> model = Models.getModel(Vector2.class);
			createEditorField(model, value, "x");
			createEditorField(model, value, "y");
			UiUtils.paintBordersFor(body);
		}

		body.layout();
	}

	private void createEditorField(final Model<Vector2> model, Vector2 value, String propertyName) {
		Property<Float> childProperty = model.getProperty(propertyName);
		Text text = UiUtils.createFloatWidget(body);
		GridData layoutData = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
		layoutData.widthHint = 50;
		layoutData.heightHint = 14;
		text.setLayoutData(layoutData);
		text.setToolTipText(propertyName);
		text.setText(childProperty.getValue(value).toString());
		text.addModifyListener(e -> valueChanged(childProperty, text.getText()));
	}

	private void valueChanged(Property<Float> childProperty, String txtValue) {
		Vector2 value = getValue();
		Vector2 newValue = new Vector2(value);
		childProperty.setValue(newValue, Values.isBlank(txtValue) ? Float.valueOf(0) : Float.valueOf(txtValue));
		setValue(newValue);
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
		setValue(new Vector2());
		rebuildUi();
	}
}
