package com.gurella.engine.graphics.material;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.gurella.engine.asset.properties.TextureProperties;
import com.gurella.engine.base.object.ManagedObject;

public class MaterialDescriptor extends ManagedObject {
	public Color diffuseColor;
	public final TextureAttributeProperties diffuseTexture = new TextureAttributeProperties();

	public Color specularColor;
	public final TextureAttributeProperties specularTexture = new TextureAttributeProperties();

	public Color ambientColor;
	public final TextureAttributeProperties ambientTexture = new TextureAttributeProperties();

	public Color emissiveColor;
	public final TextureAttributeProperties emissiveTexture = new TextureAttributeProperties();

	public Color reflectionColor;
	public final TextureAttributeProperties reflectionTexture = new TextureAttributeProperties();

	public final TextureAttributeProperties bumpTexture = new TextureAttributeProperties();

	public final TextureAttributeProperties normalTexture = new TextureAttributeProperties();

	public final BlendingAttributeProperties blend = new BlendingAttributeProperties();

	public final DepthTestAttributeProperties depthTest = new DepthTestAttributeProperties();

	public float shininess = Float.NaN;

	public float alphaTest = Float.NaN;

	Cullface cullface = null;

	public MaterialDescriptor() {
	}

	public MaterialDescriptor(Material material) {
		diffuseColor = extractColorAttribute(diffuseColor, material, ColorAttribute.Diffuse);
		extractTextureAttribute(diffuseTexture, material, TextureAttribute.Diffuse);
		specularColor = extractColorAttribute(specularColor, material, ColorAttribute.Specular);
		extractTextureAttribute(specularTexture, material, TextureAttribute.Specular);
		ambientColor = extractColorAttribute(ambientColor, material, ColorAttribute.Ambient);
		extractTextureAttribute(ambientTexture, material, TextureAttribute.Ambient);
		emissiveColor = extractColorAttribute(emissiveColor, material, ColorAttribute.Emissive);
		extractTextureAttribute(emissiveTexture, material, TextureAttribute.Emissive);
		reflectionColor = extractColorAttribute(reflectionColor, material, ColorAttribute.Reflection);
		extractTextureAttribute(reflectionTexture, material, TextureAttribute.Reflection);
		extractTextureAttribute(bumpTexture, material, TextureAttribute.Bump);
		extractTextureAttribute(normalTexture, material, TextureAttribute.Normal);
		extractBlendAttribute(material);
		extractDepthTestAttribute(material);
		shininess = extractFloatAttribute(material, FloatAttribute.Shininess);
		alphaTest = extractFloatAttribute(material, FloatAttribute.AlphaTest);
		extractCullfaceAttribute(material);
	}

	private static Color extractColorAttribute(Color currentValue, Material material, long attributeType) {
		ColorAttribute colorAttribute = (ColorAttribute) material.get(attributeType);
		if (colorAttribute == null) {
			return null;
		} else if (currentValue == null) {
			return new Color(colorAttribute.color);
		} else {
			return currentValue.set(colorAttribute.color);
		}
	}

	private static void extractTextureAttribute(TextureAttributeProperties value, Material material,
			long attributeType) {
		TextureAttribute attribute = (TextureAttribute) material.get(attributeType);
		if (attribute == null) {
			value.reset();
		} else {
			value.texture = attribute.textureDescription.texture;
			value.offsetU = attribute.offsetU;
			value.offsetV = attribute.offsetV;
			value.scaleU = attribute.scaleU;
			value.scaleV = attribute.scaleV;
			value.uvIndex = attribute.uvIndex;
		}
	}

	private static float extractFloatAttribute(Material material, long attributeType) {
		FloatAttribute attribute = (FloatAttribute) material.get(attributeType);
		if (attribute == null) {
			return Float.NaN;
		} else {
			return attribute.value;
		}
	}

	private void extractBlendAttribute(Material material) {
		BlendingAttribute attribute = (BlendingAttribute) material.get(BlendingAttribute.Type);
		if (attribute == null) {
			blend.reset();
		} else {
			blend.blended = false;
			blend.sourceFunction = BlendFunction.value(attribute.sourceFunction);
			blend.destFunction = BlendFunction.value(attribute.destFunction);
			blend.opacity = attribute.opacity;
		}
	}

	private void extractDepthTestAttribute(Material material) {
		DepthTestAttribute attribute = (DepthTestAttribute) material.get(DepthTestAttribute.Type);
		if (attribute == null) {
			depthTest.reset();
		} else {
			depthTest.depthMask = attribute.depthMask;
			depthTest.depthRangeNear = attribute.depthRangeNear;
			depthTest.depthRangeFar = attribute.depthRangeFar;
			depthTest.depthFunc = DepthTestFunction.value(attribute.depthFunc);
		}
	}

