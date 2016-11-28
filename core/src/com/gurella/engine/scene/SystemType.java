package com.gurella.engine.scene;

import static com.gurella.engine.utils.Reflection.hasDeclaredAnnotation;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntIntMap;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.gurella.engine.utils.BitsExt;
import com.gurella.engine.utils.ImmutableBits;
import com.gurella.engine.utils.TypeRegistry;
import com.gurella.engine.utils.Values;

public class SystemType {
	public static final int invalidId = -1;

	private static final IntIntMap baseComponentTypes = new IntIntMap();
	private static final IntMap<BitsExt> componentSubtypes = new IntMap<BitsExt>();
	private static final TypeRegistry<SceneSystem> registry = new TypeRegistry<SceneSystem>();
	private static final int rootComponentType = registry.getId(SceneSystem.class);

	static {
		baseComponentTypes.put(rootComponentType, rootComponentType);
	}

	private SystemType() {
	}

	static int findBaseType(Class<? extends SceneSystem> type) {
		return baseComponentTypes.get(registry.findId(type), invalidId);
	}

	static int findBaseType(int typeId) {
		return baseComponentTypes.get(typeId, invalidId);
	}

	static int findType(Class<? extends SceneSystem> type) {
		return registry.findId(type);
	}

	public static int getBaseType(Class<? extends SceneSystem> type) {
		init(type);
		return baseComponentTypes.get(registry.findId(type), invalidId);
	}

	public static int getType(Class<? extends SceneSystem> type) {
		init(type);
		return registry.findId(type);
	}

	public static ImmutableBits getSubtypes(SceneSystem component) {
		return getSubtypes(component.getClass());
	}

	public static ImmutableBits getSubtypes(Class<? extends SceneSystem> type) {
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

	public static boolean isSubtype(Class<? extends SceneSystem> baseType, Class<? extends SceneSystem> type) {
		init(type);
		return getSubtypes(baseType).get(registry.findId(type));
	}

	private static void init(Class<? extends SceneSystem> type) {
		if (registry.contais(type) || !ClassReflection.isAssignableFrom(SceneSystem.class, type)) {
			return;
		}

		initHierarchy(type);

		Class<?> temp = type;
		BitsExt lastBits = null;
		BitsExt currentBits;

		while (temp != SceneSystem.class) {
			Class<? extends SceneSystem> casted = Values.cast(temp);
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

	private static void initHierarchy(Class<? extends SceneSystem> type) {
		if (registry.contais(type)) {
			return;
		}

		if (!ClassReflection.isAssignableFrom(SceneSystem.class, type)) {
			throw new GdxRuntimeException("Invalid class: " + type);
		}

		int typeId = registry.getId(type);
		componentSubtypes.put(typeId, new BitsExt());

		Class<? extends SceneSystem> parentType = Values.cast(type.getSuperclass());
		initHierarchy(parentType);

		int parentId = registry.getId(parentType);
		int parentBaseId = baseComponentTypes.get(parentId, invalidId);

		if (parentId == parentBaseId && (parentId == 0 || !hasDeclaredAnnotation(parentType, BaseSceneElement.class))) {
			baseComponentTypes.put(typeId, typeId);
		} else {
			baseComponentTypes.put(typeId, parentBaseId);
		}
	}
}
