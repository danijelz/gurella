package com.gurella.studio.editor.model.property;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.gurella.studio.editor.GurellaStudioPlugin;

public class StringPropertyEditor extends SimplePropertyEditor<String> {
	private Text text;

	public StringPropertyEditor(Composite parent, PropertyEditorContext<?, String> context) {
		super(parent, context);

		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		body.setLayout(layout);
		text = GurellaStudioPlugin.getToolkit().createText(body, "", SWT.BORDER);
		text.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false));

		String value = getValue();
		if (value != null) {
			text.setText(value);
		}

		text.addModifyListener((e) -> setValue(text.getText()));
	}
}
