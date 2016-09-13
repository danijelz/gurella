package com.gurella.studio.editor.engine.ui;

import org.eclipse.ui.forms.widgets.ScrolledPageBook;

import com.gurella.engine.editor.ui.EditorControl;
import com.gurella.engine.editor.ui.EditorScrolledPageBook;
import com.gurella.studio.GurellaStudioPlugin;

public class SwtEditorScrolledPageBook extends SwtEditorBaseScrolledComposite<ScrolledPageBook>
		implements EditorScrolledPageBook {
	@SuppressWarnings("unused")
	public SwtEditorScrolledPageBook(SwtEditorLayoutComposite<?> parent, int style) {
		super(GurellaStudioPlugin.getToolkit().createPageBook(parent.widget, style));
		new SwtEditorComposite(widget.getContainer());
	}

	@Override
	public boolean isDelayedReflow() {
		return widget.isDelayedReflow();
	}

	@Override
	public void setDelayedReflow(boolean delayedReflow) {
		widget.setDelayedReflow(delayedReflow);
	}

	@Override
	public void reflow() {
		widget.reflow(true);
	}

	@Override
	public SwtEditorComposite createPage(Object key) {
		return new SwtEditorComposite(widget.createPage(key));
	}

	@Override
	public SwtEditorComposite getContainer() {
		return getEditorWidget(widget.getContainer());
	}

	@Override
	public SwtEditorControl<?> getCurrentPage() {
		return getEditorWidget(widget.getCurrentPage());
	}

	@Override
	public boolean hasPage(Object key) {
		return widget.hasPage(key);
	}

	@Override
	public void registerPage(Object key, EditorControl page) {
		widget.registerPage(key, ((SwtEditorControl<?>) page).widget);
	}

	@Override
	public void removePage(Object key) {
		widget.removePage(key);
	}

	@Override
	public void removePage(Object key, boolean showEmptyPage) {
		widget.removePage(key, showEmptyPage);
	}

	@Override
	public void showEmptyPage() {
		widget.showEmptyPage();
	}

	@Override
	public void showPage(Object key) {
		widget.showPage(key);
	}
}
