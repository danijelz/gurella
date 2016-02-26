package com.gurella.engine.event;

import java.util.Comparator;

import com.badlogic.gdx.utils.SnapshotArray;

public abstract class Signal<LISTENER> {
	protected SnapshotArray<LISTENER> listeners = new SnapshotArray<LISTENER>();
	protected Comparator<LISTENER> comparator;

	public boolean addListener(LISTENER listener) {
		if (listener == null || listeners.contains(listener, true)) {
			return false;
		} else {
			listeners.add(listener);
			if (comparator != null) {
				listeners.sort(comparator);
			}
			return true;
		}
	}

	public boolean removeListener(LISTENER listener) {
		return listeners.removeValue(listener, true);
	}

	public void clear() {
		listeners.clear();
	}
}
