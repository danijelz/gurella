package com.gurella.engine.asset;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetLoaderParameters;

public class AssetSelector<T> {
	AssetSelectorPredicate predicate;

	String fileName;
	AssetLoaderParameters<T> parameters;

	AssetSelector() {
	}

	public AssetSelector(AssetSelectorPredicate predicate, String fileName) {
		this.predicate = predicate;
		this.fileName = fileName;
	}

	public static class OsAssetPredicate implements AssetSelectorPredicate {
		public ApplicationType applicationType;

		@Override
		public boolean evaluate() {
			return Gdx.app.getType() == applicationType;
		}
	}
}