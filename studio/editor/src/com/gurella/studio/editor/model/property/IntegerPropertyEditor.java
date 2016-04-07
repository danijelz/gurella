package com.gurella.studio.editor.model.property;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.gurella.studio.editor.GurellaStudioPlugin;

public class IntegerPropertyEditor extends SimplePropertyEditor<Integer> {
	private Text text;

	public IntegerPropertyEditor(Composite parent, PropertyEditorContext<?, Integer> context) {
		super(parent, context);

		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		body.setLayout(layout);
		text = GurellaStudioPlugin.getToolkit().createText(body, "", SWT.BORDER);
		text.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false));

		Integer value = getValue();
		if (value != null) {
			text.setText(value.toString());
		}

		text.addModifyListener((e) -> setValue(Integer.valueOf(text.getText())));
		text.addVerifyListener(new VerifyListener() {
			@Override
			public void verifyText(VerifyEvent e) {
				try {
					final String oldS = text.getText();
					String newS = oldS.substring(0, e.start) + e.text + oldS.substring(e.end);
					if (newS.length() > 0) {
						Integer.parseInt(newS);
					}
				} catch (Exception e2) {
					e.doit = false;
				}
			}
		});
	}
}
