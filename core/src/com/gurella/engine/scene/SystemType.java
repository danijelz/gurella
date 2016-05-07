package com.gurella.engine.scene;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntIntMap;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.gurella.engine.utils.BitsExt;
import com.gurella.engine.utils.ImmutableBits;
import com.gurella.engine.utils.Reflection;
import com.gurella.engine.utils.TypeRegistry;
import com.gurella.engine.utils.Values;

public class SystemType {
	public static final int invalidId = -1;

	private static final IntIntMap baseComponentTypes = new IntIntMap();
	private static final IntMap<BitsExt> componentSubtypes = new IntMap<BitsExt>();
	private static final TypeRegistry<SceneSystem2> registry = new TypeRegistry<SceneSystem2>();
	private static final int rootComponentType = registry.getId(SceneSystem2.class);

	static {
		baseComponentTypes.put(rootComponentType, rootComponentType);
	}

	private SystemType() {
	}

	static int findBaseType(Class<? extends SceneSystem2> type) {
		return baseComponentTypes.get(registry.findId(type), invalidId);
	}

	static int findBaseType(int typeId) {
		return baseComponentTypes.get(typeId, invalidId);
	}

	static int findType(Class<? extends SceneSystem2> type) {
		return registry.findId(type);
	}

	public static int getBaseType(Class<? extends SceneSystem2> type) {
		init(type);
		return baseComponentTypes.get(registry.findId(type), invalidId);
	}

	public static int getType(Class<? extends SceneSystem2> type) {
		init(type);
		return registry.findId(type);
	}

	public static ImmutableBits getSubtypes(SceneSystem2 component) {
		return getSubtypes(component.getClass());
	}

	public static ImmutableBits getSubtypes(Class<? extends SceneSystem2> type) {
		init(type);
		BitsExt subtypes = componentSubtypes.get(registry.findId(type));
		return subtypes == null ? ImmutableBits.empty : subtypes.immutable();
	}

	public static ImmutableBits getSubtypes(int typeId) {
		BitsExt subtypes = componentSubtypes.get(typeId);
		return subtypes == null ? ImmutableBits.empty : subtypes.immutable();
	}

	public static boolean isSubtype(int baseTypeId, int typeId) {
		BitsExt subtypes = componentSubtypes.get(baseTypeId);
		return subtypes == null ? false : subtypes.get(typeId);
	}

	public static boolean isSubtype(Class<? extends SceneSystem2> baseType, Class<? extends SceneSystem2> type) {
		init(type);
		return getSubtypes(baseType).get(registry.findId(type));
	}

	private static void init(Class<? extends SceneSystem2> type) {
		if (registry.contais(type) || !ClassReflection.isAssignableFrom(SceneSystem2.class, type)) {
			return;
		}

		initHierarchy(type);

		Class<?> temp = type;
		BitsExt lastBits = null;
		BitsExt currentBits;

		while (temp != SceneSystem2.class) {
			Class<? extends SceneSystem2> casted = Values.cast(temp);
			int componentType = registry.getId(casted);
			currentBits = componentSubtypes.get(componentType);

			if (lastBits == null) {
				currentBits.set(componentType);
			} else {
				currentBits.or(lastBits);
			}

			lastBits = currentBits;
			temp = temp.getSuperclass();
		}
	}

	private static void initHierarchy(Class<? extends SceneSystem2> type) {
		if (registry.contais(type)) {
			return;
		}

		if (!ClassReflection.isAssignableFrom(SceneSystem2.class, type)) {
			throw new GdxRuntimeException("Invalid class: " + type);
		}

		int typeId = registry.getId(type);
		componentSubtypes.put(typeId, new BitsExt());

		Class<? extends SceneSystem2> parentType = Values.cast(type.getSuperclass());
		initHierarchy(parentType);

		int parentId = registry.getId(parentType);
		int parentBaseId = baseComponentTypes.get(parentId, invalidId);

		if (parentId == parentBaseId
				&& (parentId == 0 || Reflection.getDeclaredAnnotation(parentType, BaseSceneElement.class) == null)) {
			baseComponentTypes.put(typeId, typeId);
		} else {
			baseComponentTypes.put(typeId, parentBaseId);
		}
	}
}
