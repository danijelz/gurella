package com.gurella.studio.editor.operation;

import static com.gurella.studio.GurellaStudioPlugin.log;
import static com.gurella.studio.editor.utils.FileDialogUtils.enterNewFileName;

import java.util.Optional;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.gurella.studio.editor.utils.ErrorStatusFactory;
import com.gurella.studio.editor.utils.Try;

public class DuplicateAssetOperation extends AbstractOperation implements ErrorStatusFactory {
	private final IResource resource;
	private final IFolder destinationFolder;
	private String newName;

	public DuplicateAssetOperation(IResource resource, IFolder destinationFolder) {
		super("Duplicate asset");
		this.resource = resource;
		this.destinationFolder = destinationFolder;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		newName = resource.getName();
		IResource member = destinationFolder.findMember(newName);
		if (member != null && member.exists()) {
			Optional<String> fileName = enterNewFileName(destinationFolder, newName, true, null);
			if (!fileName.isPresent()) {
				return Status.CANCEL_STATUS;
			}
			newName = fileName.get();
		}

		IPath path = destinationFolder.getFullPath().append(newName);
		String errMsg = "Error while duplicating resource.";
		return Try.successful(resource).peek(r -> r.copy(path, true, monitor)).map(r -> Status.OK_STATUS)
				.onFailure(e -> log(e, errMsg)).recover(e -> createErrorStatus(errMsg, e));
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return execute(monitor, info);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		String errMsg = "Error while deleting resource.";
		IResource member = destinationFolder.findMember(newName);
		return Try.successful(member).peek(m -> m.delete(true, monitor)).map(m -> Status.OK_STATUS)
				.onFailure(e -> log(e, errMsg)).recover(e -> createErrorStatus(errMsg, e));
	}
}
