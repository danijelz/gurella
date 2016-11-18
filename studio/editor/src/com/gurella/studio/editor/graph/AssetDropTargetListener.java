package com.gurella.studio.editor.graph;

import org.eclipse.core.resources.IFile;
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
import com.gurella.engine.scene.SceneNode2;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.engine.scene.renderable.ModelComponent;
import com.gurella.engine.scene.renderable.TextureComponent;
import com.gurella.studio.editor.SceneEditorContext;
import com.gurella.studio.editor.SceneEditorRegistry;
import com.gurella.studio.editor.assets.AssetSelection;
import com.gurella.studio.editor.operation.AddComponentOperation;

class AssetDropTargetListener extends DropTargetAdapter {
	private static final LocalSelectionTransfer localTransfer = LocalSelectionTransfer.getTransfer();
	private final SceneEditorContext context;

	AssetDropTargetListener(SceneEditorContext context) {
		this.context = context;
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
		ISelection selection = localTransfer.getSelection();
		if (!(selection instanceof AssetSelection)) {
			return null;
		}

		IFile file = ((AssetSelection) selection).getAssetFile();
		return getComponentType(file) == null ? null : file;
	}

	protected boolean isValidAssetExtension(String fileExtension) {
		return AssetType.isValidExtension(Model.class, fileExtension)
				|| AssetType.isValidExtension(Texture.class, fileExtension);
	}

	@Override
	public void dragOver(DropTargetEvent event) {
		event.feedback = DND.FEEDBACK_EXPAND | DND.FEEDBACK_SCROLL;

		if (event.item == null) {
			event.detail = DND.DROP_NONE;
			return;
		}

		TreeItem item = (TreeItem) event.item;
		Object data = item.getData();
		if (!(data instanceof SceneNode2)) {
			event.detail = DND.DROP_NONE;
			return;
		}

		IFile file = getTransferingAssetFile();
		if (file == null) {
			event.detail = DND.DROP_NONE;
			return;
		}

		SceneNode2 node = (SceneNode2) data;
		Class<? extends SceneNodeComponent2> type = getComponentType(file);
		if (node.getComponent(type) == null) {
			event.detail = DND.DROP_COPY;
			event.feedback |= DND.FEEDBACK_SELECT;
		} else {
			event.detail = DND.DROP_NONE;
		}
	}

	private static Class<? extends SceneNodeComponent2> getComponentType(IFile file) {
		String fileExtension = file.getFileExtension();

		if (AssetType.isValidExtension(Model.class, fileExtension)) {
			return ModelComponent.class;
		} else if (AssetType.isValidExtension(Texture.class, fileExtension)) {
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
		if (event.item == null) {
			event.detail = DND.DROP_NONE;
			return;
		}

		TreeItem item = (TreeItem) event.item;
		Object data = item.getData();
		if (!(data instanceof SceneNode2)) {
			event.detail = DND.DROP_NONE;
			return;
		}

		IFile file = getTransferingAssetFile();
		if (file == null) {
			event.detail = DND.DROP_NONE;
			return;
		}

		SceneNode2 node = (SceneNode2) data;
		Class<? extends SceneNodeComponent2> type = getComponentType(file);
		if (node.getComponent(type) != null) {
			event.detail = DND.DROP_NONE;
			return;
		}

		if (type == ModelComponent.class) {
			ModelComponent modelComponent = node.newComponent(ModelComponent.class);
			modelComponent.setModel(AssetService.load(getAssetPath(file), Model.class));
			int editorId = context.editorId;
			AddComponentOperation operation = new AddComponentOperation(editorId, node, modelComponent);
			SceneEditorRegistry.getContext(editorId).executeOperation(operation, "Error while adding component");
		} else if (type == TextureComponent.class) {
			TextureComponent textureComponent = node.newComponent(TextureComponent.class);
			Texture texture = AssetService.load(getAssetPath(file), Texture.class);
			textureComponent.setTexture(texture, texture.getWidth() / 100, texture.getHeight() / 100);
			int editorId = context.editorId;
			AddComponentOperation operation = new AddComponentOperation(editorId, node, textureComponent);
			SceneEditorRegistry.getContext(editorId).executeOperation(operation, "Error while adding component");
		}
	}

	private static String getAssetPath(IFile file) {
		String path = file.getLocation().toString();
		IPath rootAssetsFolder = file.getProject().getLocation().append("assets");
		IPath assetPath = new Path(path).makeRelativeTo(rootAssetsFolder);
		return assetPath.toString();
	}
}
