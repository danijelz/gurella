package com.gurella.studio.editor.launch;

import static com.gurella.studio.GurellaStudioPlugin.showError;
import static java.util.stream.Collectors.toList;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION;
import static org.eclipse.jdt.launching.JavaRuntime.newStringVariableClasspathEntry;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.Launch;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.VMRunnerConfiguration;
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

	private void run(String mode) throws CoreException {
		SceneEditorContext context = SceneEditorRegistry.getContext(editorId);
		IJavaProject javaProject = context.javaProject;
		IVMInstall vm = JavaRuntime.getVMInstall(javaProject);
		if (vm == null) {
			vm = JavaRuntime.getDefaultVMInstall();
		}

		Bundle bundle = GurellaStudioPlugin.getDefault().getBundle();
		File bundleFile = Try.successful(bundle).map(b -> FileLocator.getBundleFile(b)).getUnchecked();
		if (bundleFile.isDirectory()
				&& Arrays.stream(bundleFile.list()).filter(n -> "bin".equals(n)).findAny().isPresent()) {
			bundleFile = new File(bundleFile, "bin");
		}

		IVMRunner vmr = vm.getVMRunner(mode);
		String[] cp = JavaRuntime.computeDefaultRuntimeClassPath(javaProject);

		List<String> cpList = new ArrayList<>();
		cpList.addAll(Arrays.asList(cp));
		cpList.add(bundleFile.getAbsolutePath());
		cpList.add(GurellaStudioPlugin.locateFile("lib").getAbsolutePath().concat(File.separator + "*"));

		String main = LaunchSceneApplication.class.getName();
		VMRunnerConfiguration config = new VMRunnerConfiguration(main, cpList.toArray(new String[cpList.size()]));
		config.setWorkingDirectory(context.project.getLocation().toOSString());
		String scenePath = context.sceneResource.getProjectRelativePath().removeFirstSegments(1).toString();
		config.setVMArguments(new String[] { "-DgurellaDebugScene=" + scenePath });
		ILaunch launch = new Launch(null, mode, null);
		vmr.run(config, launch, statusLineManager.getProgressMonitor());

		// IProcess process = launch.getProcesses()[0];
		// IConsole console = DebugUITools.getConsole(process);
	}

	private void run2(String mode) throws CoreException {
		SceneEditorContext context = SceneEditorRegistry.getContext(editorId);
		IJavaProject javaProject = context.javaProject;
		DebugPlugin plugin = DebugPlugin.getDefault();
		ILaunchManager lm = plugin.getLaunchManager();
		ILaunchConfigurationType t = lm.getLaunchConfigurationType(ID_JAVA_APPLICATION);

		ILaunchConfigurationWorkingCopy wc = t.newInstance(null, "Run ");
		wc.setAttribute(ATTR_PROJECT_NAME, javaProject.getElementName());

		String main = LaunchSceneApplication.class.getName();
		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, main);

		String scenePath = context.sceneResource.getProjectRelativePath().removeFirstSegments(1).toString();
		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, "-DgurellaDebugScene=" + scenePath);

		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH, getClasspath(javaProject));
		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_DEFAULT_CLASSPATH, false);

		ILaunchConfiguration config = wc.doSave();
		config.launch(mode, statusLineManager.getProgressMonitor());
	}

	private static List<String> getClasspath(IJavaProject javaProject) throws CoreException {
		Bundle bundle = GurellaStudioPlugin.getDefault().getBundle();
		File bundleFile = Try.successful(bundle).map(b -> FileLocator.getBundleFile(b)).getUnchecked();
		if (bundleFile.isDirectory()) {
			File targetDir = new File(bundleFile, "bin");
			if (targetDir.exists()) {
				bundleFile = targetDir;
			}
		}
		List<String> cp = new ArrayList<>();
		cp.addAll(Arrays.asList(JavaRuntime.computeDefaultRuntimeClassPath(javaProject)));
		cp.add(bundleFile.getAbsolutePath());
		cp.add(GurellaStudioPlugin.locateFile("lib").getAbsolutePath().concat(File.separator + "*"));
		return cp.stream().sequential().map(e -> getClasspathMemento(e)).collect(toList());
	}

	private static String getClasspathMemento(String e) {
		return Try.successful(e).map(x -> newStringVariableClasspathEntry(e).getMemento()).getUnchecked();
	}

	@Override
	public void onEditorClose() {
		Workbench.deactivate(this);
	}
}
