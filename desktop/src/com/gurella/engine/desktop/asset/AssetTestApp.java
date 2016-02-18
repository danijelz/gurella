package com.gurella.engine.desktop.asset;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.gurella.engine.asset.manager.AssetDatabaseTest;

public class AssetTestApp {
	public static void main(String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		@SuppressWarnings("unused")
		LwjglApplication application = new LwjglApplication(new AssetDatabaseTest(), config);
	}
}
