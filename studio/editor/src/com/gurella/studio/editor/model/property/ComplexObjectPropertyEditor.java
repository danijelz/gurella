package com.gurella.studio.editor.model.property;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;

import com.gurella.engine.base.model.Property;
import com.gurella.studio.editor.model.ComplexPropertyEditor;
import com.gurella.studio.editor.model.ModelPropertiesContainer;

public class ComplexObjectPropertyEditor<T> extends ComplexPropertyEditor<T> {
	private ModelPropertiesContainer<T> propertiesContainer;

	public ComplexObjectPropertyEditor(ModelPropertiesContainer<?> parent, Property<T> property) {
		super(parent, property);
	}

	@Override
	protected void buildUi() {
		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		setLayout(layout);
		propertiesContainer = new ModelPropertiesContainer<T>(getGurellaEditor(), this,
				property.getValue(propertiesContainer.getModelInstance()));
		propertiesContainer.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false));
	}

	@Override
	protected void present(Object modelInstance) {
		// TODO Auto-generated method stub
	}
}
