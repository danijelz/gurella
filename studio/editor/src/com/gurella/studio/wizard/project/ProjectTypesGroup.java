package com.gurella.studio.wizard.project;

import static com.gurella.studio.GurellaStudioPlugin.PLUGIN_ID;
import static java.util.stream.Collectors.toList;
import static org.eclipse.core.runtime.IStatus.ERROR;
import static org.eclipse.core.runtime.IStatus.WARNING;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import com.gurella.studio.wizard.project.setup.ProjectType;

public class ProjectTypesGroup implements Validator {
	private final NewProjectDetailsPage detailsPage;
	private List<Button> buttons = new ArrayList<>();
	private BiConsumer<ProjectType, Boolean> listener;

	public ProjectTypesGroup(NewProjectDetailsPage detailsPage) {
		this.detailsPage = detailsPage;
	}

	void createControl(Composite parent) {
		Group projectsGroup = new Group(parent, SWT.NONE);
		projectsGroup.setFont(parent.getFont());
		projectsGroup.setText("Projects");
		projectsGroup.setLayout(new GridLayout(5, false));
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false).applyTo(projectsGroup);

		createButton(projectsGroup, "Desktop", ProjectType.DESKTOP);
		createButton(projectsGroup, "Android", ProjectType.ANDROID);
		createButton(projectsGroup, "IOS", ProjectType.IOSMOE);
		createButton(projectsGroup, "Html", ProjectType.HTML);
	}

	private void createButton(Composite parent, String text, ProjectType projectType) {
		Button button = new Button(parent, SWT.CHECK);
		button.setText(text);
		button.setData(projectType);
		button.addListener(SWT.Selection, e -> notifyProjectTypeEnablement(projectType, button.getSelection()));
		button.addListener(SWT.DefaultSelection, e -> notifyProjectTypeEnablement(projectType, button.getSelection()));
		GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).grab(true, false).applyTo(button);
		buttons.add(button);
	}

	public void setProjectTypeListener(BiConsumer<ProjectType, Boolean> listener) {
		this.listener = listener;
	}

	private void notifyProjectTypeEnablement(ProjectType projectType, boolean enabled) {
		detailsPage.validate();
		Optional.ofNullable(listener).ifPresent(l -> l.accept(projectType, Boolean.valueOf(enabled)));
	}

	List<ProjectType> getSelectedProjectTypes() {
		return buttons.stream().filter(b -> b.getSelection()).map(b -> (ProjectType) b.getData()).collect(toList());
	}

	boolean isSelected(ProjectType projectType) {
		return getSelectedProjectTypes().contains(projectType);
	}

	@Override
	public List<IStatus> validate() {
		if (getSelectedProjectTypes().isEmpty()) {
			Status status = new Status(WARNING, PLUGIN_ID, "No projects selected. Only core project will be created.");
			return Collections.singletonList(status);
		} else {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			return getSelectedProjectTypes().stream().map(t -> toProjectName(t))
					.filter(n -> workspace.getRoot().getProject(n).exists())
					.map(n -> new Status(ERROR, PLUGIN_ID, "A project with " + n + " already exists."))
					.collect(toList());
		}
	}

	private static String toProjectName(ProjectType t) {
		// TODO Auto-generated method stub
		return t.getName();
	}
}
