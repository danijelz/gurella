package com.gurella.engine.signal;

import java.util.Comparator;

import com.badlogic.gdx.utils.Array;

public abstract class AbstractSignal<LISTENER> {
	protected Array<LISTENER> listeners = new Array<LISTENER>();

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
