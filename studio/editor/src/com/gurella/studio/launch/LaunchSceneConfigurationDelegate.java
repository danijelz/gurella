package com.gurella.studio.launch;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.launching.JavaLaunchDelegate;

public class LaunchSceneConfigurationDelegate extends JavaLaunchDelegate {
	@Override
	public String getVMArguments(ILaunchConfiguration configuration) throws CoreException {
		String vmArguments =  super.getVMArguments(configuration);
		return vmArguments + " -DgurellaDebugScene=" + "test.gscn";
	}
}
