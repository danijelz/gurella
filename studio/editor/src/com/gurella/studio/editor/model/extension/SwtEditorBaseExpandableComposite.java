package com.gurella.studio.editor.model.extension;

import org.eclipse.swt.SWT;
import org.eclipse.ui.forms.widgets.ExpandableComposite;

import com.badlogic.gdx.graphics.Color;
import com.gurella.engine.editor.ui.EditorControl;
import com.gurella.engine.editor.ui.EditorExpandableComposite;
import com.gurella.studio.GurellaStudioPlugin;

public abstract class SwtEditorBaseExpandableComposite<T extends ExpandableComposite>
		extends SwtEditorLayoutComposite<T> implements EditorExpandableComposite {
	SwtEditorBaseExpandableComposite(SwtEditorComposite parent, int style) {
		super(parent, style);
	}

	@Override
	public SwtEditorControl<?> getClient() {
		return getEditorWidget(widget.getClient());
	}

	@Override
	public String getText() {
		return widget.getText();
	}

	@Override
	public SwtEditorControl<?> getTextClient() {
		return getEditorWidget(widget.getTextClient());
	}

	@Override
	public int getTextClientHeightDifference() {
		return widget.getTextClientHeightDifference();
	}

	@Override
	public Color getTitleBarForeground() {
		return SwtEditorUi.toGdxColor(widget.getTitleBarForeground());
	}

	@Override
	public boolean isExpanded() {
		return widget.isExpanded();
	}

	@Override
	public void setActiveToggleColor(Color color) {
		org.eclipse.swt.graphics.Color swtColor = GurellaStudioPlugin.createColor(color);
		widget.addListener(SWT.Dispose, e -> GurellaStudioPlugin.destroyColor(swtColor));
		widget.setActiveToggleColor(swtColor);
	}

	@Override
	public void setActiveToggleColor(int r, int g, int b, int a) {
		org.eclipse.swt.graphics.Color swtColor = GurellaStudioPlugin.createColor(r, g, b, a);
		widget.addListener(SWT.Dispose, e -> GurellaStudioPlugin.destroyColor(swtColor));
		widget.setActiveToggleColor(swtColor);
	}

	@Override
	public void setClient(EditorControl client) {
		widget.setClient(client == null ? null : ((SwtEditorControl<?>) client).widget);
	}

	@Override
	public void setExpanded(boolean expanded) {
		widget.setExpanded(expanded);
	}

	@Override
	public void setText(String title) {
		widget.setText(title);
	}

	@Override
	public void setTextClient(EditorControl textClient) {
		widget.setTextClient(textClient == null ? null : ((SwtEditorControl<?>) textClient).widget);
	}

	@Override
	public void setTitleBarForeground(Color color) {
		org.eclipse.swt.graphics.Color swtColor = GurellaStudioPlugin.createColor(color);
		widget.addListener(SWT.Dispose, e -> GurellaStudioPlugin.destroyColor(swtColor));
		widget.setTitleBarForeground(swtColor);
	}

	@Override
	public void setTitleBarForeground(int r, int g, int b, int a) {
		org.eclipse.swt.graphics.Color swtColor = GurellaStudioPlugin.createColor(r, g, b, a);
		widget.addListener(SWT.Dispose, e -> GurellaStudioPlugin.destroyColor(swtColor));
		widget.setTitleBarForeground(swtColor);
	}

	@Override
	public void setToggleColor(Color color) {
		org.eclipse.swt.graphics.Color swtColor = GurellaStudioPlugin.createColor(color);
		widget.addListener(SWT.Dispose, e -> GurellaStudioPlugin.destroyColor(swtColor));
		widget.setToggleColor(swtColor);
	}

	@Override
	public void setToggleColor(int r, int g, int b, int a) {
		org.eclipse.swt.graphics.Color swtColor = GurellaStudioPlugin.createColor(r, g, b, a);
		widget.addListener(SWT.Dispose, e -> GurellaStudioPlugin.destroyColor(swtColor));
		widget.setToggleColor(swtColor);
	}
}
