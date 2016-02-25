package com.gurella.engine.event;

import java.util.Comparator;

import com.badlogic.gdx.utils.SnapshotArray;

public abstract class Signal<LISTENER> {
	protected SnapshotArray<LISTENER> listeners = new SnapshotArray<LISTENER>();

	public boolean addListener(LISTENER listener) {
		if (listener == null || listeners.contains(listener, true)) {
			return false;
		} else {
			listeners.add(listener);
			sort();
			return true;
		}
	}

	private void sort() {
		Comparator<LISTENER> comparator = getComparator();
		if (comparator != null) {
			listeners.sort(comparator);
		}
	}

	public boolean removeListener(LISTENER listener) {
		return listeners.removeValue(listener, true);
	}

	public void clear() {
		listeners.clear();
	}

	protected Comparator<LISTENER> getComparator() {
		return null;
	}
}
