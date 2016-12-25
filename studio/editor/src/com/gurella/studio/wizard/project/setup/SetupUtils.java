package com.gurella.studio.wizard.project.setup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.IntStream;

import javax.swing.JOptionPane;

public class SetupUtils {
	private SetupUtils() {
	}

	private static boolean isEmptyDirectory(String destination) {
		File file = new File(destination);
		return file.exists() ? file.list().length == 0 : true;
	}

	public static boolean isSdkUpToDate(String sdkLocation) {
		File buildTools = new File(sdkLocation, "build-tools");
		if (!buildTools.exists()) {
			JOptionPane.showMessageDialog(null,
					"You have no build tools!\nUpdate your Android SDK with build tools version: "
							+ SetupConstants.androidBuildToolsVersion);
			return false;
		}

		File apis = new File(sdkLocation, "platforms");
		if (!apis.exists()) {
			JOptionPane.showMessageDialog(null, "You have no Android APIs!\nUpdate your Android SDK with API level: "
					+ SetupConstants.androidApiLevel);
			return false;
		}

		int newestLocalApi = getLatestApi(apis);
		if (newestLocalApi > SetupConstants.androidApiLevel) {
			int value = JOptionPane.showConfirmDialog(null,
					"You have a more recent Android API than the recommended.\nDo you want to use your more recent version?",
					"Warning!", JOptionPane.YES_NO_OPTION);
			if (value != 0) {
				JOptionPane.showMessageDialog(null, "Using API level: " + SetupConstants.androidApiLevel);
			} else {
				// TODO SetupConstants.androidAPILevel = String.valueOf(newestLocalApi);
			}
		} else if (newestLocalApi != SetupConstants.androidApiLevel) {
			JOptionPane.showMessageDialog(null,
					"Please update your Android SDK, you need the Android API: " + SetupConstants.androidApiLevel);
			return false;
		}

		String newestLocalTool = getLatestTools(buildTools);
		int[] localToolVersion = convertTools(newestLocalTool);
		int[] targetToolVersion = convertTools(SetupConstants.androidBuildToolsVersion.toString());
		if (compareVersions(targetToolVersion, localToolVersion)) {
			int value = JOptionPane.showConfirmDialog(null,
					"You have a more recent version of android build tools than the recommended.\nDo you want to use your more recent version?",
					"Warning!", JOptionPane.YES_NO_OPTION);
			if (value != 0) {
				JOptionPane.showMessageDialog(null, "Using build tools: " + SetupConstants.androidBuildToolsVersion);
			} else {
				// TODO SetupConstants.androidBuildToolsVersion = newestLocalTool;
			}
		} else {
			if (!versionsEqual(localToolVersion, targetToolVersion)) {
				JOptionPane.showMessageDialog(null, "Please update your Android SDK, you need build tools: "
						+ SetupConstants.androidBuildToolsVersion);
				return false;
			}
		}

		return true;
	}

	private static int getLatestApi(File apis) {
		int apiLevel = 0;
		for (File api : apis.listFiles()) {
			int level = readAPIVersion(api);
			if (level > apiLevel) {
				apiLevel = level;
			}
		}
		return apiLevel;
	}

	private static String getLatestTools(File buildTools) {
		String version = null;
		int[] versionSplit = new int[3];
		int[] testSplit = new int[3];
		for (File toolsVersion : buildTools.listFiles()) {
			if (version == null) {
				version = readBuildToolsVersion(toolsVersion);
				versionSplit = convertTools(version);
				continue;
			}
			testSplit = convertTools(readBuildToolsVersion(toolsVersion));
			if (compareVersions(versionSplit, testSplit)) {
				version = readBuildToolsVersion(toolsVersion);
				versionSplit = convertTools(version);
			}
		}
		if (version != null) {
			return version;
		} else {
			return "0.0.0";
		}
	}

