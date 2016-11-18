package com.gurella.studio.editor.graph;

import java.util.Optional;

import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.widgets.Tree;

import com.gurella.studio.editor.SceneEditorContext;

class SceneGraphDropTargetListener implements DropTargetListener {
	private final ComponentDropTargetListener componentDropTargetListener;
	private final NodeDropTargetListener nodeDropTargetListener;
	private final AssetDropTargetListener assetDropTargetListener;
	private DropTargetListener active;

	SceneGraphDropTargetListener(Tree graph, SceneEditorContext context) {
		componentDropTargetListener = new ComponentDropTargetListener(graph, context);
		nodeDropTargetListener = new NodeDropTargetListener(graph, context);
		assetDropTargetListener = new AssetDropTargetListener(context);
	}

	@Override
	public void dragEnter(DropTargetEvent event) {
		componentDropTargetListener.dragEnter(event);
		if (event.detail != DND.DROP_NONE) {
			active = componentDropTargetListener;
			return;
		}

		nodeDropTargetListener.dragEnter(event);
		if (event.detail != DND.DROP_NONE) {
			active = nodeDropTargetListener;
			return;
		}

		assetDropTargetListener.dragEnter(event);
		if (event.detail != DND.DROP_NONE) {
			active = assetDropTargetListener;
			return;
		}
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
