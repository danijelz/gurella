package com.gurella.studio.editor.ui.property;

import static com.gurella.studio.GurellaStudioPlugin.createFont;
import static org.eclipse.jdt.ui.IJavaElementSearchConstants.CONSIDER_CLASSES;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.gurella.engine.metatype.CopyContext;
import com.gurella.engine.metatype.MetaType;
import com.gurella.engine.metatype.MetaTypes;
import com.gurella.engine.metatype.Property;
import com.gurella.engine.metatype.ReflectionProperty;
import com.gurella.engine.utils.Reflection;
import com.gurella.engine.utils.Values;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.utils.Try;
import com.gurella.studio.editor.utils.UiUtils;

public class CollectionPropertyEditor<T> extends CompositePropertyEditor<Collection<T>> {
	private List<PropertyEditor<?>> itemEditors = new ArrayList<>();
	private Class<Object> componentType;

	public CollectionPropertyEditor(Composite parent, PropertyEditorContext<?, Collection<T>> context) {
		super(parent, context);

		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		content.setLayout(layout);

		buildUi();

		addMenuItem("Add item", () -> addItem());

		if (!isFinalValue()) {
			addMenuItem("Select type", () -> selectType());

			if (Reflection.getDeclaredConstructorSilently(context.getPropertyType()) != null) {
				addMenuItem("New instance", () -> newTypeInstance());
			}

			addMenuItem("Set null", () -> setNull());
		}
	}

	private void buildUi() {
		FormToolkit toolkit = getToolkit();
		Collection<T> values = getValue();

		if (values == null || values.isEmpty()) {
			Label label = toolkit.createLabel(content, values == null ? "null" : "empty");
			label.setAlignment(SWT.CENTER);
			label.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 1));
			label.addListener(SWT.MouseUp, (e) -> showMenu());
		} else {
			Class<Object> componentType = getComponentType();
			MetaType<Object> itemMetaType = MetaTypes.getMetaType(componentType);
			Iterator<T> iter = values.iterator();
			IntStream.range(0, values.size()).forEach(i -> addItemEditor(itemMetaType, iter.next(), i));
		}
	}

	private int addItemEditor(MetaType<Object> itemMetaType, T item, int index) {
		Label label = getToolkit().createLabel(content, Integer.toString(index) + ".");
		label.setAlignment(SWT.RIGHT);
		label.setFont(createFont(FontDescriptor.createFrom(label.getFont()).setStyle(SWT.BOLD)));
		label.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));

		Property<Object> property = Values.cast(getProperty());
		ItemContext<Object, Object> itemContext = new ItemContext<>(context, itemMetaType, item, property, index);
		Class<Object> type = item == null ? itemMetaType.getType() : Values.cast(item.getClass());
		PropertyEditor<Object> editor = PropertyEditorFactory.createEditor(content, itemContext, type);
		GridData layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		editor.getBody().setLayoutData(layoutData);

		addEditorMenus(editor, index);
		itemEditors.add(editor);
		return index;
	}

	private void setNull() {
		setValue(null);
		rebuildUi();
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
			return Values.cast(classLoader.loadClass(Signature.toString(Signature.getTypeErasure(typeArgument))));
		case Signature.ARRAY_TYPE_SIGNATURE:
			return Values.cast(classLoader.loadClass(typeArgument));
		default:
			return Object.class;
		}
	}

	private void addEditorMenus(PropertyEditor<Object> editor, int i) {
		editor.addMenuItem("Remove item", () -> removeItem(i));
	}

	private void removeItem(int i) {
		Collection<T> oldValue = getValue();
		PropertyEditor<?> itemEditor = itemEditors.get(i);
		Collection<T> newValue = CopyContext.copyObject(oldValue);
		newValue.remove(itemEditor.getBean());
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

	private void addItem() {
		Collection<T> oldValue = getValue();
		Collection<T> newValue = CopyContext.copyObject(oldValue);
		newValue.add(null);
		setValue(newValue);
		rebuildUi();
	}

	private void rebuildUi() {
		UiUtils.disposeChildren(content);
		buildUi();
		content.layout(true, true);
		content.redraw();
	}

	private void newTypeInstance() {
		try {
			ClassLoader classLoader = context.sceneContext.classLoader;
			Collection<T> value = Values.cast(classLoader.loadClass(context.getPropertyType().getName()).newInstance());
			setValue(value);
			rebuildUi();
		} catch (Exception e) {
			String message = "Error occurred while creating value";
			GurellaStudioPlugin.showError(e, message);
		}
	}

	private void selectType() {
		try {
			IType selectedType = findType();
			if (selectedType != null) {
				createType(selectedType);
			}
		} catch (Exception e) {
			String message = "Error occurred while creating value";
			GurellaStudioPlugin.showError(e, message);
		}
	}

	private void createType(IType selectedType) throws Exception {
		ClassLoader classLoader = context.sceneContext.classLoader;
		Collection<T> value = Values.cast(classLoader.loadClass(selectedType.getFullyQualifiedName()).newInstance());
		setValue(value);
		rebuildUi();
	}

	private IType findType() throws JavaModelException {
		SelectionDialog dialog = createJavaSearchDialog();
		if (dialog.open() != IDialogConstants.OK_ID) {
			return null;
		}

		Object[] types = dialog.getResult();
		return types == null || types.length == 0 ? null : (IType) types[0];
	}

	private SelectionDialog createJavaSearchDialog() throws JavaModelException {
		IJavaSearchScope scope = getSearchScope();
		Shell shell = content.getShell();
		ProgressMonitorDialog monitor = new ProgressMonitorDialog(shell);
		return JavaUI.createTypeDialog(shell, monitor, scope, CONSIDER_CLASSES, false);
	}

	private IJavaSearchScope getSearchScope() throws JavaModelException {
		IJavaProject javaProject = context.sceneContext.javaProject;
		Class<Collection<T>> type = context.getPropertyType();
		return SearchEngine.createHierarchyScope(javaProject.findType(type.getName()));
	}

	@Override
	protected void updateValue(Collection<T> value) {
		rebuildUi();
	}

	private static class ItemContext<M, P> extends PropertyEditorContext<M, P> {
		private Collection<P> collection;
		private int index;

		public ItemContext(PropertyEditorContext<?, ?> parent, MetaType<M> metaType, M bean, Property<P> property,
				int index) {
			super(parent, metaType, bean, property);
			this.index = index;
			collection = Values.cast(parent.getValue());
			valueGetter = this::getItemValue;
			valueSetter = this::setItemValue;
		}

		protected P getItemValue() {
			if (collection instanceof List) {
				return ((List<P>) collection).get(index);
			} else {
				Iterator<P> iter = collection.iterator();
				IntStream.range(0, index).forEach(i -> iter.next());
				return iter.next();
			}
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

		protected void setItemValue(P newValue) {
			PropertyEditorContext<?, Object> parentContext = Values.cast(parent);
			Collection<P> values = Values.cast(parentContext.getValue());

			if (collection instanceof List) {
				P oldValue = ((List<P>) collection).set(index, newValue);
				parent.propertyValueChanged(parentContext.property, oldValue, newValue);
			} else {
				Iterator<P> iter = collection.iterator();
				IntStream.range(0, index).forEach(i -> iter.next());
				P oldValue = iter.next();
				iter.remove();
				values.add(newValue);
				parent.propertyValueChanged(parentContext.property, oldValue, newValue);
			}
		}
	}
}