package com.gurella.studio.editor.model;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;

import com.gurella.engine.base.model.Property;

public class BooleanPropertyEditor extends SimplePropertyEditor<Boolean> {
	private Button check;
	
	public BooleanPropertyEditor(ModelPropertiesContainer<?> parent, Property<Boolean> property) {
		super(parent, property);
	}

	@Override
	protected void buildUi() {
		check = getToolkit().createButton(this, "", SWT.CHECK);
		check.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));
	}

	@Override
	protected void present(Object modelInstance) {
		check.setSelection(property.getValue(modelInstance));
	}
}
