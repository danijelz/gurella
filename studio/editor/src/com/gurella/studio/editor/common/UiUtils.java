package com.gurella.studio.editor.common;

import static com.gurella.studio.GurellaStudioPlugin.createFont;

import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.gurella.studio.GurellaStudioPlugin;

public class UiUtils {
	public static Text createFloatWidget(Composite parent) {
		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		Text text = toolkit.createText(parent, "", SWT.SINGLE);
		text.addVerifyListener(e -> verifyFloat(e, text.getText()));
		toolkit.adapt(text, false, false);
		text.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		text.setFont(createFont(FontDescriptor.createFrom(text.getFont()).increaseHeight(-1)));
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
