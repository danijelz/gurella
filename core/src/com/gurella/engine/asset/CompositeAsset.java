package com.gurella.engine.asset;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Pool.Poolable;

//TODO unused
public interface CompositeAsset {
	Array<AssetComposition> getCompositions();
	
	public class AssetComposition implements Poolable {
		Object asset;
		final ObjectSet<String> dependencies = new ObjectSet<String>(4);

		@Override
		public void reset() {
			asset = null;
			dependencies.clear();
		}
	}
}
