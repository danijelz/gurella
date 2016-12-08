package com.gurella.studio.launch;

import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;

import com.gurella.studio.editor.SceneEditor;

public class LaunchSceneShortcut implements ILaunchShortcut {
	@Override
	public void launch(ISelection selection, String mode) {
		System.out.println("Selection: " + mode);
		// TODO Auto-generated method stub
		
	}

	@Override
	public void launch(IEditorPart editor, String mode) {
		System.out.println("Editor: " + mode);
		SceneEditor sceneEditor = (SceneEditor) editor;
		int id = sceneEditor.id;
		// TODO Auto-generated method stub
		
	}
}
