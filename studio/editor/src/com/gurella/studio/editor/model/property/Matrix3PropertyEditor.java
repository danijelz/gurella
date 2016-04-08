package com.gurella.studio.editor.model.property;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.badlogic.gdx.math.Matrix3;
import com.gurella.studio.editor.GurellaStudioPlugin;

public class Matrix3PropertyEditor extends ComplexPropertyEditor<Matrix3> {
	private Text m00;
	private Text m01;
	private Text m02;
	private Text m10;
	private Text m11;
	private Text m12;
	private Text m20;
	private Text m21;
	private Text m22;

	public Matrix3PropertyEditor(Composite parent, PropertyEditorContext<?, Matrix3> context) {
		super(parent, context);

		body.setLayout(new GridLayout(6, false));
		m00 = createText(Matrix3.M00, "00");
		m01 = createText(Matrix3.M01, "01");
		m02 = createText(Matrix3.M02, "02");
		m10 = createText(Matrix3.M10, "10");
		m11 = createText(Matrix3.M11, "11");
		m12 = createText(Matrix3.M12, "12");
		m20 = createText(Matrix3.M20, "20");
		m21 = createText(Matrix3.M21, "21");
		m22 = createText(Matrix3.M22, "22");
	}

	private Text createText(int index, String name) {
		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		Text text = toolkit.createText(body, "", SWT.BORDER | SWT.SINGLE);
		GridData layoutData = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
		layoutData.widthHint = 60;
		text.setLayoutData(layoutData);

		Matrix3 value = getValue();
		if (value != null) {
			text.setText(value.toString());
		}

		text.addModifyListener((e) -> valueChanged(index, text.getText()));
		text.addVerifyListener(new VerifyListener() {
			@Override
			public void verifyText(VerifyEvent e) {
				try {
					final String oldS = text.getText();
					String newS = oldS.substring(0, e.start) + e.text + oldS.substring(e.end);
					if (newS.length() > 0) {
						Float.parseFloat(newS);
					}
				} catch (Exception e2) {
					e.doit = false;
				}
			}
		});

		Label label = toolkit.createLabel(body, name);
		label.setAlignment(SWT.LEFT);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));

		return text;
	}

	private void valueChanged(int index, String value) {
		Matrix3 matrix = getValue();
		Matrix3 oldValue = new Matrix3(matrix);
		matrix.val[index] = Float.valueOf(value).floatValue();
		context.propertyValueChanged(oldValue, matrix);
	}
}
