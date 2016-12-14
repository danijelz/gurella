package com.gurella.studio.wizard.setup;

import static com.gurella.studio.wizard.setup.DependencyBank.ProjectType.DESKTOP;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.IntStream;

import javax.swing.JOptionPane;

import com.badlogic.gdx.utils.StreamUtils;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.utils.Try;
import com.gurella.studio.wizard.setup.DependencyBank.ProjectType;
import com.gurella.studio.wizard.setup.Executor.LogCallback;

/**
 * Command line tool to generate libgdx projects
 * 
 * @author badlogic
 * @author Tomski
 */
public class GdxSetup {
	public static boolean isSdkLocationValid(String sdkLocation) {
		return new File(sdkLocation, "tools").exists() && new File(sdkLocation, "platforms").exists();
	}

	public static boolean isEmptyDirectory(String destination) {
		File file = new File(destination);
		return file.exists() ? file.list().length == 0 : true;
	}

	public static boolean isSdkUpToDate(String sdkLocation) {
		File buildTools = new File(sdkLocation, "build-tools");
		if (!buildTools.exists()) {
			JOptionPane.showMessageDialog(null,
					"You have no build tools!\nUpdate your Android SDK with build tools version: "
							+ DependencyBank.buildToolsVersion);
			return false;
		}

		File apis = new File(sdkLocation, "platforms");
		if (!apis.exists()) {
			JOptionPane.showMessageDialog(null, "You have no Android APIs!\nUpdate your Android SDK with API level: "
					+ DependencyBank.androidAPILevel);
			return false;
		}

		String newestLocalTool = getLatestTools(buildTools);
		int[] localToolVersion = convertTools(newestLocalTool);
		int[] targetToolVersion = convertTools(DependencyBank.buildToolsVersion);
		if (compareVersions(targetToolVersion, localToolVersion)) {
			int value = JOptionPane.showConfirmDialog(null,
					"You have a more recent version of android build tools than the recommended.\nDo you want to use your more recent version?",
					"Warning!", JOptionPane.YES_NO_OPTION);
			if (value != 0) {
				JOptionPane.showMessageDialog(null, "Using build tools: " + DependencyBank.buildToolsVersion);
			} else {
				DependencyBank.buildToolsVersion = newestLocalTool;
			}
		} else {
			if (!versionsEqual(localToolVersion, targetToolVersion)) {
				JOptionPane.showMessageDialog(null,
						"Please update your Android SDK, you need build tools: " + DependencyBank.buildToolsVersion);
				return false;
			}
		}

		int newestLocalApi = getLatestApi(apis);
		if (newestLocalApi > Integer.parseInt(DependencyBank.androidAPILevel)) {
			int value = JOptionPane.showConfirmDialog(null,
					"You have a more recent Android API than the recommended.\nDo you want to use your more recent version?",
					"Warning!", JOptionPane.YES_NO_OPTION);
			if (value != 0) {
				JOptionPane.showMessageDialog(null, "Using API level: " + DependencyBank.androidAPILevel);
			} else {
				DependencyBank.androidAPILevel = String.valueOf(newestLocalApi);
			}
		} else {
			if (newestLocalApi != Integer.parseInt(DependencyBank.androidAPILevel)) {
				JOptionPane.showMessageDialog(null,
						"Please update your Android SDK, you need the Android API: " + DependencyBank.androidAPILevel);
				return false;
			}
		}
		return true;
	}

