package com.gurella.studio.editor.assets;

import static com.gurella.studio.GurellaStudioPlugin.log;
import static org.eclipse.ltk.core.refactoring.CheckConditionsOperation.ALL_CONDITIONS;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ltk.core.refactoring.IUndoManager;
import org.eclipse.ltk.core.refactoring.PerformRefactoringOperation;
import org.eclipse.ltk.core.refactoring.RefactoringCore;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.resource.MoveResourcesDescriptor;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.widgets.TreeItem;

import com.gurella.studio.editor.SceneEditorContext;
import com.gurella.studio.editor.utils.Try;

class MoveAssetDropTargetListener extends DropTargetAdapter {
	private final SceneEditorContext context;
	private final IResource sceneResource;

	MoveAssetDropTargetListener(SceneEditorContext context) {
		this.context = context;
		this.sceneResource = context.sceneResource;
	}

	@Override
	public void dragEnter(DropTargetEvent event) {
		if ((event.operations & DND.DROP_MOVE) == 0 || getTransferingResource() == null) {
			event.detail = DND.DROP_NONE;
			return;
		}

		event.detail = DND.DROP_MOVE;
	}

	private static IResource getTransferingResource() {
		ISelection selection = LocalSelectionTransfer.getTransfer().getSelection();
		if (!(selection instanceof AssetSelection)) {
			return null;
		}

		IResource resource = ((AssetSelection) selection).getAssetResource();
		return resource instanceof IFile || resource instanceof IFolder ? resource : null;
	}

	@Override
	public void dragOver(DropTargetEvent event) {
		event.feedback = DND.FEEDBACK_EXPAND | DND.FEEDBACK_SCROLL;

		if (event.item == null) {
			event.detail = DND.DROP_NONE;
			return;
		}

		TreeItem item = (TreeItem) event.item;
		Object data = item.getData();
		if (!(data instanceof IFolder)) {
			event.detail = DND.DROP_NONE;
			return;
		}

		IResource resource = getTransferingResource();
		if (resource == null || resource == data || sceneResource.equals(resource)) {
			event.detail = DND.DROP_NONE;
			return;
		}

		event.detail = DND.DROP_MOVE;
		event.feedback |= DND.FEEDBACK_SELECT;
	}

	@Override
	public void dropAccept(DropTargetEvent event) {
		event.detail = DND.DROP_MOVE;
	}

	@Override
	public void drop(DropTargetEvent event) {
		if (event.item == null) {
			event.detail = DND.DROP_NONE;
			return;
		}

		TreeItem item = (TreeItem) event.item;
		Object data = item.getData();
		if (!(data instanceof IFolder)) {
			event.detail = DND.DROP_NONE;
			return;
		}

		IFolder folder = (IFolder) data;
		IResource resource = getTransferingResource();
		if (resource == null || resource == folder || sceneResource.equals(resource)) {
			event.detail = DND.DROP_NONE;
			return;
		}

		MoveResourcesDescriptor descriptor = new MoveResourcesDescriptor();
		descriptor.setResourcesToMove(new IResource[] { resource });
		descriptor.setDestination(folder);
		descriptor.setUpdateReferences(true);
		RefactoringStatus status = new RefactoringStatus();
		String errMsg = "Error while moving resource.";
		Try.successful(descriptor).map(d -> d.createRefactoring(status))
				.map(r -> new MoveResourceOperation(context, new PerformRefactoringOperation(r, ALL_CONDITIONS)))
				.onSuccess(o -> context.executeOperation(o, errMsg)).onFailure(e -> log(e, errMsg));
	}

	public static class MoveResourceOperation extends AbstractOperation {
		private final SceneEditorContext context;
		private final PerformRefactoringOperation operation;
		private final IUndoManager undoManager;

		public MoveResourceOperation(SceneEditorContext context, PerformRefactoringOperation operation) {
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
}
