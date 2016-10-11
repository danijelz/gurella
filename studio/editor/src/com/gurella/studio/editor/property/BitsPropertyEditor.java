package com.gurella.studio.editor.property;

import java.util.Arrays;
import java.util.stream.IntStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.badlogic.gdx.utils.Bits;
import com.gurella.studio.GurellaStudioPlugin;

public class BitsPropertyEditor extends CompositePropertyEditor<Bits> {
	private GridLayout bodyLayout;

	public BitsPropertyEditor(Composite parent, PropertyEditorContext<?, Bits> context) {
		super(parent, context);

		bodyLayout = new GridLayout(1, false);
		bodyLayout.marginWidth = 0;
		bodyLayout.marginHeight = 0;
		bodyLayout.horizontalSpacing = 2;
		bodyLayout.verticalSpacing = 0;
		body.setLayout(bodyLayout);

		buildUi();

		if (!context.isFixedValue()) {
			addMenuItem("New instance", () -> newValue(new Bits()));
			if (context.isNullable()) {
				addMenuItem("Set null", () -> newValue(null));
			}
		}
	}

	private void buildUi() {
		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		Bits value = getValue();
		if (value == null) {
			bodyLayout.numColumns = 1;
			Label label = toolkit.createLabel(body, "null");
			label.setAlignment(SWT.CENTER);
			label.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
			label.addListener(SWT.MouseUp, (e) -> showMenu());
		} else {
			bodyLayout.numColumns = 16;
			IntStream.range(0, value.numBits()).forEach(i -> buildCheck(value, i));
		}

		body.layout();
	}

	private void buildCheck(Bits value, int index) {
		Button check = GurellaStudioPlugin.getToolkit().createButton(body, "", SWT.CHECK);
		check.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		check.setSelection(value.get(index));
		check.setToolTipText(Integer.toString(index));
		check.addListener(SWT.Selection, e -> updateValue(value, index, check.getSelection()));
	}

	private static void updateValue(Bits value, int index, boolean set) {
		if (set) {
			value.set(index);
		} else {
			value.clear(index);
		}
	}

	private void rebuildUi() {
		Arrays.stream(body.getChildren()).forEach(c -> c.dispose());
		buildUi();
	}

	private void newValue(Bits value) {
		setValue(value);
		rebuildUi();
	}

	@Override
	protected void updateValue(Bits value) {
		rebuildUi();
	}
}
