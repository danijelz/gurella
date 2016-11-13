package com.gurella.studio.editor.engine.bean;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.gurella.studio.editor.common.bean.BeanEditor;
import com.gurella.studio.editor.engine.ui.SwtEditorUi;
import com.gurella.studio.editor.utils.UiUtils;

public class CustomBeanEditor<T> extends BeanEditor<T> {
	public CustomBeanEditor(Composite parent, CustomBeanEditorContextAdapter<T> context) {
		super(parent, context);
		addListener(SWT.Resize, e -> UiUtils.reflow(this));
		context.factory.buildUi(SwtEditorUi.createComposite(this), context);
	}

	@Override
	public CustomBeanEditorContextAdapter<T> getContext() {
		return (CustomBeanEditorContextAdapter<T>) super.getContext();
	}
}
