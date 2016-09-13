package com.gurella.studio.editor.model.extension;

import static com.gurella.studio.GurellaStudioPlugin.getToolkit;

import java.io.InputStream;

import org.eclipse.swt.widgets.Button;

import com.gurella.engine.editor.ui.Alignment;
import com.gurella.engine.editor.ui.EditorButton;
import com.gurella.engine.editor.ui.EditorImage;
import com.gurella.studio.editor.model.extension.style.SwtWidgetStyle;

public class SwtEditorButton extends SwtEditorControl<Button> implements EditorButton {
	public SwtEditorButton(SwtEditorLayoutComposite<?> parent, int style) {
		this(parent, "", style);
	}

	public SwtEditorButton(SwtEditorLayoutComposite<?> parent, String text, int style) {
		super(getToolkit().createButton(parent.widget, text, style));
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
		return toEditorImage(widget.getImage());
	}

	@Override
	public void setImage(InputStream imageStream) {
		widget.setImage(toSwtImage(imageStream));
	}

	@Override
	public void setImage(EditorImage image) {
		widget.setImage(toSwtImage(image));
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
}
