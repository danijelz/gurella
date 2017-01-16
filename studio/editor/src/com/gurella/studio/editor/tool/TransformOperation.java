package com.gurella.studio.editor.tool;

import org.eclipse.core.commands.operations.AbstractOperation;

import com.gurella.engine.plugin.Workbench;
import com.gurella.engine.scene.transform.TransformComponent;
import com.gurella.studio.editor.history.HistoryContributor;
import com.gurella.studio.editor.history.HistoryService;

abstract class TransformOperation extends AbstractOperation implements HistoryContributor {
	final int editorId;
	final TransformComponent transform;

	HistoryService historyService;

	public TransformOperation(String label, int editorId, TransformComponent transform) {
		super(label);
		this.editorId = editorId;
		this.transform = transform;
		Workbench.activate(editorId, this);
	}

	@Override
	public void setHistoryService(HistoryService historyService) {
		this.historyService = historyService;
	}

	abstract void rollback();

	abstract void commit();
}
