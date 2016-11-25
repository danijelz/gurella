package com.gurella.studio.editor.utils;

import java.io.File;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import com.gurella.engine.asset.AssetType;
import com.gurella.engine.utils.Values;

public class FileDialogUtils {
	private FileDialogUtils() {
	}

	public static String selectNewFileName(IFolder parent, String suggestedName, String... extensions) {
		return selectNewFileName(parent.getLocation(), suggestedName, extensions);
	}

	public static String selectNewFileName(IFolder parent, String suggestedName, AssetType type) {
		return selectNewFileName(parent.getLocation(), suggestedName, type.extensions);
	}

	public static String selectNewFileName(IPath parent, String suggestedName, AssetType type) {
		return selectNewFileName(parent, suggestedName, type.extensions);
	}

	public static String selectNewFileName(IPath parent, String suggestedName, String... extensions) {
		FileDialog dialog = new FileDialog(UiUtils.getActiveShell(), SWT.SAVE);
		dialog.setFilterPath(parent.toString());
		dialog.setFilterExtensions(extensions);
		dialog.setFileName(suggestName(parent, suggestedName));
		return getPathSafely(dialog);
	}

	private static String suggestName(IPath parentPath, String suggestedName) {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource parent = root.findMember(parentPath.makeRelativeTo(root.getLocation()));
		if (parent instanceof IFolder && parent.exists()) {
			return suggestName((IFolder) parent, suggestedName);
		} else {
			return suggestedName;
		}
	}

	private static String getPathSafely(FileDialog dialog) {
		String fileName = dialog.open();
		if (fileName == null) {
			return null;
		}

		File file = new File(fileName);
		if (file.exists()) {
			Shell shell = dialog.getParent();
			String message = fileName + " already exists. Do you want to replace it?";
			return MessageDialog.openQuestion(shell, "Confirm", message) ? fileName : getPathSafely(dialog);
		} else {
			return fileName;
		}
	}

	public static String enterNewFileName(IFolder parent, String suggestedName, String extension) {
		String name = suggestName(parent, suggestedName);
		InputDialog dlg = new InputDialog(UiUtils.getActiveShell(), "Name", "Enter name", name,
				i -> validateNewFileName(parent, i));
		return dlg.open() == Window.OK ? dlg.getValue() : null;
	}

	private static String suggestName(IFolder parent, String suggestedName) {
		IResource member = parent.findMember(suggestedName);
		if (member == null || !member.exists()) {
			return suggestedName;
		}

		for (int i = 0; i < 1000; i++) {
			String name = suggestedName + "-" + i;
			member = parent.findMember(name);
			if (member == null || !member.exists()) {
				return name;
			}
		}

		return null;
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
}
