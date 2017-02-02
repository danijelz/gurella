package com.gurella.engine.asset2.resolver;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.gurella.engine.asset2.AssetId;

public interface FileHandleResolverPredicate {
	boolean evaluate(AssetId assetId);

	public static class AppTypePredicate implements FileHandleResolverPredicate {
		public ApplicationType applicationType;

		@Override
		public boolean evaluate(AssetId assetId) {
			return Gdx.app.getType() == applicationType;
		}
	}

	public static class AssetTypePredicate implements FileHandleResolverPredicate {
		public Class<?> assetType;
		public boolean extensible;

		@Override
		public boolean evaluate(AssetId assetId) {
			Class<?> otherAssetType = assetId.getAssetType();
			return assetType == otherAssetType
					|| (extensible && ClassReflection.isAssignableFrom(assetType, otherAssetType));
		}
	}
}
