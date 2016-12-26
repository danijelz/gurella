package com.gurella.studio.wizard.project.setup;

import static com.gurella.studio.wizard.project.setup.ProjectType.DESKTOP;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.gurella.engine.utils.Values;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.utils.Try;
import com.gurella.studio.wizard.project.setup.Executor.LogCallback;

/**
 * Command line tool to generate libgdx projects
 * 
 * @author badlogic
 * @author Tomski
 */
public class Setup {
	private static final String resourceLoc = "setup/";

	private List<ProjectFile> files = new ArrayList<ProjectFile>();
	private Map<String, String> values = new HashMap<String, String>();

	private ScriptBuilder scriptBuilder;
	private String mainClass;
	private String androidAPILevel;
	private String androidBuildToolsVersion;
	private String sdkPath;

	private String packageDir;
	private String assetPath;

	public void build(ScriptBuilder scriptBuilder, String outputDir, String appName, String packageName,
			String mainClass, String sdkLocation, String androidAPILevel, String androidBuildToolsVersion,
			LogCallback callback) {
		this.mainClass = mainClass;
		this.scriptBuilder = scriptBuilder;
		this.androidAPILevel = androidAPILevel;
		this.androidBuildToolsVersion = androidBuildToolsVersion;

		packageDir = packageName.replace('.', '/');
		sdkPath = sdkLocation.replace('\\', '/');
		assetPath = scriptBuilder.projects.contains(ProjectType.ANDROID) ? "android/assets" : "core/assets";

		addRootFiles();
		addCoreFiles();
		addDesktopFiles();
		addAndroidFiles();
		addHtmlFiles();
		addIosRobovmFiles();
		addIosMoeFiles();
		addAssetsFiles();

		values.put("%APP_NAME%", appName);
		values.put("%APP_NAME_ESCAPED%", appName.replace("'", "\\'"));
		values.put("%PACKAGE%", packageName);
		values.put("%PACKAGE_DIR%", packageDir);
		values.put("%MAIN_CLASS%", mainClass);
		values.put("%ASSET_PATH%", assetPath);

		copyAndReplace(outputDir, files, values);
		executeGradle(scriptBuilder, outputDir, callback);
	}

	private void addRootFiles() {
		files.add(new ProjectFile("gitignore", ".gitignore", false));
		files.add(new TemporaryProjectFile(scriptBuilder.settingsFile, "settings.gradle", false));
		files.add(new TemporaryProjectFile(scriptBuilder.buildFile, "build.gradle", true));
		files.add(new ProjectFile("gradlew", false));
		files.add(new ProjectFile("gradlew.bat", false));
		files.add(new ProjectFile("gradle/wrapper/gradle-wrapper.jar", false));
		files.add(new ProjectFile("gradle/wrapper/gradle-wrapper.properties", false));
		files.add(new ProjectFile("gradle.properties"));
	}

	private void addCoreFiles() {
		files.add(new ProjectFile("core/build.gradle"));
		files.add(new ProjectFile("core/src/MainClass", "core/src/" + packageDir + "/" + mainClass + ".java", true));
		if (scriptBuilder.projects.contains(ProjectType.HTML)) {
			files.add(new ProjectFile("core/CoreGdxDefinition", "core/src/" + mainClass + ".gwt.xml", true));
		}
	}

	private void addDesktopFiles() {
		if (!scriptBuilder.projects.contains(ProjectType.DESKTOP)) {
			return;
		}

		files.add(new ProjectFile("desktop/build.gradle"));
		files.add(new ProjectFile("desktop/src/DesktopLauncher",
				"desktop/src/" + packageDir + "/desktop/DesktopLauncher.java", true));
	}

	private void addAndroidFiles() {
		if (!scriptBuilder.projects.contains(ProjectType.ANDROID)) {
			return;
		}

		values.put("%ANDROID_SDK%", sdkPath);
		values.put("%BUILD_TOOLS_VERSION%", androidBuildToolsVersion);
		values.put("%API_LEVEL%", androidAPILevel);

		files.add(new ProjectFile("android/res/values/strings.xml"));
		files.add(new ProjectFile("android/res/values/styles.xml", false));
		files.add(new ProjectFile("android/res/drawable-hdpi/ic_launcher.png", false));
		files.add(new ProjectFile("android/res/drawable-mdpi/ic_launcher.png", false));
		files.add(new ProjectFile("android/res/drawable-xhdpi/ic_launcher.png", false));
		files.add(new ProjectFile("android/res/drawable-xxhdpi/ic_launcher.png", false));
		files.add(new ProjectFile("android/res/drawable-xxxhdpi/ic_launcher.png", false));
		files.add(new ProjectFile("android/src/AndroidLauncher", "android/src/" + packageDir + "/AndroidLauncher.java",
				true));
		files.add(new ProjectFile("android/AndroidManifest.xml"));
		files.add(new ProjectFile("android/build.gradle", true));
		files.add(new ProjectFile("android/ic_launcher-web.png", false));
		files.add(new ProjectFile("android/proguard-project.txt", false));
		files.add(new ProjectFile("android/project.properties", false));
		files.add(new ProjectFile("local.properties", true));
	}

