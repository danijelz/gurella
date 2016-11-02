package com.gurella.studio.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;

import com.gurella.engine.event.EventService;
import com.gurella.studio.editor.assets.AssetsView;
import com.gurella.studio.editor.control.Dock;
import com.gurella.studio.editor.control.DockableView;
import com.gurella.studio.editor.graph.SceneGraphView;
import com.gurella.studio.editor.inspector.InspectorView;
import com.gurella.studio.editor.menu.ContextMenuActions;
import com.gurella.studio.editor.subscription.EditorContextMenuContributor;
import com.gurella.studio.editor.subscription.EditorPreCloseListener;
import com.gurella.studio.editor.subscription.EditorViewsListener;
import com.gurella.studio.editor.utils.Try;

class ViewRegistry implements EditorViewsListener, EditorPreCloseListener, EditorContextMenuContributor {
	private static final String viewMenuGroupName = "&View";

	private final SceneEditor editor;
	private final Dock dock;

	List<DockableView> registeredViews = new ArrayList<DockableView>();

	public ViewRegistry(SceneEditor editor) {
		this.editor = editor;
		dock = editor.getDock();

		SceneGraphView sceneGraphView = new SceneGraphView(editor, SWT.LEFT);
		registeredViews.add(sceneGraphView);
		registeredViews.add(new AssetsView(editor, SWT.LEFT));
		registeredViews.add(new InspectorView(editor, SWT.RIGHT));
		dock.setSelection(sceneGraphView);

		EventService.subscribe(editor.id, this);
	}

	public boolean isOpen(Class<? extends DockableView> type) {
		return registeredViews.stream().filter(v -> v.getClass() == type).count() != 0;
	}

	public void openView(Class<? extends DockableView> type, int position) {
		if (isOpen(type)) {
			return;
		}

		Try.ofFailable(() -> type.getConstructor(SceneEditor.class, int.class))
				.map(c -> c.newInstance(editor, Integer.valueOf(position))).onSuccess(v -> dock.setSelection(v))
				.onSuccess(v -> EventService.post(editor.id, EditorViewsListener.class, l -> l.viewOpened(v)));
	}

	@Override
	public void viewOpened(DockableView view) {
		registeredViews.add(view);
	}

	@Override
	public void viewClosed(DockableView view) {
		registeredViews.remove(view);
	}

	@Override
	public void contribute(ContextMenuActions actions) {
		actions.addGroup(viewMenuGroupName, -600);
		boolean open = isOpen(SceneGraphView.class);
		actions.addCheckAction(viewMenuGroupName, "Scene", 100, !open, open, () -> openView(SceneGraphView.class));
		open = isOpen(InspectorView.class);
		actions.addCheckAction(viewMenuGroupName, "Inspector", 200, !open, open, () -> openView(InspectorView.class));
		open = isOpen(AssetsView.class);
		actions.addCheckAction(viewMenuGroupName, "Assets", 300, !open, open, () -> openView(AssetsView.class));
	}

	private void openView(Class<? extends DockableView> type) {
		openView(type, SWT.LEFT);
	}

	@Override
	public void onEditorPreClose() {
		EventService.unsubscribe(editor.id, this);
	}
}
