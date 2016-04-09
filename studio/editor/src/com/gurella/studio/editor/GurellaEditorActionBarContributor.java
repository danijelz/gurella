package com.gurella.studio.editor;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.texteditor.BasicTextEditorActionContributor;

import com.gurella.studio.editor.scene.AssetsExplorerView;
import com.gurella.studio.editor.scene.SceneEditorView;
import com.gurella.studio.editor.scene.SceneEditorViewClosedMessage;

public class GurellaEditorActionBarContributor extends BasicTextEditorActionContributor
		implements EditorMessageListener {
	private GurellaEditor gurellaEditor;
	private ToggleEditorViewAction toggleEditorViewAction = new ToggleEditorViewAction("Assets Explorer");

	@Override
	public void contributeToMenu(IMenuManager menuManager) {
		super.contributeToMenu(menuManager);

		IMenuManager menu = new MenuManager("&Gurella");
		menuManager.prependToGroup(IWorkbenchActionConstants.MB_ADDITIONS, menu);
		menu.add(toggleEditorViewAction);
	}

	@Override
	public void setActiveEditor(IEditorPart targetEditor) {
		super.setActiveEditor(targetEditor);

		if (gurellaEditor != null) {
			gurellaEditor.removeEditorMessageListener(this);
		}

		if (targetEditor instanceof GurellaEditor) {
			gurellaEditor = (GurellaEditor) targetEditor;
			gurellaEditor.addEditorMessageListener(this);
		} else {
			gurellaEditor = null;
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
			if (gurellaEditor == null) {
				setChecked(false);
				setEnabled(false);
			} else {
				List<SceneEditorView> registeredViews = gurellaEditor.registeredViews;
				setEnabled(registeredViews.stream().filter(v -> v.getClass() == AssetsExplorerView.class).count() == 0);
				setChecked(!isEnabled());
			}
		}

		@Override
		public void run() {
			new AssetsExplorerView(gurellaEditor, SWT.LEFT);
			setChecked(true);
			setEnabled(false);
		}
	}
}
