package com.gurella.studio.launch;

import static com.gurella.studio.GurellaStudioPlugin.showError;
import static java.util.stream.Collectors.toList;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_CLASSPATH;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_DEFAULT_CLASSPATH;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS;
import static org.eclipse.jdt.launching.JavaRuntime.newStringVariableClasspathEntry;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.JavaRuntime;
import org.osgi.framework.Bundle;

import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.SceneEditor;
import com.gurella.studio.editor.SceneEditorContext;
import com.gurella.studio.editor.utils.Try;

public class SceneLauncher {
	public static final String LAUNCH_SCENE_CONFIGURATION_TYPE = "com.gurella.studio.launch.launchSceneConfigurationType";

	public static void launch(SceneEditor sceneEditor, String mode) {
		Try.successful(null).peek(n -> _launch(sceneEditor, mode))
				.onFailure(e -> showError(e, "Error while trying to run a scene."));
	}

	private static void _launch(SceneEditor sceneEditor, String mode) throws CoreException {
		SceneEditorContext context = sceneEditor.getSceneContext();
		IProgressMonitor monitor = context.getProgressMonitor();
		getLaunchConfiguration(context).launch(mode, monitor, true);
		monitor.done();
	}

	private static ILaunchConfiguration getLaunchConfiguration(SceneEditorContext context) throws CoreException {
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType type = manager.getLaunchConfigurationType(LAUNCH_SCENE_CONFIGURATION_TYPE);
		// TODO should find safer way to assets relative path
		IPath path = context.sceneFile.getProjectRelativePath().removeFirstSegments(1);
		String sceneName = "Scene - " + path.removeFileExtension().lastSegment();

		ILaunchConfiguration[] configs = manager.getLaunchConfigurations(type);
		Arrays.stream(configs).filter(c -> sceneName.equals(c.getName()))
				.forEach(c -> Try.successful(c).peek(tc -> tc.delete()));

		IJavaProject javaProject = context.javaProject;
		String projectName = javaProject.getElementName();
		String main = LaunchSceneApplication.class.getName();
		String vmArguments = "-DgurellaDebugScene=" + path.toString();

		ILaunchConfigurationWorkingCopy workingCopy = type.newInstance(null, sceneName);
		workingCopy.setAttribute(ATTR_PROJECT_NAME, projectName);
		workingCopy.setAttribute(ATTR_MAIN_TYPE_NAME, main);
		workingCopy.setAttribute(ATTR_VM_ARGUMENTS, vmArguments);
		workingCopy.setAttribute(ATTR_CLASSPATH, getClasspath(javaProject));
		workingCopy.setAttribute(ATTR_DEFAULT_CLASSPATH, false);
		ILaunchConfiguration config = workingCopy.doSave();
		return config;
	}

	private static List<String> getClasspath(IJavaProject javaProject) throws CoreException {
		List<String> cp = new ArrayList<>();
		cp.addAll(Arrays.asList(JavaRuntime.computeDefaultRuntimeClassPath(javaProject)));
		cp.add(getBundleClasspath());
		cp.add(GurellaStudioPlugin.locateFile("lib").getAbsolutePath().concat(File.separator + "*"));
		return cp.stream().sequential().map(e -> getClasspathMemento(e)).collect(toList());
	}

	private static String getBundleClasspath() {
		Bundle bundle = GurellaStudioPlugin.getDefault().getBundle();
		File bundleFile = Try.successful(bundle).map(b -> FileLocator.getBundleFile(b)).getUnchecked();
		if (bundleFile.isDirectory()) {
			File targetDir = new File(bundleFile, "bin");
			if (targetDir.exists()) {
				bundleFile = targetDir;
			}
		}
		return bundleFile.getAbsolutePath();
	}

	private static String getClasspathMemento(String e) {
		return Try.successful(e).map(x -> newStringVariableClasspathEntry(e).getMemento()).getUnchecked();
	}
}
