package com.gurella.engine.graph;

import com.badlogic.gdx.utils.Bits;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntIntMap;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.gurella.engine.utils.BitsExt;
import com.gurella.engine.utils.ImmutableBits;
import com.gurella.engine.utils.IndexedType;
import com.gurella.engine.utils.ReflectionUtils;

public class SceneNodeComponent extends SceneGraphElement {
	private static final IntIntMap baseComponentTypes = new IntIntMap();
	private static final IntMap<BitsExt> componentSubtypes = new IntMap<BitsExt>();
	private static final IndexedType<SceneNodeComponent> COMPONENT_TYPE_INDEXER = new IndexedType<SceneNodeComponent>();
	private static final int rootComponentType = COMPONENT_TYPE_INDEXER.getType(SceneNodeComponent.class);

	static {
		baseComponentTypes.put(rootComponentType, rootComponentType);
	}

	public final int componentType;
	public final int baseComponentType;
	SceneNode node;

	public SceneNodeComponent() {
		Class<? extends SceneNodeComponent> componentClass = getClass();
		initComponentData(componentClass);
		componentType = COMPONENT_TYPE_INDEXER.getType(componentClass);
		baseComponentType = baseComponentTypes.get(componentType, componentType);
	}

	public static int getBaseComponentType(Class<? extends SceneNodeComponent> componentClass) {
		initComponentData(componentClass);
		return baseComponentTypes.get(COMPONENT_TYPE_INDEXER.findType(componentClass, -1), -1);
	}

	public static int getComponentType(Class<? extends SceneNodeComponent> componentClass) {
		initComponentData(componentClass);
		return COMPONENT_TYPE_INDEXER.findType(componentClass, -1);
	}

	public static ImmutableBits getComponentSubtypes(SceneNodeComponent component) {
		return getComponentSubtypes(component.getClass());
	}

	public static ImmutableBits getComponentSubtypes(Class<? extends SceneNodeComponent> componentClass) {
		initComponentData(componentClass);
		BitsExt subtypes = componentSubtypes.get(COMPONENT_TYPE_INDEXER.findType(componentClass, -1));
		return subtypes == null ? ImmutableBits.empty : subtypes.immutable();
	}

	public static ImmutableBits getComponentSubtypes(int componentType) {
		BitsExt subtypes = componentSubtypes.get(componentType);
		return subtypes == null ? ImmutableBits.empty : subtypes.immutable();
	}

	public static boolean isSubtype(Class<? extends SceneNodeComponent> baseComponentClass,
			Class<? extends SceneNodeComponent> componentClass) {
		initComponentData(componentClass);
		return getComponentSubtypes(baseComponentClass).get(COMPONENT_TYPE_INDEXER.findType(componentClass, -1));
	}

	private static void initComponentData(Class<? extends SceneNodeComponent> componentClass) {
		if (COMPONENT_TYPE_INDEXER.contais(componentClass)) {
			return;
		}

		initComponentDataHierarchy(componentClass);

		int componentType = COMPONENT_TYPE_INDEXER.getType(componentClass);
		Class<?> temp = componentClass.getSuperclass();
		BitsExt lastBits = null;
		BitsExt currentBits;

		while (temp != SceneNodeComponent.class) {
			@SuppressWarnings("unchecked")
			Class<? extends SceneNodeComponent> casted = (Class<? extends SceneNodeComponent>) temp;
			int parentComponentType = COMPONENT_TYPE_INDEXER.getType(casted);

			if (lastBits == null) {
				lastBits = componentSubtypes.get(parentComponentType);
				lastBits.set(componentType);
			} else {
				currentBits = componentSubtypes.get(parentComponentType);
				currentBits.or(lastBits);
				lastBits = currentBits;
			}

			temp = temp.getSuperclass();
		}
	}

	private static void initComponentDataHierarchy(Class<? extends SceneNodeComponent> componentClass) {
		if (COMPONENT_TYPE_INDEXER.contais(componentClass)) {
			return;
		}

		if (!ClassReflection.isAssignableFrom(SceneNodeComponent.class, componentClass)) {
			throw new GdxRuntimeException("Invalid class: " + componentClass);
		}

		int componentType = COMPONENT_TYPE_INDEXER.getType(componentClass);
		componentSubtypes.put(componentType, new BitsExt());
		@SuppressWarnings("unchecked")
		Class<? extends SceneNodeComponent> parentComponentClass = (Class<? extends SceneNodeComponent>) componentClass
				.getSuperclass();
		initComponentDataHierarchy(parentComponentClass);
		int parentComponentType = COMPONENT_TYPE_INDEXER.getType(parentComponentClass);
		int parentBaseComponentType = baseComponentTypes.get(parentComponentType, -1);
		int baseComponentType;
		if (parentComponentType == parentBaseComponentType) {
			BaseSceneElementType annotation = ReflectionUtils.getDeclaredAnnotation(parentComponentClass,
					BaseSceneElementType.class);
			baseComponentType = annotation == null ? componentType : parentComponentType;
		} else {
			baseComponentType = parentComponentType;
		}
		baseComponentTypes.put(componentType, baseComponentType);
	}

	@SafeVarargs
	public static Bits getBitsFor(Class<? extends SceneNodeComponent>... componentClasses) {
		Bits bits = new Bits();

		for (int i = 0; i < componentClasses.length; i++) {
			bits.set(getBaseComponentType(componentClasses[i]));
		}

		return bits;
	}

	public static Bits getBitsFor(SceneNodeComponent... components) {
		Bits bits = new Bits();

		for (int i = 0; i < components.length; i++) {
			bits.set(components[i].baseComponentType);
		}

		return bits;
	}

	public SceneNode getNode() {
		return node;
	}

	@Override
	final void activate() {
		if (graph != null) {
			graph.activateComponent(this);
		}
	}

	@Override
	final void deactivate() {
		if (graph != null) {
			graph.deactivateComponent(this);
		}
	}

	@Override
	public final void detach() {
		if (graph != null) {
			graph.removeComponent(this);
		}
	}

	@Override
	public final void reset() {
		resettedSignal.dispatch();
		clearSignals();
		initialized = false;
	}

	@Override
	public final void dispose() {
		detach();
		disposedSignal.dispatch();
		INDEXER.removeIndexed(this);
	}

	public final boolean isHierarchyEnabled() {
		return this.enabled && node != null && node.isHierarchyEnabled();
	}

	@Override
	public final void setEnabled(boolean enabled) {
		if (this.enabled == enabled) {
			return;
		} else {
			this.enabled = enabled;
			boolean hierarchyEnabled = isHierarchyEnabled();
			if (!hierarchyEnabled && active) {
				deactivate();
			} else if (hierarchyEnabled && !active) {
				activate();
			}
		}
	}
}
