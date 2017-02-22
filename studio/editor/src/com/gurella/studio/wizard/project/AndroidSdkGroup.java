package com.gurella.studio.wizard.project;

import static com.gurella.studio.GurellaStudioPlugin.PLUGIN_ID;
import static com.gurella.studio.GurellaStudioPlugin.getPluginDialogSettings;
import static java.util.stream.Collectors.toList;
import static org.eclipse.core.runtime.IStatus.ERROR;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
import com.gurella.studio.GurellaStudioPlugin;

public class AndroidSdkGroup implements Validator {
	private static final String DIALOGSTORE_LAST_ANDROIDSDK_LOC = PLUGIN_ID + ".newProject.last.androidsdk.location";

	private final NewProjectDetailsPage detailsPage;

	private Text sdkLocationText;
	private Button selectSdkLocationButton;
	private ComboViewer apiLevelCombo;
	private Button runAndroidManagerButton;
	private ComboViewer buildToolsVersionCombo;

	private List<AndroidApiLevel> androidApiLevels;
	private List<BuildToolsVersion> buildToolVersions;

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
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(false, false).applyTo(selectSdkLocationButton);

		Label apiLevelLabel = new Label(androidGroup, SWT.NONE);
		apiLevelLabel.setText("API level:");
		Combo apiLevel = new Combo(androidGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
		apiLevelCombo = new ComboViewer(apiLevel);
		apiLevel.setEnabled(false);
		apiLevel.addListener(SWT.Selection, e -> fireValidate());
		apiLevelCombo.setContentProvider(ArrayContentProvider.getInstance());
		apiLevelCombo.setLabelProvider(new LabelProvider());
		GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(true, false).hint(80, SWT.DEFAULT)
				.applyTo(apiLevelCombo.getCombo());

		runAndroidManagerButton = new Button(androidGroup, SWT.PUSH);
		runAndroidManagerButton.setText("SDK manager");
		runAndroidManagerButton.setEnabled(false);
		runAndroidManagerButton.addListener(SWT.Selection, e -> runSdkManager());
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(false, false).applyTo(runAndroidManagerButton);

		Label buildToolsVersionLabel = new Label(androidGroup, SWT.NONE);
		buildToolsVersionLabel.setText("Build tools version");
		Combo buildToolsVersion = new Combo(androidGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
		buildToolsVersionCombo = new ComboViewer(buildToolsVersion);
		buildToolsVersion.setEnabled(false);
		buildToolsVersion.addListener(SWT.Selection, e -> fireValidate());
		buildToolsVersionCombo.setContentProvider(ArrayContentProvider.getInstance());
		buildToolsVersionCombo.setLabelProvider(new LabelProvider());
		GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(true, false).span(2, 1)
				.hint(80, SWT.DEFAULT).applyTo(buildToolsVersionCombo.getCombo());

		String prevLocation = getPluginDialogSettings().get(DIALOGSTORE_LAST_ANDROIDSDK_LOC);
		if (Values.isNotBlank(prevLocation) && new File(prevLocation).exists() && isSdkLocationValid(prevLocation)) {
			updateSdkLocation(prevLocation);
		}
	}

	private void runSdkManager() {
		String sdkLocation = getSdkLocation();
		try {
			ProcessBuilder builder = new ProcessBuilder(getSdkManagerCommands(sdkLocation)).redirectErrorStream(true);
			final Process process = builder.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			reader.lines().count();
			process.waitFor();
			if (process.exitValue() == 0) {
				updateSdkData(sdkLocation);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String[] getSdkManagerCommands(String sdkLocation) {
		if (System.getProperty("os.name").contains("Windows")) {
			String replaced = sdkLocation.replace("\\", "\\\\");
			return new String[] { "\"" + replaced + "\\SDK Manager.exe\"" };
		} else {
			File sdkManager = new File(sdkLocation, "tools/android");
			return new String[] { sdkManager.getAbsolutePath(), "sdk" };
		}
	}

	void setEnabled(boolean enabled) {
		sdkLocationText.setEnabled(enabled);
		selectSdkLocationButton.setEnabled(enabled);
		apiLevelCombo.getCombo().setEnabled(enabled);
		buildToolsVersionCombo.getCombo().setEnabled(enabled);
		runAndroidManagerButton.setEnabled(isRunButtonEnabled());
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
		updateSdkData(sdkLocation);
		runAndroidManagerButton.setEnabled(isRunButtonEnabled());
		fireValidate();
	}

	private void updateSdkData(String sdkLocation) {
		IStructuredSelection selection = apiLevelCombo.getStructuredSelection();
		androidApiLevels = extractApiLevels(sdkLocation);
		apiLevelCombo.setInput(androidApiLevels);
		if (!selection.isEmpty()) {
			apiLevelCombo.setSelection(selection);
		} else if (!androidApiLevels.isEmpty()) {
			apiLevelCombo.setSelection(new StructuredSelection(androidApiLevels.get(0)));
		}

		selection = buildToolsVersionCombo.getStructuredSelection();
		buildToolVersions = extractBuildToolVersions(sdkLocation);
		buildToolsVersionCombo.setInput(buildToolVersions);
		if (!selection.isEmpty()) {
			buildToolsVersionCombo.setSelection(selection);
		} else if (!buildToolVersions.isEmpty()) {
			buildToolsVersionCombo.setSelection(new StructuredSelection(buildToolVersions));
		}
	}

	private boolean isRunButtonEnabled() {
		return sdkLocationText.isEnabled() && isSdkLocationValid(sdkLocationText.getText());
	}

	private static List<AndroidApiLevel> extractApiLevels(String sdkLocation) {
		File apis = new File(sdkLocation, "platforms");
		return Stream.of(apis.listFiles()).map(f -> toApiLevel(f)).filter(al -> al != null && al.isValid())
				.collect(toList());
	}

	private static AndroidApiLevel toApiLevel(File parentFile) {
		File properties = new File(parentFile, "source.properties");
		try (FileReader reader = new FileReader(properties); BufferedReader buffer = new BufferedReader(reader)) {
			return buffer.lines().filter(l -> l.contains("AndroidVersion.ApiLevel")).map(AndroidApiLevel::parse)
					.findFirst().orElse(null);
		} catch (Exception e) {
			GurellaStudioPlugin.log(e, "Error while parsing ApiLevel");
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
			GurellaStudioPlugin.log(e, "Error while parsing BuildToolsVersion");
		}

		return null;
	}

	String getSdkLocation() {
		return sdkLocationText.getText();
	}

	String getApiLevel() {
		return getSelectedApiLevel().toString();
	}

	private AndroidApiLevel getSelectedApiLevel() {
		IStructuredSelection selection = (IStructuredSelection) apiLevelCombo.getSelection();
		return selection.isEmpty() ? null : (AndroidApiLevel) selection.getFirstElement();
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
		if (Values.isBlank(sdkLocation) || !isSdkLocationValid(sdkLocation)) {
			Status status = new Status(ERROR, PLUGIN_ID, "Select SDK location.");
			return Collections.singletonList(status);
		}

		List<IStatus> result = new ArrayList<>();

		AndroidApiLevel androidApiLevel = getSelectedApiLevel();
		if (androidApiLevel == null) {
			String message = Values.isEmpty(androidApiLevels) ? "You have no Android APIs! Update your Android SDK."
					: "Select API level.";
			Status status = new Status(ERROR, PLUGIN_ID, message);
			result.add(status);
		}

		BuildToolsVersion buildToolsVersion = getSelectedBuildToolsVersion();
		if (buildToolsVersion == null) {
			String message = Values.isEmpty(buildToolVersions) ? "You have no build tools! Update your Android SDK."
					: "Select build tools version.";
			Status status = new Status(ERROR, PLUGIN_ID, message);
			result.add(status);
		}

		return result;
	}
}
