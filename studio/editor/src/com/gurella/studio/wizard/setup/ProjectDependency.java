package com.gurella.studio.wizard.setup;

import java.util.Collections;
import java.util.List;

/**
 * This enum will hold all dependencies available for libgdx, allowing the setup to pick the ones needed by default, and
 * allow the option to choose extensions as the user wishes.
 * <p/>
 * These depedency strings can be later used in a simple gradle plugin to manipulate the users project either
 * after/before project generation
 *
 * @see Dependency for the object that handles sub-module dependencies. If no dependency is found for a sub-module, ie
 *      FreeTypeFont for gwt, an exception is thrown so the user can be notified of incompatability
 */
public enum ProjectDependency {
	//@formatter:off
	GDX(new String[] { "com.badlogicgames.gdx:gdx:$gdxVersion" },
			new String[] { "com.badlogicgames.gdx:gdx-backend-lwjgl:$gdxVersion",
					"com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop" },
			new String[] { "com.badlogicgames.gdx:gdx-backend-android:$gdxVersion",
					"com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-armeabi",
					"com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-armeabi-v7a",
					"com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-arm64-v8a",
					"com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-x86",
					"com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-x86_64" },
			new String[] { "com.mobidevelop.robovm:robovm-rt:$roboVMVersion",
					"com.mobidevelop.robovm:robovm-cocoatouch:$roboVMVersion",
					"com.badlogicgames.gdx:gdx-backend-robovm:$gdxVersion",
					"com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-ios" },
			new String[] { "com.badlogicgames.gdx:gdx-backend-moe:$gdxVersion",
					"com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-ios" },
			new String[] { "com.badlogicgames.gdx:gdx-backend-gwt:$gdxVersion",
					"com.badlogicgames.gdx:gdx:$gdxVersion:sources",
					"com.badlogicgames.gdx:gdx-backend-gwt:$gdxVersion:sources" },
			new String[] { "com.badlogic.gdx.backends.gdx_backends_gwt" }, 
			
			"Core Library for LibGDX"),

	BULLET(new String[] { "com.badlogicgames.gdx:gdx-bullet:$gdxVersion" },
			new String[] { "com.badlogicgames.gdx:gdx-bullet-platform:$gdxVersion:natives-desktop" },
			new String[] { "com.badlogicgames.gdx:gdx-bullet:$gdxVersion",
					"com.badlogicgames.gdx:gdx-bullet-platform:$gdxVersion:natives-armeabi",
					"com.badlogicgames.gdx:gdx-bullet-platform:$gdxVersion:natives-armeabi-v7a",
					"com.badlogicgames.gdx:gdx-bullet-platform:$gdxVersion:natives-arm64-v8a",
					"com.badlogicgames.gdx:gdx-bullet-platform:$gdxVersion:natives-x86",
					"com.badlogicgames.gdx:gdx-bullet-platform:$gdxVersion:natives-x86_64" },
			new String[] { "com.badlogicgames.gdx:gdx-bullet-platform:$gdxVersion:natives-ios" },
			new String[] { "com.badlogicgames.gdx:gdx-bullet-platform:$gdxVersion:natives-ios" }, 
			null,
			null, 
			
			"3D Collision Detection and Rigid Body Dynamics"),

	FREETYPE(new String[] { "com.badlogicgames.gdx:gdx-freetype:$gdxVersion" },
			new String[] { "com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-desktop" },
			new String[] { "com.badlogicgames.gdx:gdx-freetype:$gdxVersion",
					"com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-armeabi",
					"com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-armeabi-v7a",
					"com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-arm64-v8a",
					"com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-x86",
					"com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-x86_64" },
			new String[] { "com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-ios" },
			new String[] { "com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-ios" }, 
			null,
			null, 
			
			"Generate BitmapFonts from .ttf font files"),

	CONTROLLERS(new String[] { "com.badlogicgames.gdx:gdx-controllers:$gdxVersion" },
			new String[] { "com.badlogicgames.gdx:gdx-controllers-desktop:$gdxVersion",
					"com.badlogicgames.gdx:gdx-controllers-platform:$gdxVersion:natives-desktop" },
			new String[] { "com.badlogicgames.gdx:gdx-controllers:$gdxVersion",
					"com.badlogicgames.gdx:gdx-controllers-android:$gdxVersion" },
			new String[] { }, // works on iOS but never reports any controllers :)
			new String[] { }, // works on iOS but never reports any controllers :)
			new String[] { "com.badlogicgames.gdx:gdx-controllers:$gdxVersion:sources",
					"com.badlogicgames.gdx:gdx-controllers-gwt:$gdxVersion",
					"com.badlogicgames.gdx:gdx-controllers-gwt:$gdxVersion:sources" },
			new String[] { "com.badlogic.gdx.controllers.controllers-gwt" }, 
			
			"Controller/Gamepad API"),

