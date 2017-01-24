package com.gurella.studio.editor;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.EditorActionBarContributor;

import com.gurella.studio.editor.assets.AssetsView;
import com.gurella.studio.editor.control.DockableView;
import com.gurella.studio.editor.control.ViewRegistry;
import com.gurella.studio.editor.graph.SceneGraphView;
import com.gurella.studio.editor.inspector.InspectorView;
import com.gurella.studio.editor.subscription.EditorCloseListener;
import com.gurella.studio.editor.subscription.ViewActivityListener;
import com.gurella.studio.gdx.GdxContext;

public class SceneEditorActionBarContributor extends EditorActionBarContributor
		implements ViewActivityListener, EditorCloseListener {
	private SceneEditor editor;
	private ViewRegistry views;

	private ToggleEditorViewAction toggleSceneHierarcyViewAction = new ToggleEditorViewAction("Scene hierarcy",
			SceneGraphView.class);
	private ToggleEditorViewAction toggleInspectorViewAction = new ToggleEditorViewAction("Inspector",
			InspectorView.class);
	private ToggleEditorViewAction toggleAssetsViewAction = new ToggleEditorViewAction("Assets Explorer",
			AssetsView.class);

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

		if (editor != null) {
			int editorId = editor.id;
			GdxContext.unsubscribe(editorId, editorId, this);
		}

		if (part instanceof SceneEditor) {
			editor = (SceneEditor) part;
			views = editor.viewRegistry;
			int editorId = editor.id;
			GdxContext.subscribe(editorId, editorId, this);
		} else {
			editor = null;
			views = null;
		}

		updateActions();
	}

	protected void updateActions() {
		toggleSceneHierarcyViewAction.update();
		toggleInspectorViewAction.update();
		toggleAssetsViewAction.update();
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public void viewOpened(DockableView view) {
		updateActions();
	}

	@Override
	public void viewClosed(DockableView view) {
		updateActions();
	}

	@Override
	public void onEditorClose() {
		if (editor != null) {
			int editorId = editor.id;
			GdxContext.unsubscribe(editorId, editorId, this);
		}
	}

	private class ToggleEditorViewAction extends Action {
		private Class<? extends DockableView> type;

		public ToggleEditorViewAction(String text, Class<? extends DockableView> type) {
			super(text);
			this.type = type;
		}

		void update() {
			if (editor == null) {
				setChecked(false);
				setEnabled(false);
			} else {
				boolean open = views.isOpen(type);
				setEnabled(!open);
				setChecked(open);
			}
		}

		@Override
		public void run() {
			views.openView(type);
			setChecked(true);
			setEnabled(false);
		}
	}
}
