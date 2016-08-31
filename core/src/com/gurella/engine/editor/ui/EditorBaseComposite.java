package com.gurella.engine.editor.ui;

public interface EditorBaseComposite extends EditorScrollable {
	EditorControl[] getChildren();

	void layout();

	EditorControl[] getTabList();

	void setTabList(EditorControl[] tabList);
}
