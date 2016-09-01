package com.gurella.studio.editor.model.extension.event;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.gurella.engine.editor.ui.event.EditorEvent;
import com.gurella.engine.editor.ui.event.EditorEventListener;

public class SwtListenerBridge implements Listener {
	public final EditorEventListener listener;

	public SwtListenerBridge(EditorEventListener listener) {
		this.listener = listener;
	}

	@Override
	public void handleEvent(Event event) {
		EditorEvent editorEvent = new SwtEditorEvent(event);
		listener.handleEvent(editorEvent);
	}

	@Override
	public int hashCode() {
		return 31 * listener.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}

		SwtListenerBridge other = (SwtListenerBridge) obj;
		return listener.equals(other.listener);
	}
}
