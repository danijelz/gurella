package com.gurella.studio.editor.engine.bean;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.gurella.studio.editor.common.bean.BeanEditor;
import com.gurella.studio.editor.engine.ui.SwtEditorUi;

public class CustomBeanEditor<T> extends BeanEditor<T> {
	public CustomBeanEditor(Composite parent, CustomBeanEditorContextAdapter<T> context) {
		super(parent, context);
	}

	@Override
	protected void createContent() {
		CustomBeanEditorContextAdapter<T> context = getContext();
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
	public CustomBeanEditorContextAdapter<T> getContext() {
		return (CustomBeanEditorContextAdapter<T>) super.getContext();
	}
}
