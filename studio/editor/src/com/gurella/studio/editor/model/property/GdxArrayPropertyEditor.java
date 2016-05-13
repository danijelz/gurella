package com.gurella.studio.editor.model.property;

import static com.gurella.studio.GurellaStudioPlugin.createFont;
import static org.eclipse.jdt.ui.IJavaElementSearchConstants.CONSIDER_CLASSES;

import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.ErrorDialog;
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

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.reflect.Field;
import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.Models;
import com.gurella.engine.base.model.Property;
import com.gurella.engine.base.model.ReflectionProperty;
import com.gurella.engine.utils.Reflection;
import com.gurella.engine.utils.Values;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.model.PropertyEditorFactory;

public class GdxArrayPropertyEditor<T> extends ComplexPropertyEditor<Array<T>> {
	private List<PropertyEditor<?>> itemEditors = new ArrayList<>();
	private Class<Object> componentType;

	public GdxArrayPropertyEditor(Composite parent, PropertyEditorContext<?, Array<T>> context) {
		super(parent, context);

		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		body.setLayout(layout);

		buildUi();

		addMenuItem("Add item", () -> addItem());

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
			Label label = toolkit.createLabel(body, values == null ? "null" : "empty");
			label.setAlignment(SWT.CENTER);
			label.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 1));
			label.addListener(SWT.MouseUp, (e) -> showMenu());
		} else {
			Class<Object> componentType = getComponentType();
			Model<Object> itemModel = Models.getModel(componentType);
			IntStream.range(0, values.size).forEach(i -> addItemEditor(itemModel, values.get(i), i));
		}

		body.layout();
	}

	private void addItemEditor(Model<Object> itemModel, T item, int index) {
		Label label = getToolkit().createLabel(body, Integer.toString(index) + ".");
		label.setAlignment(SWT.RIGHT);
		label.setFont(createFont(FontDescriptor.createFrom(label.getFont()).setStyle(SWT.BOLD)));
		label.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));

		Property<Object> property = Values.cast(getProperty());
		ItemContext<Object, Object> itemContext = new ItemContext<>(context, itemModel, item, property, index);
		PropertyEditor<Object> editor = PropertyEditorFactory.createEditor(body, itemContext, itemModel.getType());
		GridData layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		editor.getComposite().setLayoutData(layoutData);

		addEditorMenus(editor, index);
		itemEditors.add(editor);
	}

	private void setNull() {
		setValue(null);
		rebuildUi();
	}

	private Class<Object> getComponentType() {
		if (componentType == null) {
			componentType = resolveComponentType();
		}
		return componentType;
	}

	private Class<Object> resolveComponentType() {
		try {
			return resolveComponentTypeSafely();
		} catch (Exception e) {
			e.printStackTrace();
			return Object.class;
		}
	}

	private Class<Object> resolveComponentTypeSafely() throws JavaModelException, ClassNotFoundException {
		Property<?> property = context.property;
		IJavaProject javaProject = context.sceneEditorContext.javaProject;
		String typeName = context.modelInstance.getClass().getName();
		IType type = javaProject.findType(typeName);
		final String propertyName = property.getName();
		IField field = type.getField(propertyName);
		String typeSignature = field.getTypeSignature();
		String[] typeArguments = Signature.getTypeArguments(typeSignature);
		if (typeArguments == null || typeArguments.length != 1) {
			return Object.class;
		}

		URLClassLoader classLoader = context.sceneEditorContext.classLoader;
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
		Array<T> values = getValue();
		values.removeIndex(i);
		rebuildUi();
	}

	private boolean isFinalValue() {
		Property<?> property = context.property;
		if (property instanceof ReflectionProperty) {
			ReflectionProperty<?> reflectionProperty = (ReflectionProperty<?>) property;
			Field field = reflectionProperty.getField();
			return field.isFinal();
		} else {
			return true;
		}
	}

	private void addItem() {
		Array<T> values = getValue();
		if (values != null) {
			values.add(null);
			rebuildUi();
		}
	}

	private void rebuildUi() {
		Arrays.stream(body.getChildren()).forEach(c -> c.dispose());
		buildUi();
	}

	private void newTypeInstance() {
		try {
			URLClassLoader classLoader = context.sceneEditorContext.classLoader;
			Array<T> value = Values.cast(classLoader.loadClass(context.getPropertyType().getName()).newInstance());
			setValue(value);
			rebuildUi();
		} catch (Exception e) {
			String message = "Error occurred while creating value";
			IStatus status = GurellaStudioPlugin.log(e, message);
			ErrorDialog.openError(body.getShell(), message, e.getLocalizedMessage(), status);
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
			IStatus status = GurellaStudioPlugin.log(e, message);
			ErrorDialog.openError(body.getShell(), message, e.getLocalizedMessage(), status);
		}
	}

	private void createType(IType selectedType) throws Exception {
		URLClassLoader classLoader = context.sceneEditorContext.classLoader;
		Array<T> value = Values.cast(classLoader.loadClass(selectedType.getFullyQualifiedName()).newInstance());
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
		Shell shell = body.getShell();
		ProgressMonitorDialog monitor = new ProgressMonitorDialog(shell);
		return JavaUI.createTypeDialog(shell, monitor, scope, CONSIDER_CLASSES, false);
	}

	private IJavaSearchScope getSearchScope() throws JavaModelException {
		IJavaProject javaProject = context.sceneEditorContext.javaProject;
		Class<Array<T>> type = context.getPropertyType();
		return SearchEngine.createHierarchyScope(javaProject.findType(type.getName()));
	}

	private static class ItemContext<M, P> extends PropertyEditorContext<M, P> {
		private Array<P> array;
		private int index;

		public ItemContext(PropertyEditorContext<?, ?> parent, Model<M> model, M modelInstance, Property<P> property,
				int index) {
			super(parent, model, modelInstance, property);
			this.index = index;
			array = Values.cast(parent.getValue());
			valueExtractor = this::getItemValue;
			valueUpdater = this::setItemValue;
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
			return Values.cast(model.getType());
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
