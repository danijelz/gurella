package com.gurella.studio.editor.scene;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IPathEditorInput;

import com.gurella.studio.editor.GurellaEditor;

public class ProjectExplorerView extends SceneEditorView {
	private Tree graph;

	public ProjectExplorerView(GurellaEditor editor, int style) {
		super(editor, "Project explorer", null, style);

		setLayout(new GridLayout());
		editor.getToolkit().adapt(this);
		graph = editor.getToolkit().createTree(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		graph.setHeaderVisible(false);
		graph.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		IPath assetsRoot = getAssetsRoot();
		IResource member = editor.getProject().findMember("/");
		if (member instanceof IFolder) {

		}
	}

	private IPath getAssetsRoot() {
		IPathEditorInput pathEditorInput = (IPathEditorInput) editor.getEditorInput();
		IPath projectPath = editor.getProject().getLocation().makeAbsolute();
		IPath scenePath = pathEditorInput.getPath().removeLastSegments(1).makeAbsolute();
		IPath temp = scenePath;
		while (projectPath.isPrefixOf(temp)) {
			IResource member = editor.getProject().findMember(temp);
			if (member instanceof IFolder && isProjectAssetsFolder((IFolder) member)) {
				return temp;
			}
			temp = temp.removeLastSegments(1);
		}

		return scenePath;
	}

	private static boolean isProjectAssetsFolder(IFolder folder) {
		try {
			for (IResource member : folder.members()) {
				if (member instanceof IFile && "gprj".equals(((IFile) member).getFileExtension())) {
					return true;
				}
			}
			return false;
		} catch (CoreException e) {
			return false;
		}
	}
}
