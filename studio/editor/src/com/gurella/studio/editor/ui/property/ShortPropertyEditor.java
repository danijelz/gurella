package com.gurella.studio.editor.ui.property;

import java.util.function.BiConsumer;

import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Composite;

import com.gurella.studio.editor.utils.UiUtils;

public class ShortPropertyEditor extends SingleTextPropertyEditor<Short> {
	public ShortPropertyEditor(Composite parent, PropertyEditorContext<?, Short> context) {
		super(parent, context);
	}

	@Override
	protected Short getDefaultValue() {
		return Short.valueOf((short) 0);
	}

	@Override
	protected Short extractValue(String stringValue) {
		return Short.valueOf(stringValue);
	}

	@Override
	protected BiConsumer<VerifyEvent, String> getVerifyListener() {
		return UiUtils::verifyShort;
	}

	@Override
	protected WheelEventListener getWheelEventListener() {
		return this::onWheelEvent;
	}

	private void onWheelEvent(int amount, float multiplier) {
		Short value = getValue();
		newValue(Short.valueOf((short) (value.shortValue() + (amount * multiplier))));
	}
}
