package com.gurella.studio.editor.engine.model;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.gurella.studio.editor.common.model.MetaModelEditor;
import com.gurella.studio.editor.engine.ui.SwtEditorUi;

public class CustomModelEditor<T> extends MetaModelEditor<T> {
	public CustomModelEditor(Composite parent, CustomModelEditorContextAdapter<T> context) {
		super(parent, context);
	}

	@Override
	protected void createContent() {
		CustomModelEditorContextAdapter<T> context = getContext();
		addListener(SWT.Resize, e -> reflow());
		context.factory.buildUi(SwtEditorUi.createComposite(this), context);
	}

	private void reflow() {
		layout();
		Composite temp = this;
		while (temp != null) {
			if (temp instanceof ScrolledForm) {
				((ScrolledForm) temp).reflow(true);
			}
			temp = temp.getParent();
		}
	}

	@Override
	public CustomModelEditorContextAdapter<T> getContext() {
		return (CustomModelEditorContextAdapter<T>) super.getContext();
	}
}
