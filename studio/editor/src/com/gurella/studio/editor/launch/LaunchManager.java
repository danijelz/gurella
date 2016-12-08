package com.gurella.studio.editor.launch;

import static com.gurella.studio.GurellaStudioPlugin.showError;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.Launch;
import org.eclipse.jdt.core.IJavaProject;
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
		Try.successful(this).peek(t -> t.run(mode)).onFailure(e -> showError(e, "Error while running scene."));
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
		ILaunch launch = new Launch(null, ILaunchManager.RUN_MODE, null);
		vmr.run(config, launch, statusLineManager.getProgressMonitor());
	}

	@Override
	public void onEditorClose() {
		Workbench.deactivate(this);
	}
}
