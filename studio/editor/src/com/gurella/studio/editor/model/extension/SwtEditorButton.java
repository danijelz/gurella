package com.gurella.studio.editor.model.extension;

import java.io.InputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.gurella.engine.editor.ui.Alignment;
import com.gurella.engine.editor.ui.EditorButton;
import com.gurella.engine.editor.ui.EditorImage;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.model.extension.style.SwtWidgetStyle;

public class SwtEditorButton extends SwtEditorControl<Button> implements EditorButton {
	public SwtEditorButton(SwtEditorComposite parent, int style) {
		super(parent, style);
	}

	public SwtEditorButton(SwtEditorComposite parent, String text, int style) {
		super(parent, style);
		setText(text);
	}

	@Override
	public Alignment getAlignment() {
		return SwtWidgetStyle.alignment(widget.getAlignment());
	}

	@Override
	public void setAlignment(Alignment alignment) {
		widget.setAlignment(SwtWidgetStyle.alignment(alignment));
	}

	@Override
	public boolean getGrayed() {
		return widget.getGrayed();
	}

	@Override
	public void setGrayed(boolean grayed) {
		widget.setGrayed(grayed);
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

	@Override
	public boolean getSelection() {
		return widget.getSelection();
	}

	@Override
	public void setSelection(boolean selected) {
		widget.setSelection(selected);
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
	Button createWidget(Composite parent, int style) {
		return GurellaStudioPlugin.getToolkit().createButton(parent, "", style);
	}
}
