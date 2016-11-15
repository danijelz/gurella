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
import org.eclipse.swt.widgets.Display;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
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
import com.gurella.engine.scene.renderable.ModelComponent;
import com.gurella.engine.scene.transform.TransformComponent;
import com.gurella.studio.editor.SceneEditorRegistry;
import com.gurella.studio.editor.assets.AssetSelection;
import com.gurella.studio.editor.camera.CameraProviderExtension;
import com.gurella.studio.editor.operation.AddNodeOperation;
import com.gurella.studio.editor.subscription.EditorCloseListener;
import com.gurella.studio.editor.subscription.EditorPreRenderUpdateListener;
import com.gurella.studio.editor.subscription.EditorRenderUpdateListener;
import com.gurella.studio.editor.subscription.SceneLoadedListener;
import com.gurella.studio.editor.utils.UiUtils;

public class DndAssetPlacementManager implements SceneLoadedListener, CameraProviderExtension,
		EditorPreRenderUpdateListener, EditorRenderUpdateListener, EditorCloseListener {
	private final int editorId;
	private final GLCanvas glCanvas;

	private DropTarget dropTarget;
	private final LocalSelectionTransfer transfer = LocalSelectionTransfer.getTransfer();

	private Scene scene;
	private Camera camera;

	private IFile assetFile;
	private Model loadedModel;
	private ModelInstance modelInstance;
	private Texture loadedTexture;

	private final Vector3 temp = new Vector3();
	private final Vector3 temp1 = new Vector3();
	private Vector3 lastPosition = new Vector3();
	private boolean initTranslate = true;
	private ModelBatch batch;


	public DndAssetPlacementManager(int editorId, GLCanvas glCanvas) {
		this.editorId = editorId;
		this.glCanvas = glCanvas;

		dropTarget = new DropTarget(glCanvas, DND.DROP_MOVE);
		dropTarget.setTransfer(new Transfer[] { transfer });
		dropTarget.addDropListener(new DropTargetListener());

		EventService.subscribe(editorId, this);
		Workbench.activate(this);
	}

	@Override
	public void sceneLoaded(Scene scene) {
		this.scene = scene;
	}

	@Override
	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	@Override
	public void onPreRenderUpdate() {
		if (assetFile == null || getTransferingAssetFile() == assetFile) {
			return;
		}

		assetFile = null;
		if (loadedModel != null) {
			AssetService.unload(loadedModel);
			loadedModel = null;
		}
		if (loadedTexture != null) {
			AssetService.unload(loadedTexture);
			loadedTexture = null;
		}
	}

	@Override
	public void onRenderUpdate() {
		if (camera == null || (loadedModel == null && loadedTexture == null)) {
			return;
		}

		if (loadedModel != null) {
			modelInstance.transform.getTranslation(temp);
			modelInstance.transform.getTranslation(temp1);

			camera.update();
			Point cursorLocation = glCanvas.getDisplay().getCursorLocation();
			cursorLocation = glCanvas.toControl(cursorLocation);
			final Ray ray = camera.getPickRay(cursorLocation.x, cursorLocation.y);
			Vector3 rayEnd = temp;
			float dst = camera.position.dst(rayEnd);
			rayEnd = ray.getEndPoint(rayEnd, dst);
			temp1.set(rayEnd.x - lastPosition.x, 0, rayEnd.z - lastPosition.z);
			temp1.add(0, rayEnd.y - lastPosition.y, 0);
			modelInstance.transform.translate(temp1);
			lastPosition.set(rayEnd);

			if (batch == null) {
				batch = new ModelBatch();
			}

			// TODO TerrainUtils.getRayIntersectionAndUp(context.currScene.terrains, ray);
			// updateModelPosition();
			// modelInstance.transform.setTranslation(modelPosition);
			batch.begin(camera);
			batch.render(modelInstance);
			batch.end();
		}
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
		dropTarget.dispose();
	}

	private final class DropTargetListener extends DropTargetAdapter {
		@Override
		public void dragEnter(DropTargetEvent event) {
			if (scene == null || camera == null || (event.operations & DND.DROP_MOVE) == 0) {
				event.detail = DND.DROP_NONE;
				return;
			}

			assetFile = getTransferingAssetFile();
			if (assetFile == null) {
				event.detail = DND.DROP_NONE;
				return;
			}

			if (event.detail == DND.DROP_DEFAULT) {
				event.detail = DND.DROP_MOVE;
			}

			String fileExtension = assetFile.getFileExtension();
			if (AssetType.isValidExtension(Model.class, fileExtension)) {
				event.feedback = DND.FEEDBACK_NONE;
				loadedModel = AssetService.load(getAssetPath(), Model.class);
				modelInstance = new ModelInstance(loadedModel);

				camera.update();
				Point cursorLocation = glCanvas.getDisplay().getCursorLocation();
				cursorLocation = glCanvas.toControl(cursorLocation);
				final Ray ray = camera.getPickRay(cursorLocation.x, cursorLocation.y);
				Vector3 rayEnd = temp;
				float dst = camera.position.dst(rayEnd);
				rayEnd = ray.getEndPoint(rayEnd, dst);
				lastPosition.set(ray.origin);
				modelInstance.transform.setTranslation(ray.origin);
				temp.set(lastPosition);
				temp1.set(lastPosition);
			} else {
				loadedTexture = AssetService.load(getAssetPath(), Texture.class);
			}
		}

		private String getAssetPath() {
			String path = assetFile.getLocation().toString();
			IPath rootAssetsFolder = assetFile.getProject().getLocation().append("assets");
			IPath assetPath = new Path(path).makeRelativeTo(rootAssetsFolder);
			return assetPath.toString();
		}
		
		@Override
		public void dragOver(DropTargetEvent event) {
			event.feedback = DND.FEEDBACK_NONE;
		}

		@Override
		public void drop(DropTargetEvent event) {
			if (modelInstance != null) {
				// updateModelPosition();
				SceneNode2 node = scene.newNode("Model");
				TransformComponent transformComponent = node.newComponent(TransformComponent.class);
				transformComponent.setTranslation(lastPosition);
				ModelComponent modelComponent = node.newComponent(ModelComponent.class);
				modelComponent.setModel(AssetService.load(getAssetPath(), Model.class));
				AddNodeOperation operation = new AddNodeOperation(editorId, scene, null, node);
				SceneEditorRegistry.getContext(editorId).executeOperation(operation, "Error while adding node");
			}
		}
	}
}
