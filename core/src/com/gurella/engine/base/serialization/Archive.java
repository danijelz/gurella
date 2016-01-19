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

	private Array<String> externalFileNames = new Array<String>();
	private Array<ExternalDependency> externalDependencies = new Array<ExternalDependency>();

	private int currentId;
	private IdentityObjectIntMap<Object> internalIds = new IdentityObjectIntMap<Object>();

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
	
	public void writeObjectStart(Class<?> type) {
		json.writeObjectStart();
		json.writeType(type);
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
		String str = "{\r\n" + 
				"i: 8\r\n" + 
				"s: sss\r\n" + 
				"a: [\r\n" + 
				"	{\r\n" + 
				"		class: com.gurella.engine.base.serialization.ArrayType\r\n" + 
				"		typeName: \"[Ljava.lang.String;\"\r\n" + 
				"	}\r\n" + 
				"	bbb\r\n" + 
				"	eee\r\n" + 
				"]\r\n" + 
				"b: [\r\n" + 
				"	{\r\n" + 
				"		class: com.gurella.engine.base.serialization.ArrayType\r\n" + 
				"		typeName: \"[Ljava.lang.String;\"\r\n" + 
				"	}\r\n" + 
				"	sss\r\n" + 
				"]\r\n" + 
				"t1: {\r\n" + 
				"	i1: 5\r\n" + 
				"}\r\n" + 
				"arr: {\r\n" + 
				"	componentType: java.lang.String\r\n" + 
				"	items: [\r\n" + 
				"		value\r\n" + 
				"		ddd\r\n" + 
				"	]\r\n" + 
				"}\r\n" + 
				"map: {\r\n" + 
				"	class: java.util.HashMap\r\n" + 
				"	entries: [\r\n" + 
				"		[\r\n" + 
				"			{\r\n" + 
				"				class: java.lang.String\r\n" + 
				"				value: a\r\n" + 
				"			}\r\n" + 
				"			{\r\n" + 
				"				class: java.lang.String\r\n" + 
				"				value: a\r\n" + 
				"			}\r\n" + 
				"		]\r\n" + 
				"	]\r\n" + 
				"}\r\n" + 
				"ba: {\r\n" + 
				"	items: [\r\n" + 
				"		true\r\n" + 
				"		false\r\n" + 
				"		false\r\n" + 
				"		false\r\n" + 
				"		false\r\n" + 
				"		false\r\n" + 
				"		false\r\n" + 
				"		false\r\n" + 
				"		false\r\n" + 
				"		false\r\n" + 
				"		false\r\n" + 
				"		false\r\n" + 
				"		false\r\n" + 
				"		false\r\n" + 
				"		false\r\n" + 
				"		false\r\n" + 
				"	]\r\n" + 
				"	size: 1\r\n" + 
				"}\r\n" + 
				"sb: {\r\n" + 
				"	value: [\r\n" + 
				"		d\r\n" + 
				"		d\r\n" + 
				"		d\r\n" + 
				"		d\r\n" + 
				"		d\r\n" + 
				"		d\r\n" + 
				"		d\r\n" + 
				"		d\r\n" + 
				"		d\r\n" + 
				"		d\r\n" + 
				"		d\r\n" + 
				"		d\r\n" + 
				"		d\r\n" + 
				"		d\r\n" + 
				"		d\r\n" + 
				"		d\r\n" + 
				"	]\r\n" + 
				"	count: 16\r\n" + 
				"}\r\n" + 
				"is: {\r\n" + 
				"	size: 1\r\n" + 
				"	keyTable: [ 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 ]\r\n" + 
				"}\r\n" + 
				"am: {\r\n" + 
				"	keys: [\r\n" + 
				"		{\r\n" + 
				"			class: java.lang.String\r\n" + 
				"			value: a\r\n" + 
				"		}\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"	]\r\n" + 
				"	values: [\r\n" + 
				"		{\r\n" + 
				"			class: java.lang.String\r\n" + 
				"			value: a\r\n" + 
				"		}\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"	]\r\n" + 
				"	size: 1\r\n" + 
				"}\r\n" + 
				"cls: java.lang.String\r\n" + 
				"te: {\r\n" + 
				"	class: com.gurella.engine.base.serialization.Archive$TestEnum\r\n" + 
				"	value: a\r\n" + 
				"}\r\n" + 
				"tes: {\r\n" + 
				"	type: com.gurella.engine.base.serialization.Archive$TestEnum\r\n" + 
				"	items: [\r\n" + 
				"		{\r\n" + 
				"			class: com.gurella.engine.base.serialization.Archive$TestEnum\r\n" + 
				"			value: b\r\n" + 
				"		}\r\n" + 
				"	]\r\n" + 
				"}\r\n" + 
				"lo: {\r\n" + 
				"	language: en\r\n" + 
				"	country: CA\r\n" + 
				"	variant: \"\"\r\n" + 
				"}\r\n" + 
				"om: {\r\n" + 
				"	size: 1\r\n" + 
				"	keyTable: [\r\n" + 
				"		null\r\n" + 
				"		{\r\n" + 
				"			class: java.lang.String\r\n" + 
				"			value: a\r\n" + 
				"		}\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"	]\r\n" + 
				"	valueTable: [\r\n" + 
				"		null\r\n" + 
				"		{\r\n" + 
				"			class: java.lang.String\r\n" + 
				"			value: a\r\n" + 
				"		}\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"	]\r\n" + 
				"	keys: {\r\n" + 
				"		items: [\r\n" + 
				"			{\r\n" + 
				"				class: java.lang.String\r\n" + 
				"				value: a\r\n" + 
				"			}\r\n" + 
				"		]\r\n" + 
				"	}\r\n" + 
				"}\r\n" + 
				"os: {\r\n" + 
				"	size: 1\r\n" + 
				"	keyTable: [\r\n" + 
				"		null\r\n" + 
				"		{\r\n" + 
				"			class: java.lang.String\r\n" + 
				"			value: a\r\n" + 
				"		}\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"		null\r\n" + 
				"	]\r\n" + 
				"	items: {\r\n" + 
				"		items: [\r\n" + 
				"			{\r\n" + 
				"				class: java.lang.String\r\n" + 
				"				value: a\r\n" + 
				"			}\r\n" + 
				"		]\r\n" + 
				"	}\r\n" + 
				"}\r\n" + 
				"el: {\r\n" + 
				"	class: java.util.Collections$EmptyList\r\n" + 
				"}\r\n" + 
				"em: {\r\n" + 
				"	keyType: com.gurella.engine.base.serialization.Archive$TestEnum\r\n" + 
				"	entries: [\r\n" + 
				"		[\r\n" + 
				"			{\r\n" + 
				"				class: com.gurella.engine.base.serialization.Archive$TestEnum\r\n" + 
				"				value: a\r\n" + 
				"			}\r\n" + 
				"			{\r\n" + 
				"				class: java.lang.String\r\n" + 
				"				value: a\r\n" + 
				"			}\r\n" + 
				"		]\r\n" + 
				"	]\r\n" + 
				"}\r\n" + 
				"ts: {\r\n" + 
				"	comparator: {\r\n" + 
				"		class: com.gurella.engine.base.serialization.Archive$1\r\n" + 
				"	}\r\n" + 
				"	items: [\r\n" + 
				"		{\r\n" + 
				"			class: java.lang.String\r\n" + 
				"			value: a\r\n" + 
				"		}\r\n" + 
				"	]\r\n" + 
				"}\r\n" + 
				"tm: {\r\n" + 
				"	comparator: {\r\n" + 
				"		class: com.gurella.engine.base.serialization.Archive$2\r\n" + 
				"	}\r\n" + 
				"	entries: [\r\n" + 
				"		[\r\n" + 
				"			{\r\n" + 
				"				class: java.lang.String\r\n" + 
				"				value: a\r\n" + 
				"			}\r\n" + 
				"			{\r\n" + 
				"				class: java.lang.String\r\n" + 
				"				value: a\r\n" + 
				"			}\r\n" + 
				"		]\r\n" + 
				"	]\r\n" + 
				"}\r\n" + 
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
		
		new Archive().serialize(new Test(), Test.class);
		new Archive().serialize(obj, Test.class);
		
		Model<TestEnum> sbModel = Models.getModel(TestEnum.a);
		sbModel.getProperties();

		JsonValue value = new JsonReader().parse(str);
		InitializationContext context = new InitializationContext();
		Model<Test> model = Models.getModel(Test.class);
		context.push(null, null, value);
		Test instance = model.createInstance(context);
		context.setInitializingObject(instance);
		model.initInstance(context);

		System.out.println(Objects.isEqual(obj, instance));
		
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
