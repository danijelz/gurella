package com.gurella.engine.scene.renderable.skybox;

import static com.badlogic.gdx.graphics.g3d.attributes.CubemapAttribute.EnvironmentMap;

import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.CubemapAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Disposable;
import com.gurella.engine.base.model.ModelDescriptor;
import com.gurella.engine.graphics.render.GenericBatch;
import com.gurella.engine.scene.renderable.RenderableComponent;

@ModelDescriptor(descriptiveName = "Skybox")
public class SkyboxComponent extends RenderableComponent implements Disposable {
	Cubemap cubemap;

	private Model boxModel;
	private ModelInstance boxInstance;
	private CubemapAttribute cubemapAttribute;

	public Cubemap getCubemap() {
		return cubemap;
	}

	public void setCubemap(Cubemap cubemap) {
		this.cubemap = cubemap;
		cubemapAttribute.textureDescription.texture = cubemap;
		CubemapAttribute attribute = (CubemapAttribute) boxInstance.materials.get(0).get(EnvironmentMap);
		attribute.textureDescription.texture = cubemap;
	}

	@Override
	protected void componentActivated() {
		if (boxModel == null) {
			boxModel = createModel();
			boxInstance = new ModelInstance(boxModel);
		}
	}

	private Model createModel() {
		ModelBuilder modelBuilder = new ModelBuilder();
		cubemapAttribute = new CubemapAttribute(CubemapAttribute.EnvironmentMap, cubemap);
		cubemapAttribute.textureDescription.magFilter = TextureFilter.Linear;
		cubemapAttribute.textureDescription.minFilter = TextureFilter.Linear;
		cubemapAttribute.textureDescription.uWrap = TextureWrap.ClampToEdge;
		cubemapAttribute.textureDescription.vWrap = TextureWrap.ClampToEdge;
		return modelBuilder.createBox(1, 1, 1, new Material(cubemapAttribute), VertexAttributes.Usage.Position);
	}

	@Override
	protected void updateGeometry() {
	}

	@Override
	protected void doRender(GenericBatch batch) {
		if (cubemap == null) {
			return;
		}
		batch.render(boxInstance, SkyboxShader.getInstance());
	}

	@Override
	protected void doGetBounds(BoundingBox bounds) {
		bounds.ext(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);
		bounds.ext(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
	}

	@Override
	protected boolean doGetIntersection(Ray ray, Vector3 intersection) {
		return false;
	}

	@Override
	public void dispose() {
		boxModel.dispose();
		cubemap.dispose();
	}
}
