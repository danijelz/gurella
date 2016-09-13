package com.gurella.studio.editor.property;

import java.util.function.BiConsumer;

import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Composite;

import com.gurella.studio.editor.utils.UiUtils;

public class IntegerPropertyEditor extends SingleTextPropertyEditor<Integer> {
	public IntegerPropertyEditor(Composite parent, PropertyEditorContext<?, Integer> context) {
		super(parent, context);
	}

	@Override
	protected Integer getDefaultValue() {
		return Integer.valueOf(0);
	}

	@Override
	protected Integer extractValue(String stringValue) {
		return Integer.valueOf(stringValue);
	}

	@Override
	protected BiConsumer<VerifyEvent, String> getVerifyListener() {
		return UiUtils::verifyInteger;
	}

	@Override
	protected WheelEventListener getWheelEventListener() {
		return this::onWheelEvent;
	}

	private void onWheelEvent(int amount, float multiplier) {
		Integer value = getValue();
		updateValue(Integer.valueOf((int) (value.intValue() + (amount * multiplier))));
	}
}
