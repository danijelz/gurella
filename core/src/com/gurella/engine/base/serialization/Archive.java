package com.gurella.engine.base.serialization;

import java.io.IOException;
import java.io.StringWriter;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.SerializationException;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.gurella.engine.asset.Assets;
import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.Models;
import com.gurella.engine.base.registry.ManagedObject;
import com.gurella.engine.base.resource.AsyncCallback;
import com.gurella.engine.base.resource.ResourceService;
import com.gurella.engine.utils.ArrayExt;
import com.gurella.engine.utils.IdentityObjectIntMap;

public class Archive {
	AsyncCallback<Object> callback;
	String fileName;
	Object rootObject;
	Class<?> knownType;

	Json json = new Json();
	JsonValue serializedObject;

	private Array<String> externalFileNames = new Array<String>();
	private Array<ExternalDependency> externalDependencies = new Array<ExternalDependency>();
	private Array<Object> objects = new Array<Object>();

	private int currentId;
	private IdentityObjectIntMap<Object> internalIds = new IdentityObjectIntMap<Object>();

	private Array<Object> serializingObjects = new Array<Object>();

	public <T> void serialize2(T rootObject) {
		StringWriter buffer = new StringWriter();
		JsonWriter jsonWriter = new JsonWriter(buffer);
		json.setWriter(jsonWriter);
		
		json.writeObjectStart();
		json.writeArrayStart("objects");

		addObject(rootObject);
		while (serializingObjects.size > 0) {
			Object object = serializingObjects.removeIndex(0);
			Model<Object> objectModel = Models.getModel(object);
			objectModel.serialize(object, null, this);
			if(serializingObjects.size > 0) {
				write(',');
			}
		}
		json.writeArrayEnd();
		
		json.writeObjectEnd();

		System.out.println(json.prettyPrint(buffer.toString()));
		// System.out.println(json.prettyPrint(json.toJson(rootObject)));
	}

	private int addObject(Object object) {
		int internalId = internalIds.get(object, -1);
		if(internalId < 0) {
			internalIds.put(object, currentId);
			serializingObjects.add(rootObject);
			return currentId++;
		} else {
			return internalId;
		}
	}

	private void serializeObject() {

	}

	public <T> void serialize(T rootObject, Class<T> knownType) {
		StringWriter buffer = new StringWriter();
		json.setWriter(buffer);

		if (rootObject instanceof ManagedObject) {
			objects.add(rootObject);
		}

		Model<T> model = Models.getModel(rootObject);
		model.serialize(rootObject, knownType, this);

		while (serializingObjects.size > 0) {
			ManagedObject managedObject = (ManagedObject) serializingObjects.removeIndex(0);
			Model<ManagedObject> objectModel = Models.getModel(managedObject);
			objectModel.serialize(managedObject, null, this);
		}

		System.out.println(json.prettyPrint(buffer.toString()));
		// System.out.println(json.prettyPrint(json.toJson(rootObject)));
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
		writeName(name);
		writeValue(value, knownType);
	}

	private void writeName(String name) {
		try {
			json.getWriter().name(name);
		} catch (IOException ex) {
			throw new SerializationException(ex);
		}
	}
	
	private void write(char c) {
		try {
			json.getWriter().write(c);
		} catch (IOException ex) {
			throw new SerializationException(ex);
		}
	}

	public void writeValue(Object value, Class<?> knownType) {
		if (value == null || Serialization.isSimpleType(value)) {
			json.writeValue(value, knownType);
		} else if (value instanceof ManagedObject) {
			ManagedObject managedObject = (ManagedObject) value;
			String resourceFileName = ResourceService.getResourceFileName(managedObject);
			boolean isLocalObject = fileName.equals(resourceFileName);

			if (isLocalObject && !objects.contains(managedObject, true)) {
				objects.add(managedObject);
				serializingObjects.add(managedObject);
			} else if (!isLocalObject && !externalFileNames.contains(resourceFileName, false)) {
				ExternalDependency dependency = new ExternalDependency();
				dependency.typeName = ManagedObject.class.getName();
				dependency.fileName = resourceFileName;
				externalDependencies.add(dependency);
				externalFileNames.add(resourceFileName);
			}

			String referenceFileName = isLocalObject ? null : resourceFileName;
			ObjectReference objectReference = new ObjectReference(managedObject.getId(), referenceFileName);
			Class<ObjectReference> valueType = ClassReflection.isAssignableFrom(ManagedObject.class, knownType)
					? ObjectReference.class : null;
			json.writeValue(objectReference, valueType);
		} else if (Assets.isAsset(value)) {
			AssetReference reference = new AssetReference(ResourceService.getResourceFileName(value), value.getClass());
			if (Assets.isAssetType(knownType)) {

			}
		} else {
			Model<Object> model = Models.getModel(value);
			model.serialize(value, knownType, this);
		}
	}

	private static class ExternalDependency {
		String typeName;
		String fileName;
	}

	public static void main(String[] args) {
		Test obj = new Test();
		obj.i = 8;
		obj.s = "sss";
		obj.a = new String[] { "bbb", "eee" };
		obj.t1.i1 = 5;
		obj.arr.add("ddd");
		new Archive().serialize(new Test(), Test.class);
		new Archive().serialize(obj, Test.class);
	}

	private static class Test {
		public int i = 2;
		public String s = "ddd";
		public Object[] a = new String[] { "sss" };
		public Test1 t1 = new Test1();
		public ArrayExt<String> arr;

		public Test() {
			arr = new ArrayExt<String>(String.class);
			arr.add("value");
		}
	}

	private static class Test1 {
		public int i1 = 2;
		public String s1 = "ddd";
	}
}
