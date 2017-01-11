package com.gurella.studio.wizard.project;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IStringButtonAdapter;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.LayoutUtil;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.SelectionButtonDialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringButtonDialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringDialogField;
import org.eclipse.jdt.internal.ui.workingsets.IWorkingSetIDs;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.util.BidiUtils;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.dialogs.WorkingSetConfigurationBlock;

import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.wizard.project.setup.SetupInfo;

public class NewProjectMainPage extends WizardPage {
	private final NameGroup nameGroup;
	private final LocationGroup locationGroup;
	private final WorkingSetGroup workingSetGroup;
	private final Validator fValidator;

	public NewProjectMainPage() {
		super("NewProjectWizardPageOne");
		setPageComplete(false);
		setTitle("Create Gurella Project");
		setDescription("Create Gurella project in the workspace or in an external location.");

		nameGroup = new NameGroup();
		locationGroup = new LocationGroup();
		workingSetGroup = new WorkingSetGroup();

		// establish connections
		nameGroup.addObserver(locationGroup);

		// initialize all elements
		nameGroup.notifyObservers();

		// create and connect validator
		fValidator = new Validator();
		nameGroup.addObserver(fValidator);
		locationGroup.addObserver(fValidator);

		setProjectName(""); //$NON-NLS-1$
		setProjectLocationURI(null);
		setWorkingSets(new IWorkingSet[0]);

		JavaRuntime.getDefaultVMInstall();
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
			nameGroup.postSetFocus();
		}
	}

	public String getProjectName() {
		return nameGroup.getName();
	}

	public void setProjectName(String name) {
		if (name == null) {
			throw new IllegalArgumentException();
		}

		nameGroup.setName(name);
	}

	public String getProjectLocation() {
		if (locationGroup.isUseDefaultSelected()) {
			return Platform.getLocation() + File.separator + getProjectName();
		}
		return locationGroup.getLocation().toOSString();
	}

	public void setProjectLocationURI(URI uri) {
		// TODO IPath path= uri != null ? URIUtil.toPath(uri) : null;
		locationGroup.setLocation(null);
	}

	public IWorkingSet[] getWorkingSets() {
		return workingSetGroup.getSelectedWorkingSets();
	}

	/**
	 * Sets the working sets to which the new project should be added.
	 *
	 * @param workingSets
	 *            the initial selected working sets
	 */
	public void setWorkingSets(IWorkingSet[] workingSets) {
		if (workingSets == null) {
			throw new IllegalArgumentException();
		}
		workingSetGroup.setWorkingSets(workingSets);
	}

	private static IDialogSettings getPluginDialogSettings() {
		return GurellaStudioPlugin.getDefault().getDialogSettings();
	}

	private final class NameGroup extends Observable implements IDialogFieldListener {
		protected final StringDialogField fNameField;

		public NameGroup() {
			// text field for project name
			fNameField = new StringDialogField();
			fNameField.setLabelText("&Project name:");
			fNameField.setDialogFieldListener(this);
		}

		public Control createControl(Composite composite) {
			Composite nameComposite = new Composite(composite, SWT.NONE);
			nameComposite.setFont(composite.getFont());
			nameComposite.setLayout(new GridLayout(2, false));

			fNameField.doFillIntoGrid(nameComposite, 2);
			LayoutUtil.setHorizontalGrabbing(fNameField.getTextControl(null));

			return nameComposite;
		}

		public String getName() {
			return fNameField.getText().trim();
		}

		public void postSetFocus() {
			fNameField.postSetFocusOnDialogField(getShell().getDisplay());
		}

		public void setName(String name) {
			fNameField.setText(name);
		}

		@Override
		public void dialogFieldChanged(DialogField field) {
			setChanged();
			notifyObservers();
		}
	}

	private final class LocationGroup extends Observable
			implements Observer, IStringButtonAdapter, IDialogFieldListener {
		protected final SelectionButtonDialogField useDefaults;
		protected final StringButtonDialogField location;

		private String fPreviousExternalLocation;

		private static final String DIALOGSTORE_LAST_EXTERNAL_LOC = GurellaStudioPlugin.PLUGIN_ID
				+ ".newProject.last.external.project"; //$NON-NLS-1$

		public LocationGroup() {
			useDefaults = new SelectionButtonDialogField(SWT.CHECK);
			useDefaults.setDialogFieldListener(this);
			useDefaults.setLabelText("Use &default location");

			location = new StringButtonDialogField(this);
			location.setDialogFieldListener(this);
			location.setLabelText("&Location:");
			location.setButtonLabel("B&rowse...");

			useDefaults.setSelection(true);

			fPreviousExternalLocation = ""; //$NON-NLS-1$
		}

		public Control createControl(Composite composite) {
			final int numColumns = 4;
			Group locationGroup = new Group(composite, SWT.NONE);
			locationGroup.setFont(composite.getFont());
			locationGroup.setText("Location");
			locationGroup.setLayout(new GridLayout(numColumns, false));

			useDefaults.doFillIntoGrid(locationGroup, numColumns);
			location.doFillIntoGrid(locationGroup, numColumns);
			LayoutUtil.setHorizontalGrabbing(location.getTextControl(null));
			BidiUtils.applyBidiProcessing(location.getTextControl(null),
					/* TODO StructuredTextTypeHandlerFactory.FILE */"file");

			return locationGroup;
		}

		protected void fireEvent() {
			setChanged();
			notifyObservers();
		}

		protected String getDefaultPath(String name) {
			return Platform.getLocation().append(name).toOSString();
		}

		@Override
		public void update(Observable o, Object arg) {
			if (isUseDefaultSelected()) {
				location.setText(getDefaultPath(nameGroup.getName()));
			}
			fireEvent();
		}

		public IPath getLocation() {
			if (isUseDefaultSelected()) {
				return Platform.getLocation();
			}
			return Path.fromOSString(location.getText().trim());
		}

		public boolean isUseDefaultSelected() {
			return useDefaults.isSelected();
		}

		public void setLocation(IPath path) {
			useDefaults.setSelection(path == null);
			location.setText(path == null ? getDefaultPath(nameGroup.getName()) : path.toOSString());
			fireEvent();
		}

		@Override
		public void changeControlPressed(DialogField field) {
			final DirectoryDialog dialog = new DirectoryDialog(getShell());
			dialog.setMessage("Choose a directory for the project contents:");
			String directoryName = location.getText().trim();
			if (directoryName.length() == 0) {
				String prevLocation = getPluginDialogSettings().get(DIALOGSTORE_LAST_EXTERNAL_LOC);
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
			if (selectedDirectory != null) {
				String oldDirectory = new Path(location.getText().trim()).lastSegment();
				location.setText(selectedDirectory);
				String lastSegment = new Path(selectedDirectory).lastSegment();
				if (lastSegment != null
						&& (nameGroup.getName().length() == 0 || nameGroup.getName().equals(oldDirectory))) {
					nameGroup.setName(lastSegment);
				}
				getPluginDialogSettings().put(DIALOGSTORE_LAST_EXTERNAL_LOC, selectedDirectory);
			}
		}

		@Override
		public void dialogFieldChanged(DialogField field) {
			if (field == useDefaults) {
				final boolean checked = useDefaults.isSelected();
				if (checked) {
					fPreviousExternalLocation = location.getText();
					location.setText(getDefaultPath(nameGroup.getName()));
					location.setEnabled(false);
				} else {
					location.setText(fPreviousExternalLocation);
					location.setEnabled(true);
				}
			}
			fireEvent();
		}
	}

	private final class Validator implements Observer {
		@Override
		public void update(Observable o, Object arg) {
			final IWorkspace workspace = ResourcesPlugin.getWorkspace();
			final String name = nameGroup.getName();

			// check whether the project name field is empty
			if (name.length() == 0) {
				setErrorMessage(null);
				setMessage("Enter a project name.");
				setPageComplete(false);
				return;
			}

			// check whether the project name is valid
			final IStatus nameStatus = workspace.validateName(name, IResource.PROJECT);
			if (!nameStatus.isOK()) {
				setErrorMessage(nameStatus.getMessage());
				setPageComplete(false);
				return;
			}

			// check whether project already exists
			final IProject handle = workspace.getRoot().getProject(name);
			if (handle.exists()) {
				setErrorMessage("A project with this name already exists.");
				setPageComplete(false);
				return;
			}

			IPath projectLocation = ResourcesPlugin.getWorkspace().getRoot().getLocation().append(name);
			if (projectLocation.toFile().exists()) {
				try {
					// correct casing
					String canonicalPath = projectLocation.toFile().getCanonicalPath();
					projectLocation = new Path(canonicalPath);
				} catch (IOException e) {
					GurellaStudioPlugin.log(e, "");
				}

				String existingName = projectLocation.lastSegment();
				if (!existingName.equals(nameGroup.getName())) {
					setErrorMessage(String.format("The name of the new project must be '%s'", existingName));
					setPageComplete(false);
					return;
				}
			}

			final String location = locationGroup.getLocation().toOSString();

			// check whether location is empty
			if (location.length() == 0) {
				setErrorMessage(null);
				setMessage("Enter a location for the project.");
				setPageComplete(false);
				return;
			}

			// check whether the location is a syntactically correct path
			if (!Path.EMPTY.isValidPath(location)) {
				setErrorMessage("Invalid project contents directory");
				setPageComplete(false);
				return;
			}

			IPath projectPath = null;
			if (!locationGroup.isUseDefaultSelected()) {
				projectPath = Path.fromOSString(location);
				if (!projectPath.toFile().exists()) {
					// check non-existing external location
					if (!canCreate(projectPath.toFile())) {
						setErrorMessage("Cannot create project content at the given external location.");
						setPageComplete(false);
						return;
					}
				}
			}

			// validate the location
			final IStatus locationStatus = workspace.validateProjectLocation(handle, projectPath);
			if (!locationStatus.isOK()) {
				setErrorMessage(locationStatus.getMessage());
				setPageComplete(false);
				return;
			}

			setPageComplete(true);
			setErrorMessage(null);
			setMessage(null);
		}

		private boolean canCreate(File file) {
			File temp = file;
			while (!temp.exists()) {
				temp = temp.getParentFile();
				if (temp == null) {
					return false;
				}
			}

			return temp.canWrite();
		}
	}

	private final class WorkingSetGroup {
		private WorkingSetConfigurationBlock workingSetBlock;

		public WorkingSetGroup() {
			workingSetBlock = new WorkingSetConfigurationBlock(getPluginDialogSettings(), IWorkingSetIDs.JAVA,
					IWorkingSetIDs.RESOURCE);
		}

		public Control createControl(Composite composite) {
			Group workingSetGroup = new Group(composite, SWT.NONE);
			workingSetGroup.setFont(composite.getFont());
			workingSetGroup.setText("Working sets");
			workingSetGroup.setLayout(new GridLayout(1, false));
			workingSetBlock.createContent(workingSetGroup);
			return workingSetGroup;
		}

		public void setWorkingSets(IWorkingSet[] workingSets) {
			workingSetBlock.setWorkingSets(workingSets);
		}

		public IWorkingSet[] getSelectedWorkingSets() {
			return workingSetBlock.getSelectedWorkingSets();
		}
	}

	public void updateSetupInfo(SetupInfo setupInfo) {
		setupInfo.appName = getProjectName();
		setupInfo.location = getProjectLocation();
	}
}
