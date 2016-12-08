package com.gurella.studio.editor.launch;

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
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.ui.IEditorSite;
import org.osgi.framework.Bundle;

import com.gurella.engine.plugin.Workbench;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.SceneEditor;
import com.gurella.studio.editor.SceneEditorContext;
import com.gurella.studio.editor.SceneEditorRegistry;
import com.gurella.studio.editor.menu.ContextMenuActions;
import com.gurella.studio.editor.menu.EditorContextMenuContributor;
import com.gurella.studio.editor.subscription.EditorCloseListener;
import com.gurella.studio.editor.utils.Try;

public class LaunchManager implements EditorContextMenuContributor, EditorCloseListener {
	private static final String section = "run";
	private final int editorId;
	private final IStatusLineManager statusLineManager;

	public LaunchManager(SceneEditor editor) {
		editorId = editor.id;
		IEditorSite site = (IEditorSite) editor.getSite();
		statusLineManager = site.getActionBars().getStatusLineManager();
		Workbench.activate(this);
	}

	@Override
	public void contribute(float x, float y, ContextMenuActions actions) {
		actions.addSection(section);
		actions.addAction("", section, "Run", -800, () -> runSafely(ILaunchManager.RUN_MODE));
		actions.addAction("", section, "Debug", -700, () -> runSafely(ILaunchManager.DEBUG_MODE));
	}

	private void runSafely(String mode) {
		Try.successful(this).peek(t -> t.run2(mode)).onFailure(e -> showError(e, "Error while running scene."));
	}

	private void run2(String mode) throws CoreException {
		SceneEditorContext context = SceneEditorRegistry.getContext(editorId);
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

	@Override
	public void onEditorClose() {
		Workbench.deactivate(this);
	}
}
