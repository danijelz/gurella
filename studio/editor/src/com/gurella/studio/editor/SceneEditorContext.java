package com.gurella.studio.editor;

import static com.gurella.studio.GurellaStudioPlugin.log;
import static com.gurella.studio.common.AssetsFolderLocator.getAssetsRelativePath;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPathEditorInput;

import com.badlogic.gdx.Files.FileType;
import com.gurella.engine.asset.Assets;
import com.gurella.engine.asset.loader.AssetProperties;
import com.gurella.engine.plugin.Workbench;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.utils.Reflection;
import com.gurella.engine.utils.Values;
import com.gurella.studio.common.AssetsFolderLocator;
import com.gurella.studio.editor.subscription.EditorCloseListener;
import com.gurella.studio.editor.subscription.SceneDirtyListener;
import com.gurella.studio.editor.utils.Try;
import com.gurella.studio.gdx.GdxContext;

public class SceneEditorContext implements SceneConsumer, EditorCloseListener {
	public final int editorId;

	public final IEditorSite editorSite;
	public final IPathEditorInput editorInput;
	public final IFile sceneFile;
	public final IFolder assetsFolder;
	public final IWorkspace workspace;
	public final IProject project;
	public final IJavaProject javaProject;

	private Scene scene;
	private Map<String, Object> editingAssets = new HashMap<>();
	private Map<String, Object> modifiedAssets = new HashMap<>();

	public SceneEditorContext(SceneEditor editor) {
		editorId = editor.id;

		editorSite = editor.getEditorSite();
		editorInput = (IPathEditorInput) editor.getEditorInput();
		sceneFile = editorInput.getAdapter(IFile.class);
		workspace = sceneFile.getWorkspace();
		project = sceneFile.getProject();
		assetsFolder = AssetsFolderLocator.getAssetsFolder(project);
		javaProject = JavaCore.create(project);
		Reflection.setClassResolver(DynamicURLClassLoader.newInstance(javaProject)::loadClass);
		GdxContext.subscribe(editorId, editorId, this);
		Workbench.activate(editorId, this);
	}

	@Override
	public void onEditorClose() {
		GdxContext.unsubscribe(editorId, editorId, this);
		editingAssets.entrySet().forEach(e -> GdxContext.unload(editorId, e.getValue()));
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
		if (propertiesFileName != null && Assets.fileExists(propertiesFileName, FileType.Internal)) {
			return load(propertiesFileName);
		} else {
			return null;
		}
	}

	private <T> T load(String fileName) {
		Object asset = editingAssets.get(fileName);
		if (asset == null) {
			asset = modifiedAssets.get(fileName);
		}

		if (asset == null) {
			asset = GdxContext.load(editorId, fileName);
			editingAssets.put(fileName, asset);
		}

		return Values.cast(asset);
	}

	public void unload(IFile assetFile) {
		unload(getAssetsRelativePath(assetFile).toString());
	}

	public void unload(Object asset) {
		unload(getAssetFileName(asset));
	}

	private String getAssetFileName(Object asset) {
		return GdxContext.getFileName(editorId, asset);
	}

	private void unload(String fileName) {
		Object asset = editingAssets.remove(fileName);
		if (asset != null) {
			GdxContext.unload(editorId, asset);
		}
	}

	public void save(Object asset) {
		save(asset, getAssetFileName(asset));
	}

	public <T extends AssetProperties> void saveProperties(Object asset, T assetProperties) {
		save(assetProperties, Assets.getPropertiesFileName(asset));
	}

	public <T extends AssetProperties> void saveProperties(IFile assetFile, T assetProperties) {
		String assetFileName = getAssetsRelativePath(assetFile).toString();
		save(assetProperties, Assets.getPropertiesFileName(assetFileName));
	}

	public void save(Object asset, String fileName) {
		editingAssets.remove(fileName);
		modifiedAssets.put(fileName, asset);
		GdxContext.post(editorId, editorId, SceneDirtyListener.class, l -> l.sceneDirty());
	}

	void persist(IProgressMonitor monitor) {
		String subMonitorName = "Saving loaded assets";
		SubMonitor subMonitor = SubMonitor.convert(monitor, subMonitorName, modifiedAssets.size());

		for (Entry<String, Object> entry : modifiedAssets.entrySet()) {
			SubMonitor assetSubMonitor = subMonitor.split(1);

			String fileName = entry.getKey();
			Object asset = entry.getValue();
			boolean newAsset = !GdxContext.isManaged(editorId, fileName);
			GdxContext.save(editorId, asset, fileName);
			if (newAsset) {
				refreshParentFolder(fileName, assetSubMonitor.split(1));
			}

			assetSubMonitor.worked(1);
			assetSubMonitor.done();
			subMonitor.worked(1);
		}

		subMonitor.done();
	}

	private void refreshParentFolder(String fileName, SubMonitor refreshMonitor) {
		IContainer container = assetsFolder.getFile(fileName).getParent();
		Try.unchecked(() -> container.refreshLocal(1, refreshMonitor));
		refreshMonitor.worked(1);
		refreshMonitor.done();
	}
}
