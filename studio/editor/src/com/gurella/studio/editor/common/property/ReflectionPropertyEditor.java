package com.gurella.studio.editor.common.property;

import static org.eclipse.jdt.ui.IJavaElementSearchConstants.CONSIDER_CLASSES;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.gurella.engine.base.model.DefaultModels.SimpleModel;
import com.gurella.engine.base.model.Models;
import com.gurella.engine.utils.Reflection;
import com.gurella.engine.utils.Values;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.common.model.MetaModelEditor;
import com.gurella.studio.editor.common.model.ModelEditorFactory;
import com.gurella.studio.editor.utils.UiUtils;

public class ReflectionPropertyEditor<P> extends CompositePropertyEditor<P> {
	public ReflectionPropertyEditor(Composite parent, PropertyEditorContext<?, P> context) {
		super(parent, context);

		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		content.setLayout(layout);

		buildUi();

		if (!context.isFixedValue()) {
			addMenuItem("Select type", () -> selectType());

			Class<P> type = context.getPropertyType();
			if (!Modifier.isAbstract(type.getModifiers()) && Reflection.getDeclaredConstructorSilently(type) != null) {
				addMenuItem("New " + type.getSimpleName(), () -> newTypeInstance());
			}

			if (context.isNullable()) {
				addMenuItem("Set null", () -> setNull());
			}
		}
	}

	private void newTypeInstance() {
		try {
			ClassLoader classLoader = context.sceneEditorContext.classLoader;
			Class<?> valueClass = classLoader.loadClass(context.getPropertyType().getName());
			Constructor<?> constructor = valueClass.getDeclaredConstructor(new Class[0]);
			constructor.setAccessible(true);
			P value = Values.cast(constructor.newInstance(new Object[0]));
			setValue(value);
			rebuildUi();
		} catch (Exception e) {
			String message = "Error occurred while creating value";
			GurellaStudioPlugin.showError(e, message);
		}
	}

	private void buildUi() {
		FormToolkit toolkit = getToolkit();
		P value = getValue();
		if (value == null) {
			Label label = toolkit.createLabel(content, "null (" + context.getPropertyType().getSimpleName() + ")");
			label.setAlignment(SWT.CENTER);
			label.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
			label.addListener(SWT.MouseUp, (e) -> showMenu());
		} else if (Models.getModel(value.getClass()) instanceof SimpleModel) {
			PropertyEditorContext<Object, P> casted = Values.cast(context);
			PropertyEditorContext<Object, P> child = new PropertyEditorContext<>(casted, casted.property);
			PropertyEditor<P> editor = PropertyEditorFactory.createEditor(content, child,
					Values.cast(value.getClass()));
			GridData layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
			editor.getBody().setLayoutData(layoutData);
		} else {
			MetaModelEditor<P> modelEditor = ModelEditorFactory.createEditor(content, context, value);
			modelEditor.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		}

		content.layout();
	}

	private void rebuildUi() {
		UiUtils.disposeChildren(content);
		buildUi();
	}

	private void setNull() {
		setValue(null);
		rebuildUi();
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
		ClassLoader classLoader = context.sceneEditorContext.classLoader;
		P value = Values.cast(classLoader.loadClass(selectedType.getFullyQualifiedName()).newInstance());
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
		IJavaProject javaProject = context.sceneEditorContext.javaProject;
		Class<P> type = context.getPropertyType();

		if (type == Object.class) {
			return SearchEngine.createWorkspaceScope();
		} else {
			return SearchEngine.createHierarchyScope(javaProject.findType(type.getName()));
		}
	}

	@Override
	protected void updateValue(P value) {
		rebuildUi();
	}
}
