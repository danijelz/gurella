package com.gurella.studio.editor.common.property;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.gurella.studio.editor.utils.UiUtils;

public class BooleanPropertyEditor extends SimplePropertyEditor<Boolean> {
	private Button check;

	public BooleanPropertyEditor(Composite parent, PropertyEditorContext<?, Boolean> context) {
		super(parent, context);

		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		content.setLayout(layout);

		buildUi();

		if (!context.isFixedValue()) {
			addMenuItem("Set to False", () -> newValue(Boolean.FALSE));
			addMenuItem("Set to True", () -> newValue(Boolean.TRUE));
			if (context.isNullable()) {
				addMenuItem("Set to null", () -> newValue(null));
			}
		}
	}

	private void buildUi() {
		Boolean value = getValue();
		if (value == null) {
			Label label = UiUtils.createLabel(content, "null");
			label.setAlignment(SWT.CENTER);
			label.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
			label.addListener(SWT.MouseUp, (e) -> showMenu());
		} else {
			check = getToolkit().createButton(content, "", SWT.CHECK);
			check.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));
			check.setSelection(value.booleanValue());
			check.addListener(SWT.Selection, e -> setValue(Boolean.valueOf(check.getSelection())));
			UiUtils.paintBordersFor(content);
		}

		content.layout();
	}

	private void rebuildUi() {
		UiUtils.disposeChildren(content);
		buildUi();
	}

	private void newValue(Boolean value) {
		setValue(value);
		rebuildUi();
	}

	@Override
	protected void updateValue(Boolean value) {
		rebuildUi();
	}
}
