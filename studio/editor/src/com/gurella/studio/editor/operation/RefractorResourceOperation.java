package com.gurella.studio.editor.operation;

import static com.gurella.studio.GurellaStudioPlugin.log;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.ltk.core.refactoring.IUndoManager;
import org.eclipse.ltk.core.refactoring.PerformRefactoringOperation;
import org.eclipse.ltk.core.refactoring.RefactoringCore;

import com.gurella.studio.editor.SceneEditorContext;
import com.gurella.studio.editor.utils.Try;

public class RefractorResourceOperation extends AbstractOperation {
	private final SceneEditorContext context;
	private final PerformRefactoringOperation operation;
	private final IUndoManager undoManager;

	public RefractorResourceOperation(SceneEditorContext context, PerformRefactoringOperation operation) {
		super("move resource");
		this.context = context;
		this.operation = operation;
		undoManager = RefactoringCore.getUndoManager();
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		String errMsg = "Error while undoing moving resource.";
		return Try.successful(operation).peek(o -> context.workspace.run(o, new NullProgressMonitor()))
				.map(m -> Status.OK_STATUS).onFailure(e -> log(e, errMsg)).orElse(Status.CANCEL_STATUS);
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		String errMsg = "Error while moving resource.";
		return Try.successful(undoManager).peek(m -> m.performRedo(null, monitor)).map(m -> Status.OK_STATUS)
				.onFailure(e -> log(e, errMsg)).orElse(Status.CANCEL_STATUS);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		String errMsg = "Error while undoing moving resource.";
		return Try.successful(undoManager).peek(m -> m.performUndo(null, monitor)).map(m -> Status.OK_STATUS)
				.onFailure(e -> log(e, errMsg)).orElse(Status.CANCEL_STATUS);
		// return success ? Status.OK_STATUS : Status.CANCEL_STATUS;
		// IOperationHistory history = OperationHistoryFactory.getOperationHistory();
		// IUndoContext undoContext = RefactoringCorePlugin.getUndoContext();
		// return history.undo(undoContext, monitor, info);
	}
}