package com.gurella.engine.application;

import com.badlogic.gdx.utils.Array;
import com.gurella.engine.asset.AssetService;
import com.gurella.engine.asset.resolver.FileHandleResolver;

public class ApplicationConfig {
	public String initialScenePath;
	public final Array<InitScript> initScripts = new Array<InitScript>();
	public final Array<FileHandleResolver> resolvers = new Array<FileHandleResolver>();

	public ApplicationConfig() {
	}

	public ApplicationConfig(String initialScenePath) {
		this.initialScenePath = initialScenePath;
	}

	void init() {
		for (int i = 0, n = initScripts.size; i < n; i++) {
			initScripts.get(i).init();
		}

		for (int i = 0, n = resolvers.size; i < n; i++) {
			AssetService.registerResolver(resolvers.get(i));
		}
	}
}