	private void extractCullfaceAttribute(Material material) {
		IntAttribute cullfaceAttribute = (IntAttribute) material.get(IntAttribute.CullFace);
		if (cullfaceAttribute == null) {
			cullface = null;
		} else {
			cullface = Cullface.value(cullfaceAttribute.value);
		}
	}

	public boolean isDiffuseColorEnabled() {
		return diffuseColor != null;
	}

	public boolean isDiffuseTextureEnabled() {
		return diffuseTexture.texture != null;
	}

	public boolean isSpecularColorEnabled() {
		return specularColor != null;
	}

	public boolean isSpecularTextureEnabled() {
		return specularTexture.texture != null;
	}

	public boolean isAmbientColorEnabled() {
		return ambientColor != null;
	}

	public boolean isAmbientTextureEnabled() {
		return ambientTexture.texture != null;
	}

	public boolean isEmissiveColorEnabled() {
		return emissiveColor != null;
	}

	public boolean isEmissiveTextureEnabled() {
		return emissiveTexture.texture != null;
	}

	public boolean isReflectionColorEnabled() {
		return reflectionColor != null;
	}

	public boolean isReflectionTextureEnabled() {
		return reflectionTexture.texture != null;
	}

	public boolean isBumpTextureEnabled() {
		return bumpTexture.texture != null;
	}

	public boolean isNormalTextureEnabled() {
		return normalTexture.texture != null;
	}

	public boolean isBlendEnabled() {
		return blend.blended && blend.sourceFunction != null && blend.destFunction != null;
	}

	public boolean isDepthTestEnabled() {
		return depthTest.enabled && depthTest.depthFunc != null;
	}

	public boolean isShininessEnabled() {
		return !Float.isNaN(shininess);
	}

	public boolean isAlphaTestEnabled() {
		return !Float.isNaN(alphaTest);
	}

	public boolean isCullfaceEnabled() {
		return cullface != null;
	}

	public VertexAttributes createVertexAttributes(boolean addPositionAttribute, boolean addNormalAttribute) {
		Array<VertexAttribute> attributes = new Array<VertexAttribute>();
		if (addPositionAttribute) {
			attributes.add(VertexAttribute.Position());
		}

		if (addNormalAttribute) {
			attributes.add(VertexAttribute.Normal());
		}

		if (isDiffuseTextureEnabled() || isSpecularTextureEnabled() || isNormalTextureEnabled()) {
			attributes.add(VertexAttribute.TexCoords(0));
		}
		
		if (isNormalTextureEnabled()) {
			attributes.add(VertexAttribute.Tangent());
		}

		return new VertexAttributes(attributes.<VertexAttribute> toArray(VertexAttribute.class));
	}

	public Material createMaterial() {
		Material material = new Material();

		if (isDiffuseColorEnabled()) {
			createColorAttribute(material, ColorAttribute.Diffuse, diffuseColor);
		}

		if (isDiffuseTextureEnabled()) {
			createTextureAttribute(material, TextureAttribute.Diffuse, diffuseTexture);
		}

		if (isSpecularColorEnabled()) {
			createColorAttribute(material, ColorAttribute.Specular, specularColor);
		}

		if (isSpecularTextureEnabled()) {
			createTextureAttribute(material, TextureAttribute.Specular, specularTexture);
		}

		if (isAmbientColorEnabled()) {
			createColorAttribute(material, ColorAttribute.Ambient, ambientColor);
		}

		if (isAmbientTextureEnabled()) {
			createTextureAttribute(material, TextureAttribute.Ambient, ambientTexture);
		}

		if (isEmissiveColorEnabled()) {
			createColorAttribute(material, ColorAttribute.Emissive, emissiveColor);
		}

		if (isEmissiveTextureEnabled()) {
			createTextureAttribute(material, TextureAttribute.Emissive, emissiveTexture);
		}

		if (isReflectionColorEnabled()) {
			createColorAttribute(material, ColorAttribute.Reflection, reflectionColor);
		}

		if (isReflectionTextureEnabled()) {
			createTextureAttribute(material, TextureAttribute.Reflection, reflectionTexture);
		}

		if (isBumpTextureEnabled()) {
			createTextureAttribute(material, TextureAttribute.Bump, reflectionTexture);
		}

		if (isNormalTextureEnabled()) {
			createTextureAttribute(material, TextureAttribute.Normal, reflectionTexture);
		}

		if (isShininessEnabled()) {
			material.set(FloatAttribute.createShininess(shininess));
		}

		if (isAlphaTestEnabled()) {
			material.set(FloatAttribute.createAlphaTest(alphaTest));
		}

		if (isCullfaceEnabled()) {
			material.set(IntAttribute.createCullFace(cullface.glValue));
		}

		if (isBlendEnabled()) {
			int sourceFunction = blend.sourceFunction.glValue;
			int destFunction = blend.destFunction.glValue;
			material.set(new BlendingAttribute(blend.blended, sourceFunction, destFunction, blend.opacity));
		}

		if (isDepthTestEnabled()) {
			int depthFunc = depthTest.depthFunc.glValue;
			material.set(new DepthTestAttribute(depthFunc, depthTest.depthRangeNear, depthTest.depthRangeFar,
					depthTest.depthMask));
		}

		return material;
	}

