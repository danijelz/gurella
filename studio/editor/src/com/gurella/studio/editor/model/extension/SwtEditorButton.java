package com.gurella.studio.editor.model.extension;

import java.io.InputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.gurella.engine.editor.ui.Alignment;
import com.gurella.engine.editor.ui.EditorButton;
import com.gurella.engine.editor.ui.EditorImage;
import com.gurella.studio.GurellaStudioPlugin;

public class SwtEditorButton extends SwtEditorControl<Button> implements EditorButton {
	public SwtEditorButton(SwtEditorBaseComposite<?> parent, FormToolkit toolkit) {
		super(parent);
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
		Image image = new Image(widget.getDisplay(), imageStream);
		widget.addListener(SWT.Dispose, e -> image.dispose());
		widget.setImage(image);
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
	Button createWidget(Composite parent) {
		return GurellaStudioPlugin.getToolkit().createButton(parent, "", 0);
	}
}
