package com.gurella.engine.base.serialization;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.BooleanArray;
import com.badlogic.gdx.utils.IntSet;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.utils.OrderedSet;
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
	JsonWriter writer;

	private Array<String> externalFileNames = new Array<String>();
	private Array<ExternalDependency> externalDependencies = new Array<ExternalDependency>();

	private int currentId;
	private IdentityObjectIntMap<Object> internalIds = new IdentityObjectIntMap<Object>();

	@Override
	public void reset() {
		callback = null;
		fileName = null;

		writer = null;

		externalFileNames.clear();
		externalDependencies.clear();

		currentId = 0;
		internalIds.clear();
		externalFileNames.clear();
	}

	public <T> void serialize(T rootObject, Class<?> knownType) {
		StringWriter buffer = new StringWriter();
		writer = new JsonWriter(buffer);

		internalIds.put(rootObject, currentId++);
		Model<T> objectModel = Models.getModel(rootObject);
		objectModel.serialize(rootObject, knownType, this);

		System.out.println(json.prettyPrint(buffer.toString()));
		// System.out.println(json.prettyPrint(json.toJson(rootObject)));
		reset();
	}

	public void writeObjectStart(Object value, Class<?> knownType) {
		object();
		Class<? extends Object> actualType = value.getClass();
		if (knownType != actualType) {
			writeType(actualType);
		}
	}

	private void object() {
		try {
			writer.object();
		} catch (IOException ex) {
			throw new SerializationException(ex);
		}
	}

	private void writeType(Class<?> type) {
		try {
			writer.set("class", type.getName());
		} catch (IOException ex) {
			throw new SerializationException(ex);
		}
	}

	public void writeObjectStart(Class<?> type) {
		object();
		writeType(type);
	}

	public void writeObjectEnd() {
		pop();
	}

	private void pop() {
		try {
			writer.pop();
		} catch (IOException ex) {
			throw new SerializationException(ex);
		}
	}

	public void writeArrayStart() {
		array();
	}

	private void array() {
		try {
			writer.array();
		} catch (IOException ex) {
			throw new SerializationException(ex);
		}
	}

	public void writeArrayStart(String name) {
		name(name);
		array();
	}

	public void writeArrayEnd() {
		pop();
	}

	public void writeValue(String name, Object value, Class<?> knownType) {
		name(name);
		writeValue(value, knownType);
	}

	private void name(String name) {
		try {
			writer.name(name);
		} catch (IOException ex) {
			throw new SerializationException(ex);
		}
	}

	public void writeValue(Object value, Class<?> knownType) {
		if (value == null || knownType.isPrimitive() || String.class == knownType) {
			value(value);
		} else if (value instanceof ManagedObject) {
			// ManagedObject managedObject = (ManagedObject) value;
			// String resourceFileName = ResourceService.getResourceFileName(managedObject);
			// boolean isLocalObject = fileName.equals(resourceFileName);
			// if (isLocalObject) {
			// if (internalIds.get(value, -1) > 0) {
			// ObjectReference objectReference = new ObjectReference(managedObject.getId(), null);
			// json1.writeValue(objectReference, ObjectReference.class);
			// } else {
			// Model<Object> model = Models.getModel(value);
			// model.serialize(value, knownType, this);
			// }
			// } else {
			// ExternalDependency dependency = new ExternalDependency();
			// dependency.typeName = ManagedObject.class.getName();
			// dependency.fileName = resourceFileName;
			// externalDependencies.add(dependency);
			// externalFileNames.add(resourceFileName);
			//
			// ObjectReference objectReference = new ObjectReference(managedObject.getId(), resourceFileName);
			// json1.writeValue(objectReference, ObjectReference.class);
			// }
		} else if (Assets.isAsset(value)) {
			AssetReference reference = new AssetReference(ResourceService.getResourceFileName(value), value.getClass());
			if (Assets.isAssetType(knownType)) {

			}
		} else {
			Model<Object> model = Models.getModel(value);
			model.serialize(value, knownType, this);
		}
	}

	private void value(Object value) {
		try {
			writer.value(value);
		} catch (IOException ex) {
			throw new SerializationException(ex);
		}
	}

	public <T> void serialize1(T rootObject, Class<?> knownType) {
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

	private Array<Object> objectsToSerialize = new Array<Object>();

	public void writeValue12(Object value, Class<?> knownType) {
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
						objectsToSerialize.add(value);
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
					objectsToSerialize.add(value);
				}
			}
		}
	}

	private static class ExternalDependency {
		String typeName;
		String fileName;
	}

	public static void main(String[] args) {
		String str = "{\n" + 
				"i: 8\n" + 
				"s: sss\n" + 
				"a: [\n" + 
				"	{\n" + 
				"		class: com.gurella.engine.base.serialization.ArrayType\n" + 
				"		typeName: \"[Ljava.lang.String;\"\n" + 
				"	}\n" + 
				"	bbb\n" + 
				"	eee\n" + 
				"]\n" + 
				"b: [\n" + 
				"	{\n" + 
				"		class: com.gurella.engine.base.serialization.ArrayType\n" + 
				"		typeName: \"[Ljava.lang.String;\"\n" + 
				"	}\n" + 
				"	sss\n" + 
				"]\n" + 
				"t1: {\n" + 
				"	i1: 5\n" + 
				"}\n" + 
				"arr: {\n" + 
				"	componentType: java.lang.String\n" + 
				"	items: [\n" + 
				"		value\n" + 
				"		ddd\n" + 
				"	]\n" + 
				"}\n" + 
				"map: {\n" + 
				"	class: java.util.HashMap\n" + 
				"	entries: [\n" + 
				"		[\n" + 
				"			{\n" + 
				"				class: java.lang.String\n" + 
				"				value: a\n" + 
				"			}\n" + 
				"			{\n" + 
				"				class: java.lang.String\n" + 
				"				value: a\n" + 
				"			}\n" + 
				"		]\n" + 
				"	]\n" + 
				"}\n" + 
				"ba: {\n" + 
				"	items: [\n" + 
				"		true\n" + 
				"		false\n" + 
				"		false\n" + 
				"		false\n" + 
				"		false\n" + 
				"		false\n" + 
				"		false\n" + 
				"		false\n" + 
				"		false\n" + 
				"		false\n" + 
				"		false\n" + 
				"		false\n" + 
				"		false\n" + 
				"		false\n" + 
				"		false\n" + 
				"		false\n" + 
				"	]\n" + 
				"	size: 1\n" + 
				"}\n" + 
				"sb: {\n" + 
				"	value: [\n" + 
				"		d\n" + 
				"		d\n" + 
				"		d\n" + 
				"		d\n" + 
				"		d\n" + 
				"		d\n" + 
				"		d\n" + 
				"		d\n" + 
				"		d\n" + 
				"		d\n" + 
				"		d\n" + 
				"		d\n" + 
				"		d\n" + 
				"		d\n" + 
				"		d\n" + 
				"		d\n" + 
				"	]\n" + 
				"	count: 16\n" + 
				"}\n" + 
				"is: {\n" + 
				"	size: 1\n" + 
				"	keyTable: [ 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 ]\n" + 
				"}\n" + 
				"am: {\n" + 
				"	keys: [\n" + 
				"		{\n" + 
				"			class: java.lang.String\n" + 
				"			value: a\n" + 
				"		}\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"	]\n" + 
				"	values: [\n" + 
				"		{\n" + 
				"			class: java.lang.String\n" + 
				"			value: a\n" + 
				"		}\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"	]\n" + 
				"	size: 1\n" + 
				"}\n" + 
				"cls: java.lang.String\n" + 
				"te: a\n" + 
				"tes: {\n" + 
				"	type: com.gurella.engine.base.serialization.Archive$TestEnum\n" + 
				"	items: [\n" + 
				"		{\n" + 
				"			class: com.gurella.engine.base.serialization.Archive$TestEnum\n" + 
				"			value: b\n" + 
				"		}\n" + 
				"	]\n" + 
				"}\n" + 
				"lo: {\n" + 
				"	language: en\n" + 
				"	country: CA\n" + 
				"	variant: \"\"\n" + 
				"}\n" + 
				"om: {\n" + 
				"	size: 1\n" + 
				"	keyTable: [\n" + 
				"		null\n" + 
				"		{\n" + 
				"			class: java.lang.String\n" + 
				"			value: a\n" + 
				"		}\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"	]\n" + 
				"	valueTable: [\n" + 
				"		null\n" + 
				"		{\n" + 
				"			class: java.lang.String\n" + 
				"			value: a\n" + 
				"		}\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"	]\n" + 
				"	keys: {\n" + 
				"		items: [\n" + 
				"			{\n" + 
				"				class: java.lang.String\n" + 
				"				value: a\n" + 
				"			}\n" + 
				"		]\n" + 
				"	}\n" + 
				"}\n" + 
				"os: {\n" + 
				"	size: 1\n" + 
				"	keyTable: [\n" + 
				"		null\n" + 
				"		{\n" + 
				"			class: java.lang.String\n" + 
				"			value: a\n" + 
				"		}\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"		null\n" + 
				"	]\n" + 
				"	items: {\n" + 
				"		items: [\n" + 
				"			{\n" + 
				"				class: java.lang.String\n" + 
				"				value: a\n" + 
				"			}\n" + 
				"		]\n" + 
				"	}\n" + 
				"}\n" + 
				"el: {\n" + 
				"	class: java.util.Collections$EmptyList\n" + 
				"}\n" + 
				"em: {\n" + 
				"	keyType: com.gurella.engine.base.serialization.Archive$TestEnum\n" + 
				"	entries: [\n" + 
				"		[\n" + 
				"			{\n" + 
				"				class: com.gurella.engine.base.serialization.Archive$TestEnum\n" + 
				"				value: a\n" + 
				"			}\n" + 
				"			{\n" + 
				"				class: java.lang.String\n" + 
				"				value: a\n" + 
				"			}\n" + 
				"		]\n" + 
				"	]\n" + 
				"}\n" + 
				"ts: {\n" + 
				"	comparator: {\n" + 
				"		class: com.gurella.engine.base.serialization.Archive$1\n" + 
				"	}\n" + 
				"	items: [\n" + 
				"		{\n" + 
				"			class: java.lang.String\n" + 
				"			value: a\n" + 
				"		}\n" + 
				"	]\n" + 
				"}\n" + 
				"tm: {\n" + 
				"	comparator: {\n" + 
				"		class: com.gurella.engine.base.serialization.Archive$2\n" + 
				"	}\n" + 
				"	entries: [\n" + 
				"		[\n" + 
				"			{\n" + 
				"				class: java.lang.String\n" + 
				"				value: a\n" + 
				"			}\n" + 
				"			{\n" + 
				"				class: java.lang.String\n" + 
				"				value: a\n" + 
				"			}\n" + 
				"		]\n" + 
				"	]\n" + 
				"}\n" + 
				"ia: [ 0, 0, 0 ]\n" + 
				"oia: [\n" + 
				"	{\n" + 
				"		class: com.gurella.engine.base.serialization.ArrayType\n" + 
				"		typeName: \"[I\"\n" + 
				"	}\n" + 
				"	0\n" + 
				"	0\n" + 
				"	0\n" + 
				"]\n" + 
				"di: [\n" + 
				"	[ 1, 1 ]\n" + 
				"	[ 1, 1 ]\n" + 
				"]\n" + 
				"odi: [\n" + 
				"	{\n" + 
				"		class: com.gurella.engine.base.serialization.ArrayType\n" + 
				"		typeName: \"[[I\"\n" + 
				"	}\n" + 
				"	[ 1, 1 ]\n" + 
				"	[ 1, 1 ]\n" + 
				"]\n" + 
				"}";

		Test obj = new Test();
		obj.i = 8;
		obj.s = "sss";
		obj.a = new String[] { "bbb", "eee" };
		obj.b = new String[] { "sss" };
		obj.t1.i1 = 5;
		obj.arr.add("ddd");
		obj.map.put("a", "a");
		obj.ba.add(true);
		obj.sb.append("dddddddddddddddd");
		obj.is.add(1);
		obj.am.put("a", "a");
		obj.cls = String.class;
		obj.te = TestEnum.a;
		obj.tes = EnumSet.of(TestEnum.b);
		obj.lo = Locale.CANADA;
		obj.om.put("a", "a");
		obj.os.add("a");
		obj.os.iterator();
		obj.el = Collections.emptyList();
		obj.em = new EnumMap<TestEnum, String>(TestEnum.class);
		obj.em.put(TestEnum.a, "a");
		obj.ts = new TreeSet<String>(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		});
		obj.ts.add("a");
		obj.tm = new TreeMap<String, String>(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		});
		obj.tm.put("a", "a");
		obj.ia = new int[3];
		obj.oia = new int[3];
		obj.di = new int[][] { { 1, 1 }, { 1, 1 } };
		obj.odi = new int[][] { { 1, 1 }, { 1, 1 } };

		new Archive().serialize(new Test(), Test.class);
		new Archive().serialize(obj, Test.class);

		Model<int[][]> sbModel = Models.getModel(int[][].class);
		sbModel.getProperties();

		JsonValue value = new JsonReader().parse(str);
		InitializationContext context = new InitializationContext();
		Model<Test> model = Models.getModel(Test.class);
		context.push(null, null, value);
		Test instance = model.createInstance(context);
		context.setInitializingObject(instance);
		model.initInstance(context);

		System.out.println(Objects.isEqual(obj, instance));
		instance.ts.add("b");

		Models.getModel(Color.class).getProperties();
	}

	public static class Test {
		public int i = 2;
		public String s = "ddd";
		public Object[] a = new String[] { "sss" };
		public Object b;
		public Test1 t1 = new Test1();
		public ArrayExt<String> arr;
		public Map<String, String> map = new HashMap<String, String>();
		public BooleanArray ba = new BooleanArray();
		public StringBuilder sb = new StringBuilder();
		public IntSet is = new IntSet();
		public ArrayMap<String, String> am = new ArrayMap<String, String>();
		public Class<?> cls;
		public TestEnum te;
		public EnumSet<TestEnum> tes;
		public Locale lo;
		public OrderedMap<String, String> om = new OrderedMap<String, String>();
		public OrderedSet<String> os = new OrderedSet<String>();
		public List<String> el;
		public EnumMap<TestEnum, String> em;
		public TreeSet<String> ts;
		public TreeMap<String, String> tm;
		public int[] ia;
		public Object oia;
		public int[][] di;
		public Object odi;

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

	public interface TestInterface {
		String getStr();
	}

	public static enum TestEnum implements TestInterface {
		a {
			@Override
			public String getStr() {
				return "a";
			}
		},
		b {
			@Override
			public String getStr() {
				return "b";
			}
		},
		c {
			@Override
			public String getStr() {
				return "c";
			}
		},
		d {
			@Override
			public String getStr() {
				return "d";
			}
		},
		e {
			@Override
			public String getStr() {
				return "e";
			}
		};
	}
}
