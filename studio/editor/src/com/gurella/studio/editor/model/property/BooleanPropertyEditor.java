package com.gurella.studio.editor.model.property;

import java.util.Arrays;

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
		body.setLayout(layout);

		buildUi();

		if (!context.isFixedValue()) {
			addMenuItem("Set value", () -> updateValue(Boolean.FALSE));
			if (context.isNullable()) {
				addMenuItem("Set null", () -> updateValue(null));
			}
		}
	}

	private void buildUi() {
		Boolean value = getValue();
		if (value == null) {
			Label label = UiUtils.createLabel(body, "null");
			label.setAlignment(SWT.CENTER);
			label.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
			label.addListener(SWT.MouseUp, (e) -> showMenu());
		} else {
			check = getToolkit().createButton(body, "", SWT.CHECK);
			check.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false));
			check.setSelection(value.booleanValue());
			check.addListener(SWT.Selection, e -> setValue(Boolean.valueOf(check.getSelection())));
			UiUtils.paintBordersFor(body);
		}

		body.layout();
	}

	private void rebuildUi() {
		Arrays.stream(body.getChildren()).forEach(c -> c.dispose());
		buildUi();
	}

	private void updateValue(Boolean value) {
		setValue(value);
		rebuildUi();
	}
}
