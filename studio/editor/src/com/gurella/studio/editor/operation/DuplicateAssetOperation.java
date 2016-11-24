package com.gurella.studio.editor.operation;

import static com.gurella.studio.GurellaStudioPlugin.log;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

import com.gurella.engine.utils.Values;
import com.gurella.studio.editor.utils.Try;
import com.gurella.studio.editor.utils.UiUtils;

public class DuplicateAssetOperation extends AbstractOperation {
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
		String name = resource.getName();
		if (validateRename(name) != null) {
			Shell shell = UiUtils.getActiveShell();
			InputDialog dlg = new InputDialog(shell, "Select name", "Enter new name", name, this::validateRename);
			if (dlg.open() != Window.OK) {
				return Status.CANCEL_STATUS;
			}
			newName = dlg.getValue();
		} else {
			newName = name;
		}

		String errMsg = "Error while duplicating resource.";
		IPath path = destinationFolder.getProjectRelativePath().makeRelativeTo(resource.getProjectRelativePath());
		return Try.successful(resource).peek(r -> r.copy(path, true, monitor)).map(r -> Status.OK_STATUS)
				.onFailure(e -> log(e, errMsg)).orElse(Status.CANCEL_STATUS);
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
				.onFailure(e -> log(e, errMsg)).orElse(Status.CANCEL_STATUS);
	}

	private String validateRename(String newFileName) {
		if (Values.isBlank(newFileName)) {
			return "Name must not be empty";
		}

		IResource member = destinationFolder.getParent().findMember(newFileName);
		if (member != null && member.exists()) {
			return "Resource with that name already exists";
		} else {
			return null;
		}
	}
}
