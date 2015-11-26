package com.gurella.engine.graph;

import com.badlogic.gdx.utils.Bits;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntIntMap;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntMap.Values;
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

	public static int getBaseComponentType(SceneNodeComponent component) {
		return getBaseComponentType(component.getClass());
	}

	public static int getBaseComponentType(Class<? extends SceneNodeComponent> componentClass) {
		initComponentData(componentClass);
		return baseComponentTypes.get(COMPONENT_TYPE_INDEXER.getType(componentClass), 0);
	}

	public static int getComponentType(SceneNodeComponent component) {
		return getComponentType(component.getClass());
	}

	public static int getComponentType(Class<? extends SceneNodeComponent> componentClass) {
		initComponentData(componentClass);
		return COMPONENT_TYPE_INDEXER.findType(componentClass, 0);
	}

	public static ImmutableBits getComponentSubtypes(SceneNodeComponent component) {
		return getComponentSubtypes(component.getClass());
	}

	public static ImmutableBits getComponentSubtypes(Class<? extends SceneNodeComponent> componentClass) {
		initComponentData(componentClass);
		BitsExt subtypes = componentSubtypes.get(COMPONENT_TYPE_INDEXER.findType(componentClass, 0));
		return subtypes == null ? ImmutableBits.empty : subtypes.immutable();
	}

	public static boolean isSubtype(Class<? extends SceneNodeComponent> baseComponentClass,
			Class<? extends SceneNodeComponent> componentClass) {
		initComponentData(componentClass);
		return getComponentSubtypes(baseComponentClass).get(COMPONENT_TYPE_INDEXER.findType(componentClass, 0));
	}

	private static int getBaseComponentType(int componentType, Class<? extends SceneNodeComponent> componentClass) {
		int baseComponentType = baseComponentTypes.get(componentType, -1);
		if (baseComponentType > -1) {
			return baseComponentType;
		}

		Class<?> currentComponentClass = componentClass;
		Class<? extends SceneNodeComponent> lastAnnotated = null;
		BitsExt lastBits = new BitsExt();
		BitsExt currentBits;
		componentSubtypes.put(componentType, lastBits);
		int foundBaseComponentType = -1;

		while (currentComponentClass != null && currentComponentClass != SceneNodeComponent.class) {
			@SuppressWarnings("unchecked")
			Class<? extends SceneNodeComponent> casted = (Class<? extends SceneNodeComponent>) currentComponentClass;
			int currentComponentType = COMPONENT_TYPE_INDEXER.getType(casted);
			currentBits = componentSubtypes.get(currentComponentType);
			if (currentBits == null) {
				currentBits = new BitsExt();
				componentSubtypes.put(currentComponentType, currentBits);
			}
			currentBits.or(lastBits);

			if (foundBaseComponentType == -1) {
				foundBaseComponentType = baseComponentTypes.get(currentComponentType, -1);
				if (foundBaseComponentType == -1) {
					BaseSceneElementType annotation = ReflectionUtils.getDeclaredAnnotation(currentComponentClass,
							BaseSceneElementType.class);
					if (annotation != null) {
						lastAnnotated = casted;
					}
				}
			}

			currentComponentClass = currentComponentClass.getSuperclass();
			lastBits = currentBits;
		}

		if (lastAnnotated == null) {
			baseComponentTypes.put(componentType, componentType);
			return componentType;
		} else {
			baseComponentType = COMPONENT_TYPE_INDEXER.getType(lastAnnotated);
			baseComponentTypes.put(componentType, baseComponentType);
			return baseComponentType;
		}
	}

	private static void initComponentData(Class<? extends SceneNodeComponent> componentClass) {
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
		initComponentData(parentComponentClass);
		int parentComponentType = COMPONENT_TYPE_INDEXER.getType(parentComponentClass);
		int parentBaseType = baseComponentTypes.get(parentComponentType, -1);
		
		
		
		int baseComponentType = initComponentData(componentClass, componentType, superclass);
		if (baseComponentType == rootComponentType) {
			baseComponentTypes.put(componentType, componentType);
		} else {
			baseComponentTypes.put(componentType, baseComponentType);
		}
	}
	
//	private static Class<? extends SceneNodeComponent> findBaseComponentType(Class<? extends SceneNodeComponent> componentClass) {
//		Class<?> temp = componentClass;
//		while (temp != null && !SceneNodeComponent.class.equals(componentClass) && !ScriptComponent.class.equals(componentClass) && !Object.class.equals(componentClass)) {
//			BaseSceneElementType annotation = ReflectionUtils.getDeclaredAnnotation(temp, BaseSceneElementType.class);
//			if(annotation != null) {
//				@SuppressWarnings("unchecked")
//				Class<? extends SceneNodeComponent> casted = (Class<? extends SceneNodeComponent>) temp;
//				return casted;
//			}
//			
//			temp = temp.getSuperclass();
//		}
//		return null;
//	}

	private static int initComponentData(Class<? extends SceneNodeComponent> componentClass, int componentType,
			Class<? extends SceneNodeComponent> parentClass) {
		if (parentClass == SceneNodeComponent.class) {
			return rootComponentType;
		}

		int parentType = COMPONENT_TYPE_INDEXER.getType(parentClass);
		@SuppressWarnings("unchecked")
		Class<? extends SceneNodeComponent> superclass = (Class<? extends SceneNodeComponent>) parentClass
				.getSuperclass();
		int parentBaseType = initComponentData(parentClass, parentType, superclass);

		BitsExt subtypes = componentSubtypes.get(componentType);
		BitsExt parentSubtypes = componentSubtypes.get(parentType);
		parentSubtypes.or(subtypes);
		parentSubtypes.set(componentType);

		if (parentBaseType == rootComponentType) {
			baseComponentTypes.put(componentType, componentType);
		} else {
			baseComponentTypes.put(componentType, parentBaseType);
		}
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
			bits.set(getBaseComponentType(components[i]));
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

	public <T extends SceneNodeComponent> T getNodeComponent(Class<T> componentClass) {
		return node.getComponent(componentClass);
	}

	public <T extends SceneNodeComponent> T getActiveNodeComponent(Class<T> componentClass) {
		return node.getActiveComponent(componentClass);
	}

	public Values<SceneNodeComponent> getNodeComponents() {
		return node.getComponents();
	}
}
