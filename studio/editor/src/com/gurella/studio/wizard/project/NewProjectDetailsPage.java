package com.gurella.studio.wizard.project;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class NewProjectDetailsPage extends ValidatedWizardPage {
	private final DetailsGroup detailsGroup;
	private final ProjectTypesGroup projectTypesGroup;
	private final AndroidSdkGroup androidSdkGroup;
	private final ConsoleGroup consoleGroup;

	NewProjectDetailsPage() {
		super("NewProjectDetailsPage");
		setPageComplete(false);
		setTitle("Create Gurella Project");
		setDescription("Create Gurella project in the workspace or in an external location.");

		detailsGroup = new DetailsGroup(this);
		addValidator(detailsGroup);

		projectTypesGroup = new ProjectTypesGroup(this);
		projectTypesGroup.setProjectTypeListener(this::projectTypeSelectionChanged);
		addValidator(projectTypesGroup);

		androidSdkGroup = new AndroidSdkGroup(this);
		addValidator(androidSdkGroup);

		consoleGroup = new ConsoleGroup();
	}

	private void projectTypeSelectionChanged(ProjectType projectType, Boolean selected) {
		if (projectType == ProjectType.ANDROID) {
			androidSdkGroup.setEnabled(selected.booleanValue());
			detailsGroup.setNeedsStructuredPackage(!selected.booleanValue());
		}
	}

	@Override
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);

		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());
		composite.setLayout(createGridLayout());
		composite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		detailsGroup.createControl(composite);
		projectTypesGroup.createControl(composite);
		androidSdkGroup.createControl(composite);
		consoleGroup.createControl(composite);

		setControl(composite);
		validate();
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			detailsGroup.requestFocus();
		}
	}

	private GridLayout createGridLayout() {
		GridLayout layout = new GridLayout(1, false);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		return layout;
	}

	void log(String text) {
		consoleGroup.log(text);
	}

	String getLog() {
		return consoleGroup.getLog();
	}

	void updateProjectSetup(ProjectSetup projectSetup) {
		projectSetup.packageName = detailsGroup.getPackageName();
		projectSetup.initialScene = detailsGroup.getInitialSceneName();
		boolean androidProjectSelected = projectTypesGroup.isSelected(ProjectType.ANDROID);
		projectSetup.androidSdkLocation = androidProjectSelected ? androidSdkGroup.getSdkLocation() : "";
		projectSetup.androidApiLevel = androidProjectSelected ? androidSdkGroup.getApiLevel() : "";
		projectSetup.androidBuildToolsVersion = androidProjectSelected ? androidSdkGroup.getBuildToolsVersion() : "";
		projectSetup.projects.addAll(projectTypesGroup.getSelectedProjectTypes());
	}
}
