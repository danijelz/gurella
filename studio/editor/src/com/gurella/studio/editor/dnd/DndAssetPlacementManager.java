package com.gurella.studio.editor.dnd;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.opengl.GLCanvas;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.gurella.engine.asset.Assets;
import com.gurella.engine.plugin.Workbench;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneNode;
import com.gurella.engine.scene.input.InputSystem;
import com.gurella.engine.scene.input.PickResult;
import com.gurella.engine.scene.renderable.ModelComponent;
import com.gurella.engine.scene.renderable.TextureComponent;
import com.gurella.engine.scene.transform.TransformComponent;
import com.gurella.studio.common.AssetsFolderLocator;
import com.gurella.studio.editor.SceneConsumer;
import com.gurella.studio.editor.assets.AssetSelection;
import com.gurella.studio.editor.camera.CameraConsumer;
import com.gurella.studio.editor.history.HistoryContributor;
import com.gurella.studio.editor.history.HistoryService;
import com.gurella.studio.editor.operation.AddNodeOperation;
import com.gurella.studio.editor.subscription.EditorCloseListener;
import com.gurella.studio.editor.subscription.EditorPreRenderUpdateListener;
import com.gurella.studio.editor.subscription.EditorRenderUpdateListener;
import com.gurella.studio.gdx.GdxContext;

