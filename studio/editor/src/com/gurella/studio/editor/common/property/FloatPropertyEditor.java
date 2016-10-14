package com.gurella.studio.editor.common.property;

import java.util.function.BiConsumer;

import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Composite;

import com.gurella.studio.editor.utils.UiUtils;

public class FloatPropertyEditor extends SingleTextPropertyEditor<Float> {
	public FloatPropertyEditor(Composite parent, PropertyEditorContext<?, Float> context) {
		super(parent, context);
	}

	@Override
	protected Float getDefaultValue() {
		return Float.valueOf(0);
	}

	@Override
	protected Float extractValue(String stringValue) {
		return Float.valueOf(stringValue);
	}

	@Override
	protected BiConsumer<VerifyEvent, String> getVerifyListener() {
		return UiUtils::verifyFloat;
	}

	@Override
	protected WheelEventListener getWheelEventListener() {
		return this::onWheelEvent;
	}

	private void onWheelEvent(int amount, float multiplier) {
		Float value = getValue();
		updateValue(Float.valueOf(value.floatValue() + (amount * multiplier)));
	}
}
