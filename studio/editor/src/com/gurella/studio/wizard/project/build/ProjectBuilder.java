package com.gurella.studio.wizard.project.build;

import static com.gurella.studio.wizard.project.ProjectType.ANDROID;
import static com.gurella.studio.wizard.project.ProjectType.DESKTOP;
import static com.gurella.studio.wizard.project.ProjectType.HTML;
import static com.gurella.studio.wizard.project.ProjectType.IOS;
import static com.gurella.studio.wizard.project.ProjectType.IOSMOE;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.runtime.Platform;

import com.gurella.engine.utils.Values;
import com.gurella.studio.wizard.project.ProjectSetup;
import com.gurella.studio.wizard.project.build.Executor.LogCallback;

/**
 * @author badlogic
 * @author Tomski
 */
public class ProjectBuilder {
	private static final String appNameReplacement = "%APP_NAME%";
	private static final String appNameEscapedReplacement = "%APP_NAME_ESCAPED%";
	private static final String packageReplacement = "%PACKAGE%";
	private static final String packageDirReplacement = "%PACKAGE_DIR%";
	private static final String initialSceneReplacement = "%INITIAL_SCENE%";
	private static final String buildNaturesPropertyReplacement = "%BUILD_NATURES%";
	private static final String gradleBuildNatureReplacement = "%GRADLE_BUILD_NATURE%";
	private static final String buildNatureSeparatorReplacement = "%BUILD_NATURE_SEPARATOR%";
	private static final String gradleBuildPropertyPrefixReplacement = "%GRADLE_BUILD_PROPERTY_PREFIX%";
	private static final String gradleBuildCommandReplacement = "%GRADLE_BUILD_COMMAND%";

	private final ProjectSetup projectSetup;
	private final LogCallback callback;

	private final List<ProjectFile> files = new ArrayList<ProjectFile>();
	private final Map<String, String> replacements = new HashMap<String, String>();

	private String appName;
	private String initialScene;
	private String packageName;
	private String androidApiLevel;
	private String androidBuildToolsVersion;

	private String outputDir;
	private String packageDir;

	private boolean gradleNature;

	public static void build(ProjectSetup projectSetup, LogCallback callback) {
		new ProjectBuilder(projectSetup, callback)._build();
	}

	private ProjectBuilder(ProjectSetup projectSetup, LogCallback callback) {
		this.projectSetup = projectSetup;
		this.callback = callback;

		appName = projectSetup.appName;
		initialScene = projectSetup.initialScene;
		androidApiLevel = projectSetup.androidApiLevel;
		androidBuildToolsVersion = projectSetup.androidBuildToolsVersion;
		packageName = projectSetup.packageName;
		outputDir = projectSetup.location;
		packageDir = projectSetup.packageName.replace('.', '/');

		gradleNature = Platform.getBundle(SetupConstants.eclipseGradleCorePlugin) != null;
	}

	private void _build() {
		addDefaultReplacements();

		addRootFiles();
		addCoreFiles();
		addDesktopFiles();
		addAndroidFiles();
		addGwtHtmlFiles();
		addIosRobovmFiles();
		addIosMoeFiles();

		copyFiles();
		executeGradle();
	}

	private void addDefaultReplacements() {
		replacements.put(appNameReplacement, appName);
		replacements.put(appNameEscapedReplacement, appName.replace("'", "\\'"));
		replacements.put(packageReplacement, packageName);
		replacements.put(packageDirReplacement, packageDir);
		replacements.put(initialSceneReplacement, initialScene);

		if (gradleNature) {
			replacements.put(buildNaturesPropertyReplacement, "natures ");
			replacements.put(gradleBuildNatureReplacement, "'org.eclipse.buildship.core.gradleprojectnature'");
			replacements.put(buildNatureSeparatorReplacement, ", ");
			replacements.put(gradleBuildPropertyPrefixReplacement, "\n        ");
			replacements.put(gradleBuildCommandReplacement,
					"buildCommand \"org.eclipse.buildship.core.gradleprojectbuilder\"");
		} else {
			replacements.put(buildNaturesPropertyReplacement, "");
			replacements.put(gradleBuildNatureReplacement, "");
			replacements.put(buildNatureSeparatorReplacement, "");
			replacements.put(gradleBuildPropertyPrefixReplacement, "");
			replacements.put(gradleBuildCommandReplacement, "");
		}
	}

	private void addRootFiles() {
		newResourceFile("gitignore", ".gitignore");
		addProjectFile(new GradleSettingsFile(projectSetup.projects));
		addProjectFile(new GradleBuildScriptFile(projectSetup));
		newResourceFile("gradlew");
		newResourceFile("gradlew.bat");
		newResourceFile("gradle/wrapper/gradle-wrapper.jar");
		newResourceFile("gradle/wrapper/gradle-wrapper.properties");
		newResourceFile("gradle.properties");

		if (gradleNature) {
			newResourceFile(".settings/org.eclipse.buildship.core.prefs");
		}
	}

