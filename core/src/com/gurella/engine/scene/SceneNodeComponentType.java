package com.gurella.engine.scene;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntIntMap;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.gurella.engine.utils.BitsExt;
import com.gurella.engine.utils.ImmutableBits;
import com.gurella.engine.utils.Reflection;
import com.gurella.engine.utils.TypeSequence;

public final class SceneNodeComponentType {
	private static final int invalidId = -1;
	private static final IntIntMap baseComponentTypes = new IntIntMap();
	private static final IntMap<BitsExt> componentSubtypes = new IntMap<BitsExt>();
	private static final TypeSequence<SceneNodeComponent2> typeSequence = new TypeSequence<SceneNodeComponent2>();
	private static final int rootComponentType = typeSequence.getTypeId(SceneNodeComponent2.class);

	static {
		baseComponentTypes.put(rootComponentType, rootComponentType);
	}

	private SceneNodeComponentType() {
	}

	static int findBaseType(Class<? extends SceneNodeComponent2> type) {
		return baseComponentTypes.get(typeSequence.findTypeId(type), invalidId);
	}

	public static int getBaseType(Class<? extends SceneNodeComponent2> type) {
		init(type);
		return baseComponentTypes.get(typeSequence.findTypeId(type), invalidId);
	}

	public static int getType(Class<? extends SceneNodeComponent2> type) {
		init(type);
		return typeSequence.findTypeId(type);
	}

	public static ImmutableBits getSubtypes(SceneNodeComponent2 component) {
		return getSubtypes(component.getClass());
	}

	public static ImmutableBits getSubtypes(Class<? extends SceneNodeComponent2> type) {
		init(type);
		BitsExt subtypes = componentSubtypes.get(typeSequence.findTypeId(type));
		return subtypes == null ? ImmutableBits.empty : subtypes.immutable();
	}

	public static ImmutableBits getSubtypes(int id) {
		BitsExt subtypes = componentSubtypes.get(id);
		return subtypes == null ? ImmutableBits.empty : subtypes.immutable();
	}

	public static boolean isSubtype(int baseComponentType, int componentType) {
		return getSubtypes(baseComponentType).get(componentType);
	}

	public static boolean isSubtype(Class<? extends SceneNodeComponent2> baseType,
			Class<? extends SceneNodeComponent2> type) {
		init(type);
		return getSubtypes(baseType).get(typeSequence.findTypeId(type));
	}

	private static void init(Class<? extends SceneNodeComponent2> type) {
		if (typeSequence.contais(type)) {
			return;
		}

		initComponentDataHierarchy(type);

		Class<?> temp = type;
		BitsExt lastBits = null;
		BitsExt currentBits;

		while (temp != SceneNodeComponent2.class) {
			@SuppressWarnings("unchecked")
			Class<? extends SceneNodeComponent2> casted = (Class<? extends SceneNodeComponent2>) temp;
			int componentType = typeSequence.getTypeId(casted);
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

	private static void initComponentDataHierarchy(Class<? extends SceneNodeComponent2> type) {
		if (typeSequence.contais(type)) {
			return;
		}

		if (!ClassReflection.isAssignableFrom(SceneNodeComponent2.class, type)) {
			throw new GdxRuntimeException("Invalid class: " + type);
		}

		int id = typeSequence.getTypeId(type);
		componentSubtypes.put(id, new BitsExt());
		@SuppressWarnings("unchecked")
		Class<? extends SceneNodeComponent2> parentType = (Class<? extends SceneNodeComponent2>) type.getSuperclass();
		initComponentDataHierarchy(parentType);
		int parentId = typeSequence.getTypeId(parentType);
		int parentBaseId = baseComponentTypes.get(parentId, invalidId);

		if (parentId == parentBaseId) {
			BaseSceneElement base = Reflection.getDeclaredAnnotation(parentType, BaseSceneElement.class);
			baseComponentTypes.put(id, base == null ? id : parentId);
		} else {
			baseComponentTypes.put(id, parentId);
		}
	}
}
