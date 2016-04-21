package com.gurella.engine.graphics.material;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.gurella.engine.base.object.ManagedObject;

//TODO unused
public abstract class MaterialDescriptor extends ManagedObject {
	public Color diffuseColor;
	public TextureAttributeProperties diffuseTexture;

	public Color specularColor;
	public TextureAttributeProperties specularTexture;

	public Color ambientColor;
	public TextureAttributeProperties ambientTexture;

	public Color emissiveColor;
	public TextureAttributeProperties emissiveTexture;

	public Color reflectionColor;
	public TextureAttributeProperties reflectionTexture;

	public TextureAttributeProperties bumpTexture;

	public TextureAttributeProperties normalTexture;

	public BlendingAttributeProperties blend;

	public float shininess;

	public float alphaTest;

	Cullface cullface = Cullface.back;

	public static class TextureAttributeProperties {
		public Texture texture;
		public float offsetU = 0;
		public float offsetV = 0;
		public float scaleU = 1;
		public float scaleV = 1;
	}

	public static class BlendingAttributeProperties {
		public BlendFunction sourceFunction = BlendFunction.srcAlpha;
		public BlendFunction destFunction = BlendFunction.oneMinusSrcAlpha;
		public float opacity = 1.f;
	}

	public enum Cullface {
		front(GL20.GL_FRONT), back(GL20.GL_BACK), frontAndBack(GL20.GL_FRONT_AND_BACK), none(-1);

		public final int glValue;

		private Cullface(int glValue) {
			this.glValue = glValue;
		}
	}

	public enum BlendFunction {
		zero(GL20.GL_ZERO),

		one(GL20.GL_ONE), srcColor(GL20.GL_SRC_COLOR),

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
