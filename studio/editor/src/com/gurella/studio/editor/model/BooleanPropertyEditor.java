package com.gurella.studio.editor.model;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;

import com.gurella.engine.base.model.Property;

public class BooleanPropertyEditor extends SimplePropertyEditor<Boolean> {
	private Button check;

	public BooleanPropertyEditor(ModelPropertiesContainer<?> parent, Property<Boolean> property) {
		super(parent, property);
	}

	@Override
	protected void buildUi() {
		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		setLayout(layout);
		check = getToolkit().createButton(this, "", SWT.CHECK);
		check.setBackground(getGurellaEditor().getResourceManager().createColor(new RGB(240, 240, 240)));
		check.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false));
		check.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				property.setValue(getModelInstance(), check.getSelection());
				setDirty();
			}
		});
	}

	@Override
	protected void present(Object modelInstance) {
		check.setSelection(property.getValue(modelInstance));
	}
}
