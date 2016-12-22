package com.gurella.studio.wizard;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Text;

import com.gurella.studio.wizard.setup.SetupConstants;

public class NewProjectWizardPageTwo extends WizardPage {
	private Text packageName;
	private Text className;
	private Button desktop;
	private Button android;
	private Button ios;
	private Button html;
	private Text androidSdkLocation;
	private Button selectSdkLocationButton;
	private Text console;

	protected NewProjectWizardPageTwo() {
		super("NewProjectWizardPageTwo");
		setPageComplete(false);
		setTitle("Create Gurella Project");
		setDescription("Create Gurella project in the workspace or in an external location.");
	}

	@Override
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);

		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());
		composite.setLayout(initGridLayout());
		composite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		Group detailsGroup = new Group(composite, SWT.NONE);
		detailsGroup.setFont(composite.getFont());
		detailsGroup.setText("Details");
		detailsGroup.setLayout(new GridLayout(2, false));
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false).applyTo(detailsGroup);

		Label packageNameLabel = new Label(detailsGroup, SWT.NONE);
		packageNameLabel.setText("Package:");
		packageName = new Text(detailsGroup, SWT.LEFT | SWT.BORDER);
		packageName.addModifyListener(e -> validatePackageName());
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false).applyTo(packageName);

		Label classNameLabel = new Label(detailsGroup, SWT.NONE);
		classNameLabel.setText("Main class:");
		className = new Text(detailsGroup, SWT.LEFT | SWT.BORDER);
		className.addModifyListener(e -> validateClassName());
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false).applyTo(className);

		Group projectsGroup = new Group(composite, SWT.NONE);
		projectsGroup.setFont(composite.getFont());
		projectsGroup.setText("Projects");
		projectsGroup.setLayout(new GridLayout(5, false));
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false).applyTo(projectsGroup);

		desktop = new Button(projectsGroup, SWT.CHECK);
		desktop.setText("Desktop");
		GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).grab(true, false).applyTo(desktop);
		android = new Button(projectsGroup, SWT.CHECK);
		android.setText("Android");
		android.addListener(SWT.Selection, e -> androidSelectionChanged());
		android.addListener(SWT.DefaultSelection, e -> androidSelectionChanged());
		GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).grab(true, false).applyTo(android);
		ios = new Button(projectsGroup, SWT.CHECK);
		ios.setText("IOS");
		GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).grab(true, false).applyTo(ios);
		html = new Button(projectsGroup, SWT.CHECK);
		html.setText("Html");
		GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).grab(true, false).applyTo(html);

		Group androidGroup = new Group(composite, SWT.NONE);
		androidGroup.setFont(composite.getFont());
		androidGroup.setText("Android SDK");
		androidGroup.setLayout(new GridLayout(3, false));
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false).applyTo(androidGroup);

		Label sdkLocationLabel = new Label(androidGroup, SWT.NONE);
		sdkLocationLabel.setText("&Location:");
		androidSdkLocation = new Text(androidGroup, SWT.LEFT | SWT.BORDER);
		androidSdkLocation.setEnabled(false);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(androidSdkLocation);
		selectSdkLocationButton = new Button(androidGroup, SWT.PUSH);
		selectSdkLocationButton.setText("B&rowse...");
		selectSdkLocationButton.setEnabled(false);
		GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false, false)
				.applyTo(selectSdkLocationButton);

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

	private void androidSelectionChanged() {
		boolean selected = android.getSelection();
		androidSdkLocation.setEnabled(selected);
		selectSdkLocationButton.setEnabled(selected);
	}

	void log(String text) {
		console.getDisplay().asyncExec(() -> appendLog(text));
	}

	private void appendLog(String text) {
		synchronized (console) {
			console.setText(console.getText() + text);
			ScrollBar verticalBar = console.getVerticalBar();
			if (verticalBar != null) {
				verticalBar.setSelection(verticalBar.getMaximum());
			}
		}
	}

	String getLog() {
		return console.getText();
	}

	private boolean validatePackageName() {
		IStatus status = JavaConventions.validatePackageName(getPackageName(), JavaCore.VERSION_1_6,
				JavaCore.VERSION_1_6);
		presentStatus(status);

		// TODO Auto-generated method stub
		return false;
	}

	private void presentStatus(IStatus status) {
		switch (status.getSeverity()) {
		case IStatus.OK:
			setPageComplete(true);
			setMessage(null, IMessageProvider.NONE);
			break;
		case IStatus.INFO:
			setPageComplete(true);
			setMessage(status.getMessage(), IMessageProvider.INFORMATION);
			break;
		case IStatus.WARNING:
			setMessage(status.getMessage(), IMessageProvider.WARNING);
			break;
		case IStatus.ERROR:
			setMessage(status.getMessage(), IMessageProvider.ERROR);
			break;
		default:
			setPageComplete(true);
			setMessage(null, IMessageProvider.NONE);
			break;
		}
	}

	private boolean validateClassName() {
		// return SourceVersion.isIdentifier(className) && !SourceVersion.isKeyword(className);
		IStatus status = JavaConventions.validateJavaTypeName(getClassName(), JavaCore.VERSION_1_6,
				JavaCore.VERSION_1_6);
		presentStatus(status);

		// TODO Auto-generated method stub
		return false;
	}

	String getClassName() {
		return className.getText().trim();
	}

	String getPackageName() {
		return packageName.getText().trim();
	}

	String getAndroidAPILevel() {
		return SetupConstants.androidAPILevel;
	}

	String getAndroidBuildToolsVersion() {
		return SetupConstants.androidBuildToolsVersion;
	}
}
