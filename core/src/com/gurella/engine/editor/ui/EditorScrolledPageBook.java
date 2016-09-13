package com.gurella.engine.editor.ui;

public interface EditorScrolledPageBook extends EditorScrolledComposite {
	boolean isDelayedReflow();

	void setDelayedReflow(boolean delayedReflow);

	void reflow();

	EditorComposite createPage(Object key);

	EditorComposite getContainer();

	EditorControl getCurrentPage();

	boolean hasPage(Object key);

	void registerPage(Object key, EditorControl page);

	void removePage(Object key);

	void removePage(Object key, boolean showEmptyPage);

	void showEmptyPage();

	void showPage(Object key);

	public static class ScrolledPageBookStyle extends ScrollableStyle<ScrolledPageBookStyle> {
	}
}
