package com.gurella.engine.application;

import com.badlogic.gdx.utils.Array;

public class ApplicationConfig {
	public String initialScenePath;
	public final Array<InitScript> initScripts = new Array<InitScript>();

	public ApplicationConfig() {
	}

	public ApplicationConfig(String initialScenePath) {
		this.initialScenePath = initialScenePath;
	}

	void init() {
		for (int i = 0, n = initScripts.size; i < n; i++) {
			initScripts.get(i).init();
		}
	}
}
