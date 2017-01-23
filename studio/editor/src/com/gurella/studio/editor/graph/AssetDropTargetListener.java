package com.gurella.studio.editor.graph;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.widgets.TreeItem;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.gurella.engine.asset.AssetService;
import com.gurella.engine.asset.AssetType;
import com.gurella.engine.asset.Assets;
import com.gurella.engine.metatype.CopyContext;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneNode;
import com.gurella.engine.scene.SceneNodeComponent;
import com.gurella.engine.scene.renderable.ModelComponent;
import com.gurella.engine.scene.renderable.TextureComponent;
import com.gurella.studio.editor.SceneConsumer;
import com.gurella.studio.editor.assets.AssetSelection;
import com.gurella.studio.editor.history.HistoryContributor;
import com.gurella.studio.editor.history.HistoryService;
import com.gurella.studio.editor.operation.AddComponentOperation;
import com.gurella.studio.editor.operation.AddNodeOperation;
import com.gurella.studio.editor.swtgdx.GdxContext;

class AssetDropTargetListener extends DropTargetAdapter implements SceneConsumer, HistoryContributor {
	private final int editorId;
	private Scene scene;
	private HistoryService historyService;

	AssetDropTargetListener(int editorId) {
		this.editorId = editorId;
	}

	@Override
	public void setScene(Scene scene) {
		this.scene = scene;
	}

	@Override
	public void setHistoryService(HistoryService historyService) {
		this.historyService = historyService;
	}

	@Override
	public void dragEnter(DropTargetEvent event) {
		if ((event.operations & DND.DROP_COPY) == 0 || getTransferingAssetFile() == null) {
			event.detail = DND.DROP_NONE;
			return;
		}

		event.detail = DND.DROP_COPY;
	}

	private static IFile getTransferingAssetFile() {
		ISelection selection = LocalSelectionTransfer.getTransfer().getSelection();
		if (!(selection instanceof AssetSelection)) {
			return null;
		}

		IResource resource = ((AssetSelection) selection).getAssetResource();
		if (!(resource instanceof IFile)) {
			return null;
		}

		IFile file = (IFile) resource;
		if (AssetType.prefab.isValidExtension(file.getFileExtension())) {
			return file;
		} else {
			return getComponentType(file) == null ? null : file;
		}
	}

	protected boolean isValidAssetExtension(String fileExtension) {
		return Assets.isValidExtension(Model.class, fileExtension)
				|| Assets.isValidExtension(Texture.class, fileExtension)
				|| AssetType.prefab.isValidExtension(fileExtension);
	}

	@Override
	public void dragOver(DropTargetEvent event) {
		event.feedback = DND.FEEDBACK_EXPAND | DND.FEEDBACK_SCROLL;

		IFile file = getTransferingAssetFile();
		if (file == null) {
			event.detail = DND.DROP_NONE;
			return;
		}

		TreeItem item = (TreeItem) event.item;
		boolean isPrefab = AssetType.prefab.isValidExtension(file.getFileExtension());
		if (item == null) {
			if (isPrefab) {
				event.detail = DND.DROP_COPY;
				event.feedback |= DND.FEEDBACK_SELECT;
			} else {
				event.detail = DND.DROP_NONE;
			}
		} else {
			Object data = item.getData();
			if (!(data instanceof SceneNode)) {
				event.detail = DND.DROP_NONE;
				return;
			}

			if (isPrefab) {
				event.detail = DND.DROP_COPY;
				event.feedback |= DND.FEEDBACK_SELECT;
			} else {
				SceneNode node = (SceneNode) data;
				Class<? extends SceneNodeComponent> type = getComponentType(file);
				if (node.getComponent(type, true) == null) {
					event.detail = DND.DROP_COPY;
					event.feedback |= DND.FEEDBACK_SELECT;
				} else {
					event.detail = DND.DROP_NONE;
				}
			}
		}
	}

	private static Class<? extends SceneNodeComponent> getComponentType(IFile file) {
		String fileExtension = file.getFileExtension();

		if (Assets.isValidExtension(Model.class, fileExtension)) {
			return ModelComponent.class;
		} else if (Assets.isValidExtension(Texture.class, fileExtension)) {
			return TextureComponent.class;
		} else {
			return null;
		}
	}

	@Override
	public void dropAccept(DropTargetEvent event) {
		event.detail = DND.DROP_COPY;
	}

	@Override
	public void drop(DropTargetEvent event) {
		IFile file = getTransferingAssetFile();
		if (file == null) {
			event.detail = DND.DROP_NONE;
			return;
		}

		TreeItem item = (TreeItem) event.item;
		boolean isPrefab = AssetType.prefab.isValidExtension(file.getFileExtension());
		if (item == null) {
			if (isPrefab) {
				SceneNode prefab = loadAsset(file, SceneNode.class);
				SceneNode instance = CopyContext.copyObject(prefab);
				AddNodeOperation operation = new AddNodeOperation(editorId, scene, null, instance);
				historyService.executeOperation(operation, "Error while instantiating prefab.");
			} else {
				event.detail = DND.DROP_NONE;
			}
		} else {
			Object data = item.getData();
			if (!(data instanceof SceneNode)) {
				event.detail = DND.DROP_NONE;
				return;
			}

			SceneNode node = (SceneNode) data;
			if (isPrefab) {
				SceneNode prefab = loadAsset(file, SceneNode.class);
				SceneNode instance = CopyContext.copyObject(prefab);
				AddNodeOperation operation = new AddNodeOperation(editorId, node.getScene(), node, instance);
				historyService.executeOperation(operation, "Error while instantiating prefab.");
			} else {
				Class<? extends SceneNodeComponent> type = getComponentType(file);
				if (node.getComponent(type, true) != null) {
					event.detail = DND.DROP_NONE;
					return;
				}

				if (type == ModelComponent.class) {
					ModelComponent modelComponent = node.newComponent(ModelComponent.class);
					Model model = loadAsset(file, Model.class);
					modelComponent.setModel(model);
					AddComponentOperation operation = new AddComponentOperation(editorId, node, modelComponent);
					historyService.executeOperation(operation, "Error while adding component");
				} else if (type == TextureComponent.class) {
					TextureComponent textureComponent = node.newComponent(TextureComponent.class);
					Texture texture = loadAsset(file, Texture.class);
					textureComponent.setTexture(texture, texture.getWidth() / 100, texture.getHeight() / 100);
					AddComponentOperation operation = new AddComponentOperation(editorId, node, textureComponent);
					historyService.executeOperation(operation, "Error while adding component");
				}
			}
		}
	}

	private <T> T loadAsset(IFile file, Class<T> type) {
		return GdxContext.get(editorId, () -> AssetService.load(getAssetPath(file), type));
	}

	private static String getAssetPath(IFile file) {
		String path = file.getLocation().toString();
		IPath rootAssetsFolder = file.getProject().getLocation().append("assets");
		IPath assetPath = new Path(path).makeRelativeTo(rootAssetsFolder);
		return assetPath.toString();
	}
}
