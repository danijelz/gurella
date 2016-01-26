package com.gurella.engine.base.serialization;

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

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.BooleanArray;
import com.badlogic.gdx.utils.IntSet;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.utils.OrderedSet;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.base.model.CopyContext;
import com.gurella.engine.base.registry.Objects;
import com.gurella.engine.base.serialization.json.JsonInput;
import com.gurella.engine.base.serialization.json.JsonOutput;
import com.gurella.engine.utils.ArrayExt;

public class Archive implements Poolable {
	Json json = new Json();
	private Array<String> externalFileNames = new Array<String>();
	private Array<ExternalDependency> externalDependencies = new Array<ExternalDependency>();

	@Override
	public void reset() {
		externalFileNames.clear();
		externalDependencies.clear();
		externalFileNames.clear();
	}

	static class ExternalDependency {
		String typeName;
		String fileName;
	}

	public static void main(String[] args) {
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
		obj.test2.iiiiiiiiiiiiiiii1 = 0;
		// obj.child = obj;

		JsonOutput output = new JsonOutput();
		String string = output.serialize(Test.class, obj);
		
		System.out.println(new JsonReader().parse(string).prettyPrint(OutputType.minimal, 120));

		JsonInput input = new JsonInput();
		Test deserialized = input.deserialize(Test.class, string);
		System.out.println(Objects.isEqual(obj, deserialized));
		
		Test duplicate = new CopyContext().copy(obj);
		System.out.println(Objects.isEqual(obj, duplicate));
		
		Object copied = new CopyContext().copyProperties(obj, new Archive.Test());
		System.out.println(Objects.isEqual(obj, copied));
		
		System.out.println("\n\n\n\n");
		
		String string1 = output.serialize(Test.class, obj, duplicate);
		System.out.println(new JsonReader().parse(string1).prettyPrint(OutputType.minimal, 120));
		Test deserialized1 = input.deserialize(Test.class, string1, obj);
		System.out.println(Objects.isEqual(obj, deserialized1));
	}

	public static class Test {
		public Test2 test2 = new Test2();
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
		public Test child;

		public Test() {
			arr = new ArrayExt<String>(String.class);
			arr.add("value");
		}

		@Override
		public String toString() {
			return "Test [i=" + i + ", s=" + s + ", a=" + Arrays.toString(a) + ", t1=" + t1 + ", arr=" + arr + "]";
		}

		public class Test2 {
			public int iiiiiiiiiiiiiiii1 = 2;
			public String s1 = "ddd";

			@Override
			public String toString() {
				return "Test1 [i1=" + iiiiiiiiiiiiiiii1 + ", s1=" + s1 + "]";
			}
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
