package com.gurella.studio.editor.common.property;

import static com.gurella.studio.GurellaStudioPlugin.createFont;
import static com.gurella.studio.GurellaStudioPlugin.showError;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.badlogic.gdx.utils.Array;
import com.gurella.engine.metatype.CopyContext;
import com.gurella.engine.metatype.MetaType;
import com.gurella.engine.metatype.MetaTypes;
import com.gurella.engine.metatype.Property;
import com.gurella.engine.metatype.ReflectionProperty;
import com.gurella.engine.utils.Reflection;
import com.gurella.engine.utils.Values;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.utils.Try;
import com.gurella.studio.editor.utils.TypeSelectionUtils;
import com.gurella.studio.editor.utils.UiUtils;

public class GdxArrayPropertyEditor<T> extends CompositePropertyEditor<Array<T>> {
	private List<PropertyEditor<?>> itemEditors = new ArrayList<>();
	private Class<Object> componentType;

	public GdxArrayPropertyEditor(Composite parent, PropertyEditorContext<?, Array<T>> context) {
		super(parent, context);

		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		content.setLayout(layout);

		buildUi();

		addMenuItem("Add item", () -> addDefaultItem());
		addMenuItem("Resize", () -> resize());

		if (!isFinalValue()) {
			addMenuItem("Select type", () -> selectType());

			Class<Array<T>> type = context.getPropertyType();
			if (Reflection.getDeclaredConstructorSilently(type) != null) {
				addMenuItem("New " + type.getSimpleName(), () -> newTypeInstance());
			}

			if (context.isNullable()) {
				addMenuItem("Set null", () -> setNull());
			}
		}
	}

