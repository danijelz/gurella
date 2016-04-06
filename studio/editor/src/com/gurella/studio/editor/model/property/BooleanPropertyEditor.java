package com.gurella.studio.editor.model.property;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.gurella.engine.base.model.Property;

public class BooleanPropertyEditor extends SimplePropertyEditor<Boolean> {
	private Button check;

	public BooleanPropertyEditor(Composite parent, ModelPropertiesContainer<?> propertiesContainer,
			Property<Boolean> property, Object modelInstance) {
		super(parent, propertiesContainer, property, modelInstance);
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
				property.setValue(getModelInstance(), Boolean.valueOf(check.getSelection()));
				setDirty();
			}
		});
	}

	@Override
	public void present(Object modelInstance) {
		Boolean value = property.getValue(modelInstance);
		if (value != null) {
			check.setSelection(value.booleanValue());
		}
	}
}
