package com.gurella.studio.editor.model.extension.event;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.gurella.engine.editor.event.EditorEvent;
import com.gurella.engine.editor.event.EditorEventListener;

public class SwtListenerBridge implements Listener {
	public final EditorEventListener listener;

	public SwtListenerBridge(EditorEventListener listener) {
		this.listener = listener;
	}

	@Override
	public void handleEvent(Event event) {
		EditorEvent editorEvent = toEditorEvent(event);
		listener.handleEvent(editorEvent);
		syncEvent(editorEvent, event);
	}

	private static EditorEvent toEditorEvent(Event event) {
		// TODO Auto-generated method stub
		return null;
	}

	private static void syncEvent(EditorEvent editorEvent, Event event) {
	}
}
