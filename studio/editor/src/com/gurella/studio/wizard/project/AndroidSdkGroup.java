package com.gurella.studio.wizard.project;

import static com.gurella.studio.GurellaStudioPlugin.PLUGIN_ID;
import static com.gurella.studio.GurellaStudioPlugin.getPluginDialogSettings;
import static java.util.stream.Collectors.toList;
import static org.eclipse.core.runtime.IStatus.ERROR;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.gurella.engine.utils.Values;

public class AndroidSdkGroup implements Validator {
	private static final String DIALOGSTORE_LAST_ANDROIDSDK_LOC = PLUGIN_ID + ".newProject.last.androidsdk.location";

	private final NewProjectDetailsPage detailsPage;

	private Text sdkLocationText;
	private Button selectSdkLocationButton;
	private ComboViewer apiLevelCombo;
	private Button runAndroidManagerButton;
	private ComboViewer buildToolsVersionCombo;

	public AndroidSdkGroup(NewProjectDetailsPage detailsPage) {
		this.detailsPage = detailsPage;
	}

	void createControl(Composite parent) {
		Group androidGroup = new Group(parent, SWT.NONE);
		androidGroup.setFont(parent.getFont());
		androidGroup.setText("Android SDK");
		androidGroup.setLayout(new GridLayout(3, false));
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false).applyTo(androidGroup);

		Label sdkLocationLabel = new Label(androidGroup, SWT.NONE);
		sdkLocationLabel.setText("&Location:");
		sdkLocationText = new Text(androidGroup, SWT.LEFT | SWT.BORDER);
		sdkLocationText.setEnabled(false);
		sdkLocationText.addModifyListener(e -> fireValidate());
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(sdkLocationText);

