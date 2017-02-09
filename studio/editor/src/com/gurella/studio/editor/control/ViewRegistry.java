package com.gurella.studio.editor.control;

import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.SWT;

import com.gurella.engine.utils.Reflection;
import com.gurella.engine.utils.Values;
import com.gurella.engine.utils.plugin.Workbench;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.SceneEditorContext;
import com.gurella.studio.editor.assets.AssetsView;
import com.gurella.studio.editor.graph.SceneGraphView;
import com.gurella.studio.editor.inspector.InspectorView;
import com.gurella.studio.editor.menu.ContextMenuActions;
import com.gurella.studio.editor.menu.EditorContextMenuContributor;
import com.gurella.studio.editor.preferences.PreferencesExtension;
import com.gurella.studio.editor.preferences.PreferencesNode;
import com.gurella.studio.editor.preferences.PreferencesStore;
import com.gurella.studio.editor.subscription.EditorCloseListener;
import com.gurella.studio.editor.subscription.EditorPreCloseListener;
import com.gurella.studio.editor.subscription.ViewActivityListener;
import com.gurella.studio.editor.subscription.ViewOrientationListener;
import com.gurella.studio.editor.utils.Try;
import com.gurella.studio.gdx.GdxContext;

public class ViewRegistry implements ViewActivityListener, EditorPreCloseListener, EditorCloseListener,
		EditorContextMenuContributor, ViewOrientationListener, PreferencesExtension {
	private static final String editorMenuSectionName = "Editor";
	private static final String viewMenuGroupName = "&View";

	private final int editorId;
	private final SceneEditorContext context;
	private final Dock dock;

	private PreferencesNode preferences;

	List<DockableView> registeredViews = new ArrayList<DockableView>();

	public ViewRegistry(SceneEditorContext context, Dock dock) {
		this.editorId = context.editorId;
		this.context = context;
		this.dock = dock;

		GdxContext.subscribe(editorId, editorId, this);
		Workbench.activate(editorId, this);
	}

	public boolean isOpen(Class<? extends DockableView> type) {
		return registeredViews.stream().filter(v -> v.getClass() == type).findAny().isPresent();
	}

	public void openView(Class<? extends DockableView> type) {
		if (isOpen(type)) {
			return;
		}

		int defaultOrientation = type == InspectorView.class ? SWT.RIGHT : SWT.LEFT;
		int orientation = preferences.node(type).getInt("orientation", defaultOrientation);

		Try.ofFailable(() -> Reflection.newInstance(type, SceneEditorContext.class, context))
				.onSuccess(v -> openView(v, orientation))
				.onFailure(e -> GurellaStudioPlugin.showError(e, "Error creating view."));
	}

	private void openView(DockableView view, int orientation) {
		view.createControl(dock.getDockItemParent(orientation));
		dock.addDockItem(view, orientation);
		GdxContext.post(editorId, editorId, ViewActivityListener.class, l -> l.viewOpened(view));
	}

	@Override
	public void viewOpened(DockableView view) {
		registeredViews.add(view);
		view.content.layout(true, true);
	}

	private int getViewOrder(DockableView view) {
		return dock.getOrientation(view) * 10 + dock.getIndex(view);
	}

	@Override
	public void viewClosed(DockableView view) {
		registeredViews.remove(view);
	}

	@Override
	public void contribute(float x, float y, ContextMenuActions actions) {
		actions.addSection(editorMenuSectionName, 200);

		actions.addGroup(editorMenuSectionName, viewMenuGroupName, -600);
		boolean open = isOpen(SceneGraphView.class);
		actions.addCheckAction(viewMenuGroupName, "Scene", 100, !open, open, () -> openView(SceneGraphView.class));
		open = isOpen(InspectorView.class);
		actions.addCheckAction(viewMenuGroupName, "Inspector", 200, !open, open, () -> openView(InspectorView.class));
		open = isOpen(AssetsView.class);
		actions.addCheckAction(viewMenuGroupName, "Assets", 300, !open, open, () -> openView(AssetsView.class));
	}

	@Override
	public void orientationChanged(DockableView view, int newOrientation) {
		preferences.node(view.getClass()).putInt("orientation", newOrientation);
	}

	@Override
	public void onEditorPreClose() {
		if (preferences == null || dock.isDisposed()) {
			return;
		}

		registeredViews.sort((v1, v2) -> Integer.compare(getViewOrder(v1), getViewOrder(v2)));
		String openViews = registeredViews.stream().map(v -> v.getClass().getName()).collect(joining(","));
		preferences.put("openViews", openViews);

		preferences.putBoolean("minimizedEast", dock.isMinimized(SWT.RIGHT));
		preferences.putBoolean("minimizedSouth", dock.isMinimized(SWT.BOTTOM));
		preferences.putBoolean("minimizedWest", dock.isMinimized(SWT.LEFT));

		preferences.putInt("selectionEast", dock.getSelectionIndex(SWT.RIGHT));
		preferences.putInt("selectionSouth", dock.getSelectionIndex(SWT.BOTTOM));
		preferences.putInt("selectionWest", dock.getSelectionIndex(SWT.LEFT));
	}

	@Override
	public void setPreferencesStore(PreferencesStore preferencesStore) {
		if (preferencesStore == null) {
			preferences = null;
			return;
		}

		preferences = preferencesStore.resourceNode().node(ViewRegistry.class);

		String openViews = preferences.get("openViews", "");
		if (Values.isBlank(openViews)) {
			openView(SceneGraphView.class);
			openView(AssetsView.class);
			openView(InspectorView.class);
		} else {
			Arrays.stream(openViews.split(",")).filter(Values::isNotBlank)
					.map(t -> Try.ofFailable(() -> Class.forName(t.trim()))).filter(t -> t.isSuccess())
					.map(t -> Values.<Class<? extends DockableView>> cast(t.getUnchecked()))
					.forEachOrdered(this::openView);
		}

		dock.setMinimized(SWT.RIGHT, preferences.getBoolean("minimizedEast", false));
		dock.setMinimized(SWT.BOTTOM, preferences.getBoolean("minimizedSouth", false));
		dock.setMinimized(SWT.LEFT, preferences.getBoolean("minimizedWest", false));

		dock.setSelection(SWT.RIGHT, preferences.getInt("selectionEast", 0));
		dock.setSelection(SWT.BOTTOM, preferences.getInt("selectionSouth", 0));
		dock.setSelection(SWT.LEFT, preferences.getInt("selectionWest", 0));
	}

	@Override
	public void onEditorClose() {
		Workbench.deactivate(editorId, this);
		GdxContext.unsubscribe(editorId, editorId, this);
	}
}
