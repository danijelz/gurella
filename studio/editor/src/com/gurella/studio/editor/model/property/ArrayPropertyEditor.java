package com.gurella.studio.editor.model.property;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.Models;
import com.gurella.engine.base.model.Property;
import com.gurella.engine.utils.Values;
import com.gurella.studio.editor.model.PropertyEditorFactory;

public class ArrayPropertyEditor<P> extends ComplexPropertyEditor<P> {
	private List<PropertyEditor<?>> itemEditors = new ArrayList<>();

	public ArrayPropertyEditor(Composite parent, PropertyEditorContext<?, P> context) {
		super(parent, context);

		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		body.setLayout(layout);
		P values = getValue();
		if (values == null) {
			return;
		}

		Property<Object> property = Values.cast(getProperty());
		Class<Object> componentType = Values.cast(property.getType().getComponentType());
		Model<Object> model = Models.getModel(componentType);

		for (int i = 0, n = Array.getLength(values); i < n; i++) {
			Object item = Array.get(values, i);
			ArrayItemPropertyEditorContext<Object, Object> itemContext = new ArrayItemPropertyEditorContext<>(context,
					model, item, property, i);
			itemEditors.add(PropertyEditorFactory.createEditor(body, itemContext, componentType));
		}
	}

	private static class ArrayItemPropertyEditorContext<M, P> extends PropertyEditorContext<M, P> {
		private Object parentModelInstance;
		private int index;

		public ArrayItemPropertyEditorContext(PropertyEditorContext<?, ?> parent, Model<M> model, M modelInstance,
				Property<P> property, int index) {
			super(parent, model, modelInstance, property);
			parentModelInstance = parent.getValue();
			this.index = index;
			valueExtractor = this::getItemValue;
			valueUpdater = this::setItemValue;
		}

		protected P getItemValue() {
			return Values.cast(Array.get(parentModelInstance, index));
		}

		protected void setItemValue(P newValue) {
			@SuppressWarnings("unchecked")
			PropertyEditorContext<?, Object> parentContext = (PropertyEditorContext<?, Object>) parent;
			Object value = parentContext.getValue();
			int length = Array.getLength(value);
			Object oldValue = Array.newInstance(value.getClass().getComponentType(), length);
			System.arraycopy(value, 0, oldValue, 0, length);
			Array.set(parentModelInstance, index, newValue);
			parent.propertyValueChanged(parentContext.property, oldValue, getValue());
		}
	}
}
