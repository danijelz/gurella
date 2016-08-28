package com.gurella.studio.editor.model.extension;

import java.io.InputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.gurella.engine.editor.ui.Alignment;
import com.gurella.engine.editor.ui.EditorImage;
import com.gurella.engine.editor.ui.EditorLabel;
import com.gurella.studio.GurellaStudioPlugin;

public class SwtEditorLabel extends SwtEditorControl<Label> implements EditorLabel {
	public SwtEditorLabel(SwtEditorBaseComposite<?> parent, int style) {
		super(parent, style);
	}

	public SwtEditorLabel(SwtEditorBaseComposite<?> parent, String text, int style) {
		super(parent, style);
		setText(text);
	}

	@Override
	Label createWidget(Composite parent, int style) {
		return GurellaStudioPlugin.getToolkit().createLabel(parent, "", style);
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
		if (imageStream == null) {
			widget.setImage(null);
		} else {
			Image image = new Image(widget.getDisplay(), imageStream);
			widget.addListener(SWT.Dispose, e -> image.dispose());
			widget.setImage(image);
		}
	}

	@Override
	public void setImage(EditorImage image) {
		widget.setImage(image == null ? null : ((SwtEditorImage) image).image);
	}
}
