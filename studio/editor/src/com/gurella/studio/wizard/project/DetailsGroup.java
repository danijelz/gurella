package com.gurella.studio.wizard.project;

import static com.gurella.studio.GurellaStudioPlugin.PLUGIN_ID;
import static org.eclipse.core.runtime.IStatus.ERROR;
import static org.eclipse.jdt.core.JavaCore.VERSION_1_6;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

class DetailsGroup implements Validator {
	private final NewProjectDetailsPage detailsPage;

	private Text packageName;
	private Text className;
	private boolean needsStructuredPackage;

	DetailsGroup(NewProjectDetailsPage detailsPage) {
		this.detailsPage = detailsPage;
	}

	void createControl(Composite parent) {
		Group detailsGroup = new Group(parent, SWT.NONE);
		detailsGroup.setFont(parent.getFont());
		detailsGroup.setText("Details");
		detailsGroup.setLayout(new GridLayout(2, false));
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false).applyTo(detailsGroup);

		Label packageNameLabel = new Label(detailsGroup, SWT.NONE);
		packageNameLabel.setText("Package:");
		packageName = new Text(detailsGroup, SWT.LEFT | SWT.BORDER);
		packageName.addModifyListener(e -> fireValidate());
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false).applyTo(packageName);

		Label classNameLabel = new Label(detailsGroup, SWT.NONE);
		classNameLabel.setText("Main class:");
		className = new Text(detailsGroup, SWT.LEFT | SWT.BORDER);
		className.addModifyListener(e -> fireValidate());
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false).applyTo(className);
	}

	private void fireValidate() {
		detailsPage.validate();
	}

	@Override
	public List<IStatus> validate() {
		List<IStatus> result = new ArrayList<>();
		String packageName = getPackageName();
		IStatus status = JavaConventions.validatePackageName(packageName, VERSION_1_6, VERSION_1_6);
		if (status != null) {
			result.add(status);
		}

		if (needsStructuredPackage && packageName.indexOf('.') < 0) {
			String message = "Android projects require that package name must have at least two parts e.g. 'com.project'";
			status = new Status(ERROR, PLUGIN_ID, message);
			result.add(status);
		}

		status = JavaConventions.validateJavaTypeName(getMainClassName(), VERSION_1_6, VERSION_1_6);
		if (status != null) {
			result.add(status);
		}

		return result;
	}

	void setNeedsStructuredPackage(boolean needsStructuredPackage) {
		this.needsStructuredPackage = needsStructuredPackage;
	}

	String getMainClassName() {
		return className.getText().trim();
	}

	String getPackageName() {
		return packageName.getText().trim();
	}
}
