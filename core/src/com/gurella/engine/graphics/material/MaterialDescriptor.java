package com.gurella.engine.graphics.material;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.gurella.engine.base.object.ManagedObject;

public class MaterialDescriptor extends ManagedObject {
	public static final Color none = new Color(Color.WHITE);

	public Color diffuseColor = new Color(Color.WHITE);
	public final TextureAttributeProperties diffuseTexture = new TextureAttributeProperties();

	public Color specularColor = none;
	public final TextureAttributeProperties specularTexture = new TextureAttributeProperties();

	public Color ambientColor = none;
	public final TextureAttributeProperties ambientTexture = new TextureAttributeProperties();

	public Color emissiveColor = none;
	public final TextureAttributeProperties emissiveTexture = new TextureAttributeProperties();

	public Color reflectionColor = none;
	public final TextureAttributeProperties reflectionTexture = new TextureAttributeProperties();

	public final TextureAttributeProperties bumpTexture = new TextureAttributeProperties();

	public final TextureAttributeProperties normalTexture = new TextureAttributeProperties();

	public final BlendingAttributeProperties blend = new BlendingAttributeProperties();

	public float shininess;

	public float alphaTest;

	Cullface cullface = Cullface.back;

	public MaterialDescriptor() {
	}

	public MaterialDescriptor(Material material) {
		diffuseColor = setColorAttribute(diffuseColor, material, ColorAttribute.Diffuse);
	}

	private static Color setColorAttribute(Color currentValue, Material material, long colorAttributeType) {
		ColorAttribute colorAttribute = (ColorAttribute) material.get(colorAttributeType);
		if (colorAttribute == null) {
			return none;
		}
		if (currentValue == null || currentValue == none) {
			return new Color(colorAttribute.color);
		} else {
			return currentValue.set(colorAttribute.color);
		}
	}

	public Material createMaterial() {
		Material material = new Material();
		material.set(ColorAttribute.createDiffuse(diffuseColor));

		return material;
	}

	public static class TextureAttributeProperties {
		public Texture texture;
		public float offsetU = 0;
		public float offsetV = 0;
		public float scaleU = 1;
		public float scaleV = 1;
		public int uvIndex = 0;
	}

	public static class BlendingAttributeProperties {
		public boolean blend;
		public BlendFunction sourceFunction = BlendFunction.srcAlpha;
		public BlendFunction destFunction = BlendFunction.oneMinusSrcAlpha;
		public float opacity = 1.f;
	}

	public static enum Cullface {
		front(GL20.GL_FRONT), back(GL20.GL_BACK), frontAndBack(GL20.GL_FRONT_AND_BACK), none(-1);

		public final int glValue;

		private Cullface(int glValue) {
			this.glValue = glValue;
		}
	}

	public static enum BlendFunction {
		zero(GL20.GL_ZERO),

		one(GL20.GL_ONE),
		srcColor(GL20.GL_SRC_COLOR),

		oneMinusSrcColor(GL20.GL_ONE_MINUS_SRC_COLOR),

		dstColor(GL20.GL_DST_COLOR),

		oneMinusDstColor(GL20.GL_ONE_MINUS_DST_COLOR),

		srcAlpha(GL20.GL_SRC_ALPHA),

		oneMinusSrcAlpha(GL20.GL_ONE_MINUS_SRC_ALPHA),

		dstAlpha(GL20.GL_DST_ALPHA),

		oneMinusDstAlpha(GL20.GL_ONE_MINUS_DST_ALPHA),

		constantColor(GL20.GL_CONSTANT_COLOR),

		oneMinusConstantColor(GL20.GL_ONE_MINUS_CONSTANT_COLOR),

		constantAlpha(GL20.GL_CONSTANT_ALPHA),

		oneMinusConstantAlpha(GL20.GL_ONE_MINUS_CONSTANT_ALPHA),

		srcAlphaSaturate(GL20.GL_SRC_ALPHA_SATURATE);

		public final int glValue;

		private BlendFunction(int glValue) {
			this.glValue = glValue;
		}
	}
}
