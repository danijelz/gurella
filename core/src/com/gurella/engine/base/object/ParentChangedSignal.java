package com.gurella.engine.base.object;

import com.gurella.engine.event.AbstractSignal;

//TODO unused
public class ParentChangedSignal extends AbstractSignal<ParentChangedSignal.ParentChangedListener> {
	void parentChanged(ManagedObject oldParent, ManagedObject newParent) {
		ParentChangedListener[] items = listeners.begin();
		for (int i = 0, n = listeners.size; i < n; i++) {
			items[i].parentChanged(oldParent, newParent);
		}
		listeners.end();
	}

	public interface ParentChangedListener {
		void parentChanged(ManagedObject oldParent, ManagedObject newParent);
	}
}