	private void addHtmlFiles() {
		if (!scriptBuilder.projects.contains(ProjectType.HTML)) {
			return;
		}

		values.put("%GWT_VERSION%", SetupConstants.gwtVersion);
		values.put("%GWT_INHERITS%", parseGwtInherits());

		files.add(new ProjectFile("html/build.gradle"));
		files.add(
				new ProjectFile("html/src/HtmlLauncher", "html/src/" + packageDir + "/client/HtmlLauncher.java", true));
		files.add(new ProjectFile("html/GdxDefinition", "html/src/" + packageDir + "/GdxDefinition.gwt.xml", true));
		files.add(new ProjectFile("html/GdxDefinitionSuperdev",
				"html/src/" + packageDir + "/GdxDefinitionSuperdev.gwt.xml", true));
		files.add(new ProjectFile("html/war/index", "html/webapp/index.html", true));
		files.add(new ProjectFile("html/war/styles.css", "html/webapp/styles.css", false));
		files.add(new ProjectFile("html/war/refresh.png", "html/webapp/refresh.png", false));
		files.add(new ProjectFile("html/war/soundmanager2-jsmin.js", "html/webapp/soundmanager2-jsmin.js", false));
		files.add(new ProjectFile("html/war/soundmanager2-setup.js", "html/webapp/soundmanager2-setup.js", false));
		files.add(new ProjectFile("html/war/WEB-INF/web.xml", "html/webapp/WEB-INF/web.xml", true));
	}

	private String parseGwtInherits() {
		return scriptBuilder.dependencies.stream().map(d -> d.getGwtInherits()).filter(d -> Values.isNotEmpty(d))
				.flatMap(d -> Stream.of(d)).filter(d -> Values.isNotBlank(d))
				.map(d -> "\t<inherits name='" + d + "' />\n").collect(Collectors.joining());
	}

	private void addIosRobovmFiles() {
		if (!scriptBuilder.projects.contains(ProjectType.IOS)) {
			return;
		}

		files.add(new ProjectFile("ios/src/IOSLauncher", "ios/src/" + packageDir + "/IOSLauncher.java", true));
		files.add(new ProjectFile("ios/data/Default.png", false));
		files.add(new ProjectFile("ios/data/Default@2x.png", false));
		files.add(new ProjectFile("ios/data/Default@2x~ipad.png", false));
		files.add(new ProjectFile("ios/data/Default-568h@2x.png", false));
		files.add(new ProjectFile("ios/data/Default~ipad.png", false));
		files.add(new ProjectFile("ios/data/Default-375w-667h@2x.png", false));
		files.add(new ProjectFile("ios/data/Default-414w-736h@3x.png", false));
		files.add(new ProjectFile("ios/data/Default-1024w-1366h@2x~ipad.png", false));
		files.add(new ProjectFile("ios/data/Icon.png", false));
		files.add(new ProjectFile("ios/data/Icon@2x.png", false));
		files.add(new ProjectFile("ios/data/Icon-72.png", false));
		files.add(new ProjectFile("ios/data/Icon-72@2x.png", false));
		files.add(new ProjectFile("ios/build.gradle", true));
		files.add(new ProjectFile("ios/Info.plist.xml", false));
		files.add(new ProjectFile("ios/robovm.properties"));
		files.add(new ProjectFile("ios/robovm.xml", true));
	}

