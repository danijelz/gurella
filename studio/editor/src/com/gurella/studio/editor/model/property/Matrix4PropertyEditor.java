package com.gurella.studio.editor.model.property;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.badlogic.gdx.math.Matrix4;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.utils.UiUtils;

public class Matrix4PropertyEditor extends ComplexPropertyEditor<Matrix4> {
	public Matrix4PropertyEditor(Composite parent, PropertyEditorContext<?, Matrix4> context) {
		super(parent, context);

		body.setLayout(new GridLayout(8, false));
		createText(Matrix4.M00, "00");
		createText(Matrix4.M01, "01");
		createText(Matrix4.M02, "02");
		createText(Matrix4.M03, "03");
		createText(Matrix4.M10, "10");
		createText(Matrix4.M11, "11");
		createText(Matrix4.M12, "12");
		createText(Matrix4.M13, "13");
		createText(Matrix4.M20, "20");
		createText(Matrix4.M21, "21");
		createText(Matrix4.M22, "22");
		createText(Matrix4.M23, "23");
		createText(Matrix4.M30, "30");
		createText(Matrix4.M31, "31");
		createText(Matrix4.M32, "32");
		createText(Matrix4.M33, "33");
		UiUtils.paintBordersFor(body);
	}

	private void createText(int index, String name) {
		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();

		Label label = toolkit.createLabel(body, name);
		label.setAlignment(SWT.LEFT);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));

		Text text = UiUtils.createFloatWidget(body);
		GridData layoutData = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
		layoutData.widthHint = 60;
		layoutData.heightHint = 10;
		text.setLayoutData(layoutData);

		Matrix4 value = getValue();
		if (value != null) {
			text.setText(Float.toString(value.val[index]));
		}

		text.addModifyListener((e) -> valueChanged(index, text.getText()));
	}

	private void valueChanged(int index, String value) {
		Matrix4 matrix = getValue();
		Matrix4 oldValue = new Matrix4(matrix);
		matrix.val[index] = Float.valueOf(value).floatValue();
		context.propertyValueChanged(oldValue, matrix);
	}
}
