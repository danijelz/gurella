package com.gurella.studio.editor.engine.ui;

import java.io.InputStream;
import java.util.Arrays;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.Form;

import com.badlogic.gdx.graphics.Color;
import com.gurella.engine.editor.ui.EditorComposite;
import com.gurella.engine.editor.ui.EditorControl;
import com.gurella.engine.editor.ui.EditorForm;
import com.gurella.engine.editor.ui.EditorImage;
import com.gurella.studio.GurellaStudioPlugin;

public class SwtEditorForm extends SwtEditorLayoutComposite<Form> implements EditorForm {
	SwtEditorForm(Form form) {
		super(form);
	}

	SwtEditorForm(SwtEditorLayoutComposite<?> parent) {
		super(GurellaStudioPlugin.getToolkit().createForm(parent.widget));
	}

	@Override
	public SwtEditorImage getBackgroundImage() {
		return toEditorImage(widget.getBackgroundImage());
	}

	@Override
	public SwtEditorComposite getBody() {
		Composite body = widget.getBody();
		SwtEditorComposite editorBody = getEditorWidget(body);
		return editorBody == null ? new SwtEditorComposite(body) : editorBody;
	}

	@Override
	public EditorComposite getHead() {
		Composite head = widget.getHead();
		SwtEditorComposite editorHead = getEditorWidget(head);
		return editorHead == null ? new SwtEditorComposite(head) : editorHead;
	}

	@Override
	public SwtEditorControl<?> getHeadClient() {
		return getEditorWidget(widget.getHeadClient());
	}

	@Override
	public Color getHeadColor(HeadColorKey key) {
		String swtKey = key == null ? null : key.name();
		return toGdxColor(widget.getHeadColor(swtKey));
	}

	@Override
	public EditorImage getImage() {
		return toEditorImage(widget.getImage());
	}

	@Override
	public String getText() {
		return widget.getText();
	}

	@Override
	public boolean isBackgroundImageTiled() {
		return widget.isBackgroundImageTiled();
	}

	@Override
	public boolean isBusy() {
		return widget.isBusy();
	}

	@Override
	public boolean isSeparatorVisible() {
		return widget.isSeparatorVisible();
	}

	@Override
	public void setBackgroundImage(EditorImage image) {
		widget.setBackgroundImage(toSwtImage(image));
	}

	@Override
	public void setBackgroundImage(InputStream imageStream) {
		widget.setBackgroundImage(toSwtImage(imageStream));
	}

	@Override
	public void setBackgroundImageTiled(boolean backgroundImageTiled) {
		widget.setBackgroundImageTiled(backgroundImageTiled);
	}

	@Override
	public void setBusy(boolean busy) {
		widget.setBusy(busy);
	}

	@Override
	public void setHeadClient(EditorControl headClient) {
		widget.setHeadClient(headClient == null ? null : ((SwtEditorControl<?>) headClient).widget);
	}

	@Override
	public void setHeadColor(HeadColorKey key, Color color) {
		String swtKey = key == null ? null : key.name();
		widget.setHeadColor(swtKey, toSwtColor(color));
	}

	@Override
	public void setHeadColor(HeadColorKey key, int r, int g, int b, int a) {
		String swtKey = key == null ? null : key.name();
		widget.setHeadColor(swtKey, toSwtColor(r, g, b, a));
	}

	@Override
	public void setImage(EditorImage image) {
		widget.setImage(toSwtImage(image));
	}

	@Override
	public void setImage(InputStream imageStream) {
		widget.setImage(toSwtImage(imageStream));
	}

	@Override
	public void setSeparatorVisible(boolean addSeparator) {
		widget.setSeparatorVisible(addSeparator);
	}

	@Override
	public void setText(String text) {
		widget.setText(text);
	}

	@Override
	public void setTextBackground(Color[] gradientColors, int[] percents, boolean vertical) {
		widget.setTextBackground(Arrays.stream(gradientColors).map(c -> toSwtColor(c))
				.toArray(i -> new org.eclipse.swt.graphics.Color[i]), percents, vertical);
	}
}
