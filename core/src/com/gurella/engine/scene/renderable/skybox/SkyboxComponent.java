package com.gurella.engine.scene.renderable.skybox;

import static com.badlogic.gdx.graphics.VertexAttributes.Usage.Position;
import static com.badlogic.gdx.graphics.g3d.attributes.CubemapAttribute.EnvironmentMap;

import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.CubemapAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Disposable;
import com.gurella.engine.graphics.render.GenericBatch;
import com.gurella.engine.metatype.MetaTypeDescriptor;
import com.gurella.engine.scene.renderable.Layer;
import com.gurella.engine.scene.renderable.RenderableComponent;

@MetaTypeDescriptor(descriptiveName = "Skybox")
public class SkyboxComponent extends RenderableComponent implements Disposable {
	Pixmap texture;

	private Pixmap positiveX;
	private Pixmap negativeX;
	private Pixmap positiveY;
	private Pixmap negativeY;
	private Pixmap positiveZ;
	private Pixmap negativeZ;

	private Cubemap cubemap;

	private Model boxModel;
	private ModelInstance boxInstance;
	private CubemapAttribute cubemapAttribute;

	public SkyboxComponent() {
		setLayer(Layer.SKY);
	}

	public Pixmap getTexture() {
		return texture;
	}

	public void setTexture(Pixmap sky) {
		disposeData();
		this.texture = sky;
		if (texture != null && isActive()) {
			initData();
		}
	}

	private void initData() {
		int width = texture.getWidth() / 4;
		int height = texture.getHeight() / 3;

		negativeX = new Pixmap(width, height, texture.getFormat());
		negativeX.drawPixmap(texture, 0, 0, 0, height, width, height);

		positiveY = new Pixmap(width, height, texture.getFormat());
		positiveY.drawPixmap(texture, 0, 0, width, 0, width, height);

		positiveZ = new Pixmap(width, height, texture.getFormat());
		positiveZ.drawPixmap(texture, 0, 0, width, height, width, height);

		negativeY = new Pixmap(width, height, texture.getFormat());
		negativeY.drawPixmap(texture, 0, 0, width, height * 2, width, height);

		positiveX = new Pixmap(width, height, texture.getFormat());
		positiveX.drawPixmap(texture, 0, 0, width * 2, height, width, height);

		negativeZ = new Pixmap(width, height, texture.getFormat());
		negativeZ.drawPixmap(texture, 0, 0, width * 3, height, width, height);

		cubemap = new Cubemap(positiveX, negativeX, positiveY, negativeY, positiveZ, negativeZ);

		if (cubemapAttribute == null) {
			cubemapAttribute = new CubemapAttribute(CubemapAttribute.EnvironmentMap, cubemap);
			cubemapAttribute.textureDescription.magFilter = TextureFilter.Linear;
			cubemapAttribute.textureDescription.minFilter = TextureFilter.Linear;
			cubemapAttribute.textureDescription.uWrap = TextureWrap.ClampToEdge;
			cubemapAttribute.textureDescription.vWrap = TextureWrap.ClampToEdge;
		} else {
			cubemapAttribute.textureDescription.texture = cubemap;
			CubemapAttribute attribute = (CubemapAttribute) boxInstance.materials.get(0).get(EnvironmentMap);
			attribute.textureDescription.texture = cubemap;
		}

		if (boxModel == null) {
			boxModel = new ModelBuilder().createBox(1, 1, 1, new Material(cubemapAttribute), Position);
			boxInstance = new ModelInstance(boxModel);
		}
	}

	@Override
	protected void componentActivated() {
		if (texture != null && cubemap == null) {
			initData();
		}
	}

	@Override
	protected void updateGeometry() {
	}

	@Override
	protected void doRender(GenericBatch batch) {
		if (cubemap != null) {
			batch.render(boxInstance, SkyboxShader.getInstance());
		}
	}

	@Override
	protected void calculateBounds(BoundingBox bounds) {
		bounds.ext(-Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE);
		bounds.ext(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
	}

	@Override
	public void dispose() {
		texture = null;
		disposeData();
		if (boxModel != null) {
			boxModel.dispose();
		}
	}

	protected void disposeData() {
		if (cubemap == null) {
			return;
		}

		cubemap.dispose();
		cubemap = null;

		positiveX.dispose();
		positiveX = null;

		negativeX.dispose();
		negativeX = null;

		positiveY.dispose();
		positiveY = null;

		negativeY.dispose();
		negativeY = null;

		positiveZ.dispose();
		positiveZ = null;

		negativeZ.dispose();
		negativeZ = null;
	}
}