	private void addCoreFiles() {
		newTemplateFile("core/build.gradle");
		newTemplateFile("core/src/SampleComponent", "core/src/" + packageDir + "/SampleComponent.java");

		addProjectFile(new ApplicationConfigFile(initialScene));
		addProjectFile(new IntroSceneFile(initialScene));
		addProjectFile(new SampleMaterialFile());
		newResourceFile("core/assets/cloudySea.jpg", "core/assets/sky/cloudySea.jpg");

		if (projectSetup.isSelected(HTML)) {
			String gwtAppName = Character.toUpperCase(appName.charAt(0)) + appName.substring(1);
			newTemplateFile("core/CoreGdxDefinition", "core/src/" + gwtAppName + ".gwt.xml");
		}

		if (gradleNature) {
			newResourceFile("core/.settings/org.eclipse.buildship.core.prefs");
		}
	}

	private void addDesktopFiles() {
		if (!projectSetup.isSelected(DESKTOP)) {
			return;
		}

		newTemplateFile("desktop/build.gradle");
		String launcherPath = "desktop/src/" + packageDir + "/desktop/DesktopLauncher.java";
		newTemplateFile("desktop/src/DesktopLauncher", launcherPath);

		if (gradleNature) {
			newResourceFile("desktop/.settings/org.eclipse.buildship.core.prefs");
		}
	}

	private void addAndroidFiles() {
		if (!projectSetup.isSelected(ANDROID)) {
			return;
		}

		String sdkPath = projectSetup.androidSdkLocation.replace('\\', '/');
		replacements.put("%ANDROID_SDK%", sdkPath);
		replacements.put("%BUILD_TOOLS_VERSION%", androidBuildToolsVersion);
		replacements.put("%API_LEVEL%", androidApiLevel);

		newTemplateFile("android/res/values/strings.xml");
		newResourceFile("android/res/values/styles.xml");
		newResourceFile("android/res/drawable-hdpi/ic_launcher.png");
		newResourceFile("android/res/drawable-mdpi/ic_launcher.png");
		newResourceFile("android/res/drawable-xhdpi/ic_launcher.png");
		newResourceFile("android/res/drawable-xxhdpi/ic_launcher.png");
		newResourceFile("android/res/drawable-xxxhdpi/ic_launcher.png");
		newTemplateFile("android/src/AndroidLauncher", "android/src/" + packageDir + "/AndroidLauncher.java");
		newTemplateFile("android/AndroidManifest.xml");
		newTemplateFile("android/build.gradle");
		newResourceFile("android/ic_launcher-web.png");
		newResourceFile("android/proguard-project.txt");
		newResourceFile("android/project.properties");
		newTemplateFile("local.properties");

		if (gradleNature) {
			newResourceFile("android/.settings/org.eclipse.buildship.core.prefs");
		}
	}

	private void addGwtHtmlFiles() {
		if (!projectSetup.isSelected(HTML)) {
			return;
		}

		replacements.put("%GWT_VERSION%", SetupConstants.gwtVersion);
		replacements.put("%GWT_INHERITS%", parseGwtInherits());

		newTemplateFile("html/build.gradle");
		newTemplateFile("html/src/HtmlLauncher", "html/src/" + packageDir + "/client/HtmlLauncher.java");
		newTemplateFile("html/GdxDefinition", "html/src/" + packageDir + "/GdxDefinition.gwt.xml");
		newTemplateFile("html/GdxDefinitionSuperdev", "html/src/" + packageDir + "/GdxDefinitionSuperdev.gwt.xml");
		newTemplateFile("html/war/index", "html/webapp/index.html");
		newResourceFile("html/war/styles.css", "html/webapp/styles.css");
		newResourceFile("html/war/refresh.png", "html/webapp/refresh.png");
		newResourceFile("html/war/soundmanager2-jsmin.js", "html/webapp/soundmanager2-jsmin.js");
		newResourceFile("html/war/soundmanager2-setup.js", "html/webapp/soundmanager2-setup.js");
		newTemplateFile("html/war/WEB-INF/web.xml", "html/webapp/WEB-INF/web.xml");
	}

	private String parseGwtInherits() {
		return projectSetup.dependencies.stream().map(d -> d.getGwtInherits()).filter(Values::isNotEmpty)
				.flatMap(d -> Stream.<String> of(d)).filter(Values::isNotBlank)
				.map(d -> "\t<inherits name='" + d + "' />\n").collect(Collectors.joining());
	}

