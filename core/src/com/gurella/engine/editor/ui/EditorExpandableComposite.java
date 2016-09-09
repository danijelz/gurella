package com.gurella.engine.editor.ui;

import com.badlogic.gdx.graphics.Color;
import com.gurella.engine.editor.ui.layout.EditorLayout;

public interface EditorExpandableComposite extends EditorBaseComposite {
	// static int CLIENT_INDENT
	// If this style is used, the client origin will be vertically aligned with the title text.
	// static int COMPACT
	// If this style is used, computed size of the composite will take the client width into consideration only in the
	// expanded state.
	// static int EXPANDED
	// If this style is used, the control will be created in the expanded state.
	// static int FOCUS_TITLE
	// If this style is used, the title text will be rendered as a hyperlink that can individually accept focus.
	// static int LEFT_TEXT_CLIENT_ALIGNMENT
	// By default, text client is right-aligned.
	// static int NO_TITLE
	// If this style is used, title will not be rendered.
	// static int NO_TITLE_FOCUS_BOX
	// By default, a focus box is painted around the title when it receives focus.
	// static int SHORT_TITLE_BAR
	// If this style is used, a short version of the title bar decoration will be painted behind the text.
	// static int TITLE_BAR
	// If this style is used, title bar decoration will be painted behind the text.
	// static int TREE_NODE
	// If this style is used, a tree node with either + or - signs will be used to render the expansion toggle.
	// static int TWISTIE
	// If this style is used, a twistie will be used to render the expansion toggle.

	EditorControl getClient();

	int getExpansionStyle();

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
}
