package com.gurella.studio.editor;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.EditorActionBarContributor;

public class TestEditorActionBarContributor extends EditorActionBarContributor {
	private IEditorPart targetEditor;

	@Override
	public void contributeToMenu(IMenuManager menuManager) {
		IMenuManager menu = new MenuManager("Test &Menu");
		menuManager.prependToGroup(IWorkbenchActionConstants.MB_ADDITIONS, menu);
		menu.add(new Action("Action 1") {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
			}
		});
		menu.add(new Action("Action 2") {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
			}
		});
	}
	
	@Override
	public void setActiveEditor(IEditorPart targetEditor) {
		super.setActiveEditor(targetEditor);
		this.targetEditor = targetEditor;
	}
}
