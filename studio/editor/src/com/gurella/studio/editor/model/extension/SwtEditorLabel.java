package com.gurella.studio.editor.model.extension;

import java.io.InputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.gurella.engine.editor.ui.Alignment;
import com.gurella.engine.editor.ui.EditorImage;
import com.gurella.engine.editor.ui.EditorLabel;

public class SwtEditorLabel extends SwtEditorControl<Label> implements EditorLabel {
	public SwtEditorLabel(SwtEditorComposite parent, FormToolkit toolkit) {
		super(parent, toolkit);
	}

	public SwtEditorLabel(SwtEditorComposite parent, FormToolkit toolkit, String text) {
		super(parent, toolkit);
		setText(text);
	}

	@Override
	Label createWidget(Composite parent, FormToolkit toolkit) {
		return toolkit.createLabel(parent, "");
	}

	@Override
	public String getText() {
		return widget.getText();
	}

	@Override
	public void setText(String string) {
		widget.setText(string);
	}

	@Override
	public Alignment getAlignment() {
		return SwtEditorUiFactoryUtils.alignmentFromSwt(widget.getAlignment());
	}

	@Override
	public void setAlignment(Alignment alignment) {
		widget.setAlignment(SwtEditorUiFactoryUtils.alignmentToSwt(alignment));
	}

	@Override
	public EditorImage getImage() {
		Image image = widget.getImage();
		return image == null ? null : new SwtEditorImage(image);
	}

	@Override
	public void setImage(InputStream imageStream) {
		Image image = new Image(widget.getDisplay(), imageStream);
		widget.addListener(SWT.Dispose, e -> image.dispose());
		widget.setImage(image);
	}
}
