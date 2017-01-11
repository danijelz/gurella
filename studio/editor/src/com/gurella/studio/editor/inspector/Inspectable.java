package com.gurella.studio.editor.inspector;

import com.gurella.engine.utils.Values;
import com.gurella.studio.GurellaStudioPlugin;

public interface Inspectable<T> {
	T getTarget();

	InspectableContainer<T> createControl(InspectorView parent, T target);

	default InspectableContainer<T> createControl(InspectorView parent) {
		T target = getTarget();
		return target == null ? new EmptyInspectableContainer<T>(parent, target) : createControl(parent, target);
	}

	class EmptyInspectable<T> implements Inspectable<T> {
		private static final Object target = new Object();
		private static final EmptyInspectable<Object> instance = new EmptyInspectable<>();

		private EmptyInspectable() {
		}

		@Override
		public T getTarget() {
			return Values.cast(target);
		}

		@Override
		public InspectableContainer<T> createControl(InspectorView parent, T target) {
			return new EmptyInspectableContainer<T>(parent, target);
		}

		static <T> EmptyInspectable<T> getInstance() {
			return Values.cast(instance);
		}
	}

	class EmptyInspectableContainer<T> extends InspectableContainer<T> {
		EmptyInspectableContainer(InspectorView parent, T target) {
			super(parent, target);
			GurellaStudioPlugin.getToolkit().adapt(this);
		}
	}
}