	private static int getLatestApi(File apis) {
		int apiLevel = 0;
		for (File api : apis.listFiles()) {
			int level = readAPIVersion(api);
			if (level > apiLevel)
				apiLevel = level;
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

	public void build(ProjectBuilder builder, String outputDir, String appName, String packageName, String mainClass,
			String sdkLocation, LogCallback callback, List<String> gradleArgs) {
		Project project = new Project();

		String packageDir = packageName.replace('.', '/');
		String sdkPath = sdkLocation.replace('\\', '/');

		if (!isSdkLocationValid(sdkLocation)) {
			System.out.println("Android SDK location '" + sdkLocation + "' doesn't contain an SDK");
		}

		// root dir/gradle files
		project.files.add(new ProjectFile("gitignore", ".gitignore", false));
		project.files.add(new TemporaryProjectFile(builder.settingsFile, "settings.gradle", false));
		project.files.add(new TemporaryProjectFile(builder.buildFile, "build.gradle", true));
		project.files.add(new ProjectFile("gradlew", false));
		project.files.add(new ProjectFile("gradlew.bat", false));
		project.files.add(new ProjectFile("gradle/wrapper/gradle-wrapper.jar", false));
		project.files.add(new ProjectFile("gradle/wrapper/gradle-wrapper.properties", false));
		project.files.add(new ProjectFile("gradle.properties"));

		// core project
		project.files.add(new ProjectFile("core/build.gradle"));
		project.files
				.add(new ProjectFile("core/src/MainClass", "core/src/" + packageDir + "/" + mainClass + ".java", true));
		if (builder.projects.contains(ProjectType.HTML)) {
			project.files.add(new ProjectFile("core/CoreGdxDefinition", "core/src/" + mainClass + ".gwt.xml", true));
		}

		// desktop project
		if (builder.projects.contains(ProjectType.DESKTOP)) {
			project.files.add(new ProjectFile("desktop/build.gradle"));
			project.files.add(new ProjectFile("desktop/src/DesktopLauncher",
					"desktop/src/" + packageDir + "/desktop/DesktopLauncher.java", true));
		}

		// Assets
		String assetPath = builder.projects.contains(ProjectType.ANDROID) ? "android/assets" : "core/assets";
		project.files.add(new ProjectFile("android/assets/badlogic.jpg", assetPath + "/badlogic.jpg", false));

		// android project
		if (builder.projects.contains(ProjectType.ANDROID)) {
			project.files.add(new ProjectFile("android/res/values/strings.xml"));
			project.files.add(new ProjectFile("android/res/values/styles.xml", false));
			project.files.add(new ProjectFile("android/res/drawable-hdpi/ic_launcher.png", false));
			project.files.add(new ProjectFile("android/res/drawable-mdpi/ic_launcher.png", false));
			project.files.add(new ProjectFile("android/res/drawable-xhdpi/ic_launcher.png", false));
			project.files.add(new ProjectFile("android/res/drawable-xxhdpi/ic_launcher.png", false));
			project.files.add(new ProjectFile("android/res/drawable-xxxhdpi/ic_launcher.png", false));
			project.files.add(new ProjectFile("android/src/AndroidLauncher",
					"android/src/" + packageDir + "/AndroidLauncher.java", true));
			project.files.add(new ProjectFile("android/AndroidManifest.xml"));
			project.files.add(new ProjectFile("android/build.gradle", true));
			project.files.add(new ProjectFile("android/ic_launcher-web.png", false));
			project.files.add(new ProjectFile("android/proguard-project.txt", false));
			project.files.add(new ProjectFile("android/project.properties", false));
			project.files.add(new ProjectFile("local.properties", true));
		}

		// html project
		if (builder.projects.contains(ProjectType.HTML)) {
			project.files.add(new ProjectFile("html/build.gradle"));
			project.files.add(new ProjectFile("html/src/HtmlLauncher",
					"html/src/" + packageDir + "/client/HtmlLauncher.java", true));
			project.files.add(
					new ProjectFile("html/GdxDefinition", "html/src/" + packageDir + "/GdxDefinition.gwt.xml", true));
			project.files.add(new ProjectFile("html/GdxDefinitionSuperdev",
					"html/src/" + packageDir + "/GdxDefinitionSuperdev.gwt.xml", true));
			project.files.add(new ProjectFile("html/war/index", "html/webapp/index.html", true));
			project.files.add(new ProjectFile("html/war/styles.css", "html/webapp/styles.css", false));
			project.files.add(new ProjectFile("html/war/refresh.png", "html/webapp/refresh.png", false));
			project.files.add(
					new ProjectFile("html/war/soundmanager2-jsmin.js", "html/webapp/soundmanager2-jsmin.js", false));
			project.files.add(
					new ProjectFile("html/war/soundmanager2-setup.js", "html/webapp/soundmanager2-setup.js", false));
			project.files.add(new ProjectFile("html/war/WEB-INF/web.xml", "html/webapp/WEB-INF/web.xml", true));
		}

		// ios robovm
		if (builder.projects.contains(ProjectType.IOS)) {
			project.files
					.add(new ProjectFile("ios/src/IOSLauncher", "ios/src/" + packageDir + "/IOSLauncher.java", true));
			project.files.add(new ProjectFile("ios/data/Default.png", false));
			project.files.add(new ProjectFile("ios/data/Default@2x.png", false));
			project.files.add(new ProjectFile("ios/data/Default@2x~ipad.png", false));
			project.files.add(new ProjectFile("ios/data/Default-568h@2x.png", false));
			project.files.add(new ProjectFile("ios/data/Default~ipad.png", false));
			project.files.add(new ProjectFile("ios/data/Default-375w-667h@2x.png", false));
			project.files.add(new ProjectFile("ios/data/Default-414w-736h@3x.png", false));
			project.files.add(new ProjectFile("ios/data/Default-1024w-1366h@2x~ipad.png", false));
			project.files.add(new ProjectFile("ios/data/Icon.png", false));
			project.files.add(new ProjectFile("ios/data/Icon@2x.png", false));
			project.files.add(new ProjectFile("ios/data/Icon-72.png", false));
			project.files.add(new ProjectFile("ios/data/Icon-72@2x.png", false));
			project.files.add(new ProjectFile("ios/build.gradle", true));
			project.files.add(new ProjectFile("ios/Info.plist.xml", false));
			project.files.add(new ProjectFile("ios/robovm.properties"));
			project.files.add(new ProjectFile("ios/robovm.xml", true));
		}

		if (builder.projects.contains(ProjectType.IOSMOE)) {
			project.files.add(new ProjectFile("ios-moe/resources/Default.png", false));
			project.files.add(new ProjectFile("ios-moe/resources/Default@2x.png", false));
			project.files.add(new ProjectFile("ios-moe/resources/Default@2x~ipad.png", false));
			project.files.add(new ProjectFile("ios-moe/resources/Default-568h@2x.png", false));
			project.files.add(new ProjectFile("ios-moe/resources/Default~ipad.png", false));
			project.files.add(new ProjectFile("ios-moe/resources/Default-375w-667h@2x.png", false));
			project.files.add(new ProjectFile("ios-moe/resources/Default-414w-736h@3x.png", false));
			project.files.add(new ProjectFile("ios-moe/resources/Default-1024w-1366h@2x~ipad.png", false));
			project.files.add(new ProjectFile("ios-moe/resources/Icon.png", false));
			project.files.add(new ProjectFile("ios-moe/resources/Icon@2x.png", false));
			project.files.add(new ProjectFile("ios-moe/resources/Icon-72.png", false));
			project.files.add(new ProjectFile("ios-moe/resources/Icon-72@2x.png", false));
			project.files.add(new ProjectFile("ios-moe/src/IOSMoeLauncher",
					"ios-moe/src/" + packageDir + "/IOSMoeLauncher.java", true));
			project.files.add(new ProjectFile("ios-moe/xcode/ios-moe/build.xcconfig", false));
			project.files.add(new ProjectFile("ios-moe/xcode/ios-moe/custom.xcconfig", false));
			project.files.add(new ProjectFile("ios-moe/xcode/ios-moe-Test/build.xcconfig", false));
			project.files.add(new ProjectFile("ios-moe/xcode/ios-moe-Test/Info-Test.plist", false));
			project.files.add(new ProjectFile("ios-moe/xcode/ios-moe/Info.plist", true));
			project.files.add(new ProjectFile("ios-moe/xcode/ios-moe/main.cpp", false));
			project.files.add(new ProjectFile("ios-moe/xcode/ios-moe.xcodeproj/project.pbxproj", true));
			project.files.add(new ProjectFile("ios-moe/build.gradle", true));
		}

		Map<String, String> values = new HashMap<String, String>();
		values.put("%APP_NAME%", appName);
		values.put("%APP_NAME_ESCAPED%", appName.replace("'", "\\'"));
		values.put("%PACKAGE%", packageName);
		values.put("%PACKAGE_DIR%", packageDir);
		values.put("%MAIN_CLASS%", mainClass);
		values.put("%ANDROID_SDK%", sdkPath);
		values.put("%ASSET_PATH%", assetPath);
		values.put("%BUILD_TOOLS_VERSION%", DependencyBank.buildToolsVersion);
		values.put("%API_LEVEL%", DependencyBank.androidAPILevel);
		values.put("%GWT_VERSION%", DependencyBank.gwtVersion);
		if (builder.projects.contains(ProjectType.HTML)) {
			values.put("%GWT_INHERITS%", parseGwtInherits(builder));
		}

		copyAndReplace(outputDir, project, values);
		builder.cleanUp();

		// HACK executable flag isn't preserved for whatever reason...
		new File(outputDir, "gradlew").setExecutable(true);
		String args = "clean" + parseGradleArgs(builder.projects, gradleArgs);
		Executor.execute(new File(outputDir), args, callback);
	}

	private static void copyAndReplace(String outputDir, Project project, Map<String, String> values) {
		File out = new File(outputDir);
		Optional.of(out).filter(o -> o.exists() || o.mkdirs()).orElseThrow(
				() -> new RuntimeException("Couldn't create output directory '" + out.getAbsolutePath() + "'"));
		project.files.forEach(f -> copyFile(f, out, values));
	}

	private static byte[] readResource(String resource, String path) {
		InputStream in = null;
		try {
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			in = GurellaStudioPlugin.getFileInputStream(path + resource);
			if (in == null) {
				throw new RuntimeException("Couldn't read resource '" + resource + "'");
			}
			return readBytes(in, bytes);
		} catch (IOException e) {
			throw new RuntimeException("Couldn't read resource '" + resource + "'", e);
		} finally {
			StreamUtils.closeQuietly(in);
		}
	}

	private static byte[] readBytes(InputStream in, ByteArrayOutputStream bytes) throws IOException {
		int read = 0;
		byte[] buffer = new byte[1024 * 10];
		while ((read = in.read(buffer)) > 0) {
			bytes.write(buffer, 0, read);
		}
		return bytes.toByteArray();
	}

	private static byte[] readResource(File file) {
		InputStream in = null;
		try {
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			in = new FileInputStream(file);
			return readBytes(in, bytes);
		} catch (Throwable e) {
			throw new RuntimeException("Couldn't read resource '" + file.getAbsoluteFile() + "'", e);
		} finally {
			StreamUtils.closeQuietly(in);
		}
	}

	private static String readResourceAsString(String resource, String path) {
		return Try.ofFailable(() -> new String(readResource(resource, path), "UTF-8")).getUnchecked();
	}

	private static String readResourceAsString(File file) {
		return Try.ofFailable(() -> new String(readResource(file), "UTF-8")).getUnchecked();
	}

	private static void writeFile(File outFile, byte[] bytes) {
		OutputStream out = null;
		try {
			out = new BufferedOutputStream(new FileOutputStream(outFile));
			out.write(bytes);
		} catch (IOException e) {
			throw new RuntimeException("Couldn't write file '" + outFile.getAbsolutePath() + "'", e);
		} finally {
			StreamUtils.closeQuietly(out);
		}
	}

	private static void writeFile(File outFile, String text) {
		writeFile(outFile, Try.ofFailable(() -> text.getBytes("UTF-8")).getUnchecked());
	}

	private static void copyFile(ProjectFile file, File out, Map<String, String> values) {
		File outFile = new File(out, file.outputName);
		if (!outFile.getParentFile().exists() && !outFile.getParentFile().mkdirs()) {
			throw new RuntimeException("Couldn't create dir '" + outFile.getAbsolutePath() + "'");
		}

		if (file.isTemplate) {
			String txt;
			if (file instanceof TemporaryProjectFile) {
				txt = readResourceAsString(((TemporaryProjectFile) file).file);
			} else {
				txt = readResourceAsString(file.resourceName, file.resourceLoc);
			}
			txt = replace(txt, values);
			writeFile(outFile, txt);
		} else {
			if (file instanceof TemporaryProjectFile) {
				writeFile(outFile, readResource(((TemporaryProjectFile) file).file));
			} else {
				writeFile(outFile, readResource(file.resourceName, file.resourceLoc));
			}
		}
	}

	private static String replace(String txt, Map<String, String> values) {
		String result = txt;
		for (Entry<String, String> entry : values.entrySet()) {
			result = result.replace(entry.getKey(), entry.getValue());
		}
		return result;
	}

	private static String parseGwtInherits(ProjectBuilder builder) {
		// StringBuilder stringBuilder = new StringBuilder();
		// builder.dependencies.stream().sequential().filter(d -> d.getGwtInherits() != null)
		// .flatMap(d -> Arrays.stream(d.getGwtInherits()))
		// .forEach(i -> stringBuilder.append("\t<inherits name='" + i + "' />\n"));

		String parsed = "";
		for (Dependency dep : builder.dependencies) {
			if (dep.getGwtInherits() != null) {
				for (String inherit : dep.getGwtInherits()) {
					parsed += "\t<inherits name='" + inherit + "' />\n";
				}
			}
		}

		return parsed;
	}

	private static String parseGradleArgs(List<ProjectType> modules, List<String> args) {
		if (args == null) {
			return "";
		}

		StringBuilder builder = new StringBuilder();
		boolean desktop = modules.contains(DESKTOP);
		args.stream().filter(a -> desktop || !a.equals("afterEclipseImport")).forEach(a -> builder.append(" " + a));
		return builder.toString();
	}
}
