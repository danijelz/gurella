package com.gurella.engine.graphics.material;

import com.badlogic.gdx.graphics.Color;
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
import com.gurella.engine.asset2.properties.TextureProperties;
import com.gurella.engine.graphics.render.gl.BlendFunction;
import com.gurella.engine.graphics.render.gl.CullFace;
import com.gurella.engine.graphics.render.gl.DepthTestFunction;
import com.gurella.engine.managedobject.ManagedObject;

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

	CullFace cullFace = null;

	private final Material sharedMaterial = new Material();

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
		extractCullFaceAttribute(material);
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

	private void extractCullFaceAttribute(Material material) {
		IntAttribute cullFaceAttribute = (IntAttribute) material.get(IntAttribute.CullFace);
		if (cullFaceAttribute == null) {
			cullFace = null;
		} else {
			cullFace = CullFace.value(cullFaceAttribute.value);
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

	public boolean isCullFaceEnabled() {
		return CullFace.isEnabled(cullFace);
	}

	public VertexAttributes createVertexAttributes(boolean addPositionAttribute, boolean addNormalAttribute) {
		Array<VertexAttribute> attributes = new Array<VertexAttribute>();
		if (addPositionAttribute) {
			attributes.add(VertexAttribute.Position());
		}

		if (addNormalAttribute) {
			attributes.add(VertexAttribute.Normal());
		}

		if (isDiffuseTextureEnabled() || isSpecularTextureEnabled() || isEmissiveTextureEnabled()
				|| isNormalTextureEnabled()) {
			attributes.add(VertexAttribute.TexCoords(0));
		}

		if (isNormalTextureEnabled()) {
			attributes.add(VertexAttribute.Tangent());
		} else {
			attributes.add(VertexAttribute.Tangent());
		}

		return new VertexAttributes(attributes.<VertexAttribute> toArray(VertexAttribute.class));
	}

	public Material createMaterial() {
		Material material = new Material();
		updateMaterial(material);
		return material;
	}

	public Material getMaterial() {
		updateMaterial(sharedMaterial);
		return sharedMaterial;
	}

	public void updateMaterial(Material material) {
		material.clear();

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

		if (isCullFaceEnabled()) {
			material.set(IntAttribute.createCullFace(cullFace.glValue));
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

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Float.floatToIntBits(offsetU);
			result = prime * result + Float.floatToIntBits(offsetV);
			result = prime * result + Float.floatToIntBits(scaleU);
			result = prime * result + Float.floatToIntBits(scaleV);
			result = prime * result + ((texture == null) ? 0 : texture.hashCode());
			result = prime * result + uvIndex;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}

			if (obj == null) {
				return false;
			}

			if (getClass() != obj.getClass()) {
				return false;
			}

			TextureAttributeProperties other = (TextureAttributeProperties) obj;
			if (Float.floatToIntBits(offsetU) != Float.floatToIntBits(other.offsetU)) {
				return false;
			}

			if (Float.floatToIntBits(offsetV) != Float.floatToIntBits(other.offsetV)) {
				return false;
			}

			if (Float.floatToIntBits(scaleU) != Float.floatToIntBits(other.scaleU)) {
				return false;
			}

			if (Float.floatToIntBits(scaleV) != Float.floatToIntBits(other.scaleV)) {
				return false;
			}

			if (texture != other.texture) {
				return false;
			}

			if (uvIndex != other.uvIndex) {
				return false;
			}

			return true;
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

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (blended ? 1231 : 1237);
			result = prime * result + ((destFunction == null) ? 0 : destFunction.hashCode());
			result = prime * result + Float.floatToIntBits(opacity);
			result = prime * result + ((sourceFunction == null) ? 0 : sourceFunction.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}

			if (obj == null) {
				return false;
			}

			if (getClass() != obj.getClass()) {
				return false;
			}

			BlendingAttributeProperties other = (BlendingAttributeProperties) obj;
			if (blended != other.blended) {
				return false;
			}

			if (destFunction != other.destFunction) {
				return false;
			}

			if (Float.floatToIntBits(opacity) != Float.floatToIntBits(other.opacity)) {
				return false;
			}

			if (sourceFunction != other.sourceFunction) {
				return false;
			}

			return true;
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

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((depthFunc == null) ? 0 : depthFunc.hashCode());
			result = prime * result + (depthMask ? 1231 : 1237);
			result = prime * result + Float.floatToIntBits(depthRangeFar);
			result = prime * result + Float.floatToIntBits(depthRangeNear);
			result = prime * result + (enabled ? 1231 : 1237);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}

			if (obj == null) {
				return false;
			}

			if (getClass() != obj.getClass()) {
				return false;
			}

			DepthTestAttributeProperties other = (DepthTestAttributeProperties) obj;
			if (depthFunc != other.depthFunc) {
				return false;
			}

			if (depthMask != other.depthMask) {
				return false;
			}

			if (Float.floatToIntBits(depthRangeFar) != Float.floatToIntBits(other.depthRangeFar)) {
				return false;
			}

			if (Float.floatToIntBits(depthRangeNear) != Float.floatToIntBits(other.depthRangeNear)) {
				return false;
			}

			if (enabled != other.enabled) {
				return false;
			}

			return true;
		}
	}
}
