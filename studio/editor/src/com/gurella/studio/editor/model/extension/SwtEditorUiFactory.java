package com.gurella.studio.editor.model.extension;

import static com.gurella.engine.utils.Values.cast;

import org.eclipse.ui.forms.widgets.FormToolkit;

import com.gurella.engine.editor.ui.EditorComposite;
import com.gurella.engine.editor.ui.EditorLabel;
import com.gurella.engine.editor.ui.EditorUiFactory;
import com.gurella.studio.GurellaStudioPlugin;

public class SwtEditorUiFactory implements EditorUiFactory {
	public static final SwtEditorUiFactory instance = new SwtEditorUiFactory();

	private FormToolkit toolkit = GurellaStudioPlugin.getToolkit();

	private SwtEditorUiFactory() {
	}

	@Override
	public EditorComposite createComposite(EditorComposite parent) {
		return new SwtEditorComposite(cast(parent), toolkit);
	}

	@Override
	public EditorLabel createLabel(EditorComposite parent) {
		return new SwtEditorLabel(cast(parent), toolkit);
	}

	@Override
	public EditorLabel createLabel(EditorComposite parent, String text) {
		return new SwtEditorLabel(cast(parent), toolkit, text);
	}
}
