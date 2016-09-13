package com.gurella.studio.editor.property;

import java.util.function.BiConsumer;

import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Composite;

import com.gurella.studio.editor.utils.UiUtils;

public class CharacterPropertyEditor extends SingleTextPropertyEditor<Character> {
	public CharacterPropertyEditor(Composite parent, PropertyEditorContext<?, Character> context) {
		super(parent, context);
	}

	@Override
	protected Character getDefaultValue() {
		return Character.valueOf((char) 0);
	}

	@Override
	protected Character extractValue(String stringValue) {
		return Character.valueOf(stringValue.charAt(0));
	}

	@Override
	protected BiConsumer<VerifyEvent, String> getVerifyListener() {
		return UiUtils::verifyCharacter;
	}
}
