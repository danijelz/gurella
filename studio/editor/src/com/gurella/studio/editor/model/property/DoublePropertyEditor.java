package com.gurella.studio.editor.model.property;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.gurella.studio.editor.GurellaStudioPlugin;

public class DoublePropertyEditor extends SimplePropertyEditor<Double> {
	private Text text;

	public DoublePropertyEditor(Composite parent, PropertyEditorContext<?, Double> context) {
		super(parent, context);

		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		setLayout(layout);
		text = GurellaStudioPlugin.getToolkit().createText(this, "", SWT.BORDER);
		text.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false));

		Double value = getValue();
		if (value != null) {
			text.setText(value.toString());
		}

		text.addModifyListener((e) -> setValue(Double.valueOf(text.getText())));
		text.addVerifyListener(new VerifyListener() {
			@Override
			public void verifyText(VerifyEvent e) {
				try {
					final String oldS = text.getText();
					String newS = oldS.substring(0, e.start) + e.text + oldS.substring(e.end);
					if (newS.length() > 0) {
						Double.parseDouble(newS);
					}
				} catch (Exception e2) {
					e.doit = false;
				}
			}
		});
	}
}
