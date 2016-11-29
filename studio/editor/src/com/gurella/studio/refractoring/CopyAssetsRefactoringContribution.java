package com.gurella.studio.refractoring;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ltk.core.refactoring.RefactoringContribution;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;
import org.eclipse.ltk.core.refactoring.resource.MoveResourcesDescriptor;

public class CopyAssetsRefactoringContribution extends RefactoringContribution {
	private static final String ATTRIBUTE_NUMBER_OF_RESOURCES = "resources"; //$NON-NLS-1$
	private static final String ATTRIBUTE_ELEMENT = "element"; //$NON-NLS-1$
	private static final String ATTRIBUTE_DESTINATION = "destination"; //$NON-NLS-1$

	@Override
	public Map<String, String> retrieveArgumentMap(RefactoringDescriptor descriptor) {
		if (!(descriptor instanceof CopyAssetsDescriptor)) {
			return null;
		}

		HashMap<String, String> map = new HashMap<>();
		MoveResourcesDescriptor moveDescriptor = (MoveResourcesDescriptor) descriptor;
		IPath[] paths = moveDescriptor.getResourcePathsToMove();
		String project = moveDescriptor.getProject();
		IPath destinationPath = moveDescriptor.getDestinationPath();

		map.put(ATTRIBUTE_NUMBER_OF_RESOURCES, String.valueOf(paths.length));
		for (int i = 0; i < paths.length; i++) {
			map.put(ATTRIBUTE_ELEMENT + (i + 1), resourcePathToHandle(project, paths[i]));
		}
		map.put(ATTRIBUTE_DESTINATION, resourcePathToHandle(project, destinationPath));
		return map;
	}

	private static String resourcePathToHandle(final String project, final IPath resourcePath) {
		if (project != null && project.length() > 0 && resourcePath.segmentCount() != 1
				&& resourcePath.segment(0).equals(project)) {
			return resourcePath.removeFirstSegments(1).toPortableString();
		}
		return resourcePath.toPortableString();
	}

	@Override
	public RefactoringDescriptor createDescriptor() {
		return new CopyAssetsDescriptor();
	}

	@Override
	public RefactoringDescriptor createDescriptor(String id, String project, String description, String comment,
			Map<String, String> arguments, int flags) throws IllegalArgumentException {
		try {
			int numResources = Integer.parseInt(arguments.get(ATTRIBUTE_NUMBER_OF_RESOURCES));
			if (numResources < 0 || numResources > 100000) {
				String msg = "Can not restore CopyAssetsDescriptor from map, number of elements is invalid";
				throw new IllegalArgumentException(msg);
			}

			IPath[] resourcePaths = new IPath[numResources];
			for (int i = 0; i < numResources; i++) {
				String resource = arguments.get(ATTRIBUTE_ELEMENT + String.valueOf(i + 1));
				if (resource == null) {
					String msg = "Can not restore CopyAssetsDescriptor from map, resource missing";
					throw new IllegalArgumentException(msg);
				}

				resourcePaths[i] = handleToResourcePath(project, resource);
			}

			String destination = arguments.get(ATTRIBUTE_DESTINATION);
			if (destination == null) {
				String msg = "Can not restore CopyAssetsDescriptor from map, destination missing";
				throw new IllegalArgumentException(msg);
			}

			IPath destPath = handleToResourcePath(project, destination);

			CopyAssetsDescriptor descriptor = new CopyAssetsDescriptor();
			descriptor.setProject(project);
			descriptor.setDescription(description);
			descriptor.setComment(comment);
			descriptor.setFlags(flags);
			descriptor.setResourcePathsToCopy(resourcePaths);
			descriptor.setDestinationPath(destPath);
			return descriptor;
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Can not restore CopyAssetsDescriptor from map");
		}
	}

	private static IPath handleToResourcePath(final String project, final String handle) {
		final IPath path = Path.fromPortableString(handle);
		if (project != null && project.length() > 0 && !path.isAbsolute()) {
			return new Path(project).append(path).makeAbsolute();
		}
		return path;
	}
}
