package com.gurella.studio.editor.model.property;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.common.UiUtils;

public class FloatPropertyEditor extends SimplePropertyEditor<Float> {
	private Text text;

	public FloatPropertyEditor(Composite parent, PropertyEditorContext<?, Float> context) {
		super(parent, context);

		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 1;
		layout.marginHeight = 2;
		body.setLayout(layout);
		text = UiUtils.createFloatWidget(body);
		GridData layoutData = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
		layoutData.widthHint = 60;
		layoutData.heightHint = 16;
		text.setLayoutData(layoutData);

		Float value = getValue();
		if (value != null) {
			text.setText(value.toString());
		}

		text.addModifyListener(e -> setValue(Float.valueOf(text.getText())));
		GurellaStudioPlugin.getToolkit().paintBordersFor(body);
	}
}
