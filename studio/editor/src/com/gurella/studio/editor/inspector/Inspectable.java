package com.gurella.studio.editor.inspector;

public interface Inspectable<T> {
	T getTarget();

	InspectableContainer<T> createContainer(InspectorView parent, T target);
}