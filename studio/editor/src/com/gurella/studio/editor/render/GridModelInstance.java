package com.gurella.studio.editor.render;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.gurella.engine.event.EventService;
import com.gurella.engine.graphics.render.GenericBatch;
import com.gurella.studio.editor.subscription.EditorActiveCameraProvider;
import com.gurella.studio.editor.subscription.EditorCameraChangedListener;
import com.gurella.studio.editor.subscription.EditorPreCloseListener;

public class GridModelInstance implements EditorCameraChangedListener, EditorPreCloseListener {
	private final int editorId;

	private Model model;
	private ModelInstance instance;

	private Environment environment;

	private Camera camera;

	public GridModelInstance(int editorId) {
		this.editorId = editorId;
		init();
		EventService.post(editorId, EditorActiveCameraProvider.class, l -> camera = l.getActiveCamera());
		EventService.subscribe(editorId, this);
	}

	private void init() {
		ModelBuilder builder = new ModelBuilder();
		ColorAttribute diffuse = ColorAttribute.createDiffuse(Color.WHITE);
		model = builder.createLineGrid(20, 20, 0.5f, 0.5f, new Material(diffuse), Usage.Position | Usage.ColorUnpacked);
		instance = new ModelInstance(model);

		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.6f, 0.6f, 0.6f, 1f));
		environment.set(new DepthTestAttribute());
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
	}

	@Override
	public void cameraChanged(Camera camera) {
		this.camera = camera;
	}

	public void render(GenericBatch batch) {
		batch.begin(camera);
		batch.render(instance, environment);
		batch.end();
	}

	@Override
	public void onEditorPreClose() {
		EventService.unsubscribe(editorId, this);
		model.dispose();
	}
}
