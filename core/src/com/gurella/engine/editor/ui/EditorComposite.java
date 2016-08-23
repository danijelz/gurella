package com.gurella.engine.editor.ui;

import java.util.List;

public interface EditorComposite extends EditorControl {
	List<EditorControl> getChildren();
	
	void layout();
}
