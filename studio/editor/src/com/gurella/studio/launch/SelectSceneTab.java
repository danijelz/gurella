package com.gurella.studio.launch;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

//import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_CLASSPATH;
//import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_DEFAULT_CLASSPATH;
//import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME;
//import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME;
//import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS;

import com.gurella.studio.GurellaStudioPlugin;

public class SelectSceneTab extends AbstractLaunchConfigurationTab {
	protected Text fProjText;
	private Button fProjButton;
	
	@Override
	public void createControl(Composite parent) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
//		configuration.setAttribute(ATTR_PROJECT_NAME, projectName);
//		configuration.setAttribute(ATTR_MAIN_TYPE_NAME, main);
//		configuration.setAttribute(ATTR_VM_ARGUMENTS, vmArguments);
//		configuration.setAttribute(ATTR_CLASSPATH, getClasspath(javaProject));
//		configuration.setAttribute(ATTR_DEFAULT_CLASSPATH, false);
		
		
		// TODO Auto-generated method stub
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		
		// TODO Auto-generated method stub
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		// TODO Auto-generated method stub
	}

	@Override
	public String getName() {
		return "Main";
	}
	
	@Override
	public Image getImage() {
		return GurellaStudioPlugin.getImage("icons/Umbrella-16.png");
	}
}
