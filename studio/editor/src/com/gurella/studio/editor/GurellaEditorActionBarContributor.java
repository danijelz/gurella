package com.gurella.studio.editor;

import java.util.List;
import java.util.function.BiConsumer;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.texteditor.BasicTextEditorActionContributor;

import com.gurella.studio.editor.assets.AssetsExplorerView;
import com.gurella.studio.editor.inspector.InspectorView;
import com.gurella.studio.editor.scene.SceneEditorView;
import com.gurella.studio.editor.scene.SceneEditorViewClosedMessage;
import com.gurella.studio.editor.scene.SceneHierarchyView;

public class GurellaEditorActionBarContributor extends BasicTextEditorActionContributor
		implements EditorMessageListener {
	private GurellaSceneEditor gurellaSceneEditor;
	private ToggleEditorViewAction toggleSceneHierarcyViewAction = new ToggleEditorViewAction("Scene hierarcy",
			SceneHierarchyView.class, SceneHierarchyView::new);
	private ToggleEditorViewAction toggleInspectorViewAction = new ToggleEditorViewAction("Inspector",
			InspectorView.class, InspectorView::new);
	private ToggleEditorViewAction toggleAssetsViewAction = new ToggleEditorViewAction("Assets Explorer",
			AssetsExplorerView.class, AssetsExplorerView::new);

	@Override
	public void contributeToMenu(IMenuManager menuManager) {
		super.contributeToMenu(menuManager);

		IMenuManager gurellaMenu = new MenuManager("&Gurella");
		menuManager.prependToGroup(IWorkbenchActionConstants.MB_ADDITIONS, gurellaMenu);
		IMenuManager viewsSubMenu = new MenuManager("&View");
		gurellaMenu.add(viewsSubMenu);
		viewsSubMenu.add(toggleSceneHierarcyViewAction);
		viewsSubMenu.add(toggleInspectorViewAction);
		viewsSubMenu.add(toggleAssetsViewAction);
	}

	@Override
	public void setActiveEditor(IEditorPart part) {
		super.setActiveEditor(part);

		if (gurellaSceneEditor != null) {
			gurellaSceneEditor.removeEditorMessageListener(this);
		}

		if (part instanceof GurellaSceneEditor) {
			gurellaSceneEditor = (GurellaSceneEditor) part;
			gurellaSceneEditor.addEditorMessageListener(this);

			IActionBars actionBars = this.getActionBars();
			if (actionBars != null) {
				actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(), ((GurellaSceneEditor) part).undoAction);
				actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(), ((GurellaSceneEditor) part).redoAction);
			}

		} else {
			gurellaSceneEditor = null;
		}

		toggleSceneHierarcyViewAction.updateActiveEditor();
		toggleInspectorViewAction.updateActiveEditor();
		toggleAssetsViewAction.updateActiveEditor();
	}

	@Override
	public void handleMessage(Object source, Object message) {
		if (message instanceof SceneEditorViewClosedMessage) {
			SceneEditorViewClosedMessage closedMessage = (SceneEditorViewClosedMessage) message;
			if (closedMessage.view.getClass() == SceneHierarchyView.class) {
				toggleSceneHierarcyViewAction.setChecked(false);
				toggleSceneHierarcyViewAction.setEnabled(true);
			} else if (closedMessage.view.getClass() == InspectorView.class) {
				toggleInspectorViewAction.setChecked(false);
				toggleInspectorViewAction.setEnabled(true);
			} else

			if (closedMessage.view.getClass() == AssetsExplorerView.class) {
				toggleAssetsViewAction.setChecked(false);
				toggleAssetsViewAction.setEnabled(true);
			}
		}
	}

	private class ToggleEditorViewAction extends Action {
		private Class<? extends SceneEditorView> type;
		private BiConsumer<GurellaSceneEditor, Integer> constructor;

		public ToggleEditorViewAction(String text, Class<? extends SceneEditorView> type,
				BiConsumer<GurellaSceneEditor, Integer> constructor) {
			super(text);
			this.type = type;
			this.constructor = constructor;
		}

		void updateActiveEditor() {
			if (gurellaSceneEditor == null) {
				setChecked(false);
				setEnabled(false);
			} else {
				List<SceneEditorView> registeredViews = gurellaSceneEditor.registeredViews;
				setEnabled(registeredViews.stream().filter(v -> v.getClass() == type).count() == 0);
				setChecked(!isEnabled());
			}
		}

		@Override
		public void run() {
			constructor.accept(gurellaSceneEditor, Integer.valueOf(SWT.LEFT));
			setChecked(true);
			setEnabled(false);
		}
	}
}
