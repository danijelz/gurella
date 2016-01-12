package com.gurella.engine.base.serialization;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.SerializationException;
import com.gurella.engine.asset.Assets;
import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.Models;
import com.gurella.engine.base.registry.ManagedObject;
import com.gurella.engine.base.resource.AsyncCallback;
import com.gurella.engine.base.resource.ResourceService;

public class ObjectArchive {
	AsyncCallback<Object> callback;
	String fileName;
	Object rootObject;
	Class<?> knownType;

	Json json = new Json();
	JsonValue serializedObject;

	private Array<AssetReference> dependentAssets = new Array<AssetReference>();
	private Array<ObjectReference> dependentObjects = new Array<ObjectReference>();
	private IntMap<ManagedObject> objects = new IntMap<ManagedObject>();
	private Array<ManagedObject> slicedObjects = new Array<ManagedObject>();

	public <T> void serialize(T rootObject, Class<T> knownType) {
		StringWriter buffer = new StringWriter();
		json.setWriter(buffer);
		Model<T> model = Models.getModel(rootObject);
		model.serialize(rootObject, knownType, this);
		System.out.print(json.prettyPrint(buffer.toString()));
		json.fromJson(Test.class, json.toJson(rootObject));
	}

	public static void main(String[] args) {
		Object[][] f = new Object[][] { { "1", "11" }, { "2" } };
		Object[][] s = new Object[][] { { "1" }, { "2" } };
		Object[][] t = new Object[1][];
		long[][] ll1 = null;
		long[][] ll2 = null;
		Arrays.equals(ll1, ll2);
		boolean b = f == s;
		Test obj = new Test();
		obj.i = 8;
		obj.s = "sss";
		obj.a = new String[] { "bbb", "eee" };
		new ObjectArchive().serialize(new Test(), Test.class);
		new ObjectArchive().serialize(obj, Test.class);
	}

	private static class Test {
		public int i = 2;
		public String s = "ddd";
		public Object[] a = new String[] { "sss" };
	}

	public void writeObjectStart(Object value, Class<?> knownType) {
		json.writeObjectStart();
		Class<? extends Object> actualType = value.getClass();
		if (knownType != actualType) {
			json.writeType(actualType);
		}
	}

	public void writeObjectEnd() {
		json.writeObjectEnd();
	}

	public void writeArrayStart() {
		json.writeArrayStart();
	}

	public void writeArrayStart(String name) {
		json.writeArrayStart(name);
	}

	public void writeArrayEnd() {
		json.writeArrayEnd();
	}

	public void writeValue(String name, Object value, Class<?> knownType) {
		if (value == null || Serialization.isSimpleTypeOrPrimitive(value)) {
			json.writeValue(name, value, knownType);
		} else if (value instanceof ManagedObject) {

		} else if (Assets.isAsset(value)) {
			AssetReference reference = new AssetReference(ResourceService.getResourceFileName(value), value.getClass());
			if (Assets.isAssetType(knownType)) {

			}
		} else {
			Model<Object> model = Models.getModel(value);
			try {
				json.getWriter().name(name);
			} catch (IOException ex) {
				throw new SerializationException(ex);
			}
			model.serialize(value, knownType, this);
		}
	}

	public void writeValue(Object value, Class<?> knownType) {
		if (value == null || Serialization.isSimpleTypeOrPrimitive(value)) {
			json.writeValue(value, knownType);
		} else if (value instanceof ManagedObject) {

		} else if (Assets.isAsset(value)) {

		} else {
			Model<Object> model = Models.getModel(value);
			model.serialize(value, knownType, this);
		}
	}
}
