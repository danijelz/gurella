package com.gurella.studio.editor.model.property;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.badlogic.gdx.utils.Array;

public class ArrayPropertyEditor<P> extends ComplexPropertyEditor<P> {
	private Button addButton;
	private Array<PropertyEditor<?>> itemEditors = new Array<>();

	public ArrayPropertyEditor(Composite parent, PropertyEditorContext<?, P> context) {
		super(parent, context);
	}

	@Override
	protected void buildUi() {
		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		setLayout(layout);
		P values = getValue();
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
