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

	private final SetupInfo setupInfo;
	private final LogCallback callback;

	private final List<ProjectFile> files = new ArrayList<ProjectFile>();
	private final Map<String, String> replacements = new HashMap<String, String>();

	private String appName;
	private String mainClass;
	private String initialScene;
	private String packageName;
	private String androidApiLevel;
	private String androidBuildToolsVersion;

	private String outputDir;
	private String packageDir;
	private String assetPath;

	public Setup(SetupInfo setupInfo, LogCallback callback) {
		this.setupInfo = setupInfo;
		this.callback = callback;

		mainClass = setupInfo.mainClass;
		initialScene = "initial.gscn";
		androidApiLevel = setupInfo.androidApiLevel;
		androidBuildToolsVersion = setupInfo.androidBuildToolsVersion;
		appName = setupInfo.appName;
		packageName = setupInfo.packageName;
		outputDir = setupInfo.location;
		packageDir = setupInfo.packageName.replace('.', '/');
		assetPath = setupInfo.projects.contains(ProjectType.ANDROID) ? "android/assets" : "core/assets";

		addDefaultReplacements();
	}

	private void addDefaultReplacements() {
		replacements.put("%APP_NAME%", appName);
		replacements.put("%APP_NAME_ESCAPED%", appName.replace("'", "\\'"));
		replacements.put("%PACKAGE%", packageName);
		replacements.put("%PACKAGE_DIR%", packageDir);
		replacements.put("%MAIN_CLASS%", mainClass);
		replacements.put("%ASSET_PATH%", assetPath);
		replacements.put("%INITIAL_SCENE%", initialScene);
	}

	public void build() {
		addRootFiles();
		addCoreFiles();
		addDesktopFiles();
		addAndroidFiles();
		addHtmlFiles();
		addIosRobovmFiles();
		addIosMoeFiles();
		addAssetsFiles();

		copyAndReplace();
		executeGradle();
	}

	private void addRootFiles() {
		GradleScriptBuilder gradleScriptBuilder = new GradleScriptBuilder(setupInfo);
		files.add(new ProjectFile("gitignore", ".gitignore", false));
		files.add(new TemporaryProjectFile(gradleScriptBuilder.createSettingsScript(), "settings.gradle", false));
		files.add(new TemporaryProjectFile(gradleScriptBuilder.createBuildScript(), "build.gradle", false));
		files.add(new ProjectFile("gradlew", false));
		files.add(new ProjectFile("gradlew.bat", false));
		files.add(new ProjectFile("gradle/wrapper/gradle-wrapper.jar", false));
		files.add(new ProjectFile("gradle/wrapper/gradle-wrapper.properties", false));
		files.add(new ProjectFile("gradle.properties"));
	}

	private void addCoreFiles() {
		files.add(new ProjectFile("core/build.gradle"));
		files.add(new ProjectFile("core/src/MainClass", "core/src/" + packageDir + "/" + mainClass + ".java", true));
		if (setupInfo.projects.contains(ProjectType.HTML)) {
			files.add(new ProjectFile("core/CoreGdxDefinition", "core/src/" + mainClass + ".gwt.xml", true));
		}
	}

	private void addDesktopFiles() {
		if (!setupInfo.projects.contains(ProjectType.DESKTOP)) {
			return;
		}

		files.add(new ProjectFile("desktop/build.gradle"));
		files.add(new ProjectFile("desktop/src/DesktopLauncher",
				"desktop/src/" + packageDir + "/desktop/DesktopLauncher.java", true));
	}

	private void addAndroidFiles() {
		if (!setupInfo.projects.contains(ProjectType.ANDROID)) {
			return;
		}

		String sdkPath = setupInfo.androidSdkLocation.replace('\\', '/');
		replacements.put("%ANDROID_SDK%", sdkPath);
		replacements.put("%BUILD_TOOLS_VERSION%", androidBuildToolsVersion);
		replacements.put("%API_LEVEL%", androidApiLevel);

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
		if (!setupInfo.projects.contains(ProjectType.HTML)) {
			return;
		}

		replacements.put("%GWT_VERSION%", SetupConstants.gwtVersion);
		replacements.put("%GWT_INHERITS%", parseGwtInherits());

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
		return setupInfo.dependencies.stream().map(d -> d.getGwtInherits()).filter(d -> Values.isNotEmpty(d))
				.flatMap(d -> Stream.of(d)).filter(d -> Values.isNotBlank(d))
				.map(d -> "\t<inherits name='" + d + "' />\n").collect(Collectors.joining());
	}

	private void addIosRobovmFiles() {
		if (!setupInfo.projects.contains(ProjectType.IOS)) {
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
		if (!setupInfo.projects.contains(ProjectType.IOSMOE)) {
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
		files.add(new ProjectFile("android/assets/application.gcfg", assetPath + "/application.gcfg", true));
		files.add(new ProjectFile("android/assets/initial.gscn", assetPath + "/" + initialScene, false));
		files.add(new ProjectFile("android/assets/cloudySea.jpg", assetPath + "/sky/cloudySea.jpg", false));
	}

	private void copyAndReplace() {
		File out = new File(outputDir);
		Optional.of(out).filter(o -> o.exists() || o.mkdirs()).orElseThrow(
				() -> new RuntimeException("Couldn't create output directory '" + out.getAbsolutePath() + "'"));
		files.forEach(f -> copyFile(out, f));
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

	private void copyFile(File parent, ProjectFile file) {
		File outFile = new File(parent, file.outputName);
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
			txt = replace(txt);
			writeFile(outFile, txt);
		} else {
			if (file instanceof TemporaryProjectFile) {
				writeFile(outFile, readResource(((TemporaryProjectFile) file).file));
			} else {
				writeFile(outFile, readResource(file.resourceName, resourceLoc));
			}
		}
	}

	private String replace(String txt) {
		String result = txt;
		for (Entry<String, String> entry : replacements.entrySet()) {
			result = result.replace(entry.getKey(), entry.getValue());
		}
		return result;
	}

	private void executeGradle() {
		// HACK executable flag isn't preserved for whatever reason...
		new File(outputDir, "gradlew").setExecutable(true);
		Executor.execute(new File(outputDir), getGradleArgs(), callback);
	}

	private List<String> getGradleArgs() {
		final List<String> gradleArgs = new ArrayList<String>();

		gradleArgs.add("clean");
		gradleArgs.add("eclipse");
		if (setupInfo.projects.contains(DESKTOP)) {
			gradleArgs.add("afterEclipseImport");
		}

		gradleArgs.add("--daemon");
		gradleArgs.add("--configure-on-demand");
		gradleArgs.add("--parallel");

		return gradleArgs;
	}
}
