package com.gurella.studio.editor.dnd;

import org.eclipse.core.resources.IFile;
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
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.gurella.engine.asset.AssetService;
import com.gurella.engine.asset.AssetType;
import com.gurella.engine.event.EventService;
import com.gurella.engine.plugin.Workbench;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneNode2;
import com.gurella.engine.scene.input.InputSystem;
import com.gurella.engine.scene.input.PickResult;
import com.gurella.engine.scene.renderable.ModelComponent;
import com.gurella.engine.scene.renderable.RenderableComponent2d;
import com.gurella.engine.scene.renderable.TextureComponent;
import com.gurella.engine.scene.transform.TransformComponent;
import com.gurella.studio.editor.SceneEditorRegistry;
import com.gurella.studio.editor.assets.AssetSelection;
import com.gurella.studio.editor.camera.CameraProviderExtension;
import com.gurella.studio.editor.operation.AddNodeOperation;
import com.gurella.studio.editor.subscription.EditorCloseListener;
import com.gurella.studio.editor.subscription.EditorPreRenderUpdateListener;
import com.gurella.studio.editor.subscription.EditorRenderUpdateListener;
import com.gurella.studio.editor.subscription.SceneLoadedListener;

public class DndAssetPlacementManager implements SceneLoadedListener, CameraProviderExtension,
		EditorPreRenderUpdateListener, EditorRenderUpdateListener, EditorCloseListener {
	private final int editorId;
	private final GLCanvas glCanvas;

	private final LocalSelectionTransfer transfer = LocalSelectionTransfer.getTransfer();

	private Scene scene;
	private InputSystem inputSystem;
	private Camera camera;
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

		EventService.subscribe(editorId, this);
		Workbench.activate(this);
	}

	@Override
	public void sceneLoaded(Scene scene) {
		this.scene = scene;
		inputSystem = scene.inputSystem;
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
			unloadTemporaryAssets();
		}
	}

	private void unloadTemporaryAssets() {
		assetFile = null;
		if (model != null) {
			AssetService.unload(model);
			model = null;
			modelInstance = null;
		} else {
			AssetService.unload(texture);
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
		inputSystem.pickNode(pickResult, cursorLocation.x, cursorLocation.y, camera, null);

		if (pickResult.isPositive()) {
			position.set(pickResult.location);
		} else {
			position.set(ray.origin).add(temp.set(ray.direction).scl(3f));
		}

		modelInstance.transform.setTranslation(position);
		pickResult.reset();
	}

	private void updateSprite() {
		camera.update();
		Point cursorLocation = glCanvas.getDisplay().getCursorLocation();
		cursorLocation = glCanvas.toControl(cursorLocation);
		final Ray ray = camera.getPickRay(cursorLocation.x, cursorLocation.y);
		inputSystem.pickNode(pickResult, cursorLocation.x, cursorLocation.y, camera, null);

		if (pickResult.isPositive()) {
			position.set(pickResult.location);
		} else {
			position.set(ray.origin).add(temp.set(ray.direction).scl(3f));
		}

		getSpriteBatch().getTransformMatrix().setToTranslation(position);
		pickResult.reset();
	}

	private IFile getTransferingAssetFile() {
		ISelection selection = transfer.getSelection();
		if (!(selection instanceof AssetSelection)) {
			return null;
		}

		AssetSelection assetSelection = (AssetSelection) selection;
		IFile file = assetSelection.getAssetFile();
		String fileExtension = file.getFileExtension();
		return AssetType.isValidExtension(Model.class, fileExtension)
				|| AssetType.isValidExtension(Texture.class, fileExtension) ? file : null;
	}

	@Override
	public void onEditorClose() {
		EventService.unsubscribe(editorId, this);
		Workbench.deactivate(this);
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

			if (event.detail == DND.DROP_DEFAULT) {
				event.detail = DND.DROP_COPY;
			}

			String fileExtension = assetFile.getFileExtension();
			if (AssetType.isValidExtension(Model.class, fileExtension)) {
				model = AssetService.load(getAssetPath(), Model.class);
				modelInstance = new ModelInstance(model);
				updateModelInstance();
			} else {
				texture = AssetService.load(getAssetPath(), Texture.class);
				sprite.setTexture(texture);
				sprite.setRegion(0, 0, texture.getWidth(), texture.getHeight());
				int ratio = RenderableComponent2d.textureImportPixelsPerUnit;
				sprite.setSize(texture.getWidth() / ratio, texture.getHeight() / ratio);
				sprite.setCenter(0, 0);
				sprite.setOriginCenter();
				updateSprite();
			}
		}

		private String getAssetPath() {
			String path = assetFile.getLocation().toString();
			IPath rootAssetsFolder = assetFile.getProject().getLocation().append("assets");
			IPath assetPath = new Path(path).makeRelativeTo(rootAssetsFolder);
			return assetPath.toString();
		}
		
		@Override
		public void dragLeave(DropTargetEvent event) {
			unloadTemporaryAssetsIfDragEnded();
		}

		@Override
		public void drop(DropTargetEvent event) {
			// TODO handle resource deprendencies
			if (model != null) {
				updateModelInstance();
				SceneNode2 node = scene.newNode("Model");
				TransformComponent transformComponent = node.newComponent(TransformComponent.class);
				transformComponent.setTranslation(position);
				ModelComponent modelComponent = node.newComponent(ModelComponent.class);
				modelComponent.setModel(AssetService.load(getAssetPath(), Model.class));
				AddNodeOperation operation = new AddNodeOperation(editorId, scene, null, node);
				SceneEditorRegistry.getContext(editorId).executeOperation(operation, "Error while adding node");
			} else {
				updateSprite();
				SceneNode2 node = scene.newNode("Sprite");
				TransformComponent transformComponent = node.newComponent(TransformComponent.class);
				if(camera instanceof OrthographicCamera) {
					position.z = 0;
				}
				transformComponent.setTranslation(position);
				TextureComponent textureComponent = node.newComponent(TextureComponent.class);
				textureComponent.updateTexture(AssetService.load(getAssetPath(), Texture.class));
				AddNodeOperation operation = new AddNodeOperation(editorId, scene, null, node);
				SceneEditorRegistry.getContext(editorId).executeOperation(operation, "Error while adding node");
			}

			unloadTemporaryAssets();
		}
	}
}
