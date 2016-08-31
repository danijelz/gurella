package com.gurella.engine.editor.ui;

import com.gurella.engine.editor.ui.layout.EditorLayout;

public interface EditorComposite extends EditorBaseComposite {
	EditorLayout getLayout();

	void setLayout(EditorLayout layout);

	public static class CompositeStyle extends ScrollableStyle<CompositeStyle> {
	}
}
