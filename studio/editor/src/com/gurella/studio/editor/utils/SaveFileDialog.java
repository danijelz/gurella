package com.gurella.studio.editor.utils;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;

public class SaveFileDialog {
	private SaveFileDialog() {
	}

	public static String getPath(IPath location, String extension, String name) {
		FileDialog dialog = new FileDialog(UiUtils.getActiveShell(), SWT.SAVE);
		dialog.setFilterPath(location.toString());
		dialog.setFilterExtensions(new String[] { extension });
		dialog.setFileName(name);
		return getPathSafely(dialog);
	}

	private static String getPathSafely(FileDialog dialog) {
		String fileName = dialog.open();
		if (fileName == null) {
			return null;
		}

		File file = new File(fileName);
		if (file.exists()) {
			MessageBox mb = new MessageBox(dialog.getParent(), SWT.ICON_WARNING | SWT.YES | SWT.NO);
			mb.setMessage(fileName + " already exists. Do you want to replace it?");
			return mb.open() == SWT.YES ? fileName : getPathSafely(dialog);
		} else {
			return fileName;
		}
	}
}
