package com.gurella.studio.wizard.project;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.dialogs.WorkingSetConfigurationBlock;

import com.gurella.studio.GurellaStudioPlugin;

final class WorkingSetGroup {
	private static final String RESOURCE_WORKING_SET_ID = "org.eclipse.ui.resourceWorkingSetPage";
	private static final String JAVA_WORKING_SET_ID = "org.eclipse.jdt.ui.JavaWorkingSetPage";
	private static final String[] WORKING_SET_IDS = { JAVA_WORKING_SET_ID, RESOURCE_WORKING_SET_ID };

	private WorkingSetConfigurationBlock content;

	WorkingSetGroup() {
		IDialogSettings dialogSettings = GurellaStudioPlugin.getDefault().getDialogSettings();
		content = new WorkingSetConfigurationBlock(dialogSettings, WORKING_SET_IDS);
	}

	public Control createControl(Composite composite) {
		Group group = new Group(composite, SWT.NONE);
		group.setText("Working sets");
		group.setLayout(new GridLayout(1, false));
		content.createContent(group);
		return group;
	}

	public IWorkingSet[] getSelectedWorkingSets() {
		return content.getSelectedWorkingSets();
	}
}