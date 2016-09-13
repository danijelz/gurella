package com.gurella.studio.editor.property;

import static org.eclipse.jdt.ui.IJavaElementSearchConstants.CONSIDER_CLASSES;

import java.lang.reflect.Modifier;
import java.net.URLClassLoader;
import java.util.Arrays;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.ErrorDialog;
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
import com.gurella.studio.editor.model.ModelEditorContext;
import com.gurella.studio.editor.model.DefaultMetaModelEditor;

public class ReflectionPropertyEditor<P> extends ComplexPropertyEditor<P> {
	public ReflectionPropertyEditor(Composite parent, PropertyEditorContext<?, P> context) {
		super(parent, context);

		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		body.setLayout(layout);

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
			URLClassLoader classLoader = context.sceneEditorContext.classLoader;
			P value = Values.cast(classLoader.loadClass(context.getPropertyType().getName()).newInstance());
			setValue(value);
			rebuildUi();
		} catch (Exception e) {
			String message = "Error occurred while creating value";
			IStatus status = GurellaStudioPlugin.log(e, message);
			ErrorDialog.openError(body.getShell(), message, e.getLocalizedMessage(), status);
		}
	}

	private void buildUi() {
		FormToolkit toolkit = getToolkit();
		P value = getValue();
		if (value == null) {
			Label label = toolkit.createLabel(body, "null");
			label.setAlignment(SWT.CENTER);
			label.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
			label.addListener(SWT.MouseUp, (e) -> showMenu());
		} else if (Models.getModel(value.getClass()) instanceof SimpleModel) {
			PropertyEditorContext<Object, P> casted = Values.cast(context);
			PropertyEditorContext<Object, P> child = new PropertyEditorContext<>(casted, casted.property);
			PropertyEditor<P> editor = PropertyEditorFactory.createEditor(body, child, Values.cast(value.getClass()));
			GridData layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
			editor.getComposite().setLayoutData(layoutData);
		} else {
			ModelEditorContext<P> child = new ModelEditorContext<>(context, value);
			DefaultMetaModelEditor<P> container = new DefaultMetaModelEditor<>(body, child);
			container.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		}

		body.layout();
	}

	private void rebuildUi() {
		Arrays.stream(body.getChildren()).forEach(c -> c.dispose());
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
			IStatus status = GurellaStudioPlugin.log(e, message);
			ErrorDialog.openError(body.getShell(), message, e.getLocalizedMessage(), status);
		}
	}

	private void createType(IType selectedType) throws Exception {
		URLClassLoader classLoader = context.sceneEditorContext.classLoader;
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
		Shell shell = body.getShell();
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
}
