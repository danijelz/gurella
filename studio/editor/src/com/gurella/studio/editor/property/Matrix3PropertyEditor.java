package com.gurella.studio.editor.property;

import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.badlogic.gdx.math.Matrix3;
import com.gurella.engine.utils.Values;
import com.gurella.studio.editor.utils.UiUtils;

public class Matrix3PropertyEditor extends CompositePropertyEditor<Matrix3> {
	public Matrix3PropertyEditor(Composite parent, PropertyEditorContext<?, Matrix3> context) {
		super(parent, context);

		body.setLayout(new GridLayout(3, false));
		buildUi();

		if (!context.isFixedValue()) {
			addMenuItem("New instance", () -> updateValue(new Matrix3()));
			if (context.isNullable()) {
				addMenuItem("Set null", () -> updateValue(null));
			}
		}
	}

	private void buildUi() {
		Matrix3 value = getValue();
		if (value == null) {
			Label label = UiUtils.createLabel(body, "null");
			label.setAlignment(SWT.CENTER);
			label.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 3, 1));
			label.addListener(SWT.MouseUp, (e) -> showMenu());
		} else {
			createText(Matrix3.M00);
			createText(Matrix3.M01);
			createText(Matrix3.M02);
			createText(Matrix3.M10);
			createText(Matrix3.M11);
			createText(Matrix3.M12);
			createText(Matrix3.M20);
			createText(Matrix3.M21);
			createText(Matrix3.M22);
			UiUtils.paintBordersFor(body);
		}

		body.layout();
	}

	private void createText(int index) {
		Text text = UiUtils.createFloatWidget(body);
		GridData layoutData = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
		layoutData.widthHint = 60;
		layoutData.heightHint = 16;
		text.setLayoutData(layoutData);

		Matrix3 value = getValue();
		if (value != null) {
			text.setText(Float.toString(value.val[index]));
		}

		text.addModifyListener((e) -> valueChanged(index, text.getText()));
	}

	private void valueChanged(int index, String value) {
		Matrix3 matrix = getValue();
		Matrix3 oldValue = new Matrix3(matrix);
		matrix.val[index] = Values.isBlank(value) ? 0 : Float.valueOf(value).floatValue();
		context.propertyValueChanged(oldValue, matrix);
	}

	private void rebuildUi() {
		Arrays.stream(body.getChildren()).forEach(c -> c.dispose());
		buildUi();
	}

	private void updateValue(Matrix3 value) {
		setValue(value);
		rebuildUi();
	}
}
