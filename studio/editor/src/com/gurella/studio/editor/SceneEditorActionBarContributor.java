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
import org.eclipse.ui.part.EditorActionBarContributor;

import com.gurella.engine.event.EventService;
import com.gurella.studio.editor.assets.AssetsView;
import com.gurella.studio.editor.control.DockableView;
import com.gurella.studio.editor.graph.SceneGraphView;
import com.gurella.studio.editor.inspector.InspectorView;
import com.gurella.studio.editor.subscription.SceneEditorViewClosedListener;

public class SceneEditorActionBarContributor extends EditorActionBarContributor
		implements SceneEditorViewClosedListener {
	private SceneEditor editor;
	private ToggleEditorViewAction toggleSceneHierarcyViewAction = new ToggleEditorViewAction("Scene hierarcy",
			SceneGraphView.class, SceneGraphView::new);
	private ToggleEditorViewAction toggleInspectorViewAction = new ToggleEditorViewAction("Inspector",
			InspectorView.class, InspectorView::new);
	private ToggleEditorViewAction toggleAssetsViewAction = new ToggleEditorViewAction("Assets Explorer",
			AssetsView.class, AssetsView::new);

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
			EventService.subscribe(editor.id, this);

			IActionBars actionBars = this.getActionBars();
			if (actionBars != null) {
				actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(), ((SceneEditor) part).undoAction);
				actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(), ((SceneEditor) part).redoAction);
			}
		} else {
			editor = null;
		}

		toggleSceneHierarcyViewAction.updateActiveEditor();
		toggleInspectorViewAction.updateActiveEditor();
		toggleAssetsViewAction.updateActiveEditor();
	}

	@Override
	public void dispose() {
		super.dispose();
		if (editor != null) {
			EventService.unsubscribe(editor.id, this);
		}
	}

	@Override
	public void viewClosed(DockableView view) {
		if (view instanceof SceneGraphView) {
			toggleSceneHierarcyViewAction.setChecked(false);
			toggleSceneHierarcyViewAction.setEnabled(true);
		} else if (view instanceof InspectorView) {
			toggleInspectorViewAction.setChecked(false);
			toggleInspectorViewAction.setEnabled(true);
		} else

		if (view instanceof AssetsView) {
			toggleAssetsViewAction.setChecked(false);
			toggleAssetsViewAction.setEnabled(true);
		}
	}

	private class ToggleEditorViewAction extends Action {
		private Class<? extends DockableView> type;
		private BiConsumer<SceneEditor, Integer> constructor;

		public ToggleEditorViewAction(String text, Class<? extends DockableView> type,
				BiConsumer<SceneEditor, Integer> constructor) {
			super(text);
			this.type = type;
			this.constructor = constructor;
		}

		void updateActiveEditor() {
			if (editor == null) {
				setChecked(false);
				setEnabled(false);
			} else {
				List<DockableView> registeredViews = editor.registeredViews;
				setEnabled(registeredViews.stream().filter(v -> v.getClass() == type).count() == 0);
				setChecked(!isEnabled());
			}
		}

		@Override
		public void run() {
			constructor.accept(editor, Integer.valueOf(SWT.LEFT));
			setChecked(true);
			setEnabled(false);
		}
	}
}
