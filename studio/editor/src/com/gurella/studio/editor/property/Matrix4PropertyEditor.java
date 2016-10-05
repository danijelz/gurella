package com.gurella.studio.editor.property;

import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.badlogic.gdx.math.Matrix4;
import com.gurella.engine.utils.Values;
import com.gurella.studio.editor.utils.UiUtils;

public class Matrix4PropertyEditor extends CompositePropertyEditor<Matrix4> {
	public Matrix4PropertyEditor(Composite parent, PropertyEditorContext<?, Matrix4> context) {
		super(parent, context);

		body.setLayout(new GridLayout(4, false));
		buildUi();

		if (!context.isFixedValue()) {
			addMenuItem("New instance", () -> updateValue(new Matrix4()));
			if (context.isNullable()) {
				addMenuItem("Set null", () -> updateValue(null));
			}
		}
	}

	private void buildUi() {
		Matrix4 value = getValue();
		if (value == null) {
			Label label = UiUtils.createLabel(body, "null");
			label.setAlignment(SWT.CENTER);
			label.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 4, 1));
			label.addListener(SWT.MouseUp, (e) -> showMenu());
		} else {
			createText(Matrix4.M00, value);
			createText(Matrix4.M01, value);
			createText(Matrix4.M02, value);
			createText(Matrix4.M03, value);
			createText(Matrix4.M10, value);
			createText(Matrix4.M11, value);
			createText(Matrix4.M12, value);
			createText(Matrix4.M13, value);
			createText(Matrix4.M20, value);
			createText(Matrix4.M21, value);
			createText(Matrix4.M22, value);
			createText(Matrix4.M23, value);
			createText(Matrix4.M30, value);
			createText(Matrix4.M31, value);
			createText(Matrix4.M32, value);
			createText(Matrix4.M33, value);
			UiUtils.paintBordersFor(body);
		}

		body.layout();
	}

	private void createText(int index, Matrix4 value) {
		Text text = UiUtils.createFloatWidget(body);
		GridData layoutData = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
		layoutData.widthHint = 43;
		layoutData.heightHint = 16;
		text.setLayoutData(layoutData);
		text.setText(Float.toString(value.val[index]));
		text.addModifyListener((e) -> valueChanged(index, text.getText()));
	}

	private void valueChanged(int index, String value) {
		Matrix4 matrix = getValue();
		Matrix4 oldValue = new Matrix4(matrix);
		matrix.val[index] = Values.isBlank(value) ? 0 : Float.valueOf(value).floatValue();
		context.propertyValueChanged(oldValue, matrix);
	}

	private void rebuildUi() {
		Arrays.stream(body.getChildren()).forEach(c -> c.dispose());
		buildUi();
	}

	private void updateValue(Matrix4 value) {
		setValue(value);
		rebuildUi();
	}
}
