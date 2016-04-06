package com.gurella.studio.editor.model.property;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.gurella.engine.base.model.Property;
import com.gurella.engine.utils.Values;
import com.gurella.studio.editor.model.ComplexPropertyEditor;

public class ReflectionPropertyEditor<T> extends ComplexPropertyEditor<T> {
	private ModelPropertiesContainer<T> objectPropertiesContainer;

	public ReflectionPropertyEditor(Composite parent, ModelPropertiesContainer<?> propertiesContainer, Property<T> property) {
		super(parent, propertiesContainer, property);
	}

	@Override
	protected void buildUi() {
		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		setLayout(layout);
		T value = getValue();
		objectPropertiesContainer = value == null
				? new ModelPropertiesContainer<T>(getGurellaEditor(), this, Values.cast(new Object()))
				: new ModelPropertiesContainer<T>(getGurellaEditor(), this, value);
		objectPropertiesContainer.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
	}

	@Override
	public void present(Object modelInstance) {
		// TODO Auto-generated method stub
	}
}
