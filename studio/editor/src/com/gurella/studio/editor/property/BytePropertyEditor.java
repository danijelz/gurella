package com.gurella.studio.editor.property;

import java.util.function.BiConsumer;

import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Composite;

import com.gurella.studio.editor.utils.UiUtils;

public class BytePropertyEditor extends SingleTextPropertyEditor<Byte> {
	public BytePropertyEditor(Composite parent, PropertyEditorContext<?, Byte> context) {
		super(parent, context);
	}

	@Override
	protected Byte getDefaultValue() {
		return Byte.valueOf((byte) 0);
	}

	@Override
	protected Byte extractValue(String stringValue) {
		return Byte.valueOf(stringValue);
	}

	@Override
	protected BiConsumer<VerifyEvent, String> getVerifyListener() {
		return UiUtils::verifyByte;
	}

	@Override
	protected WheelEventListener getWheelEventListener() {
		return this::onWheelEvent;
	}

	private void onWheelEvent(int amount, float multiplier) {
		Byte value = getValue();
		updateValue(Byte.valueOf((byte) (value.byteValue() + (amount * multiplier))));
	}
}
