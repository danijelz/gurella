package com.gurella.studio.editor.model.property;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.gurella.studio.editor.GurellaStudioPlugin;

public class BooleanPropertyEditor extends SimplePropertyEditor<Boolean> {
	private Button check;

	public BooleanPropertyEditor(Composite parent, PropertyEditorContext<?, Boolean> context) {
		super(parent, context);

		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		setLayout(layout);
		check = GurellaStudioPlugin.getToolkit().createButton(this, "", SWT.CHECK);
		check.setBackground(GurellaStudioPlugin.getResourceManager().createColor(new RGB(240, 240, 240)));
		check.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false));

		Boolean value = getValue();
		if (value != null) {
			check.setSelection(value.booleanValue());
		}
		check.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setValue(Boolean.valueOf(check.getSelection()));
			}
		});
	}
}
