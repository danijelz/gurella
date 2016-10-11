package com.gurella.studio.editor.property;

import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.badlogic.gdx.math.Quaternion;
import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.Models;
import com.gurella.engine.base.model.Property;
import com.gurella.engine.utils.Values;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.utils.UiUtils;

public class QuaternionPropertyEditor extends SimplePropertyEditor<Quaternion> {
	public QuaternionPropertyEditor(Composite parent, PropertyEditorContext<?, Quaternion> context) {
		super(parent, context);

		GridLayout layout = new GridLayout(4, false);
		layout.marginWidth = 1;
		layout.marginHeight = 2;
		layout.horizontalSpacing = 4;
		layout.verticalSpacing = 0;
		body.setLayout(layout);

		buildUi();

		if (!context.isFixedValue()) {
			addMenuItem("New Vector3", () -> newValue(new Quaternion()));
			if (context.isNullable()) {
				addMenuItem("Set to null", () -> newValue(null));
			}
		}
	}

	private void buildUi() {
		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		Quaternion value = getValue();
		if (value == null) {
			Label label = toolkit.createLabel(body, "null");
			label.setAlignment(SWT.CENTER);
			label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 4, 1));
			label.addListener(SWT.MouseUp, (e) -> showMenu());
		} else {
			Model<Quaternion> model = Models.getModel(Quaternion.class);
			createEditorField(model, value, "x");
			createEditorField(model, value, "y");
			createEditorField(model, value, "z");
			UiUtils.paintBordersFor(body);
		}

		body.layout();
	}

	private void createEditorField(final Model<Quaternion> model, Quaternion value, String propertyName) {
		Property<Float> childProperty = model.getProperty(propertyName);
		Text text = UiUtils.createFloatWidget(body);
		GridData layoutData = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
		layoutData.widthHint = 40;
		layoutData.heightHint = 14;
		text.setLayoutData(layoutData);
		text.setToolTipText(propertyName);
		text.setText(childProperty.getValue(value).toString());
		text.addModifyListener(e -> valueChanged(childProperty, text.getText()));
	}

	private void valueChanged(Property<Float> childProperty, String txtValue) {
		Quaternion value = getValue();
		Quaternion newValue = new Quaternion(value);
		childProperty.setValue(newValue, Values.isBlank(txtValue) ? Float.valueOf(0) : Float.valueOf(txtValue));
		setValue(newValue);
	}

	private void rebuildUi() {
		Arrays.stream(body.getChildren()).forEach(c -> c.dispose());
		buildUi();
	}

	private void newValue(Quaternion value) {
		setValue(value);
		rebuildUi();
	}

	@Override
	protected void updateValue(Quaternion value) {
		rebuildUi();
	}
}
