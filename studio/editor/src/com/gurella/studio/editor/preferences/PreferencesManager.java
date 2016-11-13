package com.gurella.studio.editor.preferences;

import static com.gurella.studio.GurellaStudioPlugin.log;

import java.util.Optional;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.IPath;
import org.osgi.service.prefs.Preferences;

import com.gurella.engine.event.EventService;
import com.gurella.engine.plugin.Workbench;
import com.gurella.engine.scene.Scene;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.SceneEditor;
import com.gurella.studio.editor.subscription.EditorCloseListener;
import com.gurella.studio.editor.subscription.SceneLoadedListener;
import com.gurella.studio.editor.subscription.ScenePreferencesLoadedListener;
import com.gurella.studio.editor.utils.Try;

public class PreferencesManager implements PreferencesProvider, SceneLoadedListener, EditorCloseListener {
	private final int editorId;
	private PreferencesProviderExtensionRegistry extensionRegistry;

	private Preferences projectPreferences;
	private Preferences resourcePreferences;
	private Preferences scenePreferences;

	public PreferencesManager(SceneEditor editor) {
		editorId = editor.id;
		extensionRegistry = new PreferencesProviderExtensionRegistry(this);
		Workbench.addListener(extensionRegistry);

		IResource resource = editor.getEditorInput().getAdapter(IResource.class);
		projectPreferences = new ProjectScope(resource.getProject()).getNode(GurellaStudioPlugin.PLUGIN_ID);

		IPath resourcePath = resource.getProjectRelativePath();
		resourcePreferences = projectPreferences.node(resourcePath.toString().replace('/', '_').replace('.', '_'));

		EventService.subscribe(editorId, this);
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
	public Optional<PreferencesNode> sceneNode() {
		return Optional.ofNullable(new PreferencesNode(scenePreferences));
	}

	@Override
	public void sceneLoaded(Scene scene) {
		scenePreferences = projectPreferences.node(scene.ensureUuid());
		PreferencesNode node = new PreferencesNode(scenePreferences);
		EventService.post(editorId, ScenePreferencesLoadedListener.class, l -> l.scenePreferencesLoaded(node));
	}

	@Override
	public void onEditorClose() {
		String msg = "Error while flushing preferences";
		Try.successful(projectPreferences).peek(pp -> pp.flush()).onFailure(e -> log(e, msg));
		Workbench.removeListener(extensionRegistry);
		EventService.unsubscribe(editorId, this);
	}
}
