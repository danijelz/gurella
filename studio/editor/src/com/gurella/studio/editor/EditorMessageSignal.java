package com.gurella.studio.editor;

import com.gurella.engine.event.Signal;

public class EditorMessageSignal extends Signal<EditorMessageListener> {
	public void dispatch(Object source, Object message) {
		Object[] items = listeners.begin();
		for (int i = 0, n = listeners.size; i < n; i++) {
			EditorMessageListener item = (EditorMessageListener) items[i];
			if (item != source) {
				item.handleMessage(source, message);
			}
		}
		listeners.end();
	}
}