		selectSdkLocationButton = new Button(androidGroup, SWT.PUSH);
		selectSdkLocationButton.setText("B&rowse...");
		selectSdkLocationButton.setEnabled(false);
		selectSdkLocationButton.addListener(SWT.Selection, e -> selectAndroidSdkLocation());
		GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false, false)
				.applyTo(selectSdkLocationButton);

		Label apiLevelLabel = new Label(androidGroup, SWT.NONE);
		apiLevelLabel.setText("API level:");
		Combo apiLevel = new Combo(androidGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
		apiLevelCombo = new ComboViewer(apiLevel);
		apiLevel.setEnabled(false);
		apiLevel.addListener(SWT.Selection, e -> fireValidate());
		apiLevelCombo.setContentProvider(ArrayContentProvider.getInstance());
		apiLevelCombo.setLabelProvider(new LabelProvider());
		GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(true, false).span(2, 1).hint(80, 20)
				.applyTo(apiLevelCombo.getCombo());

		Label buildToolsVersionLabel = new Label(androidGroup, SWT.NONE);
		buildToolsVersionLabel.setText("Build tools version");
		Combo buildToolsVersion = new Combo(androidGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
		buildToolsVersionCombo = new ComboViewer(buildToolsVersion);
		buildToolsVersion.setEnabled(false);
		buildToolsVersion.addListener(SWT.Selection, e -> fireValidate());
		buildToolsVersionCombo.setContentProvider(ArrayContentProvider.getInstance());
		buildToolsVersionCombo.setLabelProvider(new LabelProvider());
		GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(true, false).span(2, 1).hint(80, 20)
				.applyTo(buildToolsVersionCombo.getCombo());

		String prevLocation = getPluginDialogSettings().get(DIALOGSTORE_LAST_ANDROIDSDK_LOC);
		if (Values.isNotBlank(prevLocation) && new File(prevLocation).exists() && isSdkLocationValid(prevLocation)) {
			updateSdkLocation(prevLocation);
		}
	}

	void setEnabled(boolean enabled) {
		sdkLocationText.setEnabled(enabled);
		selectSdkLocationButton.setEnabled(enabled);
		apiLevelCombo.getCombo().setEnabled(enabled);
		buildToolsVersionCombo.getCombo().setEnabled(enabled);
	}

	private void selectAndroidSdkLocation() {
		Shell shell = detailsPage.getShell();
		final DirectoryDialog dialog = new DirectoryDialog(shell);
		dialog.setMessage("Choose a directory of Android SDK:");

		String prevLocation = getPluginDialogSettings().get(DIALOGSTORE_LAST_ANDROIDSDK_LOC);
		if (Values.isNotBlank(prevLocation) && isSdkLocationValidAndContainsBuildTools(prevLocation)) {
			dialog.setFilterPath(prevLocation);
		}

		final String sdkLocation = dialog.open();
		if (sdkLocation == null) {
			return;
		}

		if (isSdkLocationValid(sdkLocation)) {
			updateSdkLocation(sdkLocation);
		} else {
			String message = "Selected directory is not valid Android SDK location";
			MessageDialog.openError(shell, "Invalid Android SDK location", message);
		}
	}

	private static boolean isSdkLocationValid(String sdkLocation) {
		return new File(sdkLocation, "tools").exists() && new File(sdkLocation, "platforms").exists();
	}

	private static boolean isSdkLocationValidAndContainsBuildTools(String sdkLocation) {
		return isSdkLocationValid(sdkLocation) && new File(sdkLocation, "build-tools").exists();
	}

	private void updateSdkLocation(String sdkLocation) {
		getPluginDialogSettings().put(DIALOGSTORE_LAST_ANDROIDSDK_LOC, sdkLocation);
		sdkLocationText.setText(sdkLocation);

		List<ApiLevel> apiLevels = extractApiLevels(sdkLocation);
		apiLevelCombo.setInput(apiLevels);
		if (!apiLevels.isEmpty()) {
			apiLevelCombo.setSelection(new StructuredSelection(apiLevels.get(0)));
		}

		List<BuildToolsVersion> buildToolVersions = extractBuildToolVersions(sdkLocation);
		buildToolsVersionCombo.setInput(buildToolVersions);
		if (!buildToolVersions.isEmpty()) {
			buildToolsVersionCombo.setSelection(new StructuredSelection(buildToolVersions));
		}
		
		fireValidate();
	}

	private static List<ApiLevel> extractApiLevels(String sdkLocation) {
		File apis = new File(sdkLocation, "platforms");
		return Stream.of(apis.listFiles()).map(f -> toApiLevel(f)).filter(al -> al != null && al.isValid())
				.collect(toList());
	}

	private static ApiLevel toApiLevel(File parentFile) {
		File properties = new File(parentFile, "source.properties");
		try (FileReader reader = new FileReader(properties); BufferedReader buffer = new BufferedReader(reader)) {
			return buffer.lines().filter(l -> l.contains("AndroidVersion.ApiLevel")).map(ApiLevel::parse).findFirst()
					.orElse(null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private static List<BuildToolsVersion> extractBuildToolVersions(String sdkLocation) {
		File buildTools = new File(sdkLocation, "build-tools");
		if (!buildTools.exists()) {
			return Collections.emptyList();
		}

		return Stream.of(buildTools.listFiles()).map(f -> toBuildTool(f)).filter(bt -> bt != null && bt.isValid())
				.collect(toList());
	}

	private static BuildToolsVersion toBuildTool(File parentFile) {
		File properties = new File(parentFile, "source.properties");
		try (FileReader reader = new FileReader(properties); BufferedReader buffer = new BufferedReader(reader)) {
			return buffer.lines().filter(l -> l.contains("Pkg.Revision")).map(BuildToolsVersion::parse).findFirst()
					.orElse(null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	String getSdkLocation() {
		return sdkLocationText.getText();
	}

	String getApiLevel() {
		return getSelectedApiLevel().toString();
	}

	private ApiLevel getSelectedApiLevel() {
		IStructuredSelection selection = (IStructuredSelection) apiLevelCombo.getSelection();
		return selection.isEmpty() ? null : (ApiLevel) selection.getFirstElement();
	}

	String getBuildToolsVersion() {
		return getSelectedBuildToolsVersion().toString();
	}

	private BuildToolsVersion getSelectedBuildToolsVersion() {
		IStructuredSelection selection = (IStructuredSelection) buildToolsVersionCombo.getSelection();
		return selection.isEmpty() ? null : (BuildToolsVersion) selection.getFirstElement();
	}

	private void fireValidate() {
		detailsPage.validate();
	}

	@Override
	public List<IStatus> validate() {
		if (!sdkLocationText.isEnabled()) {
			return Collections.emptyList();
		}

		String sdkLocation = getSdkLocation();
		if (Values.isBlank(sdkLocation)) {
			Status status = new Status(ERROR, PLUGIN_ID, "Select SDK location.");
			return Collections.singletonList(status);
		}

		ApiLevel apiLevel = getSelectedApiLevel();
		if (apiLevel == null) {
			Status status = new Status(ERROR, PLUGIN_ID, "Select API level.");
			return Collections.singletonList(status);
		}

		BuildToolsVersion buildToolsVersion = getSelectedBuildToolsVersion();
		if (buildToolsVersion == null) {
			Status status = new Status(ERROR, PLUGIN_ID, "Select build tools version.");
			return Collections.singletonList(status);
		}

		return Collections.emptyList();
	}
}
