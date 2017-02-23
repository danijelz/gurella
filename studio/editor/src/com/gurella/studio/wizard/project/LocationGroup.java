package com.gurella.studio.wizard.project;

import static com.gurella.studio.GurellaStudioPlugin.PLUGIN_ID;
import static org.eclipse.core.runtime.IStatus.ERROR;
import static org.eclipse.core.runtime.IStatus.INFO;
import static org.eclipse.core.runtime.IStatus.WARNING;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.gurella.engine.utils.Values;
import com.gurella.studio.GurellaStudioPlugin;

class LocationGroup implements Validator {
	private final NewProjectMainPage mainPage;
	private final Supplier<String> projectNameSupplier;
	private final Consumer<String> projectNameConsumer;

	private Button defaultLocationCheck;
	private Text locationField;
	private Button selectLocationButton;

	private String previousExternalLocation = "";

	private static final String DIALOGSTORE_LAST_EXTERNAL_LOC = GurellaStudioPlugin.PLUGIN_ID
			+ ".newProject.lastExternalLocation";

	LocationGroup(NewProjectMainPage mainPage, Supplier<String> projectNameSupplier,
			Consumer<String> projectNameConsumer) {
		this.mainPage = mainPage;
		this.projectNameSupplier = projectNameSupplier;
		this.projectNameConsumer = projectNameConsumer;
	}

	public Control createControl(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		group.setText("Location");
		group.setLayout(new GridLayout(3, false));

		defaultLocationCheck = new Button(group, SWT.CHECK);
		defaultLocationCheck.setText("Use &default location");
		defaultLocationCheck.setSelection(true);
		defaultLocationCheck.addListener(SWT.Selection, e -> defaultLocationSelectionChanged());
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).span(3, 1).grab(true, false)
				.applyTo(defaultLocationCheck);

		Label locationLabel = new Label(group, SWT.NONE);
		locationLabel.setText("&Location:");
		locationField = new Text(group, SWT.LEFT | SWT.BORDER);
		locationField.setText(getDefaultPath());
		locationField.setEnabled(false);
		locationField.addModifyListener(e -> mainPage.validate());
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false).applyTo(locationField);

		selectLocationButton = new Button(group, SWT.PUSH);
		selectLocationButton.setText("B&rowse...");
		selectLocationButton.setEnabled(false);
		selectLocationButton.addListener(SWT.Selection, e -> selectExternalLocation());
		GridDataFactory.fillDefaults().align(SWT.END, SWT.BEGINNING).grab(false, false).applyTo(selectLocationButton);

		return group;
	}

	private String getProjectName() {
		return projectNameSupplier.get();
	}

	private void defaultLocationSelectionChanged() {
		if (isDefaultLocationSelected()) {
			previousExternalLocation = locationField.getText();
			locationField.setText(getDefaultPath());
			locationField.setEnabled(false);
			selectLocationButton.setEnabled(false);
		} else {
			locationField.setText(previousExternalLocation);
			locationField.setEnabled(true);
			selectLocationButton.setEnabled(true);
		}

		mainPage.validate();
	}

	@Override
	public List<IStatus> validate() {
		List<IStatus> result = new ArrayList<>();
		final String location = getLocation();
		if (location.length() == 0) {
			String message = "Enter a location for the project.";
			IStatus status = new Status(WARNING, PLUGIN_ID, message);
			result.add(status);
			return result;
		}

		if (!Path.EMPTY.isValidPath(location)) {
			String message = "Invalid project contents directory";
			IStatus status = new Status(ERROR, PLUGIN_ID, message);
			result.add(status);
		}

		IPath projectPath = Path.fromOSString(location);
		File projectFile = projectPath.toFile();
		if (projectFile.exists() && projectFile.list().length > 0) {
			String message = "The destination is not empty, some files might be overwriten.";
			IStatus status = new Status(INFO, PLUGIN_ID, message);
			result.add(status);
		}

		if (isDefaultLocationSelected()) {
			return result;
		}

		if (isNonexistingLocation(projectFile)) {
			String message = "Cannot create project content at the given external location.";
			IStatus status = new Status(ERROR, PLUGIN_ID, message);
			result.add(status);
		}

		String projectName = getProjectName();
		if (Values.isBlank(projectName)) {
			return result;
		}

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProject handle = workspace.getRoot().getProject(projectName);
		final IStatus locationStatus = workspace.validateProjectLocation(handle, projectPath);
		if (locationStatus != null) {
			result.add(locationStatus);
		}

		return result;
	}

	private static boolean isNonexistingLocation(File file) {
		if (file.exists()) {
			return false;
		}

		File temp = file;
		while (!temp.exists()) {
			temp = temp.getParentFile();
			if (temp == null) {
				return true;
			}
		}

		return !temp.canWrite();
	}

	private String getDefaultPath() {
		return Platform.getLocation().append(getProjectName()).toOSString();
	}

	String getLocation() {
		return isDefaultLocationSelected() ? getDefaultPath() : getLocationText();
	}

	private String getLocationText() {
		return locationField.getText().trim();
	}

	boolean isDefaultLocationSelected() {
		return defaultLocationCheck.getSelection();
	}

	private void selectExternalLocation() {
		Shell shell = selectLocationButton.getShell();
		final DirectoryDialog dialog = new DirectoryDialog(shell);
		dialog.setMessage("Choose a directory for the project contents:");

		String directoryName = getLocationText();
		if (directoryName.length() == 0) {
			String prevLocation = getDialogSettings().get(DIALOGSTORE_LAST_EXTERNAL_LOC);
			if (prevLocation != null) {
				directoryName = prevLocation;
			}
		}

		if (directoryName.length() > 0) {
			final File path = new File(directoryName);
			if (path.exists()) {
				dialog.setFilterPath(directoryName);
			}
		}

		final String selectedDirectory = dialog.open();
		if (selectedDirectory == null) {
			return;
		}

		String oldDirectory = new Path(getLocationText()).lastSegment();
		locationField.setText(selectedDirectory);
		String lastSegment = new Path(selectedDirectory).lastSegment();
		String projectName = getProjectName();
		if (lastSegment != null && (Values.isBlank(projectName) || projectName.equals(oldDirectory))) {
			projectNameConsumer.accept(lastSegment);
		}
		getDialogSettings().put(DIALOGSTORE_LAST_EXTERNAL_LOC, selectedDirectory);
	}

	private static IDialogSettings getDialogSettings() {
		return GurellaStudioPlugin.getDefault().getDialogSettings();
	}

	void projectNameChanged() {
		if (isDefaultLocationSelected()) {
			locationField.setText(getDefaultPath());
		}
	}
}