	private void addIosRobovmFiles() {
		if (!projectSetup.isSelected(IOS)) {
			return;
		}

		newTemplateFile("ios/src/IOSLauncher", "ios/src/" + packageDir + "/IOSLauncher.java");
		newResourceFile("ios/data/Default.png");
		newResourceFile("ios/data/Default@2x.png");
		newResourceFile("ios/data/Default@2x~ipad.png");
		newResourceFile("ios/data/Default-568h@2x.png");
		newResourceFile("ios/data/Default~ipad.png");
		newResourceFile("ios/data/Default-375w-667h@2x.png");
		newResourceFile("ios/data/Default-414w-736h@3x.png");
		newResourceFile("ios/data/Default-1024w-1366h@2x~ipad.png");
		newResourceFile("ios/data/Icon.png");
		newResourceFile("ios/data/Icon@2x.png");
		newResourceFile("ios/data/Icon-72.png");
		newResourceFile("ios/data/Icon-72@2x.png");
		newTemplateFile("ios/build.gradle");
		newResourceFile("ios/Info.plist.xml");
		newResourceFile("ios/robovm.properties");
		newTemplateFile("ios/robovm.xml");
	}

	private void addIosMoeFiles() {
		if (!projectSetup.isSelected(IOSMOE)) {
			return;
		}

		newResourceFile("ios-moe/resources/Default.png");
		newResourceFile("ios-moe/resources/Default@2x.png");
		newResourceFile("ios-moe/resources/Default@2x~ipad.png");
		newResourceFile("ios-moe/resources/Default-568h@2x.png");
		newResourceFile("ios-moe/resources/Default~ipad.png");
		newResourceFile("ios-moe/resources/Default-375w-667h@2x.png");
		newResourceFile("ios-moe/resources/Default-414w-736h@3x.png");
		newResourceFile("ios-moe/resources/Default-1024w-1366h@2x~ipad.png");
		newResourceFile("ios-moe/resources/Icon.png");
		newResourceFile("ios-moe/resources/Icon@2x.png");
		newResourceFile("ios-moe/resources/Icon-72.png");
		newResourceFile("ios-moe/resources/Icon-72@2x.png");
		newTemplateFile("ios-moe/src/IOSMoeLauncher", "ios-moe/src/" + packageDir + "/IOSMoeLauncher.java");
		newResourceFile("ios-moe/xcode/ios-moe/build.xcconfig");
		newResourceFile("ios-moe/xcode/ios-moe/custom.xcconfig");
		newResourceFile("ios-moe/xcode/ios-moe-Test/build.xcconfig");
		newResourceFile("ios-moe/xcode/ios-moe-Test/Info-Test.plist");
		newTemplateFile("ios-moe/xcode/ios-moe/Info.plist");
		newResourceFile("ios-moe/xcode/ios-moe/main.cpp");
		newTemplateFile("ios-moe/xcode/ios-moe.xcodeproj/project.pbxproj");
		newTemplateFile("ios-moe/build.gradle");

		if (gradleNature) {
			newResourceFile("ios-moe/.settings/org.eclipse.buildship.core.prefs");
		}
	}

	private void newResourceFile(String resourceName) {
		files.add(new ResourceFile(resourceName, resourceName));
	}

	private void newResourceFile(String resourceName, String outputName) {
		files.add(new ResourceFile(resourceName, outputName));
	}

	private void newTemplateFile(String resourceName) {
		files.add(new TemplateFile(resourceName, resourceName, replacements));
	}

	private void newTemplateFile(String resourceName, String outputName) {
		files.add(new TemplateFile(resourceName, outputName, replacements));
	}

	private void addProjectFile(ProjectFile projectFile) {
		files.add(projectFile);
	}

	private void copyFiles() {
		File out = new File(outputDir);
		validateOutputFile(out, false);
		files.forEach(f -> copyFile(out, f));
	}

	private static void copyFile(File parent, ProjectFile file) {
		File outFile = new File(parent, file.outputName);
		validateOutputFile(outFile, true);

		byte[] bytes = file.getContent();
		try (OutputStream out = new BufferedOutputStream(new FileOutputStream(outFile))) {
			out.write(bytes);
		} catch (IOException e) {
			throw new RuntimeException("Couldn't write file '" + outFile.getAbsolutePath() + "'", e);
		}
	}

	private static void validateOutputFile(File out, boolean validateParent) {
		File testFile = validateParent ? out.getParentFile() : out;
		if (!testFile.exists() && !testFile.mkdirs()) {
			throw new RuntimeException("Couldn't create dir '" + out.getAbsolutePath() + "'");
		}
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
		if (projectSetup.isSelected(DESKTOP)) {
			gradleArgs.add("afterEclipseImport");
		}

		gradleArgs.add("--daemon");
		gradleArgs.add("--configure-on-demand");
		gradleArgs.add("--parallel");

		return gradleArgs;
	}
}
