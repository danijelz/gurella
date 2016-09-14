package com.gurella.studio.editor.scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

/**
 * https://github.com/mbrlabs/Mundus
 * 
 * @author Marcus Brummer
 */
public class Compass implements Disposable {
	private final float ARROW_LENGTH = 0.08f;
	private final float ARROW_THIKNESS = 0.4f;
	private final float ARROW_CAP_SIZE = 0.3f;
	private final int ARROW_DIVISIONS = 8;

	private PerspectiveCamera compassCamera;
	private PerspectiveCamera worldCamera;
	private Model compassModel;
	private ModelInstance compassInstance;
	private Environment environment;

	private Vector3 tempTranslation = new Vector3();
	private Quaternion tempRotation = new Quaternion();

	public Compass(PerspectiveCamera worldCamera) {
		this.worldCamera = worldCamera;
		this.compassCamera = new PerspectiveCamera();

		ModelBuilder modelBuilder = new ModelBuilder();
		modelBuilder.begin();

		int usages = Usage.Position | Usage.ColorUnpacked | Usage.Normal;
		MeshPartBuilder builder = modelBuilder.part("compass", GL20.GL_TRIANGLES, usages, new Material());
		builder.setColor(Color.RED);
		builder.arrow(0, 0, 0, ARROW_LENGTH, 0, 0, ARROW_CAP_SIZE, ARROW_THIKNESS, ARROW_DIVISIONS);
		builder.setColor(Color.GREEN);
		builder.arrow(0, 0, 0, 0, ARROW_LENGTH, 0, ARROW_CAP_SIZE, ARROW_THIKNESS, ARROW_DIVISIONS);
		builder.setColor(Color.BLUE);
		builder.arrow(0, 0, 0, 0, 0, -ARROW_LENGTH, ARROW_CAP_SIZE, ARROW_THIKNESS, ARROW_DIVISIONS);
		builder.setColor(Color.YELLOW);
		builder.box(0.02f, 0.02f, 0.02f);
		compassModel = modelBuilder.end();
		compassInstance = new ModelInstance(compassModel);

		// trans to top right corner
		compassInstance.transform.translate(tempTranslation.set(0.92f, 0.92f, 0));

		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.85f, 0.85f, 0.85f, 1f));
		environment.set(new DepthTestAttribute());
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
	}

	public void setWorldCam(PerspectiveCamera cam) {
		this.worldCamera = cam;
	}

	public void render(ModelBatch batch) {
		update();
		batch.setCamera(compassCamera);
		batch.render(compassInstance, environment);
	}

	private void update() {
		// compassInstance.transform.getTranslation(tempTranslation);
		// compassInstance.transform.set(worldCamera.view).inv();
		// compassInstance.transform.setTranslation(tempTranslation);
		worldCamera.view.getRotation(tempRotation);
		tempRotation.conjugate();
		compassInstance.transform.set(tempTranslation, tempRotation);
	}

	@Override
	public void dispose() {
		compassModel.dispose();
	}
}
