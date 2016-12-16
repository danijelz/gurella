package com.gurella.studio.wizard;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
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
import org.eclipse.jdt.internal.corext.util.Messages;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.preferences.CompliancePreferencePage;
import org.eclipse.jdt.internal.ui.preferences.PropertyAndPreferencePage;
import org.eclipse.jdt.internal.ui.viewsupport.BasicElementLabels;
import org.eclipse.jdt.internal.ui.wizards.NewWizardMessages;
import org.eclipse.jdt.internal.ui.wizards.buildpaths.BuildPathSupport;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IStringButtonAdapter;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.LayoutUtil;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.SelectionButtonDialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringButtonDialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringDialogField;
import org.eclipse.jdt.internal.ui.workingsets.IWorkingSetIDs;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.util.BidiUtils;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.dialogs.WorkingSetConfigurationBlock;

public class NewProjectWizardPage extends WizardPage {
	private final NameGroup fNameGroup;
	private final LocationGroup fLocationGroup;
	private final DetectGroup fDetectGroup;
	private final Validator fValidator;
	private final WorkingSetGroup fWorkingSetGroup;

	public NewProjectWizardPage() {
		super("NewProjectWizardPage");
		setPageComplete(false);
		setTitle("Create Gurella Project");
		setDescription("Create Gurella project in the workspace or in an external location.");

		fNameGroup = new NameGroup();
		fLocationGroup = new LocationGroup();
		fDetectGroup = new DetectGroup();
		fWorkingSetGroup= new WorkingSetGroup();

		// establish connections
		fNameGroup.addObserver(fLocationGroup);
		fLocationGroup.addObserver(fDetectGroup);

		// initialize all elements
		fNameGroup.notifyObservers();

		// create and connect validator
		fValidator = new Validator();
		fNameGroup.addObserver(fValidator);
		fLocationGroup.addObserver(fValidator);

		setProjectName(""); //$NON-NLS-1$
		setProjectLocationURI(null);
		setWorkingSets(new IWorkingSet[0]);

		JavaRuntime.getDefaultVMInstall();
	}

