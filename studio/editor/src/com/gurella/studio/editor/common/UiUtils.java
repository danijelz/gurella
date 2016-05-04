package com.gurella.studio.editor.common;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.gurella.studio.GurellaStudioPlugin;

public class UiUtils {
	public static Text createFloatWidget(Composite parent) {
		Text text = GurellaStudioPlugin.getToolkit().createText(parent, "", SWT.SINGLE);
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

	public static <T extends Enum<T>> ComboViewer createEnumComboViewer(Composite parent, Class<T> enumType) {
		Combo combo = new Combo(parent, SWT.READ_ONLY);
		combo.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false));

		ComboViewer comboViewer = new ComboViewer(combo);
		comboViewer.setContentProvider(new ArrayContentProvider());
		comboViewer.setLabelProvider(new LabelProvider());
		comboViewer.setInput(enumType.getEnumConstants());
		return comboViewer;
	}
}
