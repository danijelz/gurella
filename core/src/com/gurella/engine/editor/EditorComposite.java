package com.gurella.engine.editor;

import java.util.List;

public interface EditorComposite extends EditorControl {
	List<EditorControl> getChildren();
}
