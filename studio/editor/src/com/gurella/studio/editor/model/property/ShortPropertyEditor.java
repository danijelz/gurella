package com.gurella.studio.editor.model.property;

import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.gurella.studio.editor.utils.UiUtils;

public class ShortPropertyEditor extends SimplePropertyEditor<Short> {
	private Text text;

	public ShortPropertyEditor(Composite parent, PropertyEditorContext<?, Short> context) {
		super(parent, context);

		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 1;
		layout.marginHeight = 2;
		body.setLayout(layout);

		buildUi();

		if (!context.isFixedValue()) {
			addMenuItem("Set value", () -> updateValue(Short.valueOf((short) 0)));
			if (context.isNullable()) {
				addMenuItem("Set null", () -> updateValue(null));
			}
		}
	}

	private void buildUi() {
		Short value = getValue();
		if (value == null) {
			Label label = UiUtils.createLabel(body, "null");
			label.setAlignment(SWT.CENTER);
			label.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
		} else {
			text = UiUtils.createShortWidget(body);
			GridData layoutData = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
			layoutData.widthHint = 60;
			layoutData.heightHint = 16;
			text.setLayoutData(layoutData);
			text.setText(value.toString());
			text.addModifyListener(e -> setValue(Short.valueOf(text.getText())));
			UiUtils.paintBordersFor(body);
		}

		body.layout();
	}

	private void rebuildUi() {
		Arrays.stream(body.getChildren()).forEach(c -> c.dispose());
		buildUi();
	}

	private void updateValue(Short value) {
		setValue(value);
		rebuildUi();
	}
}
