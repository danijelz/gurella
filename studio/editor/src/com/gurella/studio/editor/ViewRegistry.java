package com.gurella.studio.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.swt.SWT;

import com.gurella.engine.event.EventService;
import com.gurella.engine.plugin.Workbench;
import com.gurella.engine.utils.Values;
import com.gurella.studio.editor.assets.AssetsView;
import com.gurella.studio.editor.control.Dock;
import com.gurella.studio.editor.control.DockableView;
import com.gurella.studio.editor.graph.SceneGraphView;
import com.gurella.studio.editor.inspector.InspectorView;
import com.gurella.studio.editor.menu.ContextMenuActions;
import com.gurella.studio.editor.preferences.PreferencesExtension;
import com.gurella.studio.editor.preferences.PreferencesNode;
import com.gurella.studio.editor.preferences.PreferencesStore;
import com.gurella.studio.editor.subscription.EditorCloseListener;
import com.gurella.studio.editor.subscription.EditorContextMenuContributor;
import com.gurella.studio.editor.subscription.EditorPreCloseListener;
import com.gurella.studio.editor.subscription.EditorViewActivityListener;
import com.gurella.studio.editor.utils.Try;

class ViewRegistry implements EditorViewActivityListener, EditorPreCloseListener, EditorCloseListener,
		EditorContextMenuContributor, PreferencesExtension {
	private static final String viewMenuGroupName = "&View";

	private final SceneEditor editor;
	private final Dock dock;
	private PreferencesNode preferences;

	List<DockableView> registeredViews = new ArrayList<DockableView>();

	public ViewRegistry(SceneEditor editor) {
		this.editor = editor;
		dock = editor.getDock();
		Workbench.activate(this);

		/*SceneGraphView sceneGraphView = new SceneGraphView(editor, SWT.LEFT);
		registeredViews.add(sceneGraphView);
		registeredViews.add(new AssetsView(editor, SWT.LEFT));
		registeredViews.add(new InspectorView(editor, SWT.RIGHT));
		dock.setSelection(sceneGraphView);*/

		EventService.subscribe(editor.id, this);
	}

	@Override
	public void setPreferencesStore(PreferencesStore preferencesStore) {
		this.preferences = preferencesStore.sceneNode().node(ViewRegistry.class);

		List<Class<? extends DockableView>> openViewTypes = new ArrayList<>();
		String openViews = preferences.get("openViews", "");
		if (Values.isBlank(openViews)) {
			openViewTypes.add(SceneGraphView.class);
			openViewTypes.add(AssetsView.class);
			openViewTypes.add(InspectorView.class);
		} else {
			Arrays.stream(openViews.split(",")).filter(t -> Values.isBlank(t))
					.map(t -> Try.ofFailable(() -> Class.forName(t.trim()))).filter(t -> t.isSuccess())
					.map(t -> Values.<Class<? extends DockableView>> cast(t.getUnchecked()))
					.forEach(openViewTypes::add);
		}

		openViewTypes.stream().forEach(this::openView);

		//TODO dock.setSelection(sceneGraphView);
	}

	public boolean isOpen(Class<? extends DockableView> type) {
		return registeredViews.stream().filter(v -> v.getClass() == type).count() != 0;
	}

	public void openView(Class<? extends DockableView> type) {
		int defaultOrientation = type == InspectorView.class ? SWT.RIGHT : SWT.LEFT;
		openView(type, preferences.node(type).getInt("orientation", defaultOrientation));
	}

	private void openView(Class<? extends DockableView> type, int position) {
		if (isOpen(type)) {
			return;
		}

		Try.ofFailable(() -> type.getConstructor(SceneEditor.class, int.class))
				.map(c -> c.newInstance(editor, Integer.valueOf(position))).onSuccess(v -> dock.setSelection(v))
				.onSuccess(v -> EventService.post(editor.id, EditorViewActivityListener.class, l -> l.viewOpened(v)));
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

	@Override
	public void onEditorPreClose() {
		if (preferences == null) {
			return;
		}

		String openViews = registeredViews.stream().map(v -> v.getClass().getName()).collect(Collectors.joining(","));
		preferences.put("openViews", openViews);

		// TODO Auto-generated method stub

	}

	@Override
	public void onEditorClose() {
		EventService.unsubscribe(editor.id, this);
	}
}
