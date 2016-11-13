package com.gurella.studio.editor.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.ArrowShapeBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.gurella.engine.event.EventService;
import com.gurella.engine.graphics.render.GenericBatch;
import com.gurella.engine.plugin.Workbench;
import com.gurella.studio.editor.camera.CameraProviderExtension;
import com.gurella.studio.editor.subscription.EditorCloseListener;
import com.gurella.studio.editor.subscription.EditorPreRenderUpdateListener;

/**
 * Adapted from https://github.com/mbrlabs/Mundus
 * 
 * @author Marcus Brummer
 */
public class Compass implements EditorCloseListener, EditorPreRenderUpdateListener, CameraProviderExtension {
	private final float ARROW_LENGTH = 0.08f;
	private final float ARROW_THIKNESS = 0.4f;
	private final float ARROW_CAP_SIZE = 0.3f;
	private final int ARROW_DIVISIONS = 8;

	private final int editorId;

	private PerspectiveCamera compassCamera;
	private Model compassModel;
	private ModelInstance compassInstance;
	private Environment environment;

	private Vector3 tempTranslation = new Vector3();
	private Vector3 tempScale = new Vector3(12, 12, 12);
	private Quaternion tempRotation = new Quaternion();

	private Camera worldCamera;

	public Compass(int editorId) {
		this.editorId = editorId;

		this.compassCamera = new PerspectiveCamera();
		ModelBuilder modelBuilder = new ModelBuilder();
		modelBuilder.begin();

		int usages = Usage.Position | Usage.ColorUnpacked | Usage.Normal;
		MeshPartBuilder builder = modelBuilder.part("compass", GL20.GL_TRIANGLES, usages, new Material());
		builder.setColor(Color.RED);
		ArrowShapeBuilder.build(builder, 0, 0, 0, ARROW_LENGTH, 0, 0, ARROW_CAP_SIZE, ARROW_THIKNESS, ARROW_DIVISIONS);
		builder.setColor(Color.GREEN);
		ArrowShapeBuilder.build(builder, 0, 0, 0, 0, ARROW_LENGTH, 0, ARROW_CAP_SIZE, ARROW_THIKNESS, ARROW_DIVISIONS);
		builder.setColor(Color.BLUE);
		ArrowShapeBuilder.build(builder, 0, 0, 0, 0, 0, -ARROW_LENGTH, ARROW_CAP_SIZE, ARROW_THIKNESS, ARROW_DIVISIONS);
		builder.setColor(Color.YELLOW);
		BoxShapeBuilder.build(builder, 0.02f, 0.02f, 0.02f);
		compassModel = modelBuilder.end();
		compassInstance = new ModelInstance(compassModel);

		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.85f, 0.85f, 0.85f, 1f));
		environment.set(new DepthTestAttribute());
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

		EventService.subscribe(editorId, this);
		Workbench.activate(this);
	}

	@Override
	public void setCamera(Camera camera) {
		this.worldCamera = camera;
	}

	public void render(GenericBatch batch) {
		Graphics graphics = Gdx.graphics;
		int width = graphics.getWidth();
		int height = graphics.getHeight();
		batch.begin(compassCamera);
		Gdx.gl.glViewport(width - 60, height - 60, 60, 60);
		batch.render(compassInstance, environment);
		batch.end();
		Gdx.gl.glViewport(0, 0, width, height);
	}

	@Override
	public void onPreRenderUpdate() {
		if (worldCamera == null) {
			return;
		}

		worldCamera.view.getRotation(tempRotation);
		tempRotation.conjugate();
		compassInstance.transform.set(tempTranslation, tempRotation, tempScale);
	}

	@Override
	public void onEditorClose() {
		EventService.unsubscribe(editorId, this);
		Workbench.deactivate(this);
		compassModel.dispose();
	}
}
