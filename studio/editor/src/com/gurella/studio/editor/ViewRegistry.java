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
import com.gurella.studio.editor.subscription.EditorClosingListener;
import com.gurella.studio.editor.subscription.EditorViewsListener;
import com.gurella.studio.editor.utils.Try;

public class ViewRegistry implements EditorViewsListener, EditorClosingListener {
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
	public void closing() {
		EventService.unsubscribe(editor.id, this);
	}
}
