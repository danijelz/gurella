package com.gurella.studio.editor.utils;

import java.util.Optional;

import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;

public class DelegatingDragSourceListener implements DragSourceListener {
	private final DragSourceListener[] listeners;
	private DragSourceListener active;

	public DelegatingDragSourceListener(DragSourceListener... listeners) {
		this.listeners = listeners;
	}

	@Override
	public void dragStart(DragSourceEvent event) {
		for (DragSourceListener listener : listeners) {
			listener.dragStart(event);
			if (event.doit) {
				active = listener;
				return;
			}
		}

		active = null;
	}

	@Override
	public void dragSetData(DragSourceEvent event) {
		Optional.ofNullable(active).ifPresent(l -> l.dragSetData(event));
	}

	@Override
	public void dragFinished(DragSourceEvent event) {
		Optional.ofNullable(active).ifPresent(l -> l.dragFinished(event));
		active = null;
	}
}
