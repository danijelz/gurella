package com.gurella.studio.wizard.project;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Text;

import com.gurella.studio.wizard.project.setup.ProjectType;
import com.gurella.studio.wizard.project.setup.SetupInfo;

public class NewProjectDetailsPage extends WizardPage {
	private final DetailsGroup detailsGroup;
	private final ProjectTypesGroup projectTypesGroup;
	private final AndroidSdkGroup androidSdkGroup;

	private Text console;

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
		composite.setLayout(initGridLayout());
		composite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		detailsGroup.createControl(composite);
		projectTypesGroup.createControl(composite);
		androidSdkGroup.createControl(composite);

		Group consoleGroup = new Group(composite, SWT.NONE);
		consoleGroup.setFont(composite.getFont());
		consoleGroup.setText("Log");
		consoleGroup.setLayout(new GridLayout(1, false));
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).minSize(200, 200).grab(true, true)
				.applyTo(consoleGroup);

		console = new Text(consoleGroup,
				SWT.MULTI | SWT.READ_ONLY | SWT.LEFT | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(console);

		setControl(composite);
	}

	private GridLayout initGridLayout() {
		GridLayout layout = new GridLayout(1, false);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		return layout;
	}

	void setLog(String text) {
		console.getDisplay().asyncExec(() -> setLogAsync(text));
	}

	private void setLogAsync(String text) {
		synchronized (console) {
			console.setText(text);
			ScrollBar verticalBar = console.getVerticalBar();
			if (verticalBar != null) {
				verticalBar.setSelection(verticalBar.getMaximum());
			}
		}
	}

	String getLog() {
		return console.getText();
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

	String getMainClassName() {
		return detailsGroup.getMainClassName();
	}

	String getPackageName() {
		return detailsGroup.getPackageName();
	}

	private boolean isSelectedAndroidProjectType() {
		return projectTypesGroup.isSelected(ProjectType.ANDROID);
	}

	String getAndroidSdkLocation() {
		return isSelectedAndroidProjectType() ? androidSdkGroup.getSdkLocation() : "";
	}

	String getAndroidApiLevel() {
		return isSelectedAndroidProjectType() ? androidSdkGroup.getApiLevel() : "";
	}

	String getAndroidBuildToolsVersion() {
		return isSelectedAndroidProjectType() ? androidSdkGroup.getBuildToolsVersion() : "";
	}

	public void updateSetupInfo(SetupInfo setupInfo) {
		setupInfo.packageName = getPackageName();
		setupInfo.mainClass = getMainClassName();
		setupInfo.androidSdkLocation = getAndroidSdkLocation();
		setupInfo.androidApiLevel = getAndroidApiLevel();
		setupInfo.androidBuildToolsVersion = getAndroidBuildToolsVersion();
	}
}