//TODO unload assets
public class DndAssetPlacementManager implements SceneConsumer, HistoryContributor, CameraConsumer,
		EditorPreRenderUpdateListener, EditorRenderUpdateListener, EditorCloseListener {
	private final int editorId;
	private final GLCanvas glCanvas;

	private final LocalSelectionTransfer transfer = LocalSelectionTransfer.getTransfer();

	private Scene scene;
	private InputSystem inputSystem;
	private Camera camera;
	private HistoryService historyService;

	private ModelBatch modelBatch;
	private SpriteBatch spriteBatch;

	private IFile assetFile;
	private Model model;
	private ModelInstance modelInstance;
	private Texture texture;
	private final Sprite sprite = new Sprite();

	private final PickResult pickResult = new PickResult();
	private final Vector3 position = new Vector3();
	private final Vector3 temp = new Vector3();

	public DndAssetPlacementManager(int editorId, GLCanvas glCanvas) {
		this.editorId = editorId;
		this.glCanvas = glCanvas;

		DropTarget dropTarget = new DropTarget(glCanvas, DND.DROP_DEFAULT | DND.DROP_COPY);
		dropTarget.setTransfer(new Transfer[] { transfer });
		dropTarget.addDropListener(new DropTargetListener());

		GdxContext.subscribe(editorId, editorId, this);
		Workbench.activate(editorId, this);
	}

	@Override
	public void setScene(Scene scene) {
		this.scene = scene;
		inputSystem = scene.inputSystem;
	}

	@Override
	public void setHistoryService(HistoryService historyService) {
		this.historyService = historyService;
	}

	@Override
	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	@Override
	public void onPreRenderUpdate() {
		unloadTemporaryAssetsIfDragEnded();
	}

	protected void unloadTemporaryAssetsIfDragEnded() {
		if (assetFile != null && getTransferingAssetFile() != assetFile) {
			GdxContext.run(editorId, this::unloadTemporaryAssets);
		}
	}

	private void unloadTemporaryAssets() {
		assetFile = null;
		if (model != null) {
			GdxContext.unload(editorId, model);
			model = null;
			modelInstance = null;
		} else {
			GdxContext.unload(editorId, texture);
			texture = null;
			sprite.setTexture(null);
		}
	}

	@Override
	public void onRenderUpdate() {
		if (camera == null || (model == null && texture == null)) {
			return;
		}

		if (model != null) {
			updateModelInstance();
			ModelBatch batch = getModelBatch();
			batch.begin(camera);
			batch.render(modelInstance);
			batch.end();
		} else {
			updateSprite();
			SpriteBatch batch = getSpriteBatch();
			batch.setProjectionMatrix(camera.combined);
			batch.begin();
			sprite.draw(batch);
			batch.end();
		}
	}

	private ModelBatch getModelBatch() {
		if (modelBatch == null) {
			modelBatch = new ModelBatch();
		}
		return modelBatch;
	}

	private SpriteBatch getSpriteBatch() {
		if (spriteBatch == null) {
			spriteBatch = new SpriteBatch();
		}
		return spriteBatch;
	}

	private void updateModelInstance() {
		camera.update();
		Point cursorLocation = glCanvas.getDisplay().getCursorLocation();
		cursorLocation = glCanvas.toControl(cursorLocation);
		final Ray ray = camera.getPickRay(cursorLocation.x, cursorLocation.y);

		if (camera instanceof PerspectiveCamera) {
			inputSystem.pickNode(pickResult, cursorLocation.x, cursorLocation.y, camera, null);
			if (pickResult.isPositive()) {
				position.set(pickResult.location);
			} else {
				position.set(ray.origin).add(temp.set(ray.direction).scl(3f));
			}
		} else {
			position.set(ray.origin);
		}

		modelInstance.transform.setTranslation(position);
		pickResult.reset();
	}

	private void updateSprite() {
		camera.update();
		Point cursorLocation = glCanvas.getDisplay().getCursorLocation();
		cursorLocation = glCanvas.toControl(cursorLocation);
		final Ray ray = camera.getPickRay(cursorLocation.x, cursorLocation.y);

		if (camera instanceof PerspectiveCamera) {
			inputSystem.pickNode(pickResult, cursorLocation.x, cursorLocation.y, camera, null);
			if (pickResult.isPositive()) {
				position.set(pickResult.location);
			} else {
				position.set(ray.origin).add(temp.set(ray.direction).scl(3f));
			}
		} else {
			position.set(ray.origin);
		}

		getSpriteBatch().getTransformMatrix().setToTranslation(position);
		pickResult.reset();
	}

	private IFile getTransferingAssetFile() {
		ISelection selection = transfer.getSelection();
		if (!(selection instanceof AssetSelection)) {
			return null;
		}

		IResource resource = ((AssetSelection) selection).getAssetResource();
		if (resource instanceof IFile) {
			IFile file = (IFile) resource;
			return isValidAssetExtension(file.getFileExtension()) ? file : null;
		} else {
			return null;
		}
	}

	protected boolean isValidAssetExtension(String fileExtension) {
		return Assets.isValidExtension(Model.class, fileExtension)
				|| Assets.isValidExtension(Texture.class, fileExtension);
	}

	@Override
	public void onEditorClose() {
		GdxContext.unsubscribe(editorId, editorId, this);
		Workbench.deactivate(editorId, this);
	}

	private final class DropTargetListener extends DropTargetAdapter {
		@Override
		public void dragEnter(DropTargetEvent event) {
			if (scene == null || camera == null || (event.operations & DND.DROP_COPY) == 0) {
				event.detail = DND.DROP_NONE;
				return;
			}

			assetFile = getTransferingAssetFile();
			if (assetFile == null) {
				event.detail = DND.DROP_NONE;
				return;
			}

			event.detail = DND.DROP_COPY;

			String fileExtension = assetFile.getFileExtension();
			if (Assets.isValidExtension(Model.class, fileExtension)) {
				model = loadAsset(Model.class);
				modelInstance = new ModelInstance(model);
				updateModelInstance();
			} else {
				texture = loadAsset(Texture.class);
				sprite.setTexture(texture);
				sprite.setRegion(0, 0, texture.getWidth(), texture.getHeight());
				int ratio = camera instanceof OrthographicCamera ? 1 : 100;
				sprite.setSize(texture.getWidth() / ratio, texture.getHeight() / ratio);
				sprite.setCenter(0, 0);
				sprite.setOriginCenter();
				updateSprite();
			}
		}

		private <T> T loadAsset(Class<T> type) {
			return GdxContext.load(editorId, getAssetPath(), type);
		}

		private String getAssetPath() {
			String path = assetFile.getLocation().toString();
			IPath rootAssetsFolder = AssetsFolderLocator.getAssetsFolder(assetFile).getLocation();
			IPath assetPath = new Path(path).makeRelativeTo(rootAssetsFolder);
			return assetPath.toString();
		}

		@Override
		public void dragLeave(DropTargetEvent event) {
			event.detail = DND.DROP_NONE;
			unloadTemporaryAssetsIfDragEnded();
		}

		@Override
		public void drop(DropTargetEvent event) {
			// TODO handle resource deprendencies
			event.detail = DND.DROP_NONE;
			if (model != null) {
				updateModelInstance();
				SceneNode node = scene.newNode("Model");
				TransformComponent transformComponent = node.newComponent(TransformComponent.class);
				transformComponent.setTranslation(position);
				ModelComponent modelComponent = node.newComponent(ModelComponent.class);
				modelComponent.setModel(GdxContext.load(editorId, getAssetPath(), Model.class));
				AddNodeOperation operation = new AddNodeOperation(editorId, scene, null, node);
				historyService.executeOperation(operation, "Error while adding node");
			} else {
				updateSprite();
				SceneNode node = scene.newNode("Sprite");
				TransformComponent transformComponent = node.newComponent(TransformComponent.class);
				TextureComponent textureComponent = node.newComponent(TextureComponent.class);
				Texture texture = GdxContext.load(editorId, getAssetPath(), Texture.class);
				if (camera instanceof OrthographicCamera) {
					transformComponent.setTranslation(position.x, position.y, 0);
					textureComponent.setTexture(texture, texture.getWidth(), texture.getHeight());
				} else {
					transformComponent.setTranslation(position);
					textureComponent.setTexture(texture, texture.getWidth() / 100, texture.getHeight() / 100);
				}

				AddNodeOperation operation = new AddNodeOperation(editorId, scene, null, node);
				historyService.executeOperation(operation, "Error while adding node");
			}

			unloadTemporaryAssets();
		}
	}
}
