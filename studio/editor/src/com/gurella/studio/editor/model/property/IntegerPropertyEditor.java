package com.gurella.studio.editor.model.property;

import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.gurella.studio.editor.utils.UiUtils;

public class IntegerPropertyEditor extends SimplePropertyEditor<Integer> {
	private Text text;

	public IntegerPropertyEditor(Composite parent, PropertyEditorContext<?, Integer> context) {
		super(parent, context);

		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 1;
		layout.marginHeight = 2;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		body.setLayout(layout);

		buildUi();

		if (context.isNullable()) {
			addMenuItem("Set value", () -> updateValue(Integer.valueOf(0)));
			addMenuItem("Set null", () -> updateValue(null));
		}
	}

	private void buildUi() {
		Integer value = getValue();
		if (value == null) {
			Label label = UiUtils.createLabel(body, "null");
			label.setAlignment(SWT.CENTER);
			label.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
		} else {
			text = UiUtils.createIntegerWidget(body);
			GridData layoutData = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
			layoutData.widthHint = 60;
			layoutData.heightHint = 16;
			text.setLayoutData(layoutData);
			text.setText(value.toString());
			text.addModifyListener(e -> setValue(Integer.valueOf(text.getText())));
			UiUtils.paintBordersFor(body);
		}

		body.layout();
	}

	private void rebuildUi() {
		Arrays.stream(body.getChildren()).forEach(c -> c.dispose());
		buildUi();
	}

	private void updateValue(Integer value) {
		setValue(value);
		rebuildUi();
	}
}
