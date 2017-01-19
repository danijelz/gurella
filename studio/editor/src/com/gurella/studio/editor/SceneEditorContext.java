package com.gurella.studio.editor;

import static com.gurella.studio.GurellaStudioPlugin.log;
import static com.gurella.studio.common.AssetsFolderLocator.getAssetsRelativePath;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPathEditorInput;

import com.badlogic.gdx.Files.FileType;
import com.gurella.engine.asset.AssetService;
import com.gurella.engine.asset.Assets;
import com.gurella.engine.event.EventService;
import com.gurella.engine.plugin.Workbench;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.utils.Reflection;
import com.gurella.engine.utils.Values;
import com.gurella.studio.editor.subscription.EditorCloseListener;
import com.gurella.studio.editor.utils.Try;

public class SceneEditorContext implements SceneConsumer, EditorCloseListener {
	public final int editorId;

	public final IEditorSite editorSite;
	public final IPathEditorInput editorInput;
	public final IFile sceneFile;
	public final IWorkspace workspace;
	public final IProject project;
	public final IJavaProject javaProject;

	private Scene scene;
	private Map<String, Object> editedAssets = new HashMap<>();

	public SceneEditorContext(SceneEditor editor) {
		editorId = editor.id;

		editorSite = editor.getEditorSite();
		editorInput = (IPathEditorInput) editor.getEditorInput();
		sceneFile = editorInput.getAdapter(IFile.class);
		workspace = sceneFile.getWorkspace();
		project = sceneFile.getProject();
		javaProject = JavaCore.create(project);
		Reflection.setClassResolver(DynamicURLClassLoader.newInstance(javaProject)::loadClass);
		EventService.subscribe(editorId, this);
		Workbench.activate(editorId, this);
	}

	@Override
	public void onEditorClose() {
		EventService.unsubscribe(editorId, this);
		editedAssets.entrySet().forEach(e -> AssetService.unload(e.getValue()));
		String msg = "Error closing java project";
		Try.successful(javaProject).filter(p -> p != null).peek(p -> p.close()).onFailure(e -> log(e, msg));
	}

	public Scene getScene() {
		return scene;
	}

	@Override
	public void setScene(Scene scene) {
		this.scene = scene;
	}

	public IProgressMonitor getProgressMonitor() {
		return editorSite.getActionBars().getStatusLineManager().getProgressMonitor();
	}

	public <T> T load(IFile assetFile) {
		return load(getAssetsRelativePath(assetFile).toString());
	}

	public <T> T loadAssetProperties(IFile assetFile, Class<?> assetType) {
		String assetFileName = getAssetsRelativePath(assetFile).toString();
		String propertiesFileName = Assets.getPropertiesFileName(assetFileName, assetType);
		if (propertiesFileName == null && Assets.fileExists(propertiesFileName, FileType.Internal)) {
			return load(propertiesFileName);
		} else {
			return null;
		}
	}

	private <T> T load(String fileName) {
		Object asset = editedAssets.get(fileName);
		if (asset == null) {
			asset = AssetService.load(fileName);
			editedAssets.put(fileName, asset);
		}
		return Values.cast(asset);
	}

	public void unload(IFile assetFile) {
		unload(getAssetsRelativePath(assetFile).toString());
	}

	private void unload(String fileName) {
		Object asset = editedAssets.remove(fileName);
		if (asset != null) {
			AssetService.unload(asset);
		}
	}
	
	public void save(Object asset, String fileName) {
		//TODO 
	}

	void persist(IProgressMonitor monitor) {
		String subMonitorName = "Saving loaded assets";
		SubMonitor subMonitor = SubMonitor.convert(monitor, subMonitorName, editedAssets.size());
		for (Entry<String, Object> entry : editedAssets.entrySet()) {
			String fileName = entry.getKey();
			Object asset = entry.getValue();
			SubMonitor assetSubMonitor = subMonitor.split(1);
			AssetService.save(asset, fileName);
			assetSubMonitor.worked(1);
			assetSubMonitor.done();
			subMonitor.worked(1);
		}
		subMonitor.done();
	}
}
