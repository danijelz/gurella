package com.gurella.engine.base.serialization;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.SerializationException;
import com.gurella.engine.asset.Assets;
import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.Models;
import com.gurella.engine.base.registry.InitializationContext;
import com.gurella.engine.base.registry.ManagedObject;
import com.gurella.engine.base.registry.Objects;
import com.gurella.engine.base.resource.AsyncCallback;
import com.gurella.engine.base.resource.ResourceService;
import com.gurella.engine.utils.ArrayExt;
import com.gurella.engine.utils.IdentityObjectIntMap;

public class Archive implements Poolable {
	AsyncCallback<Object> callback;
	String fileName;

	Json json = new Json();

	private Array<String> externalFileNames = new Array<String>();
	private Array<ExternalDependency> externalDependencies = new Array<ExternalDependency>();

	private int currentId;
	private IdentityObjectIntMap<Object> internalIds = new IdentityObjectIntMap<Object>();

	private Array<Object> serializedObjectStack = new Array<Object>();
	private Array<Object> templateStack = new Array<Object>();
	private Array<JsonValue> serializedValueStack = new Array<JsonValue>();

	@Override
	public void reset() {
		callback = null;
		fileName = null;

		externalFileNames.clear();
		externalDependencies.clear();

		currentId = 0;
		internalIds.clear();
		externalFileNames.clear();
	}

