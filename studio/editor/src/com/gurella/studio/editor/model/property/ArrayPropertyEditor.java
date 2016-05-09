package com.gurella.studio.editor.model.property;

import static com.gurella.studio.GurellaStudioPlugin.createFont;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.badlogic.gdx.utils.reflect.Field;
import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.Models;
import com.gurella.engine.base.model.Property;
import com.gurella.engine.base.model.ReflectionProperty;
import com.gurella.engine.utils.Values;
import com.gurella.studio.editor.model.PropertyEditorFactory;

public class ArrayPropertyEditor<P> extends ComplexPropertyEditor<P> {
	private List<PropertyEditor<?>> itemEditors = new ArrayList<>();

	public ArrayPropertyEditor(Composite parent, PropertyEditorContext<?, P> context) {
		super(parent, context);

		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		body.setLayout(layout);

		buildUi();

		if (isFinalValue()) {
			addMenuItem("Add item", () -> addItem());
			addMenuItem("Resize", () -> resize());
			addMenuItem("Set null", () -> setNull());
		}
	}

	private void buildUi() {
		FormToolkit toolkit = getToolkit();
		P values = getValue();

		if (values == null || Array.getLength(values) == 0) {
			Label label = toolkit.createLabel(body, values == null ? "null" : "empty");
			label.setAlignment(SWT.CENTER);
			label.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 1));
		} else {
			Property<Object> property = Values.cast(getProperty());
			Class<Object> componentType = getComponentType();
			Model<Object> model = Models.getModel(componentType);

			for (int i = 0, n = Array.getLength(values); i < n; i++) {
				Label label = toolkit.createLabel(body, Integer.toString(i) + ".");
				label.setAlignment(SWT.RIGHT);
				label.setFont(createFont(FontDescriptor.createFrom(label.getFont()).setStyle(SWT.BOLD)));
				label.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));

				Object item = Array.get(values, i);
				ItemContext<Object, Object> itemContext = new ItemContext<>(context, model, item, property, i);
				PropertyEditor<Object> editor = PropertyEditorFactory.createEditor(body, itemContext, componentType);
				GridData layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
				editor.getComposite().setLayoutData(layoutData);

				addEditorMenus(editor, i);
				itemEditors.add(editor);
			}
		}

		body.layout();
	}

	private void setNull() {
		setValue(null);
		rebuildUi();
	}

	private void resize() {
		P values = getValue();
		int length = values == null ? 0 : Array.getLength(values);
		String lengthStr = Integer.toString(length);
		String message = "Enter new array length";
		InputDialog dlg = new InputDialog(body.getShell(), "Resize", message, lengthStr, this::isValid);
		if (dlg.open() == Window.OK) {
			int newSize = Integer.parseInt(dlg.getValue());
			setValue(resizeValues(newSize - length));
			rebuildUi();
		}
	}

	public String isValid(String newText) {
		try {
			Integer.parseInt(newText);
			return null;
		} catch (Exception e) {
		}
		return "invalid length";
	}

	private Class<Object> getComponentType() {
		Property<Object> property = Values.cast(getProperty());
		return Values.cast(property.getType().getComponentType());
	}

	private void addEditorMenus(PropertyEditor<Object> editor, int i) {
		if (!context.property.isFinal()) {
			editor.addMenuItem("Remove", () -> removeItem(i));
		}

		if (!getComponentType().isPrimitive()) {
			editor.addMenuItem("Set null", () -> setNull(i));
		}
	}

	private void removeItem(int i) {
		Class<Object> componentType = getComponentType();
		P values = getValue();
		int length = Array.getLength(values);
		int newLength = length - 1;
		P newValues = Values.cast(Array.newInstance(componentType, newLength));
		if (newLength > 0) {
			if (i == 0) {
				System.arraycopy(values, 1, newValues, 0, newLength);
			} else if (i == length - 1) {
				System.arraycopy(values, 0, newValues, 0, newLength);
			} else {
				System.arraycopy(values, 0, newValues, 0, i);
				System.arraycopy(values, i + 1, newValues, i, newLength - i);
			}
		}

		setValue(newValues);
		rebuildUi();
	}

	private void setNull(int i) {
		P values = getValue();
		Array.set(values, i, null);
		rebuildUi();
	}

	private boolean isFinalValue() {
		Property<P> property = context.property;
		if (property instanceof ReflectionProperty) {
			ReflectionProperty<P> reflectionProperty = (ReflectionProperty<P>) property;
			Field field = reflectionProperty.getField();
			return !field.isFinal();
		} else {
			return false;
		}
	}

	private void addItem() {
		setValue(resizeValues(1));
		rebuildUi();
	}

	private P resizeValues(int additionalSize) {
		Class<Object> componentType = getComponentType();
		P values = getValue();
		if (values == null) {
			return Values.cast(Array.newInstance(componentType, additionalSize));
		} else {
			int length = Array.getLength(values);
			int newLength = length + additionalSize;
			P newValues = Values.cast(Array.newInstance(componentType, newLength));
			System.arraycopy(values, 0, newValues, 0, Math.min(length, newLength));
			return newValues;
		}
	}

	private void rebuildUi() {
		Arrays.stream(body.getChildren()).forEach(c -> c.dispose());
		buildUi();
	}

	private static class ItemContext<M, P> extends PropertyEditorContext<M, P> {
		private Object parentModelInstance;
		private int index;

		public ItemContext(PropertyEditorContext<?, ?> parent, Model<M> model, M modelInstance, Property<P> property,
				int index) {
			super(parent, model, modelInstance, property);
			parentModelInstance = parent.getValue();
			this.index = index;
			valueExtractor = this::getItemValue;
			valueUpdater = this::setItemValue;
		}

		protected P getItemValue() {
			return Values.cast(Array.get(parentModelInstance, index));
		}

		@Override
		public String getDescriptiveName() {
			return null;
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
