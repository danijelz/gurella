package com.gurella.studio.editor.operation;

import static com.gurella.studio.GurellaStudioPlugin.log;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ltk.core.refactoring.IUndoManager;
import org.eclipse.ltk.core.refactoring.PerformRefactoringOperation;
import org.eclipse.ltk.core.refactoring.RefactoringCore;

import com.gurella.studio.editor.SceneEditorContext;
import com.gurella.studio.editor.utils.ErrorStatusFactory;
import com.gurella.studio.editor.utils.Try;

public class RefractoringOperation extends AbstractOperation implements ErrorStatusFactory {
	private final SceneEditorContext context;
	private final PerformRefactoringOperation operation;
	private final IUndoManager undoManager;

	public RefractoringOperation(SceneEditorContext context, String label, PerformRefactoringOperation operation) {
		super(label);
		this.context = context;
		this.operation = operation;
		undoManager = RefactoringCore.getUndoManager();
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		String errMsg = "Error while refractoring resource.";
		return Try.successful(operation).peek(o -> context.workspace.run(o, monitor)).map(o -> Status.OK_STATUS)
				.onFailure(e -> log(e, errMsg)).recover(e -> createErrorStatus(errMsg, e));
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return execute(monitor, info);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		String errMsg = "Error while undoing refractoring resource.";
		return Try.successful(undoManager).peek(m -> m.performUndo(null, monitor)).map(o -> Status.OK_STATUS)
				.onFailure(e -> log(e, errMsg)).recover(e -> createErrorStatus(errMsg, e));
	}
}