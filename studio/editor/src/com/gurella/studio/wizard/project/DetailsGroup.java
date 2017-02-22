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

import com.gurella.engine.utils.Values;

class DetailsGroup implements Validator {
	private final NewProjectDetailsPage detailsPage;

	private Text packageName;
	private Text initialSceneName;
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
		requestFocus();

		Label initialSceneNameLabel = new Label(detailsGroup, SWT.NONE);
		initialSceneNameLabel.setText("Initial scene:");
		initialSceneName = new Text(detailsGroup, SWT.LEFT | SWT.BORDER);
		initialSceneName.addModifyListener(e -> fireValidate());
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false).applyTo(initialSceneName);
	}

	void requestFocus() {
		packageName.setFocus();
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

		String initialSceneName = getInitialSceneName();
		if (Values.isBlank(initialSceneName)) {
			status = new Status(ERROR, PLUGIN_ID, "Enter initial scene name.");
			result.add(status);
		}

		return result;
	}

	void setNeedsStructuredPackage(boolean needsStructuredPackage) {
		this.needsStructuredPackage = needsStructuredPackage;
	}

	String getPackageName() {
		return packageName.getText().trim();
	}

	public String getInitialSceneName() {
		String name = initialSceneName.getText();
		return Values.isBlank(name) || name.endsWith(".gscn") ? name : name + ".gscn";
	}
}