	BOX2D(new String[] { "com.badlogicgames.gdx:gdx-box2d:$gdxVersion" },
			new String[] { "com.badlogicgames.gdx:gdx-box2d-platform:$gdxVersion:natives-desktop" },
			new String[] { "com.badlogicgames.gdx:gdx-box2d:$gdxVersion",
					"com.badlogicgames.gdx:gdx-box2d-platform:$gdxVersion:natives-armeabi",
					"com.badlogicgames.gdx:gdx-box2d-platform:$gdxVersion:natives-armeabi-v7a",
					"com.badlogicgames.gdx:gdx-box2d-platform:$gdxVersion:natives-arm64-v8a",
					"com.badlogicgames.gdx:gdx-box2d-platform:$gdxVersion:natives-x86",
					"com.badlogicgames.gdx:gdx-box2d-platform:$gdxVersion:natives-x86_64" },
			new String[] { "com.badlogicgames.gdx:gdx-box2d-platform:$gdxVersion:natives-ios" },
			new String[] { "com.badlogicgames.gdx:gdx-box2d-platform:$gdxVersion:natives-ios" },
			new String[] { "com.badlogicgames.gdx:gdx-box2d:$gdxVersion:sources",
					"com.badlogicgames.gdx:gdx-box2d-gwt:$gdxVersion:sources" },
			new String[] { "com.badlogic.gdx.physics.box2d.box2d-gwt" }, 
			
			"2D Physics Library"),

	BOX2DLIGHTS(new String[] { "com.badlogicgames.box2dlights:box2dlights:$box2DLightsVersion" }, 
			new String[] { },
			new String[] { "com.badlogicgames.box2dlights:box2dlights:$box2DLightsVersion" }, 
			new String[] { },
			new String[] { }, 
			new String[] { "com.badlogicgames.box2dlights:box2dlights:$box2DLightsVersion:sources" },
			new String[] { "Box2DLights" }, 
			
			"2D Lighting framework that utilises Box2D"),

	AI(new String[] { "com.badlogicgames.gdx:gdx-ai:$aiVersion" }, 
			new String[] { },
			new String[] { "com.badlogicgames.gdx:gdx-ai:$aiVersion" }, 
			new String[] { }, 
			new String[] { },
			new String[] { "com.badlogicgames.gdx:gdx-ai:$aiVersion:sources" }, 
			new String[] { "com.badlogic.gdx.ai" },
			
			"Artificial Intelligence framework"),

	GURELLA(new String[] { "com.gurella:gurella-core:$gurellaVersion" }, 
			new String[] { }, 
			new String[] { }, 
			new String[] { },
			new String[] { }, 
			new String[] { }, 
			new String[] { }, 
			
			"Gurella framework");
	//@formatter:on

	private String[] coreDependencies;
	private String[] desktopDependencies;
	private String[] androidDependencies;
	private String[] iosDependencies;
	private String[] iosMoeDependencies;
	private String[] gwtDependencies;
	private String[] gwtInherits;
	private String description;

	ProjectDependency(String[] coreDeps, String[] desktopDeps, String[] androidDeps, String[] iosDeps,
			String[] iosMoeDeps, String[] gwtDeps, String[] gwtInherts, String description) {
		this.coreDependencies = coreDeps;
		this.desktopDependencies = desktopDeps;
		this.androidDependencies = androidDeps;
		this.iosDependencies = iosDeps;
		this.iosMoeDependencies = iosMoeDeps;
		this.gwtDependencies = gwtDeps;
		this.gwtInherits = gwtInherts;
		this.description = description;
	}

	public String[] getDependencies(ProjectType type) {
		switch (type) {
		case CORE:
			return coreDependencies;
		case DESKTOP:
			return desktopDependencies;
		case ANDROID:
			return androidDependencies;
		case IOS:
			return iosDependencies;
		case IOSMOE:
			return iosMoeDependencies;
		case HTML:
			return gwtDependencies;
		default:
			throw new IllegalArgumentException("Unsupported project type: " + type.name);
		}
	}

	public List<String> getIncompatibilities(ProjectType type) {
		if (getDependencies(type) == null) {
			String typeName = type.getName().toUpperCase();
			return Collections.singletonList("Dependency " + name() + " is not compatible with sub module " + typeName);
		} else {
			return Collections.emptyList();
		}
	}

	public String[] getGwtInherits() {
		return gwtInherits;
	}

	public String getDescription() {
		return description;
	}
}