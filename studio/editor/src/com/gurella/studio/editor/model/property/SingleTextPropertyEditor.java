package com.gurella.studio.editor.model.property;

import java.util.function.BiConsumer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;

import com.gurella.studio.editor.utils.UiUtils;

public abstract class SingleTextPropertyEditor<P> extends SimplePropertyEditor<P> {
	private Text text;

	public SingleTextPropertyEditor(Composite parent, PropertyEditorContext<?, P> context) {
		super(parent, context);

		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 1;
		layout.marginHeight = 2;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		body.setLayout(layout);

		buildUi();

		if (!context.isFixedValue()) {
			addMenuItem("Set to default", () -> updateValue(getDefaultValue()));
			if (context.isNullable()) {
				addMenuItem("Set to null", () -> updateValue(null));
			}
		}
	}

	protected abstract P getDefaultValue();

	protected abstract P extractValue(String stringValue);

	protected abstract BiConsumer<VerifyEvent, String> getVerifyListener();

	private void buildUi() {
		P value = getValue();
		text = UiUtils.createText(body);
		text.addVerifyListener(e -> getVerifyListener().accept(e, text.getText()));
		GridData layoutData = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
		layoutData.widthHint = 60;
		layoutData.heightHint = 16;
		text.setLayoutData(layoutData);

		if (value == null) {
			text.setMessage("null");
		} else {
			text.setText(value.toString());
		}

		text.addModifyListener(e -> textModified());
		text.addListener(SWT.MouseVerticalWheel, e -> onMouseVerticalWheel(e));

		UiUtils.paintBordersFor(body);
	}

	private void textModified() {
		String stringValue = text.getText();
		if (stringValue.length() > 0) {
			setValue(extractValue(stringValue));
			text.setMessage("");
		} else if (context.isNullable()) {
			setValue(null);
			text.setMessage("null");
		} else {
			P defaultValue = getDefaultValue();
			setValue(defaultValue);
			String defaultStringValue = defaultValue == null ? "null" : toStringNonNullValue(defaultValue);
			text.setMessage("default - " + defaultStringValue);
		}
	}

	protected void updateValue(P value) {
		setValue(value);
		if (value == null) {
			text.setText("");
			text.setMessage("null");
		} else {
			text.setText(toStringNonNullValue(value));
			text.setMessage("");
		}
	}

	protected String toStringNonNullValue(P value) {
		return value.toString();
	}

	private void onMouseVerticalWheel(Event e) {
		if (getValue() == null) {
			return;
		}

		float multiplier = -1;
		int stateMask = e.stateMask;
		if ((stateMask & SWT.SHIFT) != 0) {
			multiplier = 1;
		} else if ((stateMask & SWT.CONTROL) != 0) {
			multiplier = 10;
		} else if ((stateMask & SWT.ALT) != 0) {
			multiplier = 0.1f;
		}

		if (multiplier < 0) {
			return;
		}

		WheelEventListener listener = getWheelEventConsumer();
		if (listener == null) {
			return;
		}

		e.doit = false;
		int amount = e.count > 0 ? 1 : -1;
		listener.onWheelEvent(amount, multiplier);
	}

	protected WheelEventListener getWheelEventConsumer() {
		return null;
	}

	public interface WheelEventListener {
		void onWheelEvent(int amount, float multiplier);
	}
}
