package com.gurella.studio.launch;

import static com.gurella.studio.GurellaStudioPlugin.locateFile;
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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;
import org.osgi.framework.Bundle;

import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.SceneEditorContext;
import com.gurella.studio.editor.utils.Try;

public class SceneLauncher {
	public static void launch(SceneEditorContext context, String mode) {
		Try.run(() -> _launch(context, mode), e -> showError(e, "Error while trying to run a scene."));
	}

	private static void _launch(SceneEditorContext context, String mode) throws CoreException {
		IProgressMonitor monitor = context.getProgressMonitor();
		getLaunchConfiguration(context).launch(mode, monitor, true);
		monitor.done();
	}

	private static ILaunchConfiguration getLaunchConfiguration(SceneEditorContext context) throws CoreException {
		IJavaProject javaProject = context.javaProject;
		IPath path = context.sceneFile.getProjectRelativePath().removeFirstSegments(1);
		return getLaunchConfiguration(path, javaProject);
	}

	public static void launch(IFile sceneFile, String mode) {
		Try.run(() -> _launch(sceneFile, mode), e -> showError(e, "Error while trying to run a scene."));
	}

	private static void _launch(IFile sceneFile, String mode) throws CoreException {
		IProgressMonitor monitor = new NullProgressMonitor();
		IJavaProject javaProject = JavaCore.create(sceneFile.getProject());
		if (javaProject == null) {
			return;
		}

		IPath path = sceneFile.getProjectRelativePath().removeFirstSegments(1);
		getLaunchConfiguration(path, javaProject).launch(mode, monitor, true);
		monitor.done();
		javaProject.close();
	}

	private static ILaunchConfiguration getLaunchConfiguration(IPath path, IJavaProject javaProject)
			throws CoreException {
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType type = manager.getLaunchConfigurationType(LAUNCH_SCENE_CONFIGURATION_TYPE);
		String sceneName = "Scene - " + path.removeFileExtension().lastSegment();

		ILaunchConfiguration[] configs = manager.getLaunchConfigurations(type);
		Arrays.stream(configs).filter(c -> sceneName.equals(c.getName())).forEach(c -> unchecked(() -> c.delete()));

		String projectName = javaProject.getElementName();
		String main = LaunchSceneApplication.class.getName();

		ILaunchConfigurationWorkingCopy workingCopy = type.newInstance(null, sceneName);
		workingCopy.setAttribute(ATTR_PROJECT_NAME, projectName);
		workingCopy.setAttribute(ATTR_MAIN_TYPE_NAME, main);
		workingCopy.setAttribute(SceneLauncherConstants.ATTR_SCENE_NAME, path.toString());
		workingCopy.setAttribute(ATTR_CLASSPATH, computeClasspath(javaProject));
		workingCopy.setAttribute(ATTR_DEFAULT_CLASSPATH, false);
		return workingCopy.doSave();
	}

	static List<String> computeClasspath(IJavaProject javaProject) {
		return unchecked(() -> getClasspath(javaProject));
	}

	private static List<String> getClasspath(IJavaProject javaProject) throws CoreException {
		List<String> cp = new ArrayList<>();
		cp.addAll(Arrays.asList(JavaRuntime.computeDefaultRuntimeClassPath(javaProject)));
		cp.add(getBundleClasspath());
		//TODO extract versions
		cp.add(locateFile("lib/gdx-1.9.5.jar").getAbsolutePath());
		cp.add(locateFile("lib/gdx-backend-lwjgl-1.9.5.jar").getAbsolutePath());
		cp.add(locateFile("lib/gdx-box2d-1.9.5.jar").getAbsolutePath());
		cp.add(locateFile("lib/gdx-box2d-platform-1.9.5-natives-desktop.jar").getAbsolutePath());
		cp.add(locateFile("lib/gdx-bullet-1.9.5.jar").getAbsolutePath());
		cp.add(locateFile("lib/gdx-bullet-platform-1.9.5-natives-desktop.jar").getAbsolutePath());
		cp.add(locateFile("lib/gdx-platform-1.9.5-natives-desktop.jar").getAbsolutePath());
		cp.add(locateFile("lib/jlayer-1.0.1-gdx.jar").getAbsolutePath());
		cp.add(locateFile("lib/jorbis-0.0.17.jar").getAbsolutePath());
		cp.add(locateFile("lib/lwjgl_util-2.9.2.jar").getAbsolutePath());
		cp.add(locateFile("lib/lwjgl-2.9.2.jar").getAbsolutePath());
		cp.add(locateFile("lib/lwjgl-platform-2.9.2-natives-linux.jar").getAbsolutePath());
		cp.add(locateFile("lib/lwjgl-platform-2.9.2-natives-osx.jar").getAbsolutePath());
		cp.add(locateFile("lib/lwjgl-platform-2.9.2-natives-windows.jar").getAbsolutePath());
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
