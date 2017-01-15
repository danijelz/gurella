package com.gurella.studio.wizard.project.setup;

import static com.gurella.studio.wizard.project.ProjectType.ANDROID;
import static com.gurella.studio.wizard.project.ProjectType.DESKTOP;
import static com.gurella.studio.wizard.project.ProjectType.HTML;
import static com.gurella.studio.wizard.project.ProjectType.IOS;
import static com.gurella.studio.wizard.project.ProjectType.IOSMOE;

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
import com.gurella.studio.wizard.project.ProjectType;
import com.gurella.studio.wizard.project.setup.Executor.LogCallback;

/**
 * Command line tool to generate libgdx projects
 * 
 * @author badlogic
 * @author Tomski
 */
public class ProjectSetup {
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

	public ProjectSetup(SetupInfo setupInfo, LogCallback callback) {
		this.setupInfo = setupInfo;
		this.callback = callback;

		mainClass = setupInfo.mainClass;
		initialScene = setupInfo.initialScene;
		androidApiLevel = setupInfo.androidApiLevel;
		androidBuildToolsVersion = setupInfo.androidBuildToolsVersion;
		appName = setupInfo.appName;
		packageName = setupInfo.packageName;
		outputDir = setupInfo.location;
		packageDir = setupInfo.packageName.replace('.', '/');
	}

	private boolean containsProject(ProjectType projectType) {
		return setupInfo.projects.contains(projectType);
	}

	public void build() {
		addDefaultReplacements();

		addRootFiles();
		addCoreFiles();
		addDesktopFiles();
		addAndroidFiles();
		addHtmlFiles();
		addIosRobovmFiles();
		addIosMoeFiles();

		copyFiles();
		executeGradle();
	}

	private void addDefaultReplacements() {
		replacements.put("%APP_NAME%", appName);
		replacements.put("%APP_NAME_ESCAPED%", appName.replace("'", "\\'"));
		replacements.put("%PACKAGE%", packageName);
		replacements.put("%PACKAGE_DIR%", packageDir);
		replacements.put("%MAIN_CLASS%", mainClass);
		replacements.put("%INITIAL_SCENE%", initialScene);
	}

	private void addRootFiles() {
		GradleScriptBuilder scriptBuilder = new GradleScriptBuilder(setupInfo);
		newProjectFile("gitignore", ".gitignore", false);
		newTemporaryProjectFile(scriptBuilder.createSettingsScript(), "settings.gradle", false);
		newTemporaryProjectFile(scriptBuilder.createBuildScript(), "build.gradle", false);
		newProjectFile("gradlew", false);
		newProjectFile("gradlew.bat", false);
		newProjectFile("gradle/wrapper/gradle-wrapper.jar", false);
		newProjectFile("gradle/wrapper/gradle-wrapper.properties", false);
		newProjectFile("gradle.properties");
	}

	private void addCoreFiles() {
		newProjectFile("core/build.gradle");
		newProjectFile("core/src/MainClass", "core/src/" + packageDir + "/" + mainClass + ".java", true);
		
		newProjectFile("core/assets/application.gcfg", "core/assets/application.gcfg", true);
		newProjectFile("core/assets/initialScene.gscn", "core/assets/scenes/" + initialScene, false);
		newProjectFile("core/assets/cloudySea.jpg", "core/assets/sky/cloudySea.jpg", false);
		
		if (setupInfo.projects.contains(HTML)) {
			newProjectFile("core/CoreGdxDefinition", "core/src/" + mainClass + ".gwt.xml", true);
		}
	}

	private void addDesktopFiles() {
		if (!containsProject(DESKTOP)) {
			return;
		}

		newProjectFile("desktop/build.gradle");
		newProjectFile("desktop/src/DesktopLauncher", "desktop/src/" + packageDir + "/desktop/DesktopLauncher.java",
				true);
	}

