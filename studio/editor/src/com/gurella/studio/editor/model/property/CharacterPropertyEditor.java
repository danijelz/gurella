package com.gurella.studio.editor.model.property;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.gurella.studio.editor.GurellaStudioPlugin;

public class CharacterPropertyEditor extends SimplePropertyEditor<Character> {
	private Text text;

	public CharacterPropertyEditor(Composite parent, PropertyEditorContext<?, Character> context) {
		super(parent, context);

		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		body.setLayout(layout);
		text = GurellaStudioPlugin.getToolkit().createText(body, "", SWT.BORDER);
		text.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false));

		Character value = getValue();
		if (value != null) {
			text.setText(value.toString());
		}

		text.addModifyListener((e) -> setValue(Character.valueOf(text.getText().charAt(0))));
		text.addVerifyListener(new VerifyListener() {
			@Override
			public void verifyText(VerifyEvent e) {
				final String oldS = text.getText();
				String newS = oldS.substring(0, e.start) + e.text + oldS.substring(e.end);
				if (newS.length() > 1) {
					e.doit = false;
				}
			}
		});
	}
}
