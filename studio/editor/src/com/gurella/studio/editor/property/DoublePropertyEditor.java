package com.gurella.studio.editor.property;

import java.util.function.BiConsumer;

import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Composite;

import com.gurella.studio.editor.utils.UiUtils;

public class DoublePropertyEditor extends SingleTextPropertyEditor<Double> {
	public DoublePropertyEditor(Composite parent, PropertyEditorContext<?, Double> context) {
		super(parent, context);
	}

	@Override
	protected Double getDefaultValue() {
		return Double.valueOf(0);
	}

	@Override
	protected Double extractValue(String stringValue) {
		return Double.valueOf(stringValue);
	}

	@Override
	protected BiConsumer<VerifyEvent, String> getVerifyListener() {
		return UiUtils::verifyDouble;
	}

	@Override
	protected WheelEventListener getWheelEventListener() {
		return this::onWheelEvent;
	}

	private void onWheelEvent(int amount, float multiplier) {
		Double value = getValue();
		newValue(Double.valueOf(value.doubleValue() + (amount * multiplier)));
	}
}