	private void addAndroidFiles() {
		if (!containsProject(ANDROID)) {
			return;
		}

		String sdkPath = setupInfo.androidSdkLocation.replace('\\', '/');
		replacements.put("%ANDROID_SDK%", sdkPath);
		replacements.put("%BUILD_TOOLS_VERSION%", androidBuildToolsVersion);
		replacements.put("%API_LEVEL%", androidApiLevel);

		newProjectFile("android/res/values/strings.xml");
		newProjectFile("android/res/values/styles.xml", false);
		newProjectFile("android/res/drawable-hdpi/ic_launcher.png", false);
		newProjectFile("android/res/drawable-mdpi/ic_launcher.png", false);
		newProjectFile("android/res/drawable-xhdpi/ic_launcher.png", false);
		newProjectFile("android/res/drawable-xxhdpi/ic_launcher.png", false);
		newProjectFile("android/res/drawable-xxxhdpi/ic_launcher.png", false);
		newProjectFile("android/src/AndroidLauncher", "android/src/" + packageDir + "/AndroidLauncher.java", true);
		newProjectFile("android/AndroidManifest.xml");
		newProjectFile("android/build.gradle", true);
		newProjectFile("android/ic_launcher-web.png", false);
		newProjectFile("android/proguard-project.txt", false);
		newProjectFile("android/project.properties", false);
		newProjectFile("local.properties", true);
	}

	private void addHtmlFiles() {
		if (!containsProject(HTML)) {
			return;
		}

		replacements.put("%GWT_VERSION%", SetupConstants.gwtVersion);
		replacements.put("%GWT_INHERITS%", parseGwtInherits());

		newProjectFile("html/build.gradle");
		newProjectFile("html/src/HtmlLauncher", "html/src/" + packageDir + "/client/HtmlLauncher.java", true);
		newProjectFile("html/GdxDefinition", "html/src/" + packageDir + "/GdxDefinition.gwt.xml", true);
		newProjectFile("html/GdxDefinitionSuperdev", "html/src/" + packageDir + "/GdxDefinitionSuperdev.gwt.xml", true);
		newProjectFile("html/war/index", "html/webapp/index.html", true);
		newProjectFile("html/war/styles.css", "html/webapp/styles.css", false);
		newProjectFile("html/war/refresh.png", "html/webapp/refresh.png", false);
		newProjectFile("html/war/soundmanager2-jsmin.js", "html/webapp/soundmanager2-jsmin.js", false);
		newProjectFile("html/war/soundmanager2-setup.js", "html/webapp/soundmanager2-setup.js", false);
		newProjectFile("html/war/WEB-INF/web.xml", "html/webapp/WEB-INF/web.xml", true);
	}

	private String parseGwtInherits() {
		return setupInfo.dependencies.stream().map(d -> d.getGwtInherits()).filter(Values::isNotEmpty)
				.flatMap(d -> Stream.<String> of(d)).filter(Values::isNotBlank)
				.map(d -> "\t<inherits name='" + d + "' />\n").collect(Collectors.joining());
	}

	private void addIosRobovmFiles() {
		if (!containsProject(IOS)) {
			return;
		}

		newProjectFile("ios/src/IOSLauncher", "ios/src/" + packageDir + "/IOSLauncher.java", true);
		newProjectFile("ios/data/Default.png", false);
		newProjectFile("ios/data/Default@2x.png", false);
		newProjectFile("ios/data/Default@2x~ipad.png", false);
		newProjectFile("ios/data/Default-568h@2x.png", false);
		newProjectFile("ios/data/Default~ipad.png", false);
		newProjectFile("ios/data/Default-375w-667h@2x.png", false);
		newProjectFile("ios/data/Default-414w-736h@3x.png", false);
		newProjectFile("ios/data/Default-1024w-1366h@2x~ipad.png", false);
		newProjectFile("ios/data/Icon.png", false);
		newProjectFile("ios/data/Icon@2x.png", false);
		newProjectFile("ios/data/Icon-72.png", false);
		newProjectFile("ios/data/Icon-72@2x.png", false);
		newProjectFile("ios/build.gradle", true);
		newProjectFile("ios/Info.plist.xml", false);
		newProjectFile("ios/robovm.properties");
		newProjectFile("ios/robovm.xml", true);
	}

