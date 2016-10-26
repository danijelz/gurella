package com.gurella.studio.editor;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.EditorActionBarContributor;

import com.gurella.engine.event.EventService;
import com.gurella.studio.editor.assets.AssetsView;
import com.gurella.studio.editor.control.DockableView;
import com.gurella.studio.editor.graph.SceneGraphView;
import com.gurella.studio.editor.inspector.InspectorView;
import com.gurella.studio.editor.subscription.EditorViewsListener;

public class SceneEditorActionBarContributor extends EditorActionBarContributor implements EditorViewsListener {
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
			EventService.unsubscribe(editor.id, this);
		}

		if (part instanceof SceneEditor) {
			editor = (SceneEditor) part;
			views = editor.viewRegistry;
			EventService.subscribe(editor.id, this);
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
		if (editor != null) {
			EventService.unsubscribe(editor.id, this);
		}
	}

	@Override
	public void viewOpened(DockableView view) {
		updateActions();
	}

	@Override
	public void viewClosed(DockableView view) {
		updateActions();
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
			views.openView(type, SWT.LEFT);
			setChecked(true);
			setEnabled(false);
		}
	}
}
