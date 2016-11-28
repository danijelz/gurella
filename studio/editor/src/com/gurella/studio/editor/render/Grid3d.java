package com.gurella.studio.editor.render;

import com.badlogic.gdx.graphics.Camera;
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
import com.gurella.engine.plugin.Workbench;
import com.gurella.studio.editor.camera.CameraProviderExtension;
import com.gurella.studio.editor.subscription.EditorCloseListener;

public class Grid3d implements Grid, CameraProviderExtension, EditorCloseListener {
	private final int editorId;

	private Model model;
	private ModelInstance instance;

	private Environment environment;

	private Camera camera;

	public Grid3d(int editorId) {
		this.editorId = editorId;
		init();
		Workbench.activate(this);
		EventService.subscribe(editorId, this);
	}

	private void init() {
		ModelBuilder builder = new ModelBuilder();
		ColorAttribute diffuse = ColorAttribute.createDiffuse(color);
		model = builder.createLineGrid(100, 100, 0.5f, 0.5f, new Material(diffuse), Usage.Position | Usage.ColorUnpacked);
		instance = new ModelInstance(model);

		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.6f, 0.6f, 0.6f, 1f));
		environment.set(new DepthTestAttribute());
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
	}

	@Override
	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	public void render(GenericBatch batch) {
		if (camera == null) {
			return;
		}

		batch.begin(camera);
		batch.render(instance, environment);
		batch.end();
	}

	@Override
	public void onEditorClose() {
		EventService.unsubscribe(editorId, this);
		Workbench.deactivate(this);
		model.dispose();
	}
}
