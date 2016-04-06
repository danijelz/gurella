package com.gurella.studio.editor.model.property;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.badlogic.gdx.utils.Array;
import com.gurella.engine.base.model.Property;

public class ArrayPropertyEditor<T> extends ComplexPropertyEditor<T> {
	private Button addButton;
	private Array<PropertyEditor<T>> itemEditors = new Array<>();

	public ArrayPropertyEditor(Composite parent, ModelPropertiesContainer<?> propertiesContainer, Property<T> property,
			Object modelInstance) {
		super(parent, propertiesContainer, property, modelInstance);
	}

	@Override
	protected void buildUi() {
		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		setLayout(layout);
		property.getType().getComponentType();
		T values = getValue();
		if (values == null) {
			return;
		}

		for (int i = 0, n = java.lang.reflect.Array.getLength(values); i < n; i++) {
			// PropertyEditorFactory.createEditor(parent, property);
		}
		// TODO Auto-generated method stub
	}

	@Override
	public void present(Object modelInstance) {
		// TODO Auto-generated method stub

	}

}
