package com.gurella.studio.refractoring;

import static com.gurella.studio.GurellaStudioPlugin.PLUGIN_ID;
import static java.util.stream.Collectors.joining;
import static org.eclipse.core.resources.IResourceStatus.OUT_OF_SYNC_LOCAL;
import static org.eclipse.ltk.core.refactoring.RefactoringDescriptor.MULTI_CHANGE;
import static org.eclipse.ltk.core.refactoring.RefactoringDescriptor.STRUCTURAL_CHANGE;
import static org.eclipse.ltk.core.refactoring.participants.ParticipantManager.loadCopyParticipants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.mapping.IResourceChangeDescriptionFactory;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringChangeDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.CopyArguments;
import org.eclipse.ltk.core.refactoring.participants.CopyProcessor;
import org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant;
import org.eclipse.ltk.core.refactoring.participants.ReorgExecutionLog;
import org.eclipse.ltk.core.refactoring.participants.ResourceChangeChecker;
import org.eclipse.ltk.core.refactoring.participants.SharableParticipants;

import com.gurella.studio.editor.utils.Try;

public class CopyAssetsProcessor extends CopyProcessor {
	private final IResource[] resourcesToCopy;
	private IContainer destination;
	private CopyArguments arguments;

	public CopyAssetsProcessor(IResource[] resourcesToCopy) {
		if (resourcesToCopy == null) {
			throw new IllegalArgumentException("resources must not be null"); //$NON-NLS-1$
		}

		this.resourcesToCopy = resourcesToCopy;
		destination = null;
	}

	public IResource[] getResourcesToCopy() {
		return resourcesToCopy;
	}

	public void setDestination(IContainer destination) {
		Assert.isNotNull(destination);
		this.destination = destination;
	}

	@Override
	public Object[] getElements() {
		return resourcesToCopy;
	}

	@Override
	public String getIdentifier() {
		return "com.gurella.studio.refractoring.copyAssetsProcessor";
	}

	@Override
	public String getProcessorName() {
		return "Copy assets processor";
	}

	@Override
	public boolean isApplicable() throws CoreException {
		return !Arrays.stream(resourcesToCopy).anyMatch(r -> !canCopy(r));
	}

	private static boolean canCopy(IResource res) {
		return (res instanceof IFile || res instanceof IFolder) && res.exists() && !res.isPhantom();
	}

	@Override
	public RefactoringStatus checkInitialConditions(IProgressMonitor pm)
			throws CoreException, OperationCanceledException {
		RefactoringStatus result = new RefactoringStatus();
		result.merge(RefactoringStatus.create(checkInSync(resourcesToCopy)));
		return result;
	}

	private static IStatus checkInSync(IResource[] resources) {
		IStatus result = null;
		for (int i = 0; i < resources.length; i++) {
			IResource resource = resources[i];
			if (!resource.isSynchronized(IResource.DEPTH_INFINITE)) {
				result = addOutOfSync(result, resource);
			}
		}

		return result == null ? Status.OK_STATUS : result;
	}

	private static IStatus addOutOfSync(IStatus status, IResource resource) {
		String msg = String.format("Resource '%s' is out of sync with file system.", getPathLabel(resource));
		IStatus entry = new Status(IStatus.ERROR, PLUGIN_ID, OUT_OF_SYNC_LOCAL, msg, null);

		if (status == null) {
			return entry;
		} else if (status.isMultiStatus()) {
			((MultiStatus) status).add(entry);
			return status;
		} else {
			MultiStatus result = new MultiStatus(PLUGIN_ID, OUT_OF_SYNC_LOCAL, "Some resources are out of sync", null);
			result.add(status);
			result.add(entry);
			return result;
		}
	}

	@Override
	public RefactoringStatus checkFinalConditions(IProgressMonitor pm, CheckConditionsContext context)
			throws CoreException, OperationCanceledException {
		pm.beginTask("", 1); //$NON-NLS-1$
		try {
			return checkFinalConditionsSafely(context);
		} finally {
			pm.done();
		}
	}

	private RefactoringStatus checkFinalConditionsSafely(CheckConditionsContext context) {
		RefactoringStatus status = validateDestination(destination);
		if (status.hasFatalError()) {
			return status;
		}

		ResourceChangeChecker checker = context.getChecker(ResourceChangeChecker.class);
		IResourceChangeDescriptionFactory deltaFactory = checker.getDeltaFactory();
		arguments = new CopyArguments(destination, new ReorgExecutionLog());

		for (int i = 0; i < resourcesToCopy.length; i++) {
			IResource resource = resourcesToCopy[i];
			IResource newResource = destination.findMember(resource.getName());
			if (newResource != null) {
				String msg = String.format("'%s' already exist. It will be replaced.", getPathLabel(newResource));
				status.addWarning(msg);
				deltaFactory.delete(newResource);
			}
			buildCopyDelta(deltaFactory, resource, arguments);
		}
		return status;
	}

