package com.gurella.engine.serialization.json;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.gurella.engine.graphics.material.MaterialDescriptor;
import com.gurella.engine.managedobject.ManagedObject;
import com.gurella.engine.metatype.DefaultMetaType.SimpleMetaType;
import com.gurella.engine.metatype.MetaTypes;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneNode;
import com.gurella.engine.scene.audio.AudioListenerComponent;
import com.gurella.engine.scene.audio.AudioSourceComponent;
import com.gurella.engine.scene.bullet.rigidbody.BulletRigidBodyComponent;
import com.gurella.engine.scene.camera.OrtographicCameraComponent;
import com.gurella.engine.scene.camera.PerspectiveCameraComponent;
import com.gurella.engine.scene.light.DirectionalLightComponent;
import com.gurella.engine.scene.light.PointLightComponent;
import com.gurella.engine.scene.light.SpotLightComponent;
import com.gurella.engine.scene.renderable.ModelComponent;
import com.gurella.engine.scene.renderable.ShapeComponent;
import com.gurella.engine.scene.renderable.TextureComponent;
import com.gurella.engine.scene.renderable.skybox.SkyboxComponent;
import com.gurella.engine.scene.tag.TagComponent;
import com.gurella.engine.scene.transform.TransformComponent;
import com.gurella.engine.scene.velocity.LinearVelocityComponent;
import com.gurella.engine.utils.Reflection;

public class JsonSerialization {
	static final String typeTag = "#";
	static final String valueTag = "v";
	static final String dependenciesTag = "d";
	static final String arrayType = "[";
	static final String arrayTypeTag = "t";
	static final String assetReferenceType = "@";
	static final String assetReferenceIndexTag = "p";

	private static final ObjectMap<String, String> typeAbbreviations = new ObjectMap<String, String>();
	private static final ObjectMap<Class<?>, String> abbreviatedTypes = new ObjectMap<Class<?>, String>();

	private JsonSerialization() {
	}

	static {
		int i = 0;
		addToAbbrevations(ManagedObject.class, i++);
		addToAbbrevations(Scene.class, i++);
		addToAbbrevations(SceneNode.class, i++);
		addToAbbrevations(TransformComponent.class, i++);
		addToAbbrevations(AudioListenerComponent.class, i++);
		addToAbbrevations(AudioSourceComponent.class, i++);
		addToAbbrevations(BulletRigidBodyComponent.class, i++);
		addToAbbrevations(OrtographicCameraComponent.class, i++);
		addToAbbrevations(PerspectiveCameraComponent.class, i++);
		addToAbbrevations(DirectionalLightComponent.class, i++);
		addToAbbrevations(PointLightComponent.class, i++);
		addToAbbrevations(SpotLightComponent.class, i++);
		addToAbbrevations(LinearVelocityComponent.class, i++);
		addToAbbrevations(TextureComponent.class, i++);
		addToAbbrevations(ModelComponent.class, i++);
		addToAbbrevations(ShapeComponent.class, i++);
		addToAbbrevations(SkyboxComponent.class, i++);
		addToAbbrevations(TagComponent.class, i++);
		addToAbbrevations(MaterialDescriptor.class, i++);
		addToAbbrevations(Texture.class, i++);
		addToAbbrevations(Pixmap.class, i++);
		addToAbbrevations(Model.class, i++);
		addToAbbrevations(Sound.class, i++);
		addToAbbrevations(Music.class, i++);
	}

	private static void addToAbbrevations(Class<?> type, int id) {
		String strId = String.valueOf(id);
		typeAbbreviations.put(strId, type.getName());
		abbreviatedTypes.put(type, strId);
	}

	static String deserializeType(String serializedTypeName) {
		if (serializedTypeName == null) {
			return null;
		}

		char c = serializedTypeName.charAt(0);
		if (c >= '0' && c <= '9') {
			return typeAbbreviations.get(serializedTypeName, serializedTypeName);
		} else {
			return serializedTypeName;
		}
	}

	static String serializeType(Class<?> type) {
		String typeName = abbreviatedTypes.get(type);
		return typeName == null ? type.getName() : typeName;
	}

	static <T> Class<T> resolveObjectType(Class<T> knownType, JsonValue serializedObject) {
		Class<T> resolvedType = resolveObjectType(serializedObject);
		if (resolvedType != null) {
			return resolvedType;
		} else if (knownType != null) {
			return knownType;
		} else {
			throw new GdxRuntimeException("Can't resolve serialized object type.");
		}
	}

	static <T> Class<T> resolveObjectType(JsonValue serializedObject) {
		if (serializedObject.isArray()) {
			if (serializedObject.size > 0) {
				JsonValue itemValue = serializedObject.child;
				if (arrayType.equals(itemValue.getString(typeTag, null))) {
					return Reflection.forName(deserializeType(itemValue.getString(arrayTypeTag)));
				}
			}
		} else if (serializedObject.isObject()) {
			String type = deserializeType(serializedObject.getString(typeTag, null));
			if (type != null) {
				return Reflection.<T> forName(type);
			}
		}

		return null;
	}

	static boolean isSimpleType(Object obj) {
		return isSimpleType(obj.getClass());
	}

	static boolean isSimpleType(Class<?> type) {
		return type.isPrimitive() || MetaTypes.getMetaType(type) instanceof SimpleMetaType;
	}

	static Class<?> resolveOutputType(Class<?> type) {
		return (ClassReflection.isAssignableFrom(Enum.class, type) && type.getEnumConstants() == null)
				? type.getSuperclass() : type;
	}

	static <T> AssetDescriptor<T> createAssetDescriptor(String strValue) {
		int index = strValue.indexOf(' ');
		String typeName = strValue.substring(0, index);
		String fileName = strValue.substring(index + 1, strValue.length());
		Class<T> assetType = Reflection.forName(deserializeType(typeName));
		return new AssetDescriptor<T>(Gdx.files.internal(fileName), assetType);
	}
}
