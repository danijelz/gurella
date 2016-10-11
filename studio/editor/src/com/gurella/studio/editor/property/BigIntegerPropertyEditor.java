package com.gurella.studio.editor.property;

import java.math.BigInteger;
import java.util.function.BiConsumer;

import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Composite;

import com.gurella.studio.editor.utils.UiUtils;

public class BigIntegerPropertyEditor extends SingleTextPropertyEditor<BigInteger> {
	public BigIntegerPropertyEditor(Composite parent, PropertyEditorContext<?, BigInteger> context) {
		super(parent, context);
	}

	@Override
	protected BigInteger getDefaultValue() {
		return BigInteger.ZERO;
	}

	@Override
	protected BigInteger extractValue(String stringValue) {
		return new BigInteger(stringValue);
	}

	@Override
	protected BiConsumer<VerifyEvent, String> getVerifyListener() {
		return UiUtils::verifyBigInteger;
	}

	@Override
	protected WheelEventListener getWheelEventListener() {
		return this::onWheelEvent;
	}

	private void onWheelEvent(int amount, float multiplier) {
		BigInteger value = getValue();
		newValue(value.add(BigInteger.valueOf((long) (amount * multiplier))));
	}
}
