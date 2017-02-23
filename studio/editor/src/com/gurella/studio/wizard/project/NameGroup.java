package com.gurella.studio.wizard.project;

import static com.gurella.studio.GurellaStudioPlugin.PLUGIN_ID;
import static com.gurella.studio.GurellaStudioPlugin.log;
import static org.eclipse.core.runtime.IStatus.ERROR;
import static org.eclipse.core.runtime.IStatus.WARNING;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.gurella.studio.editor.utils.Try;

class NameGroup implements Validator {
	private final NewProjectMainPage mainPage;
	private final Runnable nameChangeListener;

	private Text nameField;

	NameGroup(NewProjectMainPage mainPage, Runnable nameChangeListener) {
		this.mainPage = mainPage;
		this.nameChangeListener = nameChangeListener;
	}

	Control createControl(Composite parent) {
		Composite group = new Composite(parent, SWT.NONE);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false).applyTo(group);
		group.setLayout(new GridLayout(2, false));

		Label nameLabel = new Label(group, SWT.NONE);
		nameLabel.setText("&Project name:");
		nameField = new Text(group, SWT.LEFT | SWT.BORDER);
		nameField.addModifyListener(e -> mainPage.validate());
		nameField.addModifyListener(e -> nameChangeListener.run());
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false).applyTo(nameField);

		return group;
	}

	String getName() {
		return nameField.getText().trim();
	}

	void setName(String name) {
		nameField.setText(name);
	}

	void requestFocus() {
		nameField.setFocus();
	}

	@Override
	public List<IStatus> validate() {
		List<IStatus> result = new ArrayList<>();
		final String name = getName();
		if (name.length() == 0) {
			String message = "Enter a project name.";
			IStatus status = new Status(WARNING, PLUGIN_ID, message);
			result.add(status);
			return result;
		}

		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IStatus nameStatus = workspace.validateName(name, IResource.PROJECT);
		if (nameStatus != null) {
			result.add(nameStatus);
		}

		if (workspace.getRoot().getProject(name).exists()) {
			String message = "A project with " + name + " already exists.";
			IStatus status = new Status(ERROR, PLUGIN_ID, message);
			result.add(status);
		}

		String coreProjectName = name + "-" + ProjectType.CORE.getName();
		if (workspace.getRoot().getProject(coreProjectName).exists()) {
			String message = "A project with " + coreProjectName + " already exists.";
			IStatus status = new Status(ERROR, PLUGIN_ID, message);
			result.add(status);
		}

		// TODO validate other project names

		IPath location = ResourcesPlugin.getWorkspace().getRoot().getLocation().append(name);
		if (location.toFile().exists()) {
			IPath correctedLocation = Try.ignored(() -> correctCasting(location), e -> log(e, ""), location);
			String existingName = correctedLocation.lastSegment();
			if (!existingName.equals(name)) {
				String message = String.format("The name of the new project must be '%s'", existingName);
				IStatus status = new Status(ERROR, PLUGIN_ID, message);
				result.add(status);
			}
		}

		return result;
	}

	private static IPath correctCasting(IPath projectLocation) throws IOException {
		String canonicalPath = projectLocation.toFile().getCanonicalPath();
		return new Path(canonicalPath);
	}
}