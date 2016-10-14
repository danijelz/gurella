package com.gurella.studio.editor.common.property;

import java.util.function.BiConsumer;

import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Composite;

public class StringPropertyEditor extends SingleTextPropertyEditor<String> {
	public StringPropertyEditor(Composite parent, PropertyEditorContext<?, String> context) {
		super(parent, context);
	}

	@Override
	protected String getDefaultValue() {
		return "";
	}

	@Override
	protected String extractValue(String stringValue) {
		return String.valueOf(stringValue);
	}

	@Override
	protected BiConsumer<VerifyEvent, String> getVerifyListener() {
		return StringPropertyEditor::verify;
	}

	@SuppressWarnings("unused")
	private static void verify(VerifyEvent e, String oldValue) {
	}
}
