package com.gurella.engine.scene;

import com.badlogic.gdx.utils.Bits;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntIntMap;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.gurella.engine.utils.BitsExt;
import com.gurella.engine.utils.ImmutableBits;
import com.gurella.engine.utils.Reflection;
import com.gurella.engine.utils.TypeRegistry;
import com.gurella.engine.utils.Values;

public final class SceneNodeComponentType {
	public static final int invalidId = -1;

	private static final IntIntMap baseComponentTypes = new IntIntMap();
	private static final IntMap<BitsExt> componentSubtypes = new IntMap<BitsExt>();
	private static final TypeRegistry<SceneNodeComponent2> registry = new TypeRegistry<SceneNodeComponent2>();
	private static final int rootComponentType = registry.getId(SceneNodeComponent2.class);

	static {
		baseComponentTypes.put(rootComponentType, rootComponentType);
	}

	private SceneNodeComponentType() {
	}

	static int findBaseType(Class<? extends SceneNodeComponent2> type) {
		return baseComponentTypes.get(registry.findId(type), invalidId);
	}

	static int findBaseType(int typeId) {
		return baseComponentTypes.get(typeId, invalidId);
	}

	public static int getBaseType(Class<? extends SceneNodeComponent2> type) {
		init(type);
		return baseComponentTypes.get(registry.findId(type), invalidId);
	}

	public static int getType(Class<? extends SceneNodeComponent2> type) {
		init(type);
		return registry.findId(type);
	}

	public static ImmutableBits getSubtypes(SceneNodeComponent2 component) {
		return getSubtypes(component.getClass());
	}

	public static ImmutableBits getSubtypes(Class<? extends SceneNodeComponent2> type) {
		init(type);
		BitsExt subtypes = componentSubtypes.get(registry.findId(type));
		return subtypes == null ? ImmutableBits.empty : subtypes.immutable();
	}

	public static ImmutableBits getSubtypes(int typeId) {
		BitsExt subtypes = componentSubtypes.get(typeId);
		return subtypes == null ? ImmutableBits.empty : subtypes.immutable();
	}

	public static boolean isSubtype(int baseTypeId, int typeId) {
		return getSubtypes(baseTypeId).get(typeId);
	}

	public static boolean isSubtype(Class<? extends SceneNodeComponent2> baseType,
			Class<? extends SceneNodeComponent2> type) {
		init(type);
		return getSubtypes(baseType).get(registry.findId(type));
	}

	private static void init(Class<? extends SceneNodeComponent2> type) {
		if (registry.contais(type)) {
			return;
		}

		initHierarchy(type);

		Class<?> temp = type;
		BitsExt lastBits = null;
		BitsExt currentBits;

		while (temp != SceneNodeComponent2.class) {
			@SuppressWarnings("unchecked")
			Class<? extends SceneNodeComponent2> casted = (Class<? extends SceneNodeComponent2>) temp;
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

	private static void initHierarchy(Class<? extends SceneNodeComponent2> type) {
		if (registry.contais(type)) {
			return;
		}

		if (!ClassReflection.isAssignableFrom(SceneNodeComponent2.class, type)) {
			throw new GdxRuntimeException("Invalid class: " + type);
		}

		int typeId = registry.getId(type);
		componentSubtypes.put(typeId, new BitsExt());

		Class<? extends SceneNodeComponent2> parentType = Values.cast(type.getSuperclass());
		initHierarchy(parentType);

		int parentId = registry.getId(parentType);
		int parentBaseId = baseComponentTypes.get(parentId, invalidId);

		if (parentId == parentBaseId) {
			BaseSceneElement base = Reflection.getDeclaredAnnotation(parentType, BaseSceneElement.class);
			baseComponentTypes.put(typeId, base == null ? typeId : parentId);
		} else {
			baseComponentTypes.put(typeId, parentId);
		}
	}

	public static Bits getBitsFor(Bits out, SceneNodeComponent2... components){
	for (int i = 0; i < components.length; i++) {
			out.set(components[i].baseComponentType);
		}
		return out;
	}

	@SafeVarargs
	public static Bits getBitsFor(Bits out, Class<? extends SceneNodeComponent2>... componentClasses) {
		for (int i = 0; i < componentClasses.length; i++) {
			out.set(getBaseType(componentClasses[i]));
		}
		return out;
	}
}
