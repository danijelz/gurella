package com.gurella.engine.editor.ui;

import java.util.List;

public interface EditorComposite extends EditorScrollable {
	List<EditorControl> getChildren();

	void layout();

	EditorControl[] getTabList();

	void setTabList(EditorControl[] tabList);

	public static class CompositeStyle extends ScrollableStyle {
	}
}
