package com.gurella.studio.editor.model.property;

import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.gurella.studio.editor.utils.UiUtils;

public class StringPropertyEditor extends SimplePropertyEditor<String> {
	private Text text;

	public StringPropertyEditor(Composite parent, PropertyEditorContext<?, String> context) {
		super(parent, context);

		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 1;
		layout.marginHeight = 2;
		body.setLayout(layout);

		buildUi();

		if (!context.isFinal()) {
			addMenuItem("Set value", () -> updateValue(""));
			if (context.isNullable()) {
				addMenuItem("Set null", () -> updateValue(null));
			}
		}
	}

	private void buildUi() {
		String value = getValue();
		if (value == null) {
			Label label = UiUtils.createLabel(body, "null");
			label.setAlignment(SWT.CENTER);
			label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));
		} else {
			text = UiUtils.createText(body);
			GridData layoutData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
			layoutData.widthHint = 60;
			layoutData.heightHint = 16;
			text.setLayoutData(layoutData);
			text.setText(value.toString());
			text.addModifyListener(e -> setValue(text.getText()));
			UiUtils.paintBordersFor(body);
		}

		body.layout();
	}

	private void rebuildUi() {
		Arrays.stream(body.getChildren()).forEach(c -> c.dispose());
		buildUi();
	}

	private void updateValue(String value) {
		setValue(value);
		rebuildUi();
	}
}