	@Override
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);

		final Composite composite = new Composite(parent, SWT.NULL);
		composite.setFont(parent.getFont());
		composite.setLayout(initGridLayout(new GridLayout(1, false), true));
		composite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		Control nameControl = createNameControl(composite);
		nameControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Control locationControl = createLocationControl(composite);
		locationControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Control workingSetControl= createWorkingSetControl(composite);
		workingSetControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Control infoControl = createInfoControl(composite);
		infoControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		setControl(composite);
	}

	private GridLayout initGridLayout(GridLayout layout, boolean margins) {
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		if (margins) {
			layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
			layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		} else {
			layout.marginWidth = 0;
			layout.marginHeight = 0;
		}
		return layout;
	}

	protected Control createNameControl(Composite composite) {
		return fNameGroup.createControl(composite);
	}

	protected Control createLocationControl(Composite composite) {
		return fLocationGroup.createControl(composite);
	}
	
	protected Control createWorkingSetControl(Composite composite) {
		return fWorkingSetGroup.createControl(composite);
	}

	protected Control createInfoControl(Composite composite) {
		return fDetectGroup.createControl(composite);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			fNameGroup.postSetFocus();
		}
	}

	public String getProjectName() {
		return fNameGroup.getName();
	}

	public void setProjectName(String name) {
		if (name == null) {
			throw new IllegalArgumentException();
		}

		fNameGroup.setName(name);
	}

	public String getProjectLocation() {
		return fLocationGroup.getLocation().toOSString();
	}

	public void setProjectLocationURI(URI uri) {
		// TODO IPath path= uri != null ? URIUtil.toPath(uri) : null;
		fLocationGroup.setLocation(null);
	}
	
	public IWorkingSet[] getWorkingSets() {
		return fWorkingSetGroup.getSelectedWorkingSets();
	}

	/**
	 * Sets the working sets to which the new project should be added.
	 *
	 * @param workingSets the initial selected working sets
	 */
	public void setWorkingSets(IWorkingSet[] workingSets) {
		if (workingSets == null) {
			throw new IllegalArgumentException();
		}
		fWorkingSetGroup.setWorkingSets(workingSets);
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

		protected void fireEvent() {
			setChanged();
			notifyObservers();
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
			fireEvent();
		}
	}

	private final class LocationGroup extends Observable
			implements Observer, IStringButtonAdapter, IDialogFieldListener {
		protected final SelectionButtonDialogField fUseDefaults;
		protected final StringButtonDialogField fLocation;

		private String fPreviousExternalLocation;

		private static final String DIALOGSTORE_LAST_EXTERNAL_LOC = JavaUI.ID_PLUGIN + ".last.external.project"; //$NON-NLS-1$

		public LocationGroup() {
			fUseDefaults = new SelectionButtonDialogField(SWT.CHECK);
			fUseDefaults.setDialogFieldListener(this);
			fUseDefaults.setLabelText("Use &default location");

			fLocation = new StringButtonDialogField(this);
			fLocation.setDialogFieldListener(this);
			fLocation.setLabelText("&Location:");
			fLocation.setButtonLabel("B&rowse...");

			fUseDefaults.setSelection(true);

			fPreviousExternalLocation = ""; //$NON-NLS-1$
		}

		public Control createControl(Composite composite) {
			final int numColumns = 4;

			final Composite locationComposite = new Composite(composite, SWT.NONE);
			locationComposite.setLayout(new GridLayout(numColumns, false));

			fUseDefaults.doFillIntoGrid(locationComposite, numColumns);
			fLocation.doFillIntoGrid(locationComposite, numColumns);
			LayoutUtil.setHorizontalGrabbing(fLocation.getTextControl(null));
			BidiUtils.applyBidiProcessing(fLocation.getTextControl(null),
					/* TODO StructuredTextTypeHandlerFactory.FILE */"file");

			return locationComposite;
		}

		protected void fireEvent() {
			setChanged();
			notifyObservers();
		}

		protected String getDefaultPath(String name) {
			final IPath path = Platform.getLocation().append(name);
			return path.toOSString();
		}

		@Override
		public void update(Observable o, Object arg) {
			if (isUseDefaultSelected()) {
				fLocation.setText(getDefaultPath(fNameGroup.getName()));
			}
			fireEvent();
		}

		public IPath getLocation() {
			if (isUseDefaultSelected()) {
				return Platform.getLocation();
			}
			return Path.fromOSString(fLocation.getText().trim());
		}

		public boolean isUseDefaultSelected() {
			return fUseDefaults.isSelected();
		}

		public void setLocation(IPath path) {
			fUseDefaults.setSelection(path == null);
			if (path != null) {
				fLocation.setText(path.toOSString());
			} else {
				fLocation.setText(getDefaultPath(fNameGroup.getName()));
			}
			fireEvent();
		}

		@Override
		public void changeControlPressed(DialogField field) {
			final DirectoryDialog dialog = new DirectoryDialog(getShell());
			dialog.setMessage("Choose a directory for the project contents:");
			String directoryName = fLocation.getText().trim();
			if (directoryName.length() == 0) {
				String prevLocation = JavaPlugin.getDefault().getDialogSettings().get(DIALOGSTORE_LAST_EXTERNAL_LOC);
				if (prevLocation != null) {
					directoryName = prevLocation;
				}
			}

			if (directoryName.length() > 0) {
				final File path = new File(directoryName);
				if (path.exists())
					dialog.setFilterPath(directoryName);
			}
			final String selectedDirectory = dialog.open();
			if (selectedDirectory != null) {
				String oldDirectory = new Path(fLocation.getText().trim()).lastSegment();
				fLocation.setText(selectedDirectory);
				String lastSegment = new Path(selectedDirectory).lastSegment();
				if (lastSegment != null
						&& (fNameGroup.getName().length() == 0 || fNameGroup.getName().equals(oldDirectory))) {
					fNameGroup.setName(lastSegment);
				}
				JavaPlugin.getDefault().getDialogSettings().put(DIALOGSTORE_LAST_EXTERNAL_LOC, selectedDirectory);
			}
		}

		@Override
		public void dialogFieldChanged(DialogField field) {
			if (field == fUseDefaults) {
				final boolean checked = fUseDefaults.isSelected();
				if (checked) {
					fPreviousExternalLocation = fLocation.getText();
					fLocation.setText(getDefaultPath(fNameGroup.getName()));
					fLocation.setEnabled(false);
				} else {
					fLocation.setText(fPreviousExternalLocation);
					fLocation.setEnabled(true);
				}
			}
			fireEvent();
		}
	}

	private final class DetectGroup extends Observable implements Observer, SelectionListener {
		private Link fHintText;
		private Label fIcon;
		private boolean fDetect;

		public DetectGroup() {
			fDetect = false;
		}

		public Control createControl(Composite parent) {

			Composite composite = new Composite(parent, SWT.NONE);
			composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
			GridLayout layout = new GridLayout(2, false);
			layout.horizontalSpacing = 10;
			composite.setLayout(layout);

			fIcon = new Label(composite, SWT.LEFT);
			fIcon.setImage(Dialog.getImage(Dialog.DLG_IMG_MESSAGE_WARNING));
			GridData gridData = new GridData(SWT.LEFT, SWT.TOP, false, false);
			fIcon.setLayoutData(gridData);

			fHintText = new Link(composite, SWT.WRAP);
			fHintText.setFont(composite.getFont());
			fHintText.addSelectionListener(this);
			gridData = new GridData(GridData.FILL, SWT.FILL, true, true);
			gridData.widthHint = convertWidthInCharsToPixels(50);
			gridData.heightHint = convertHeightInCharsToPixels(3);
			fHintText.setLayoutData(gridData);

			return composite;
		}

		private boolean computeDetectState() {
			if (fLocationGroup.isUseDefaultSelected()) {
				String name = fNameGroup.getName();
				if (name.length() == 0 || JavaPlugin.getWorkspace().getRoot().findMember(name) != null) {
					return false;
				} else {
					final File directory = fLocationGroup.getLocation().append(name).toFile();
					return directory.isDirectory();
				}
			} else {
				final File directory = fLocationGroup.getLocation().toFile();
				return directory.isDirectory();
			}
		}

		@Override
		public void update(Observable o, Object arg) {
			if (o instanceof LocationGroup) {
				boolean oldDetectState = fDetect;
				fDetect = computeDetectState();

				if (oldDetectState != fDetect) {
					setChanged();
					notifyObservers();

					if (fDetect) {
						fHintText.setVisible(true);
						fHintText.setText(NewWizardMessages.NewJavaProjectWizardPageOne_DetectGroup_message);
						fIcon.setImage(Dialog.getImage(Dialog.DLG_IMG_MESSAGE_INFO));
						fIcon.setVisible(true);
					}
				}
			}
		}

		public boolean mustDetect() {
			return fDetect;
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			widgetDefaultSelected(e);
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			String jreID = BuildPathSupport.JRE_PREF_PAGE_ID;
			String eeID = BuildPathSupport.EE_PREF_PAGE_ID;
			String complianceId = CompliancePreferencePage.PREF_ID;
			Map<String, Boolean> data = new HashMap<>();
			data.put(PropertyAndPreferencePage.DATA_NO_LINK, Boolean.TRUE);
			String id = "JRE".equals(e.text) ? jreID : complianceId; //$NON-NLS-1$
			PreferencesUtil.createPreferenceDialogOn(getShell(), id, new String[] { jreID, complianceId, eeID }, data)
					.open();
		}
	}

	private final class Validator implements Observer {
		@Override
		public void update(Observable o, Object arg) {

			final IWorkspace workspace = JavaPlugin.getWorkspace();

			final String name = fNameGroup.getName();

			// check whether the project name field is empty
			if (name.length() == 0) {
				setErrorMessage(null);
				setMessage(NewWizardMessages.NewJavaProjectWizardPageOne_Message_enterProjectName);
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
				setErrorMessage(NewWizardMessages.NewJavaProjectWizardPageOne_Message_projectAlreadyExists);
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
					JavaPlugin.log(e);
				}

				String existingName = projectLocation.lastSegment();
				if (!existingName.equals(fNameGroup.getName())) {
					setErrorMessage(Messages.format(
							NewWizardMessages.NewJavaProjectWizardPageOne_Message_invalidProjectNameForWorkspaceRoot,
							BasicElementLabels.getResourceName(existingName)));
					setPageComplete(false);
					return;
				}

			}

			final String location = fLocationGroup.getLocation().toOSString();

			// check whether location is empty
			if (location.length() == 0) {
				setErrorMessage(null);
				setMessage(NewWizardMessages.NewJavaProjectWizardPageOne_Message_enterLocation);
				setPageComplete(false);
				return;
			}

			// check whether the location is a syntactically correct path
			if (!Path.EMPTY.isValidPath(location)) {
				setErrorMessage(NewWizardMessages.NewJavaProjectWizardPageOne_Message_invalidDirectory);
				setPageComplete(false);
				return;
			}

			IPath projectPath = null;
			if (!fLocationGroup.isUseDefaultSelected()) {
				projectPath = Path.fromOSString(location);
				if (!projectPath.toFile().exists()) {
					// check non-existing external location
					if (!canCreate(projectPath.toFile())) {
						setErrorMessage(
								NewWizardMessages.NewJavaProjectWizardPageOne_Message_cannotCreateAtExternalLocation);
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
			while (!file.exists()) {
				file = file.getParentFile();
				if (file == null)
					return false;
			}

			return file.canWrite();
		}
	}

	private final class WorkingSetGroup {
		private WorkingSetConfigurationBlock fWorkingSetBlock;

		public WorkingSetGroup() {
			String[] workingSetIds = new String[] { IWorkingSetIDs.JAVA, IWorkingSetIDs.RESOURCE };
			fWorkingSetBlock = new WorkingSetConfigurationBlock(workingSetIds,
					JavaPlugin.getDefault().getDialogSettings());
		}

		public Control createControl(Composite composite) {
			Group workingSetGroup = new Group(composite, SWT.NONE);
			workingSetGroup.setFont(composite.getFont());
			workingSetGroup.setText(NewWizardMessages.NewJavaProjectWizardPageOne_WorkingSets_group);
			workingSetGroup.setLayout(new GridLayout(1, false));
			fWorkingSetBlock.createContent(workingSetGroup);
			return workingSetGroup;
		}

		public void setWorkingSets(IWorkingSet[] workingSets) {
			fWorkingSetBlock.setWorkingSets(workingSets);
		}

		public IWorkingSet[] getSelectedWorkingSets() {
			return fWorkingSetBlock.getSelectedWorkingSets();
		}
	}
}
