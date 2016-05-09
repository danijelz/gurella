package com.gurella.studio.editor.model.property;

import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.gurella.studio.editor.utils.UiUtils;

public class CharacterPropertyEditor extends SimplePropertyEditor<Character> {
	private Text text;

	public CharacterPropertyEditor(Composite parent, PropertyEditorContext<?, Character> context) {
		super(parent, context);

		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 1;
		layout.marginHeight = 2;
		body.setLayout(layout);

		buildUi();

		if (!context.isFinal()) {
			addMenuItem("Set value", () -> updateValue(Character.valueOf('0')));
			if (context.isNullable()) {
				addMenuItem("Set null", () -> updateValue(null));
			}
		}
	}

	private void buildUi() {
		Character value = getValue();
		if (value == null) {
			Label label = UiUtils.createLabel(body, "null");
			label.setAlignment(SWT.CENTER);
			label.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
		} else {
			text = UiUtils.createCharacterWidget(body);
			GridData layoutData = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
			layoutData.widthHint = 60;
			layoutData.heightHint = 16;
			text.setLayoutData(layoutData);
			text.setText(value.toString());
			text.addModifyListener(e -> setValue(Character.valueOf(text.getText().charAt(0))));
			UiUtils.paintBordersFor(body);
		}

		body.layout();
	}

	private void rebuildUi() {
		Arrays.stream(body.getChildren()).forEach(c -> c.dispose());
		buildUi();
	}

	private void updateValue(Character value) {
		setValue(value);
		rebuildUi();
	}
}
