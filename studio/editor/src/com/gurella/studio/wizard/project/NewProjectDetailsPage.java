package com.gurella.studio.wizard.project;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.gurella.studio.wizard.project.setup.ProjectType;
import com.gurella.studio.wizard.project.setup.SetupInfo;

public class NewProjectDetailsPage extends WizardPage {
	private final DetailsGroup detailsGroup;
	private final ProjectTypesGroup projectTypesGroup;
	private final AndroidSdkGroup androidSdkGroup;
	private final ConsoleGroup consoleGroup;

	private List<Validator> validators = new ArrayList<>();

	protected NewProjectDetailsPage() {
		super("NewProjectWizardPageTwo");
		setPageComplete(false);
		setTitle("Create Gurella Project");
		setDescription("Create Gurella project in the workspace or in an external location.");

		detailsGroup = new DetailsGroup(this);
		validators.add(detailsGroup);

		projectTypesGroup = new ProjectTypesGroup(this);
		validators.add(projectTypesGroup);

		androidSdkGroup = new AndroidSdkGroup(this);
		validators.add(androidSdkGroup);

		consoleGroup = new ConsoleGroup();

		projectTypesGroup.setProjectTypeListener(this::projectTypeSelectionChanged);
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

	void validate() {
		IStatus status = validators.stream().flatMap(v -> v.validate().stream())
				.sorted((s1, s2) -> Integer.compare(s2.getSeverity(), s1.getSeverity())).findFirst().orElse(null);

		if (status == null) {
			setPageComplete(true);
			setErrorMessage(null);
			setMessage(null);
			return;
		}

		switch (status.getSeverity()) {
		case IStatus.OK:
			setPageComplete(true);
			setErrorMessage(null);
			setMessage(null);
			break;
		case IStatus.INFO:
			setPageComplete(true);
			setErrorMessage(null);
			setMessage(status.getMessage(), IMessageProvider.INFORMATION);
			break;
		case IStatus.WARNING:
			setPageComplete(true);
			setErrorMessage(null);
			setMessage(status.getMessage(), IMessageProvider.WARNING);
			break;
		case IStatus.ERROR:
		case IStatus.CANCEL:
			setPageComplete(false);
			setMessage(null);
			setErrorMessage(status.getMessage());
			break;
		default:
			setPageComplete(true);
			setErrorMessage(null);
			setMessage(null);
			break;
		}
	}

	void updateSetupInfo(SetupInfo setupInfo) {
		setupInfo.packageName = detailsGroup.getPackageName();
		setupInfo.mainClass = detailsGroup.getMainClassName();
		setupInfo.initialScene = detailsGroup.getInitialSceneName();
		boolean androidProjectSelected = projectTypesGroup.isSelected(ProjectType.ANDROID);
		setupInfo.androidSdkLocation = androidProjectSelected ? androidSdkGroup.getSdkLocation() : "";
		setupInfo.androidApiLevel = androidProjectSelected ? androidSdkGroup.getApiLevel() : "";
		setupInfo.androidBuildToolsVersion = androidProjectSelected ? androidSdkGroup.getBuildToolsVersion() : "";
		setupInfo.projects.addAll(projectTypesGroup.getSelectedProjectTypes());
	}
}
