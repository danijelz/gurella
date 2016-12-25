package com.gurella.studio.wizard.project;

import static com.gurella.studio.GurellaStudioPlugin.PLUGIN_ID;
import static com.gurella.studio.GurellaStudioPlugin.getPluginDialogSettings;
import static java.util.stream.Collectors.toList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.LabelProvider;
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
import com.gurella.studio.wizard.project.setup.SetupConstants;

public class AndroidSdkGroup {
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
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(sdkLocationText);

		selectSdkLocationButton = new Button(androidGroup, SWT.PUSH);
		selectSdkLocationButton.setText("B&rowse...");
		selectSdkLocationButton.setEnabled(false);
		selectSdkLocationButton.addListener(SWT.Selection, e -> selectAndroidSdkLocation());
		GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false, false)
				.applyTo(selectSdkLocationButton);

		Label apiLevelLabel = new Label(androidGroup, SWT.NONE);
		apiLevelLabel.setText("API level:");
		apiLevelCombo = new ComboViewer(new Combo(androidGroup, SWT.DROP_DOWN | SWT.READ_ONLY));
		apiLevelCombo.getCombo().setEnabled(false);
		apiLevelCombo.setContentProvider(ArrayContentProvider.getInstance());
		apiLevelCombo.setLabelProvider(new LabelProvider());
		GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(true, false).span(2, 1).hint(80, 20)
				.applyTo(apiLevelCombo.getCombo());

		Label buildToolsVersionLabel = new Label(androidGroup, SWT.NONE);
		buildToolsVersionLabel.setText("Build tools version");
		buildToolsVersionCombo = new ComboViewer(new Combo(androidGroup, SWT.DROP_DOWN | SWT.READ_ONLY));
		buildToolsVersionCombo.getCombo().setEnabled(false);
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
		if (sdkLocation != null) {
			File buildTools = new File(sdkLocation, "build-tools");
			if (isSdkLocationValid(sdkLocation) && buildTools.exists()) {
				updateSdkLocation(sdkLocation);
			} else if (!buildTools.exists()) {
				String message = "You have no build tools!\nUpdate your Android SDK with build tools version: "
						+ SetupConstants.androidBuildToolsVersion;
				MessageDialog.openError(shell, "Invalid Android SDK location", message);
			} else {
				String message = "Selected directory is not valid Android SDK location";
				MessageDialog.openError(shell, "Invalid Android SDK location", message);
			}
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
		apiLevelCombo.setSelection(findMostAppropriateApiLevel(apiLevels));

		List<BuildToolsVersion> buildToolVersions = extractBuildToolVersions(sdkLocation);
		buildToolsVersionCombo.setInput(buildToolVersions);
		buildToolsVersionCombo.setSelection(findMostAppropriateBuildToolVersion(buildToolVersions));
	}

	private static List<ApiLevel> extractApiLevels(String sdkLocation) {
		File apis = new File(sdkLocation, "platforms");
		return Stream.of(apis.listFiles()).map(f -> toApiLevel(f)).filter(al -> al != null).collect(toList());
	}

	private static ApiLevel toApiLevel(File parentFile) {
		File properties = new File(parentFile, "source.properties");
		try (FileReader reader = new FileReader(properties); BufferedReader buffer = new BufferedReader(reader)) {
			return buffer.lines().filter(l -> l.contains("AndroidVersion.ApiLevel")).map(AndroidSdkGroup::parseApiLevel)
					.findFirst().orElse(null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private static ApiLevel parseApiLevel(String line) {
		String levelString = line.split("\\=")[1];
		int level = Integer.parseInt(levelString);
		return new ApiLevel(level);
	}

	private ISelection findMostAppropriateApiLevel(List<ApiLevel> apiLevels) {
		apiLevelCombo.getInput();
		// TODO Auto-generated method stub
		return null;
	}

	private static List<BuildToolsVersion> extractBuildToolVersions(String sdkLocation) {
		File buildTools = new File(sdkLocation, "build-tools");
		return Stream.of(buildTools.listFiles()).map(f -> toBuildTool(f)).filter(bt -> bt != null).collect(toList());
	}

	private static BuildToolsVersion toBuildTool(File parentFile) {
		File properties = new File(parentFile, "source.properties");
		try (FileReader reader = new FileReader(properties); BufferedReader buffer = new BufferedReader(reader)) {
			return buffer.lines().filter(l -> l.contains("Pkg.Revision")).map(AndroidSdkGroup::parseBuildToolVersion)
					.findFirst().orElse(null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private static BuildToolsVersion parseBuildToolVersion(String line) {
		String versionString = line.split("\\=")[1];
		String[] versionComponents = versionString.split("\\.");
		int[] version = new int[3];

		for (int i = 0; i < 3; i++) {
			version[i] = versionComponents.length < i - 1 ? 0 : Integer.parseInt(versionComponents[i]);
		}
		return new BuildToolsVersion(version);
	}

	private ISelection findMostAppropriateBuildToolVersion(List<BuildToolsVersion> buildToolVersions) {
		// TODO Auto-generated method stub
		return null;
	}

	String getSdkLocationText() {
		return sdkLocationText.getText();
	}

	String getApiLevelCombo() {
		return apiLevelCombo.getSelection().toString();
	}

	String getBuildToolsVersionCombo() {
		return SetupConstants.androidBuildToolsVersion;
	}

	private static final class ApiLevel implements Comparable<ApiLevel> {
		int level;

		ApiLevel(int level) {
			this.level = level;
		}

		@Override
		public int hashCode() {
			return 31 + level;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			ApiLevel other = (ApiLevel) obj;
			return level == other.level;
		}

		@Override
		public String toString() {
			return String.valueOf(level);
		}

		@Override
		public int compareTo(ApiLevel other) {
			return Integer.compare(level, other.level);
		}
	}

	private static class BuildToolsVersion implements Comparable<BuildToolsVersion> {
		int maj;
		int mid;
		int min;

		public BuildToolsVersion(int[] version) {
			this.maj = version[0];
			this.mid = version[1];
			this.min = version[2];
		}

		public int getMaj() {
			return maj;
		}

		public int getMid() {
			return mid;
		}

		public int getMin() {
			return min;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + maj;
			result = prime * result + mid;
			result = prime * result + min;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			BuildToolsVersion other = (BuildToolsVersion) obj;
			if (maj != other.maj) {
				return false;
			}
			if (mid != other.mid) {
				return false;
			}
			if (min != other.min) {
				return false;
			}
			return true;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append(maj);
			builder.append('.');
			builder.append(mid);
			builder.append('.');
			builder.append(min);
			return builder.toString();
		}

		@Override
		public int compareTo(BuildToolsVersion other) {
			return Comparator.comparingInt(BuildToolsVersion::getMaj).thenComparingInt(BuildToolsVersion::getMid)
					.thenComparingInt(BuildToolsVersion::getMin).compare(this, other);
		}
	}
}
