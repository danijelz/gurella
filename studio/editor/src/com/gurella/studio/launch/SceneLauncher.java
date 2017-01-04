package com.gurella.studio.launch;

import static com.gurella.studio.GurellaStudioPlugin.showError;
import static com.gurella.studio.editor.utils.Try.unchecked;
import static com.gurella.studio.launch.SceneLauncherConstants.LAUNCH_SCENE_CONFIGURATION_TYPE;
import static java.util.stream.Collectors.toList;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_CLASSPATH;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_DEFAULT_CLASSPATH;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME;
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
	public static void launch(SceneEditor sceneEditor, String mode) {
		Try.run(() -> _launch(sceneEditor, mode), e -> showError(e, "Error while trying to run a scene."));
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
		Arrays.stream(configs).filter(c -> sceneName.equals(c.getName())).forEach(c -> unchecked(() -> c.delete()));

		IJavaProject javaProject = context.javaProject;
		String projectName = javaProject.getElementName();
		String main = LaunchSceneApplication.class.getName();

		ILaunchConfigurationWorkingCopy workingCopy = type.newInstance(null, sceneName);
		workingCopy.setAttribute(ATTR_PROJECT_NAME, projectName);
		workingCopy.setAttribute(ATTR_MAIN_TYPE_NAME, main);
		workingCopy.setAttribute(SceneLauncherConstants.ATTR_SCENE_NAME, path.toString());
		workingCopy.setAttribute(ATTR_CLASSPATH, computeClasspath(javaProject));
		workingCopy.setAttribute(ATTR_DEFAULT_CLASSPATH, false);
		ILaunchConfiguration config = workingCopy.doSave();
		return config;
	}

	static List<String> computeClasspath(IJavaProject javaProject) {
		return unchecked(() -> getClasspath(javaProject));
	}

	private static List<String> getClasspath(IJavaProject javaProject) throws CoreException {
		List<String> cp = new ArrayList<>();
		cp.addAll(Arrays.asList(JavaRuntime.computeDefaultRuntimeClassPath(javaProject)));
		cp.add(getBundleClasspath());
		//TODO cp.add(GurellaStudioPlugin.locateFile("lib").getAbsolutePath().concat(File.separator + "*"));
		return cp.stream().map(e -> unchecked(() -> newStringVariableClasspathEntry(e).getMemento())).collect(toList());
	}

	private static String getBundleClasspath() {
		Bundle bundle = GurellaStudioPlugin.getDefault().getBundle();
		File bundleFile = unchecked(() -> FileLocator.getBundleFile(bundle));
		if (bundleFile.isDirectory()) {
			File targetDir = new File(bundleFile, "bin");
			if (targetDir.exists()) {
				bundleFile = targetDir;
			}
		}
		return bundleFile.getAbsolutePath();
	}
}