	private static String getPathLabel(IResource newResource) {
		return getPathLabel(newResource.getFullPath());
	}

	private static String getPathLabel(IPath path) {
		return path.makeRelative().toString();
	}

	private static void buildCopyDelta(IResourceChangeDescriptionFactory builder, IResource resource,
			CopyArguments args) {
		IPath destination = ((IResource) args.getDestination()).getFullPath().append(resource.getName());
		builder.copy(resource, destination);
	}

	public RefactoringStatus validateDestination(IContainer destination) {
		Assert.isNotNull(destination, "container is null"); //$NON-NLS-1$
		if (destination instanceof IWorkspaceRoot) {
			return RefactoringStatus.createFatalErrorStatus("Invalid parent");
		}

		if (!destination.exists()) {
			return RefactoringStatus.createFatalErrorStatus("Destination does not exist");
		}

		IPath destinationPath = destination.getFullPath();
		for (int i = 0; i < resourcesToCopy.length; i++) {
			IPath path = resourcesToCopy[i].getFullPath();
			if (path.isPrefixOf(destinationPath) || path.equals(destinationPath)) {
				return RefactoringStatus.createFatalErrorStatus(
						String.format("Destination is inside copied resource '%s'", getPathLabel(path)));
			}
			if (path.removeLastSegments(1).equals(destinationPath)) {
				return RefactoringStatus.createFatalErrorStatus("The destination contains a resource to be copied");
			}
		}
		return new RefactoringStatus();
	}

	@Override
	public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		pm.beginTask("", resourcesToCopy.length); //$NON-NLS-1$
		try {
			return createChangeSafely();
		} finally {
			pm.done();
		}
	}

	private Change createChangeSafely() {
		CompositeChange compositeChange = new CompositeChange(getCopyDescription());
		compositeChange.markAsSynthetic();

		RefactoringChangeDescriptor descriptor = new RefactoringChangeDescriptor(createDescriptor());
		for (int i = 0; i < resourcesToCopy.length; i++) {
			CopyAssetChange copyChange = new CopyAssetChange(resourcesToCopy[i], destination);
			copyChange.setDescriptor(descriptor);
			compositeChange.add(copyChange);
		}

		return compositeChange;
	}

	private String getCopyDescription() {
		if (resourcesToCopy.length == 1) {
			return String.format("Copy '%s' to '%s'", resourcesToCopy[0].getName(), destination.getName());
		} else {
			return String.format("Copy %d resources to '%s'", Integer.valueOf(resourcesToCopy.length),
					destination.getName());
		}
	}

	protected CopyAssetsDescriptor createDescriptor() {
		CopyAssetsDescriptor descriptor = new CopyAssetsDescriptor();
		descriptor.setProject(destination.getProject().getName());
		descriptor.setDescription(getCopyDescription());
		if (resourcesToCopy.length <= 1) {
			descriptor.setComment(descriptor.getDescription());
		} else {
			String resources = Arrays.stream(resourcesToCopy).map(r -> r.getName()).collect(joining(", "));
			descriptor.setComment(String.format("Copy '%s' to '%s'", resources, destination.getName()));
		}
		descriptor.setFlags(STRUCTURAL_CHANGE | MULTI_CHANGE);
		descriptor.setDestination(destination);
		descriptor.setResourcesToCopy(resourcesToCopy);
		return descriptor;
	}

	@Override
	public RefactoringParticipant[] loadParticipants(RefactoringStatus status, SharableParticipants shared) {
		String[] affectedNatures = computeAffectedNatures(resourcesToCopy);
		return Arrays.stream(resourcesToCopy)
				.map(r -> loadCopyParticipants(status, this, r, arguments, null, affectedNatures, shared))
				.flatMap(c -> Arrays.stream(c)).toArray(i -> new RefactoringParticipant[i]);
	}

	private static String[] computeAffectedNatures(IResource[] resources) {
		Set<String> result = new HashSet<>();
		Set<IProject> visitedProjects = new HashSet<>();
		Arrays.stream(resources).forEach(r -> computeNatures(result, visitedProjects, r.getProject()));
		return result.toArray(new String[result.size()]);
	}

	private static void computeNatures(Set<String> result, Set<IProject> visitedProjects, IProject project) {
		if (visitedProjects.contains(project)) {
			return;
		}

		String[] pns = Try.ofFailable(() -> project.getDescription().getNatureIds()).getUnchecked();
		Arrays.stream(pns).forEach(p -> result.add(p));
		visitedProjects.add(project);
		IProject[] referencing = project.getReferencingProjects();
		Arrays.stream(referencing).forEach(r -> computeNatures(result, visitedProjects, r));
	}
}
