package com.gurella.engine.editor.ui;

import com.gurella.engine.editor.ui.EditorToolItem.ToolItemType;

public interface EditorToolBar extends EditorBaseComposite {
	EditorToolItem getItem(int index);

	EditorToolItem getItem(int x, int y);

	int getItemCount();

	EditorToolItem[] getItems();

	int getRowCount();

	int indexOf(EditorToolItem item);

	EditorToolItem createItem(ToolItemType type);

	EditorToolItem createItem(int index, ToolItemType type);

	public static class ToolBarStyle extends ScrollableStyle<ToolBarStyle> {
		public boolean wrap;
		public boolean right;
		public boolean flat;
		public boolean shadowOut;

		public ToolBarStyle wrap(boolean wrap) {
			this.wrap = wrap;
			return cast();
		}

		public ToolBarStyle right(boolean right) {
			this.right = right;
			return cast();
		}

		public ToolBarStyle flat(boolean flat) {
			this.flat = flat;
			return cast();
		}

		public ToolBarStyle shadowOut(boolean shadowOut) {
			this.shadowOut = shadowOut;
			return cast();
		}
	}
}
