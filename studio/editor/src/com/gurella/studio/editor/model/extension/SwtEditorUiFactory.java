package com.gurella.studio.editor.model.extension;

import static com.gurella.engine.utils.Values.cast;

import org.eclipse.swt.widgets.Composite;

import com.gurella.engine.editor.ui.EditorComposite;
import com.gurella.engine.editor.ui.EditorLabel;
import com.gurella.engine.editor.ui.EditorUiFactory;
import com.gurella.engine.utils.Values;

public class SwtEditorUiFactory implements EditorUiFactory {
	public static final SwtEditorUiFactory instance = new SwtEditorUiFactory();

	private SwtEditorUiFactory() {
	}

	public EditorComposite createComposite(Composite parent) {
		return new SwtEditorComposite(parent);
	}

	@Override
	public EditorComposite createComposite(EditorComposite parent) {
		return new SwtEditorComposite(Values.<SwtEditorComposite> cast(parent));
	}

	@Override
	public EditorLabel createLabel(EditorComposite parent) {
		return new SwtEditorLabel(cast(parent));
	}

	@Override
	public EditorLabel createLabel(EditorComposite parent, String text) {
		return new SwtEditorLabel(cast(parent), text);
	}
}
