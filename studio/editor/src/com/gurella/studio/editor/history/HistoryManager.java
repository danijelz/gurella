package com.gurella.studio.editor.history;

import static com.gurella.studio.GurellaStudioPlugin.log;
import static com.gurella.studio.GurellaStudioPlugin.showError;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.commands.operations.UndoContext;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.operations.RedoActionHandler;
import org.eclipse.ui.operations.UndoActionHandler;
import org.eclipse.ui.operations.UndoRedoActionGroup;

import com.gurella.engine.event.EventService;
import com.gurella.studio.editor.SceneEditor;
import com.gurella.studio.editor.menu.ContextMenuActions;
import com.gurella.studio.editor.subscription.EditorContextMenuContributor;
import com.gurella.studio.editor.subscription.EditorPreCloseListener;
import com.gurella.studio.editor.utils.Try;

public class HistoryManager extends UndoContext implements EditorPreCloseListener, EditorContextMenuContributor {
	private final int editorId;

	private final IOperationHistory operationHistory;
	private final UndoActionHandler undoAction;
	private final RedoActionHandler redoAction;
	private final UndoRedoActionGroup historyActionGroup;

	public HistoryManager(SceneEditor editor) {
		editorId = editor.id;

		IEditorSite site = (IEditorSite) editor.getSite();
		undoAction = new UndoActionHandler(site, this);
		redoAction = new RedoActionHandler(site, this);

		IWorkbench workbench = site.getWorkbenchWindow().getWorkbench();
		operationHistory = workbench.getOperationSupport().getOperationHistory();
		historyActionGroup = new UndoRedoActionGroup(site, this, true);
		historyActionGroup.fillActionBars(site.getActionBars());

		EventService.subscribe(editorId, this);
	}

	@Override
	public void onEditorPreClose() {
		EventService.unsubscribe(editorId, this);
		historyActionGroup.dispose();
		operationHistory.dispose(this, true, true, true);
		redoAction.dispose();
		undoAction.dispose();
	}

	public void executeOperation(IUndoableOperation operation, String errorMsg) {
		operation.addContext(this);
		Try.ofFailable(() -> operationHistory.execute(operation, null, null)).onFailure(e -> showError(e, errorMsg));
	}

	public boolean canUndo() {
		return operationHistory.canUndo(this);
	}

	public void undo() {
		if (canUndo()) {
			IProgressMonitor monitor = new NullProgressMonitor();
			String msg = "Error while executing undo.";
			Try.ofFailable(() -> operationHistory.undo(this, monitor, null)).onFailure(e -> log(e, msg));
		}
	}

	public boolean canRedo() {
		return operationHistory.canRedo(this);
	}

	public void redo() {
		if (canRedo()) {
			IProgressMonitor monitor = new NullProgressMonitor();
			String msg = "Error while executing redo.";
			Try.ofFailable(() -> operationHistory.redo(this, monitor, null)).onFailure(e -> log(e, msg));
		}
	}

	@Override
	public void contribute(ContextMenuActions actions) {
		actions.addAction("&Undo", -1000, canUndo(), this::undo);
		actions.addAction("&Redo", -900, canRedo(), this::redo);
	}
}
