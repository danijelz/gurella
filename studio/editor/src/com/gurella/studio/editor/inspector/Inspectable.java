package com.gurella.studio.editor.inspector;

import com.gurella.engine.utils.Values;
import com.gurella.studio.editor.inspector.InspectableContainer.EmptyInspectableContainer;

public interface Inspectable<T> {
	T getTarget();

	InspectableContainer<T> createControl(InspectorView parent, T target);

	default InspectableContainer<T> createControl(InspectorView parent) {
		T target = getTarget();
		return target == null ? new EmptyInspectableContainer<T>(parent) : createControl(parent, target);
	}

	class EmptyInspectable<T> implements Inspectable<T> {
		private static final EmptyInspectable<Object> instance = new EmptyInspectable<>();

		private EmptyInspectable() {
		}

		@Override
		public T getTarget() {
			return null;
		}

		@Override
		public InspectableContainer<T> createControl(InspectorView parent, T target) {
			return new EmptyInspectableContainer<T>(parent);
		}

		static <T> EmptyInspectable<T> getInstance() {
			return Values.cast(instance);
		}
	}
}