package com.gurella.studio.launch;

import org.eclipse.core.resources.IFile;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;

import com.gurella.studio.editor.SceneEditor;

public class LaunchSceneShortcut implements ILaunchShortcut {
	@Override
	public void launch(ISelection selection, String mode) {
		if (selection.isEmpty() || !(selection instanceof IStructuredSelection)) {
			return;
		}
		IFile sceneFile = (IFile) ((IStructuredSelection) selection).getFirstElement();
		SceneLauncher.launch(sceneFile, mode);
	}

	@Override
	public void launch(IEditorPart editor, String mode) {
		SceneEditor sceneEditor = (SceneEditor) editor;
		SceneLauncher.launch(sceneEditor.getSceneContext(), mode);
	}
}
