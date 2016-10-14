package com.gurella.studio.editor.common.property;

import java.util.function.BiConsumer;

import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Composite;

import com.gurella.studio.editor.utils.UiUtils;

public class LongPropertyEditor extends SingleTextPropertyEditor<Long> {
	public LongPropertyEditor(Composite parent, PropertyEditorContext<?, Long> context) {
		super(parent, context);
	}

	@Override
	protected Long getDefaultValue() {
		return Long.valueOf(0);
	}

	@Override
	protected Long extractValue(String stringValue) {
		return Long.valueOf(stringValue);
	}

	@Override
	protected BiConsumer<VerifyEvent, String> getVerifyListener() {
		return UiUtils::verifyLong;
	}

	@Override
	protected WheelEventListener getWheelEventListener() {
		return this::onWheelEvent;
	}

	private void onWheelEvent(int amount, float multiplier) {
		Long value = getValue();
		newValue(Long.valueOf((long) (value.longValue() + (amount * multiplier))));
	}
}
