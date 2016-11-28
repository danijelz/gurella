package com.gurella.studio.editor.common.property;

import static com.gurella.studio.GurellaStudioPlugin.createFont;

import java.lang.reflect.Array;
import java.util.ArrayList;
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

import com.gurella.engine.metatype.MetaType;
import com.gurella.engine.metatype.MetaTypes;
import com.gurella.engine.metatype.Property;
import com.gurella.engine.metatype.ReflectionProperty;
import com.gurella.engine.utils.Values;
import com.gurella.studio.editor.utils.Try;
import com.gurella.studio.editor.utils.UiUtils;

public class ArrayPropertyEditor<P> extends CompositePropertyEditor<P> {
	private List<PropertyEditor<?>> itemEditors = new ArrayList<>();

	public ArrayPropertyEditor(Composite parent, PropertyEditorContext<?, P> context) {
		super(parent, context);

		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		content.setLayout(layout);

		buildUi();

		if (!isFinalValue()) {
			addMenuItem("Add item", () -> addItem());
			addMenuItem("Resize", () -> resize());
			addMenuItem("Set null", () -> setNull());
		}
	}

	private void buildUi() {
		FormToolkit toolkit = getToolkit();
		P values = getValue();

		if (values == null || Array.getLength(values) == 0) {
			Label label = toolkit.createLabel(content, values == null ? "null" : "empty");
			label.setAlignment(SWT.CENTER);
			label.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 1));
			label.addListener(SWT.MouseUp, (e) -> showMenu());
		} else {
			Property<Object> property = Values.cast(getProperty());
			Class<Object> componentType = getComponentType();
			MetaType<Object> metaType = MetaTypes.getMetaType(componentType);

			for (int i = 0, n = Array.getLength(values); i < n; i++) {
				Label label = toolkit.createLabel(content, Integer.toString(i) + ".");
				label.setAlignment(SWT.RIGHT);
				//TODO createFont
				label.setFont(createFont(FontDescriptor.createFrom(label.getFont()).setStyle(SWT.BOLD)));
				label.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));

				Object item = Array.get(values, i);
				ItemContext<Object, Object> itemContext = new ItemContext<>(context, metaType, item, property, i);
				Class<Object> type = item == null ? componentType : Values.cast(item.getClass());
				PropertyEditor<Object> editor = PropertyEditorFactory.createEditor(content, itemContext, type);
				GridData layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
				editor.getBody().setLayoutData(layoutData);

				label.addListener(SWT.MouseUp, e -> editor.showMenu());

				addEditorMenus(editor, i);
				itemEditors.add(editor);
			}
		}
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
		InputDialog dlg = new InputDialog(content.getShell(), "Resize", message, lengthStr, this::isValid);
		if (dlg.open() == Window.OK) {
			int newSize = Integer.parseInt(dlg.getValue());
			setValue(resizeValues(newSize - length));
			rebuildUi();
		}
	}

	public String isValid(String newText) {
		Try<Integer> failable = Try.ofFailable(() -> Integer.valueOf(newText));
		return failable.filter(i -> i.intValue() >= 0).isSuccess() ? null : "invalid length";
	}

	private Class<Object> getComponentType() {
		return Values.cast(context.getPropertyType().getComponentType());
	}

	private void addEditorMenus(PropertyEditor<Object> editor, int i) {
		if (!context.isFixedValue()) {
			editor.addMenuItem("Remove item", () -> removeItem(i));
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

	private boolean isFinalValue() {
		Property<P> property = context.property;
		if (property instanceof ReflectionProperty) {
			ReflectionProperty<P> reflectionProperty = (ReflectionProperty<P>) property;
			return reflectionProperty.isFinal();
		} else {
			return true;
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
		UiUtils.disposeChildren(content);
		buildUi();
		content.layout(true, true);
		content.redraw();
		UiUtils.reflow(content);
	}

	@Override
	protected void updateValue(P value) {
		rebuildUi();
	}

	private static class ItemContext<M, P> extends PropertyEditorContext<M, P> {
		private Object parentBean;
		private int index;

		public ItemContext(PropertyEditorContext<?, ?> parent, MetaType<M> metaType, M bean, Property<P> property,
				int index) {
			super(parent, metaType, bean, property);
			parentBean = parent.getValue();
			this.index = index;
			valueGetter = this::getItemValue;
			valueSetter = this::setItemValue;
		}

		protected P getItemValue() {
			return Values.cast(Array.get(parentBean, index));
		}

		@Override
		public boolean isNullable() {
			return !parentBean.getClass().getComponentType().isPrimitive();
		}

		@Override
		public boolean isFixedValue() {
			return false;
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
			Array.set(parentBean, index, newValue);
			parent.propertyValueChanged(parentContext.property, oldValue, getValue());
		}
	}
}