	private void addIosMoeFiles() {
		if (!scriptBuilder.projects.contains(ProjectType.IOSMOE)) {
			return;
		}

		files.add(new ProjectFile("ios-moe/resources/Default.png", false));
		files.add(new ProjectFile("ios-moe/resources/Default@2x.png", false));
		files.add(new ProjectFile("ios-moe/resources/Default@2x~ipad.png", false));
		files.add(new ProjectFile("ios-moe/resources/Default-568h@2x.png", false));
		files.add(new ProjectFile("ios-moe/resources/Default~ipad.png", false));
		files.add(new ProjectFile("ios-moe/resources/Default-375w-667h@2x.png", false));
		files.add(new ProjectFile("ios-moe/resources/Default-414w-736h@3x.png", false));
		files.add(new ProjectFile("ios-moe/resources/Default-1024w-1366h@2x~ipad.png", false));
		files.add(new ProjectFile("ios-moe/resources/Icon.png", false));
		files.add(new ProjectFile("ios-moe/resources/Icon@2x.png", false));
		files.add(new ProjectFile("ios-moe/resources/Icon-72.png", false));
		files.add(new ProjectFile("ios-moe/resources/Icon-72@2x.png", false));
		files.add(new ProjectFile("ios-moe/src/IOSMoeLauncher", "ios-moe/src/" + packageDir + "/IOSMoeLauncher.java",
				true));
		files.add(new ProjectFile("ios-moe/xcode/ios-moe/build.xcconfig", false));
		files.add(new ProjectFile("ios-moe/xcode/ios-moe/custom.xcconfig", false));
		files.add(new ProjectFile("ios-moe/xcode/ios-moe-Test/build.xcconfig", false));
		files.add(new ProjectFile("ios-moe/xcode/ios-moe-Test/Info-Test.plist", false));
		files.add(new ProjectFile("ios-moe/xcode/ios-moe/Info.plist", true));
		files.add(new ProjectFile("ios-moe/xcode/ios-moe/main.cpp", false));
		files.add(new ProjectFile("ios-moe/xcode/ios-moe.xcodeproj/project.pbxproj", true));
		files.add(new ProjectFile("ios-moe/build.gradle", true));
	}

	private void addAssetsFiles() {
		files.add(new ProjectFile("android/assets/badlogic.jpg", assetPath + "/badlogic.jpg", false));
	}

	private static void copyAndReplace(String outputDir, List<ProjectFile> files, Map<String, String> values) {
		File out = new File(outputDir);
		Optional.of(out).filter(o -> o.exists() || o.mkdirs()).orElseThrow(
				() -> new RuntimeException("Couldn't create output directory '" + out.getAbsolutePath() + "'"));
		files.forEach(f -> copyFile(f, out, values));
	}

	private static byte[] readResource(String resource, String path) {
		String filePath = path + resource;
		try (ByteArrayOutputStream bytes = new ByteArrayOutputStream();
				InputStream in = GurellaStudioPlugin.getFileInputStream(filePath)) {
			return readBytes(bytes, in);
		} catch (Throwable e) {
			throw new RuntimeException("Couldn't read resource '" + filePath + "'", e);
		}
	}

	private static byte[] readBytes(ByteArrayOutputStream bytes, InputStream in) throws IOException {
		int read = 0;
		byte[] buffer = new byte[1024 * 10];
		while ((read = in.read(buffer)) > 0) {
			bytes.write(buffer, 0, read);
		}
		return bytes.toByteArray();
	}

	private static byte[] readResource(File file) {
		try (ByteArrayOutputStream bytes = new ByteArrayOutputStream(); InputStream in = new FileInputStream(file)) {
			return readBytes(bytes, in);
		} catch (Throwable e) {
			throw new RuntimeException("Couldn't read resource '" + file.getAbsoluteFile() + "'", e);
		}
	}

	private static String readResourceAsString(String resource, String path) {
		return Try.ofFailable(() -> new String(readResource(resource, path), "UTF-8")).getUnchecked();
	}

	private static String readResourceAsString(File file) {
		return Try.ofFailable(() -> new String(readResource(file), "UTF-8")).getUnchecked();
	}

	private static void writeFile(File outFile, byte[] bytes) {
		try (OutputStream out = new BufferedOutputStream(new FileOutputStream(outFile))) {
			out.write(bytes);
		} catch (IOException e) {
			throw new RuntimeException("Couldn't write file '" + outFile.getAbsolutePath() + "'", e);
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
				txt = readResourceAsString(file.resourceName, resourceLoc);
			}
			txt = replace(txt, values);
			writeFile(outFile, txt);
		} else {
			if (file instanceof TemporaryProjectFile) {
				writeFile(outFile, readResource(((TemporaryProjectFile) file).file));
			} else {
				writeFile(outFile, readResource(file.resourceName, resourceLoc));
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

	private static void executeGradle(ScriptBuilder scriptBuilder, String outputDir, LogCallback callback) {
		// HACK executable flag isn't preserved for whatever reason...
		new File(outputDir, "gradlew").setExecutable(true);
		Executor.execute(new File(outputDir), getGradleArgs(scriptBuilder.projects), callback);
	}

	private static List<String> getGradleArgs(List<ProjectType> modules) {
		final List<String> gradleArgs = new ArrayList<String>();

		gradleArgs.add("clean");
		gradleArgs.add("eclipse");
		if (modules.contains(DESKTOP)) {
			gradleArgs.add("afterEclipseImport");
		}

		gradleArgs.add("--daemon");
		gradleArgs.add("--configure-on-demand");
		gradleArgs.add("--parallel");

		return gradleArgs;
	}
}
