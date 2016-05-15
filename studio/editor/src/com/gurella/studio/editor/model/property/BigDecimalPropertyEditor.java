package com.gurella.studio.editor.model.property;

import java.math.BigDecimal;
import java.util.function.BiConsumer;

import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Composite;

import com.gurella.studio.editor.utils.UiUtils;

public class BigDecimalPropertyEditor extends SingleTextPropertyEditor<BigDecimal> {
	public BigDecimalPropertyEditor(Composite parent, PropertyEditorContext<?, BigDecimal> context) {
		super(parent, context);
	}

	@Override
	protected BigDecimal getDefaultValue() {
		return BigDecimal.valueOf(0);
	}

	@Override
	protected BigDecimal extractValue(String stringValue) {
		return new BigDecimal(stringValue);
	}

	@Override
	protected BiConsumer<VerifyEvent, String> getVerifyListener() {
		return UiUtils::verifyBigDecimal;
	}

	@Override
	protected String toStringNonNullValue(BigDecimal value) {
		return value.toPlainString();
	}

	@Override
	protected WheelEventListener getWheelEventListener() {
		return this::onWheelEvent;
	}

	private void onWheelEvent(int amount, float multiplier) {
		BigDecimal value = getValue();
		updateValue(value.add(new BigDecimal(amount * multiplier)));
	}
}