	private void buildUi() {
		FormToolkit toolkit = getToolkit();
		Array<T> values = getValue();

		if (values == null || values.size == 0) {
			Label label = toolkit.createLabel(content, values == null ? "null" : "empty");
			label.setAlignment(SWT.CENTER);
			label.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 1));
			label.addListener(SWT.MouseUp, (e) -> showMenu());
		} else {
			Class<Object> componentType = getComponentType();
			MetaType<Object> itemMetaType = MetaTypes.getMetaType(componentType);
			IntStream.range(0, values.size).forEach(i -> addItemEditor(itemMetaType, values.get(i), i));
		}
	}

	private void addItemEditor(MetaType<Object> itemMetaType, T item, int index) {
		Label label = getToolkit().createLabel(content, Integer.toString(index) + ".");
		label.setAlignment(SWT.RIGHT);
		label.setFont(createFont(FontDescriptor.createFrom(label.getFont()).setStyle(SWT.BOLD)));
		label.setLayoutData(new GridData(SWT.END, SWT.BEGINNING, false, false));

		Property<Object> property = Values.cast(getProperty());
		ItemContext<Object, Object> itemContext = new ItemContext<>(context, itemMetaType, item, property, index);
		Class<Object> type = item == null ? itemMetaType.getType() : Values.cast(item.getClass());
		PropertyEditor<Object> editor = PropertyEditorFactory.createEditor(content, itemContext, type);
		GridData layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		editor.getBody().setLayoutData(layoutData);

		editor.addMenuItem("Remove item", () -> removeItem(index));
		itemEditors.add(editor);
	}

	private void setNull() {
		setValue(null);
		rebuildUi();
	}

	private Class<Object> getComponentType() {
		if (componentType == null) {
			componentType = Try.ofFailable(() -> resolveComponentType()).orElse(Object.class);
		}
		return componentType;
	}

	private Class<Object> resolveComponentType() throws JavaModelException, ClassNotFoundException {
		ClassLoader classLoader = context.sceneContext.classLoader;
		List<String> genericTypes = PropertyEditorData.getGenericTypes(context);
		if (genericTypes.size() > 0) {
			try {
				return Values.cast(classLoader.loadClass(genericTypes.get(0)));
			} catch (Exception e) {
			}
		}

		Property<?> property = context.property;
		IJavaProject javaProject = context.sceneContext.javaProject;
		String typeName = context.bean.getClass().getName();
		IType type = javaProject.findType(typeName);
		final String propertyName = property.getName();
		IField field = type.getField(propertyName);
		String typeSignature = field.getTypeSignature();
		String[] typeArguments = Signature.getTypeArguments(typeSignature);
		if (typeArguments == null || typeArguments.length != 1) {
			return Object.class;
		}

		String typeArgument = typeArguments[0];

		switch (Signature.getTypeSignatureKind(typeArgument)) {
		case Signature.CLASS_TYPE_SIGNATURE:
			String componentTypeName = Signature.toString(Signature.getTypeErasure(typeArgument));
			return Values.cast(classLoader.loadClass(componentTypeName));
		case Signature.ARRAY_TYPE_SIGNATURE:
			return Values.cast(classLoader.loadClass(typeArgument));
		default:
			return Object.class;
		}
	}

	private void removeItem(int i) {
		Array<T> oldValue = getValue();
		Array<T> newValue = CopyContext.copyObject(oldValue);
		newValue.removeIndex(i);
		setValue(newValue);
		rebuildUi();
	}

	private boolean isFinalValue() {
		Property<?> property = context.property;
		if (property instanceof ReflectionProperty) {
			ReflectionProperty<?> reflectionProperty = (ReflectionProperty<?>) property;
			return reflectionProperty.isFinal();
		} else {
			return true;
		}
	}

	private void addDefaultItem() {
		Array<T> oldValue = getValue();
		Array<T> newValue = Optional.ofNullable(oldValue).map(CopyContext::copyObject).orElse(new Array<T>());
		newValue.add(null);
		setValue(newValue);
		rebuildUi();
	}

	private void resize() {
		Array<T> oldValue = getValue();
		int size = oldValue == null ? 0 : oldValue.size;
		String sizeStr = Integer.toString(size);
		String message = "Enter new array length";
		InputDialog dlg = new InputDialog(content.getShell(), "Resize", message, sizeStr, this::isValid);
		if (dlg.open() == Window.OK) {
			int newSize = Integer.parseInt(dlg.getValue());
			Array<T> newValue = Optional.ofNullable(oldValue).map(CopyContext::copyObject).orElse(new Array<T>());
			newValue.ensureCapacity(newSize - size);
			newValue.size = newSize;
			setValue(newValue);
			rebuildUi();
		}
	}

	public String isValid(String newText) {
		Try<Integer> failable = Try.ofFailable(() -> Integer.valueOf(newText));
		return failable.filter(i -> i.intValue() >= 0).isSuccess() ? null : "invalid length";
	}

	private void rebuildUi() {
		UiUtils.disposeChildren(content);
		buildUi();
		UiUtils.reflow(getContent());
	}

	private void newTypeInstance() {
		try {
			ClassLoader classLoader = context.sceneContext.classLoader;
			Array<T> value = Values.cast(classLoader.loadClass(context.getPropertyType().getName()).newInstance());
			setValue(value);
			rebuildUi();
		} catch (Exception e) {
			String message = "Error occurred while creating value";
			GurellaStudioPlugin.showError(e, message);
		}
	}

	private void selectType() {
		try {
			selectTypeSafely();
		} catch (Exception e) {
			showError(e, "Error occurred while creating value");
		}
	}

	private void selectTypeSafely() throws InstantiationException, IllegalAccessException {
		Class<Array<T>> propertyType = context.getPropertyType();
		Class<? extends Array<T>> selected = TypeSelectionUtils.selectType(context.sceneContext, propertyType);
		if (selected != null) {
			Array<T> value = selected.newInstance();
			setValue(value);
			rebuildUi();
		}
	}

	@Override
	protected void updateValue(Array<T> value) {
		rebuildUi();
	}

	private static class ItemContext<M, P> extends PropertyEditorContext<M, P> {
		private Array<P> array;
		private int index;

		public ItemContext(PropertyEditorContext<?, ?> parent, MetaType<M> metaType, M bean, Property<P> property,
				int index) {
			super(parent, metaType, bean, property);
			this.index = index;
			valueGetter = this::getItemValue;
			valueSetter = this::setItemValue;
			array = Values.cast(parent.getValue());
		}

		protected P getItemValue() {
			return array.get(index);
		}

		@Override
		public boolean isNullable() {
			return false;
		}

		@Override
		public boolean isFixedValue() {
			return false;
		}

		@Override
		public String getDescriptiveName() {
			return null;
		}

		@Override
		public Class<P> getPropertyType() {
			return Values.cast(metaType.getType());
		}

		protected void setItemValue(P newValue) {
			PropertyEditorContext<?, Object> parentContext = Values.cast(parent);
			Array<P> values = Values.cast(parentContext.getValue());
			P oldValue = values.get(index);
			values.set(index, newValue);
			parent.propertyValueChanged(parentContext.property, oldValue, newValue);
		}
	}
}