	private void addIosMoeFiles() {
		if (!containsProject(IOSMOE)) {
			return;
		}

		newProjectFile("ios-moe/resources/Default.png", false);
		newProjectFile("ios-moe/resources/Default@2x.png", false);
		newProjectFile("ios-moe/resources/Default@2x~ipad.png", false);
		newProjectFile("ios-moe/resources/Default-568h@2x.png", false);
		newProjectFile("ios-moe/resources/Default~ipad.png", false);
		newProjectFile("ios-moe/resources/Default-375w-667h@2x.png", false);
		newProjectFile("ios-moe/resources/Default-414w-736h@3x.png", false);
		newProjectFile("ios-moe/resources/Default-1024w-1366h@2x~ipad.png", false);
		newProjectFile("ios-moe/resources/Icon.png", false);
		newProjectFile("ios-moe/resources/Icon@2x.png", false);
		newProjectFile("ios-moe/resources/Icon-72.png", false);
		newProjectFile("ios-moe/resources/Icon-72@2x.png", false);
		newProjectFile("ios-moe/src/IOSMoeLauncher", "ios-moe/src/" + packageDir + "/IOSMoeLauncher.java", true);
		newProjectFile("ios-moe/xcode/ios-moe/build.xcconfig", false);
		newProjectFile("ios-moe/xcode/ios-moe/custom.xcconfig", false);
		newProjectFile("ios-moe/xcode/ios-moe-Test/build.xcconfig", false);
		newProjectFile("ios-moe/xcode/ios-moe-Test/Info-Test.plist", false);
		newProjectFile("ios-moe/xcode/ios-moe/Info.plist", true);
		newProjectFile("ios-moe/xcode/ios-moe/main.cpp", false);
		newProjectFile("ios-moe/xcode/ios-moe.xcodeproj/project.pbxproj", true);
		newProjectFile("ios-moe/build.gradle", true);
	}

	private void newProjectFile(String resourceName) {
		files.add(new ProjectFile(resourceName));
	}

	private void newProjectFile(String resourceName, boolean isTemplate) {
		files.add(new ProjectFile(resourceName, isTemplate));
	}

	private void newProjectFile(String resourceName, String outputName, boolean isTemplate) {
		files.add(new ProjectFile(resourceName, outputName, isTemplate));
	}

	private void newTemporaryProjectFile(File file, String outputString, boolean isTemplate) {
		files.add(new TemporaryProjectFile(file, outputString, isTemplate));
	}

	private void copyFiles() {
		File out = new File(outputDir);
		Optional.of(out).filter(o -> o.exists() || o.mkdirs()).orElseThrow(
				() -> new RuntimeException("Couldn't create output directory '" + out.getAbsolutePath() + "'"));
		files.forEach(f -> copyFile(out, f));
	}

	private static byte[] readResource(String path, String resource) {
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

	private static String readResourceAsString(String path, String resource) {
		return Try.ofFailable(() -> new String(readResource(path, resource), "UTF-8")).getUnchecked();
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
				txt = readResourceAsString(resourceLoc, file.resourceName);
			}
			txt = replace(txt);
			writeFile(outFile, txt);
		} else {
			if (file instanceof TemporaryProjectFile) {
				writeFile(outFile, readResource(((TemporaryProjectFile) file).file));
			} else {
				writeFile(outFile, readResource(resourceLoc, file.resourceName));
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
		if (containsProject(DESKTOP)) {
			gradleArgs.add("afterEclipseImport");
		}

		gradleArgs.add("--daemon");
		gradleArgs.add("--configure-on-demand");
		gradleArgs.add("--parallel");

		return gradleArgs;
	}
}