	private static int readAPIVersion(File parentFile) {
		File properties = new File(parentFile, "source.properties");
		FileReader reader;
		BufferedReader buffer;
		try {
			reader = new FileReader(properties);
			buffer = new BufferedReader(reader);

			String line = null;

			while ((line = buffer.readLine()) != null) {
				if (line.contains("AndroidVersion.ApiLevel")) {

					String versionString = line.split("\\=")[1];
					int apiLevel = Integer.parseInt(versionString);

					buffer.close();
					reader.close();

					return apiLevel;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private static String readBuildToolsVersion(File parentFile) {
		File properties = new File(parentFile, "source.properties");
		FileReader reader;
		BufferedReader buffer;
		try {
			reader = new FileReader(properties);
			buffer = new BufferedReader(reader);

			String line = null;

			while ((line = buffer.readLine()) != null) {
				if (line.contains("Pkg.Revision")) {
					String versionString = line.split("\\=")[1];
					int count = versionString.split("\\.").length;
					for (int i = 0; i < 3 - count; i++) {
						versionString += ".0";
					}

					buffer.close();
					reader.close();

					return versionString;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "0.0.0";
	}

	private static boolean versionsEqual(int[] testVersion, int[] targetVersion) {
		return !IntStream.range(0, 3).filter(i -> testVersion[i] != targetVersion[i]).findAny().isPresent();
	}

	private static boolean compareVersions(int[] version, int[] testVersion) {
		if (testVersion[0] > version[0]) {
			return true;
		} else if (testVersion[0] == version[0]) {
			if (testVersion[1] > version[1]) {
				return true;
			} else if (testVersion[1] == version[1]) {
				return testVersion[2] > version[2];
			}
		}
		return false;
	}

	private static int[] convertTools(String toolsVersion) {
		String[] stringSplit = toolsVersion.split("\\.");
		int[] versionSplit = new int[3];
		if (stringSplit.length == 3) {
			try {
				versionSplit[0] = Integer.parseInt(stringSplit[0]);
				versionSplit[1] = Integer.parseInt(stringSplit[1]);
				versionSplit[2] = Integer.parseInt(stringSplit[2]);
				return versionSplit;
			} catch (NumberFormatException nfe) {
				return new int[] { 0, 0, 0 };
			}
		} else {
			return new int[] { 0, 0, 0 };
		}
	}

	//////////////////////////////////TODO

	/*private void buildProjects(Setup builder) throws InvocationTargetException, InterruptedException {
		if (!SetupUtils.isSdkLocationValid(sdkLocation) && modules.contains(ProjectType.ANDROID)) {
			JOptionPane.showMessageDialog(this,
					"Your Android SDK path doesn't contain an SDK! Please install the Android SDK, including all platforms and build tools!");
			return;
		}
	
		if (modules.contains(ProjectType.ANDROID)) {
			if (!GdxSetup.isSdkUpToDate(sdkLocation)) {
				File sdkLocationFile = new File(sdkLocation);
				try { //give them a poke in the right direction
					if (System.getProperty("os.name").contains("Windows")) {
						String replaced = sdkLocation.replace("\\", "\\\\");
						Runtime.getRuntime().exec("\"" + replaced + "\\SDK Manager.exe\"");
					} else {
						File sdkManager = new File(sdkLocation, "tools/android");
						Runtime.getRuntime().exec(new String[] { sdkManager.getAbsolutePath(), "sdk" });
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				return;
			}
		}
	
		if (!GdxSetup.isEmptyDirectory(destination)) {
			int value = JOptionPane.showConfirmDialog(this, "The destination is not empty, do you want to overwrite?",
					"Warning!", JOptionPane.YES_NO_OPTION);
			if (value != 0) {
				return;
			}
		}
	
		List<String> incompatList = builder.buildProject(modules, dependencies);
		if (incompatList.size() == 0) {
			try {
				builder.build();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
			for (String subIncompat : incompatList) {
				JLabel label = new JLabel(subIncompat);
				label.setAlignmentX(Component.CENTER_ALIGNMENT);
				panel.add(label);
			}
	
			JLabel infoLabel = new JLabel(
					"<html><br><br>The project can be generated, but you wont be able to use these extensions in the respective sub modules<br>Please see the link to learn about extensions</html>");
			infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
			panel.add(infoLabel);
			JEditorPane pane = new JEditorPane("text/html",
					"<a href=\"https://github.com/libgdx/libgdx/wiki/Dependency-management-with-Gradle\">Dependency Management</a>");
			pane.addHyperlinkListener(new HyperlinkListener() {
				@Override
				public void hyperlinkUpdate(HyperlinkEvent e) {
					if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED))
						try {
							Desktop.getDesktop().browse(new URI(e.getURL().toString()));
						} catch (IOException e1) {
							e1.printStackTrace();
						} catch (URISyntaxException e1) {
							e1.printStackTrace();
						}
				}
			});
			pane.setEditable(false);
			pane.setOpaque(false);
			pane.setAlignmentX(Component.CENTER_ALIGNMENT);
			panel.add(pane);
	
			Object[] options = { "Yes, build it!", "No, I'll change my extensions" };
			int value = JOptionPane.showOptionDialog(null, panel, "Extension Incompatibilities",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
			if (value != 0) {
				return;
			} else {
				try {
					builder.build();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}*/
}
