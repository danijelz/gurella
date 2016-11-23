package com.gurella.studio.editor.utils;

import java.util.Optional;

import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;

public class DelegatingDropTargetListener implements DropTargetListener {
	private final DropTargetListener[] listeners;
	private DropTargetListener active;

	public DelegatingDropTargetListener(DropTargetListener... listeners) {
		this.listeners = listeners;
	}

	@Override
	public void dragEnter(DropTargetEvent event) {
		for (DropTargetListener listener : listeners) {
			listener.dragEnter(event);
			if (event.detail != DND.DROP_NONE) {
				active = listener;
				return;
			}
		}

		active = null;
	}

	@Override
	public void dragLeave(DropTargetEvent event) {
		Optional.ofNullable(active).ifPresent(l -> l.dragLeave(event));
	}

	@Override
	public void dragOperationChanged(DropTargetEvent event) {
		Optional.ofNullable(active).ifPresent(l -> l.dragOperationChanged(event));
	}

	@Override
	public void dragOver(DropTargetEvent event) {
		Optional.ofNullable(active).ifPresent(l -> l.dragOver(event));
	}

	@Override
	public void drop(DropTargetEvent event) {
		Optional.ofNullable(active).ifPresent(l -> l.drop(event));
		active = null;
	}

	@Override
	public void dropAccept(DropTargetEvent event) {
		Optional.ofNullable(active).ifPresent(l -> l.dropAccept(event));
	}
}
