package com.gurella.studio.editor.utils;

import static com.gurella.studio.GurellaStudioPlugin.showError;
import static org.eclipse.jdt.ui.IJavaElementSearchConstants.CONSIDER_CLASSES;

import java.lang.reflect.Modifier;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.SelectionDialog;

import com.gurella.studio.editor.SceneEditorContext;

public class TypeSelectionUtils {
	private TypeSelectionUtils() {
	}

	public static <T> Class<? extends T> selectType(SceneEditorContext context, Class<T> baseType) {
		try {
			IType selectedType = findType(context, baseType);
			return selectedType == null ? null : loadType(context, selectedType);
		} catch (Exception e) {
			showError(e, "Error occurred while loading class");
			return null;
		}
	}

	private static IType findType(SceneEditorContext context, Class<?> baseType) throws JavaModelException {
		SelectionDialog dialog = createSearchDialog(context, baseType);
		if (dialog.open() != IDialogConstants.OK_ID) {
			return null;
		}

		Object[] types = dialog.getResult();
		return types == null || types.length == 0 ? null : (IType) types[0];
	}

	private static SelectionDialog createSearchDialog(SceneEditorContext context, Class<?> baseType)
			throws JavaModelException {
		IJavaSearchScope scope = createSearchScope(context, baseType);
		Shell shell = UiUtils.getDisplay().getActiveShell();
		ProgressMonitorDialog monitor = new ProgressMonitorDialog(shell);
		return JavaUI.createTypeDialog(shell, monitor, scope, CONSIDER_CLASSES, false);
	}

	private static IJavaSearchScope createSearchScope(SceneEditorContext context, Class<?> baseType)
			throws JavaModelException {
		IJavaProject javaProject = context.javaProject;
		if (baseType == Object.class) {
			return SearchEngine.createWorkspaceScope();
		} else {
			IType type = javaProject.findType(baseType.getName());
			boolean excludeBaseType = Modifier.isAbstract(baseType.getModifiers()) || baseType.isInterface();
			return SearchEngine.createStrictHierarchyScope(javaProject, type, true, excludeBaseType, null);
		}
	}

	private static <T> Class<T> loadType(SceneEditorContext context, IType selectedType) throws Exception {
		ClassLoader classLoader = context.classLoader;
		@SuppressWarnings("unchecked")
		Class<T> result = (Class<T>) classLoader.loadClass(selectedType.getFullyQualifiedName());
		return result;
	}
}