	private static void createColorAttribute(Material material, long type, Color color) {
		material.set(new ColorAttribute(type, color));
	}

	private static void createTextureAttribute(Material material, long type, TextureAttributeProperties properties) {
		TextureAttribute attribute = new TextureAttribute(type, properties.texture);
		attribute.offsetU = properties.offsetU;
		attribute.offsetV = properties.offsetV;
		attribute.scaleU = properties.scaleU;
		attribute.scaleV = properties.scaleV;
		attribute.uvIndex = properties.uvIndex;
		TextureProperties textureProperties = null;
		// TODO attribute.textureDescription.magFilter ... should be resolved from texture properties
		attribute.textureDescription.minFilter = TextureFilter.Linear;
		attribute.textureDescription.magFilter = TextureFilter.Linear;
		attribute.textureDescription.uWrap = TextureWrap.Repeat;
		attribute.textureDescription.vWrap = TextureWrap.Repeat;
		material.set(attribute);
	}

	public static class TextureAttributeProperties {
		public Texture texture;
		public float offsetU = 0;
		public float offsetV = 0;
		public float scaleU = 1;
		public float scaleV = 1;
		public int uvIndex = 0;

		private void reset() {
			texture = null;
			offsetU = 0;
			offsetV = 0;
			scaleU = 1;
			scaleV = 1;
			uvIndex = 0;
		}
	}

	public static class BlendingAttributeProperties {
		public boolean blended;
		public BlendFunction sourceFunction = BlendFunction.srcAlpha;
		public BlendFunction destFunction = BlendFunction.oneMinusSrcAlpha;
		public float opacity = 1.f;

		private void reset() {
			blended = true;
			sourceFunction = BlendFunction.srcAlpha;
			destFunction = BlendFunction.oneMinusSrcAlpha;
			opacity = 1.f;
		}
	}

	public static class DepthTestAttributeProperties {
		public DepthTestFunction depthFunc = DepthTestFunction.lequal;
		public float depthRangeNear = 0;
		public float depthRangeFar = 1;
		public boolean depthMask = true;
		public boolean enabled;

		private void reset() {
			depthFunc = DepthTestFunction.lequal;
			depthRangeNear = 0;
			depthRangeFar = 1;
			depthMask = true;
			enabled = false;
		}
	}

	public static enum Cullface {
		front(GL20.GL_FRONT), back(GL20.GL_BACK), frontAndBack(GL20.GL_FRONT_AND_BACK);

		private static IntMap<Cullface> valuesByGlValue = new IntMap<Cullface>();

		static {
			Cullface[] values = values();
			for (int i = 0, n = values.length; i < n; i++) {
				Cullface value = values[i];
				valuesByGlValue.put(value.glValue, value);
			}
		}

		public final int glValue;

		private Cullface(int glValue) {
			this.glValue = glValue;
		}

		public static Cullface value(int glValue) {
			return valuesByGlValue.get(glValue);
		}
	}

	public static enum DepthTestFunction {
		never(GL20.GL_NEVER),

		less(GL20.GL_LESS),

		equal(GL20.GL_EQUAL),

		lequal(GL20.GL_LEQUAL),

		greater(GL20.GL_GREATER),

		notequal(GL20.GL_NOTEQUAL),

		gequal(GL20.GL_GEQUAL),

		always(GL20.GL_ALWAYS);

		private static IntMap<DepthTestFunction> valuesByGlValue = new IntMap<DepthTestFunction>();

		static {
			DepthTestFunction[] values = values();
			for (int i = 0, n = values.length; i < n; i++) {
				DepthTestFunction value = values[i];
				valuesByGlValue.put(value.glValue, value);
			}
		}

		public final int glValue;

		private DepthTestFunction(int glValue) {
			this.glValue = glValue;
		}

		public static DepthTestFunction value(int glValue) {
			return valuesByGlValue.get(glValue);
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

		oneMinusConstantAlpha(GL20.GL_ONE_MINUS_CONSTANT_ALPHA);

		private static IntMap<BlendFunction> functionsByGlValue = new IntMap<BlendFunction>();

		static {
			BlendFunction[] values = values();
			for (int i = 0, n = values.length; i < n; i++) {
				BlendFunction value = values[i];
				functionsByGlValue.put(value.glValue, value);
			}
		}

		public final int glValue;

		private BlendFunction(int glValue) {
			this.glValue = glValue;
		}

		public static BlendFunction value(int glValue) {
			return functionsByGlValue.get(glValue);
		}
	}
}
