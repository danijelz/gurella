package com.gurella.engine.editor.ui;

import com.badlogic.gdx.graphics.Color;

public interface EditorExpandableComposite extends EditorComposite {
	EditorControl getClient();

	String getText();

	EditorControl getTextClient();

	int getTextClientHeightDifference();

	Color getTitleBarForeground();

	boolean isExpanded();

	void setActiveToggleColor(Color c);

	void setActiveToggleColor(int r, int g, int b, int a);

	void setClient(EditorControl client);

	void setExpanded(boolean expanded);

	void setText(String title);

	void setTextClient(EditorControl textClient);

	void setTitleBarForeground(Color color);

	void setTitleBarForeground(int r, int g, int b, int a);

	void setToggleColor(Color c);

	void setToggleColor(int r, int g, int b, int a);

	public static class BaseExpandableCompositeStyle<T extends BaseExpandableCompositeStyle<T>>
			extends ScrollableStyle<T> {
		public boolean treeNodeToggleType;
		public boolean clientIdent = true;
		public boolean expanded = true;
		public boolean compact;
		public boolean focusTitle;
		public boolean noTitleFocusBox = true;
		public boolean leftTextClientAlignment;
		public TitleBarTypeType titleBarTypeType = TitleBarTypeType.TITLE_BAR;

		public T treeNodeToggleType(boolean treeNodeToggleType) {
			this.treeNodeToggleType = treeNodeToggleType;
			return cast();
		}

		public T clientIdent(boolean clientIdent) {
			this.clientIdent = clientIdent;
			return cast();
		}

		public T expanded(boolean expanded) {
			this.expanded = expanded;
			return cast();
		}

		public T compact(boolean compact) {
			this.compact = compact;
			return cast();
		}

		public T focusTitle(boolean focusTitle) {
			this.focusTitle = focusTitle;
			return cast();
		}

		public T noTitleFocusBox(boolean noTitleFocusBox) {
			this.noTitleFocusBox = noTitleFocusBox;
			return cast();
		}

		public T leftTextClientAlignment(boolean leftTextClientAlignment) {
			this.leftTextClientAlignment = leftTextClientAlignment;
			return cast();
		}

		public T titleBarTypeType(TitleBarTypeType titleBarTypeType) {
			this.titleBarTypeType = titleBarTypeType;
			return cast();
		}
	}

	public enum TitleBarTypeType {
		NO_TITLE, SHORT_TITLE_BAR, TITLE_BAR;
	}

	public static class ExpandableCompositeStyle extends BaseExpandableCompositeStyle<ExpandableCompositeStyle> {
	}
}
