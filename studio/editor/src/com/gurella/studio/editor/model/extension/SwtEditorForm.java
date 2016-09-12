package com.gurella.studio.editor.model.extension;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.Form;

import com.badlogic.gdx.graphics.Color;
import com.gurella.engine.editor.ui.EditorComposite;
import com.gurella.engine.editor.ui.EditorControl;
import com.gurella.engine.editor.ui.EditorForm;
import com.gurella.engine.editor.ui.EditorImage;
import com.gurella.studio.GurellaStudioPlugin;

public class SwtEditorForm extends SwtEditorLayoutComposite<Form> implements EditorForm {
	SwtEditorForm(SwtEditorLayoutComposite<?> parent, int style) {
		super(parent, style);
	}

	@Override
	Form createWidget(Composite parent, int style) {
		return GurellaStudioPlugin.getToolkit().createForm(parent);
	}

	@Override
	public EditorImage getBackgroundImage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EditorComposite getBody() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EditorComposite getHead() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EditorControl getHeadClient() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Color getHeadColor(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EditorImage getImage() {
		// TODO Auto-generated method stub
		return null;
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
	public void setBackgroundImage(EditorImage backgroundImage) {
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

	}

	@Override
	public void setHeadColor(String key, Color color) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setImage(EditorImage image) {
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

	}
}
