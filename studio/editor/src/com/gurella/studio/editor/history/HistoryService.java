package com.gurella.studio.editor.history;

import org.eclipse.core.commands.operations.IUndoableOperation;

public interface HistoryService {
	void executeOperation(IUndoableOperation operation, String errorMsg);
}
