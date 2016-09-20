package com.gurella.engine.editor.ui;

import com.gurella.engine.editor.ui.layout.EditorLayout;

public interface EditorLayoutComposite extends EditorBaseComposite {
	EditorLayout getLayout();

	EditorLayout getOrCreateLayout();

	EditorLayout getOrCreateDefaultLayout();

	void setLayout(EditorLayout layout);

	void setLayout(int numColumns);

	public void removeAllChildren();
}
