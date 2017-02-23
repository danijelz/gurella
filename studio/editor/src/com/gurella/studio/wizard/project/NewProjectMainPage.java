package com.gurella.studio.wizard.project;

import java.io.File;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkingSet;

public class NewProjectMainPage extends ValidatedWizardPage {
	private final NameGroup nameGroup;
	private final LocationGroup locationGroup;
	private final WorkingSetGroup workingSetGroup;

	public NewProjectMainPage() {
		super("NewProjectMainPage");
		setPageComplete(false);
		setTitle("Create Gurella Project");
		setDescription("Create Gurella project in the workspace or in an external location.");

		nameGroup = new NameGroup(this, () -> notifayNameChanged());
		addValidator(nameGroup);
		locationGroup = new LocationGroup(this, () -> nameGroup.getName(), n -> nameGroup.setName(n));
		addValidator(locationGroup);
		workingSetGroup = new WorkingSetGroup();
	}

	private void notifayNameChanged() {
		locationGroup.projectNameChanged();
	}

	@Override
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);

		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());
		composite.setLayout(initGridLayout());
		composite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		Control nameControl = nameGroup.createControl(composite);
		nameControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Control locationControl = locationGroup.createControl(composite);
		locationControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Control workingSetControl = workingSetGroup.createControl(composite);
		workingSetControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		setControl(composite);
		validate();
	}

	private GridLayout initGridLayout() {
		GridLayout layout = new GridLayout(1, false);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		return layout;
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			nameGroup.requestFocus();
		}
	}

	// TODO check if project folder is empty
	private static boolean isEmptyDirectory(String destination) {
		File file = new File(destination);
		return file.exists() ? file.list().length == 0 : true;
	}

	public IWorkingSet[] getWorkingSets() {
		return workingSetGroup.getSelectedWorkingSets();
	}

	public void updateProjectSetup(ProjectSetup projectSetup) {
		projectSetup.appName = nameGroup.getName();
		projectSetup.location = locationGroup.getLocation();
	}
}
