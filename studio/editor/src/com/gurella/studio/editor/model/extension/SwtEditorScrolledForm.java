package com.gurella.studio.editor.model.extension;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.gurella.engine.editor.ui.EditorControl;
import com.gurella.engine.editor.ui.EditorImage;
import com.gurella.engine.editor.ui.EditorScrolledForm;
import com.gurella.studio.GurellaStudioPlugin;

public class SwtEditorScrolledForm extends SwtEditorBaseScrolledComposite<ScrolledForm> implements EditorScrolledForm {
	public SwtEditorScrolledForm(SwtEditorLayoutComposite<?> parent) {
		super(GurellaStudioPlugin.getToolkit().createScrolledForm(parent.widget));
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
	public SwtEditorForm getForm() {
		Form form = widget.getForm();
		SwtEditorForm editorForm = getEditorWidget(form);
		return editorForm == null ? new SwtEditorForm(form) : editorForm;
	}

	@Override
	public SwtEditorImage getImage() {
		return toEditorImage(widget.getImage());
	}

	@Override
	public String getText() {
		return widget.getText();
	}

	@Override
	public void setBackgroundImage(EditorImage backgroundImage) {
		// TODO Auto-generated method stub

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
	public void setImage(EditorImage image) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setText(String text) {
		widget.setText(text);
	}
}