	public <T> void serialize(T rootObject, Class<?> knownType) {
		StringWriter buffer = new StringWriter();
		JsonWriter jsonWriter = new JsonWriter(buffer);
		json.setWriter(jsonWriter);

		internalIds.put(rootObject, currentId++);
		Model<T> objectModel = Models.getModel(rootObject);
		objectModel.serialize(rootObject, knownType, this);

		System.out.println(json.prettyPrint(buffer.toString()));
		// System.out.println(json.prettyPrint(json.toJson(rootObject)));
		reset();
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

	public void writeValue(Object value, Class<?> knownType) {
		if (value == null || Serialization.isSimpleType(value)) {
			json.writeValue(value, knownType);
		} else if (value instanceof ManagedObject) {
			ManagedObject managedObject = (ManagedObject) value;
			String resourceFileName = ResourceService.getResourceFileName(managedObject);
			boolean isLocalObject = fileName.equals(resourceFileName);
			if (isLocalObject) {
				if (internalIds.get(value, -1) > 0) {
					ObjectReference objectReference = new ObjectReference(managedObject.getId(), null);
					json.writeValue(objectReference, ObjectReference.class);
				} else {
					Model<Object> model = Models.getModel(value);
					model.serialize(value, knownType, this);
				}
			} else {
				ExternalDependency dependency = new ExternalDependency();
				dependency.typeName = ManagedObject.class.getName();
				dependency.fileName = resourceFileName;
				externalDependencies.add(dependency);
				externalFileNames.add(resourceFileName);

				ObjectReference objectReference = new ObjectReference(managedObject.getId(), resourceFileName);
				json.writeValue(objectReference, ObjectReference.class);
			}
		} else if (Assets.isAsset(value)) {
			AssetReference reference = new AssetReference(ResourceService.getResourceFileName(value), value.getClass());
			if (Assets.isAssetType(knownType)) {

			}
		} else {
			Model<Object> model = Models.getModel(value);
			model.serialize(value, knownType, this);
		}
	}

	private Array<Object> objects = new Array<Object>();

	public void writeValue1(Object value, Class<?> knownType) {
		if (value == null || Serialization.isSimpleType(value)) {
			json.writeValue(value, knownType);
		} else if (value instanceof ManagedObject) {
			ManagedObject managedObject = (ManagedObject) value;
			String resourceFileName = ResourceService.getResourceFileName(managedObject);
			boolean isLocalObject = fileName.equals(resourceFileName);
			if (isLocalObject) {
				if (internalIds.get(value, -1) > 0) {
					ObjectReference objectReference = new ObjectReference(managedObject.getId(), null);
					json.writeValue(objectReference, ObjectReference.class);
				} else {
					Model<Object> model = Models.getModel(value);
					model.serialize(value, knownType, this);
				}
			} else {
				ExternalDependency dependency = new ExternalDependency();
				dependency.typeName = ManagedObject.class.getName();
				dependency.fileName = resourceFileName;
				externalDependencies.add(dependency);
				externalFileNames.add(resourceFileName);

				ObjectReference objectReference = new ObjectReference(managedObject.getId(), resourceFileName);
				json.writeValue(objectReference, ObjectReference.class);
			}
		} else if (Assets.isAsset(value)) {
			AssetReference reference = new AssetReference(ResourceService.getResourceFileName(value), value.getClass());
			if (Assets.isAssetType(knownType)) {

			}
		} else {
			int internalId = internalIds.get(value, -1);
			if (internalId > 0) {
				json.writeValue(Integer.valueOf(internalId), int.class);
			} else {
				if (value instanceof ManagedObject) {
					ManagedObject managedObject = (ManagedObject) value;
					String resourceFileName = ResourceService.getResourceFileName(managedObject);
					boolean isLocalObject = fileName.equals(resourceFileName);
					if (isLocalObject) {
						internalIds.put(value, currentId);
						json.writeValue(Integer.valueOf(currentId), int.class);
						currentId++;
						objects.add(value);
					} else {
						ExternalDependency dependency = new ExternalDependency();
						dependency.typeName = ManagedObject.class.getName();
						dependency.fileName = resourceFileName;
						externalDependencies.add(dependency);
						externalFileNames.add(resourceFileName);

						ObjectReference objectReference = new ObjectReference(managedObject.getId(), resourceFileName);
						json.writeValue(objectReference, ObjectReference.class);
					}
				} else {
					internalIds.put(value, currentId);
					json.writeValue(Integer.valueOf(currentId), int.class);
					currentId++;
					objects.add(value);
				}
			}
		}
	}

	private static class ExternalDependency {
		String typeName;
		String fileName;
	}

	public static void main(String[] args) {
		String str = "{\n" + "i: 8\n" + "s: sss\n" + "a: [\n" + "	{\n"
				+ "		class: com.gurella.engine.base.serialization.ArrayType\n"
				+ "		typeName: \"[Ljava.lang.String;\"\n" + "	}\n" + "	bbb\n" + "	eee\n" + "]\n" + "t1: {\n"
				+ "	i1: 5\n" + "}\n" + "arr: {\n" + "	componentType: java.lang.String\n" + "	items: [\n"
				+ "		value\n" + "		ddd\n" + "	]\n" + "}\n" + "}\n";

		Test obj = new Test();
		obj.i = 8;
		obj.s = "sss";
		obj.a = new String[] { "bbb", "eee" };
		obj.t1.i1 = 5;
		obj.arr.add("ddd");

		JsonValue value = new JsonReader().parse(str);
		InitializationContext context = new InitializationContext();
		Model<Test> model = Models.getModel(Test.class);
		context.push(null, null, value);
		Test instance = model.createInstance(context);
		context.setInitializingObject(instance);
		model.initInstance(context);

		System.out.println(Objects.isEqual(obj, instance));

		new Archive().serialize(new Test(), Test.class);
		new Archive().serialize(obj, Test.class);
	}

	public static class Test {
		public int i = 2;
		public String s = "ddd";
		public Object[] a = new String[] { "sss" };
		public Test1 t1 = new Test1();
		public ArrayExt<String> arr;

		public Test() {
			arr = new ArrayExt<String>(String.class);
			arr.add("value");
		}

		@Override
		public String toString() {
			return "Test [i=" + i + ", s=" + s + ", a=" + Arrays.toString(a) + ", t1=" + t1 + ", arr=" + arr + "]";
		}
	}

	public static class Test1 {
		public int i1 = 2;
		public String s1 = "ddd";

		@Override
		public String toString() {
			return "Test1 [i1=" + i1 + ", s1=" + s1 + "]";
		}
	}
}
