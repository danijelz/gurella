package com.gurella.studio.editor.model.extension;

import org.eclipse.ui.forms.widgets.FormToolkit;

import com.gurella.engine.editor.ui.EditorComposite;
import com.gurella.engine.editor.ui.EditorLabel;
import com.gurella.engine.editor.ui.EditorUiFactory;
import com.gurella.studio.GurellaStudioPlugin;

public class SwtEditorUiFactory implements EditorUiFactory {
	private FormToolkit toolkit = GurellaStudioPlugin.getToolkit();

	@Override
	public EditorComposite createComposite(EditorComposite parent) {
		SwtEditorComposite swtParent = (SwtEditorComposite) parent;
		return new SwtEditorComposite(swtParent, toolkit);
	}

	@Override
	public EditorLabel createLabel(EditorComposite parent) {
		SwtEditorComposite swtParent = (SwtEditorComposite) parent;
		return new SwtEditorLabel(swtParent, toolkit);
	}

	@Override
	public EditorLabel createLabel(EditorComposite parent, String text) {
		SwtEditorComposite swtParent = (SwtEditorComposite) parent;
		return new SwtEditorLabel(swtParent, toolkit, text);
	}
}
