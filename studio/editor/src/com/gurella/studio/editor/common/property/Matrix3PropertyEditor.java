package com.gurella.studio.editor.common.property;

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

		content.setLayout(new GridLayout(3, false));
		buildUi();

		if (!context.isFixedValue()) {
			addMenuItem("New instance", () -> newValue(new Matrix3()));
			if (context.isNullable()) {
				addMenuItem("Set null", () -> newValue(null));
			}
		}
	}

	private void buildUi() {
		Matrix3 value = getValue();
		if (value == null) {
			Label label = UiUtils.createLabel(content, "null");
			label.setAlignment(SWT.CENTER);
			label.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 3, 1));
			label.addListener(SWT.MouseUp, (e) -> showMenu());
		} else {
			createText(Matrix3.M00, value);
			createText(Matrix3.M01, value);
			createText(Matrix3.M02, value);
			createText(Matrix3.M10, value);
			createText(Matrix3.M11, value);
			createText(Matrix3.M12, value);
			createText(Matrix3.M20, value);
			createText(Matrix3.M21, value);
			createText(Matrix3.M22, value);
			UiUtils.paintBordersFor(content);
		}

		content.layout();
	}

	private void createText(int index, Matrix3 value) {
		Text text = UiUtils.createFloatWidget(content);
		GridData layoutData = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
		layoutData.widthHint = 50;
		layoutData.heightHint = 14;
		text.setLayoutData(layoutData);
		text.setText(Float.toString(value.val[index]));
		text.addModifyListener((e) -> valueChanged(index, text.getText()));
	}

	private void valueChanged(int index, String strValue) {
		Matrix3 value = getValue();
		Matrix3 newValue = new Matrix3(value);
		newValue.val[index] = Values.isBlank(strValue) ? 0 : Float.valueOf(strValue).floatValue();
		setValue(newValue);
	}

	private void rebuildUi() {
		UiUtils.disposeChildren(content);
		buildUi();
	}

	private void newValue(Matrix3 value) {
		setValue(value);
		rebuildUi();
	}

	@Override
	protected void updateValue(Matrix3 value) {
		rebuildUi();
	}
}
