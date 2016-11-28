package com.gurella.studio.editor.utils;

import static org.eclipse.jface.dialogs.MessageDialog.openQuestion;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import com.gurella.engine.asset.AssetType;
import com.gurella.engine.utils.Values;

public class FileDialogUtils {
	private FileDialogUtils() {
	}

	public static Optional<String> selectNewFileName(IFolder parent, String defaultName, String... extensions) {
		return selectNewFileName(parent.getLocation(), defaultName, extensions);
	}

	public static Optional<String> selectNewFileName(IFolder parent, String defaultName, AssetType type) {
		return selectNewFileName(parent.getLocation(), defaultName, type.extensions);
	}

	public static Optional<String> selectNewFileName(IPath parent, String defaultName, AssetType type) {
		return selectNewFileName(parent, defaultName, type.extensions);
	}

	public static Optional<String> selectNewFileName(IPath parent, String defaultName, String... extensions) {
		FileDialog dialog = new FileDialog(UiUtils.getActiveShell(), SWT.SAVE);
		dialog.setFilterPath(parent.toString());
		Function<? super String, ? extends String> prependDot = e -> e.indexOf('.') < 0 ? "*." + e : e;
		dialog.setFilterExtensions(Arrays.stream(extensions).map(prependDot).toArray(i -> new String[i]));
		String extension = extensions.length == 0 ? null : extensions[0];
		dialog.setFileName(suggestName(parent, defaultName, extension));
		return getPathSafely(dialog);
	}

	private static String suggestName(IPath parentPath, String defaultName, String extension) {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource parent = root.findMember(parentPath.makeRelativeTo(root.getLocation()));
		if (parent instanceof IFolder && parent.exists()) {
			return suggestName((IFolder) parent, defaultName, extension);
		} else {
			return defaultName;
		}
	}

	private static Optional<String> getPathSafely(FileDialog dialog) {
		String fileName = dialog.open();
		if (fileName == null) {
			return Optional.empty();
		}

		File file = new File(fileName);
		if (file.exists()) {
			Shell shell = dialog.getParent();
			String message = fileName + " already exists. Do you want to replace it?";
			return openQuestion(shell, "Confirm", message) ? Optional.of(fileName) : getPathSafely(dialog);
		} else {
			return Optional.of(fileName);
		}
	}

	public static Optional<String> enterNewFileName(IFolder parent, String defaultName, boolean suggestName,
			String extension) {
		String name = suggestName ? suggestName(parent, defaultName, extension) : defaultName;
		Shell shell = UiUtils.getActiveShell();
		IInputValidator validator = i -> validateNewFileName(parent, i);
		FileNameDialog dlg = new FileNameDialog(shell, "Name", "Enter name", name, validator);
		return dlg.open() == Window.OK ? Optional.of(dlg.getValue()) : Optional.empty();
	}

	private static String suggestName(IFolder parent, String defaultName, String extension) {
		int index = defaultName.lastIndexOf('.');
		String name;
		if (index > 0) {
			name = defaultName.substring(0, index);
		} else {
			name = defaultName;
		}

		String ext;
		if (Values.isNotBlank(extension)) {
			ext = extension;
		} else {
			ext = index > 0 && index < defaultName.length() - 1 ? defaultName.substring(index + 1) : null;
		}

		String composedName = composeName(name, ext);
		IResource member = parent.findMember(composedName);
		if (member == null || !member.exists()) {
			return composedName;
		}

		for (int i = 1; i < 1000; i++) {
			composedName = composeName(name + "-" + i, ext);
			member = parent.findMember(composedName);
			if (member == null || !member.exists()) {
				return composedName;
			}
		}

		return null;
	}

	private static String composeName(String name, String extension) {
		return name + (extension == null ? "" : '.' + extension);
	}

	private static String validateNewFileName(IFolder parent, String newFileName) {
		if (Values.isBlank(newFileName)) {
			return "Name must not be empty";
		}

		IResource member = parent.findMember(newFileName);
		if (member != null && member.exists()) {
			return "Resource with that name already exists";
		} else {
			return null;
		}
	}

	private static class FileNameDialog extends InputDialog {
		public FileNameDialog(Shell parentShell, String dialogTitle, String dialogMessage, String initialValue,
				IInputValidator validator) {
			super(parentShell, dialogTitle, dialogMessage, initialValue, validator);
		}

		@Override
		protected Control createContents(Composite parent) {
			Control control = super.createContents(parent);
			int index = getValue().lastIndexOf('.');
			if (index > 0) {
				getText().setSelection(0, index);
			}
			return control;
		}
	}
}
