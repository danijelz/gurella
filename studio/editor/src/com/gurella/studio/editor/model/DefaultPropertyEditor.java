package com.gurella.studio.editor.model;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;

import com.gurella.engine.base.model.Property;

public class DefaultPropertyEditor<T> extends SimplePropertyEditor<T> {
	public DefaultPropertyEditor(ModelPropertiesContainer<?> parent, Property<T> property) {
		super(parent, property);
	}

	@Override
	protected void buildUi() {
		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		setLayout(layout);
		Label label = getToolkit().createLabel(this, "Missing editor");
		label.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
	}

	@Override
	protected void present(Object modelInstance) {
	}
}
