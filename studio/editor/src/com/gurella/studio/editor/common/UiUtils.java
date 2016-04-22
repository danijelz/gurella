package com.gurella.studio.editor.common;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.gurella.studio.editor.GurellaStudioPlugin;

public class UiUtils {
	public static Text createFloatWidget(Composite parent) {
		Text text = GurellaStudioPlugin.getToolkit().createText(parent, "", SWT.BORDER | SWT.SINGLE);
		text.addVerifyListener(e -> verifyFloat(e, text.getText()));
		return text;
	}

	public static void verifyFloat(VerifyEvent e, String oldValue) {
		try {
			String newS = oldValue.substring(0, e.start) + e.text + oldValue.substring(e.end);
			if (newS.length() > 0) {
				Float.parseFloat(newS);
			}
		} catch (Exception e2) {
			e.doit = false;
		}
	}
}
