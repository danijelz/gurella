package com.gurella.studio.editor;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.texteditor.BasicTextEditorActionContributor;

import com.gurella.studio.editor.assets.AssetsExplorerView;
import com.gurella.studio.editor.scene.SceneEditorView;
import com.gurella.studio.editor.scene.SceneEditorViewClosedMessage;

public class GurellaEditorActionBarContributor extends BasicTextEditorActionContributor
		implements EditorMessageListener {
	private GurellaSceneEditor gurellaSceneEditor;
	private ToggleEditorViewAction toggleEditorViewAction = new ToggleEditorViewAction("Assets Explorer");

	@Override
	public void contributeToMenu(IMenuManager menuManager) {
		super.contributeToMenu(menuManager);

		IMenuManager gurellaMenu = new MenuManager("&Gurella");
		menuManager.prependToGroup(IWorkbenchActionConstants.MB_ADDITIONS, gurellaMenu);
		IMenuManager viewsSubMenu = new MenuManager("&View");
		gurellaMenu.add(viewsSubMenu);
		viewsSubMenu.add(toggleEditorViewAction);
	}

	@Override
	public void setActiveEditor(IEditorPart targetEditor) {
		super.setActiveEditor(targetEditor);

		if (gurellaSceneEditor != null) {
			gurellaSceneEditor.removeEditorMessageListener(this);
		}

		if (targetEditor instanceof GurellaSceneEditor) {
			gurellaSceneEditor = (GurellaSceneEditor) targetEditor;
			gurellaSceneEditor.addEditorMessageListener(this);
		} else {
			gurellaSceneEditor = null;
		}

		toggleEditorViewAction.updateActiveEditor();
	}

	@Override
	public void handleMessage(Object source, Object message) {
		if (message instanceof SceneEditorViewClosedMessage) {
			SceneEditorViewClosedMessage closedMessage = (SceneEditorViewClosedMessage) message;
			if (closedMessage.view.getClass() == AssetsExplorerView.class) {
				toggleEditorViewAction.setChecked(false);
				toggleEditorViewAction.setEnabled(true);
			}
		}
	}

	private class ToggleEditorViewAction extends Action {
		public ToggleEditorViewAction(String text) {
			super(text);
		}

		void updateActiveEditor() {
			if (gurellaSceneEditor == null) {
				setChecked(false);
				setEnabled(false);
			} else {
				List<SceneEditorView> registeredViews = gurellaSceneEditor.registeredViews;
				setEnabled(registeredViews.stream().filter(v -> v.getClass() == AssetsExplorerView.class).count() == 0);
				setChecked(!isEnabled());
			}
		}

		@Override
		public void run() {
			new AssetsExplorerView(gurellaSceneEditor, SWT.LEFT);
			setChecked(true);
			setEnabled(false);
		}
	}
}
