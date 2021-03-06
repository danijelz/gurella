package com.gurella.studio.editor.preferences;

import static com.gurella.studio.GurellaStudioPlugin.log;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.IPath;
import org.osgi.service.prefs.Preferences;

import com.gurella.engine.scene.Scene;
import com.gurella.engine.utils.plugin.Workbench;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.SceneConsumer;
import com.gurella.studio.editor.SceneEditor;
import com.gurella.studio.editor.subscription.EditorCloseListener;
import com.gurella.studio.editor.utils.Try;
import com.gurella.studio.gdx.GdxContext;

public class PreferencesManager implements PreferencesStore, SceneConsumer, EditorCloseListener {
	private final int editorId;
	private PreferencesExtensionRegistry extensionRegistry;

	private Preferences projectPreferences;
	private Preferences resourcePreferences;
	private Preferences scenePreferences;

	public PreferencesManager(SceneEditor editor) {
		editorId = editor.id;
		extensionRegistry = new PreferencesExtensionRegistry(this);

		IResource resource = editor.getEditorInput().getAdapter(IResource.class);
		projectPreferences = new ProjectScope(resource.getProject()).getNode(GurellaStudioPlugin.PLUGIN_ID);

		IPath resourcePath = resource.getProjectRelativePath();
		resourcePreferences = projectPreferences.node(resourcePath.toString().replace('/', '_').replace('.', '_'));

		GdxContext.subscribe(editorId, editorId, this);
		Workbench.addListener(editorId, extensionRegistry);
		Workbench.activate(editorId, this);
	}

	@Override
	public PreferencesNode projectNode() {
		return new PreferencesNode(resourcePreferences);
	}

	@Override
	public PreferencesNode resourceNode() {
		return new PreferencesNode(resourcePreferences);
	}

	@Override
	public PreferencesNode sceneNode() {
		return new PreferencesNode(scenePreferences);
	}

	@Override
	public void setScene(Scene scene) {
		scenePreferences = projectPreferences.node(scene.ensureUuid());
	}

	@Override
	public void onEditorClose() {
		String msg = "Error while flushing preferences";
		Try.run(() -> projectPreferences.flush(), e -> log(e, msg));
		Workbench.deactivate(editorId, this);
		Workbench.removeListener(editorId, extensionRegistry);
		GdxContext.unsubscribe(editorId, editorId, this);
	}
}
