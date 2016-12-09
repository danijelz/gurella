package com.gurella.studio.launch;

import static com.gurella.studio.GurellaStudioPlugin.showError;
import static java.util.stream.Collectors.toList;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_CLASSPATH;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_DEFAULT_CLASSPATH;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION;
import static org.eclipse.jdt.launching.JavaRuntime.newStringVariableClasspathEntry;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.osgi.framework.Bundle;

import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.SceneEditor;
import com.gurella.studio.editor.SceneEditorContext;
import com.gurella.studio.editor.launch.LaunchSceneApplication;
import com.gurella.studio.editor.utils.Try;

public class LaunchSceneShortcut implements ILaunchShortcut {
	@Override
	public void launch(ISelection selection, String mode) {
	}

	@Override
	public void launch(IEditorPart editor, String mode) {
		SceneEditor sceneEditor = (SceneEditor) editor;
		Try.successful(this).peek(t -> t.launch(sceneEditor, mode))
				.onFailure(e -> showError(e, "Error while running scene."));
	}

	private void launch(SceneEditor sceneEditor, String mode) throws CoreException {
		SceneEditorContext context = sceneEditor.getSceneContext();
		IJavaProject javaProject = context.javaProject;
		DebugPlugin plugin = DebugPlugin.getDefault();
		ILaunchManager lm = plugin.getLaunchManager();
		ILaunchConfigurationType type = lm.getLaunchConfigurationType(ID_JAVA_APPLICATION);

		ILaunchConfigurationWorkingCopy wc = type.newInstance(null, "Run ");
		wc.setAttribute(ATTR_PROJECT_NAME, javaProject.getElementName());
		wc.setAttribute(ATTR_MAIN_TYPE_NAME, LaunchSceneApplication.class.getName());
		wc.setAttribute(ATTR_VM_ARGUMENTS, "-DgurellaDebugScene=" + getScenePath(context));
		wc.setAttribute(ATTR_CLASSPATH, getClasspath(javaProject));
		wc.setAttribute(ATTR_DEFAULT_CLASSPATH, false);

		ILaunchConfiguration config = wc.doSave();

		IStatusLineManager statusLineManager = sceneEditor.getEditorSite().getActionBars().getStatusLineManager();
		IProgressMonitor monitor = statusLineManager.getProgressMonitor();
		config.launch(mode, monitor, true);
		monitor.done();
	}

	private static String getScenePath(SceneEditorContext context) {
		// TODO should find safer way to assets relative path
		return context.sceneResource.getProjectRelativePath().removeFirstSegments(1).toString();
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
