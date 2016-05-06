package com.gurella.studio.editor.model.property;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.gurella.studio.editor.utils.UiUtils;

public class ShortPropertyEditor extends SimplePropertyEditor<Short> {
	private Text text;

	public ShortPropertyEditor(Composite parent, PropertyEditorContext<?, Short> context) {
		super(parent, context);

		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 1;
		layout.marginHeight = 2;
		body.setLayout(layout);

		text = UiUtils.createShortWidget(body);
		GridData layoutData = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
		layoutData.widthHint = 60;
		layoutData.heightHint = 16;
		text.setLayoutData(layoutData);

		Short value = getValue();
		if (value != null) {
			text.setText(value.toString());
		}

		text.addModifyListener((event) -> setValue(Short.valueOf(text.getText())));
		UiUtils.paintBordersFor(body);
	}
}
