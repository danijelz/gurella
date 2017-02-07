package com.gurella.engine.asset.loader.model;

import java.util.Iterator;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.model.data.ModelData;
import com.badlogic.gdx.graphics.g3d.model.data.ModelMaterial;
import com.badlogic.gdx.graphics.g3d.model.data.ModelTexture;
import com.badlogic.gdx.graphics.g3d.utils.TextureProvider;
import com.badlogic.gdx.utils.Disposable;
import com.gurella.engine.asset.loader.AssetLoader;
import com.gurella.engine.asset.loader.DependencyCollector;
import com.gurella.engine.asset.loader.DependencySupplier;

public abstract class ModelLoader<PROPS extends ModelProperties> implements AssetLoader<ModelData, Model, PROPS> {
	protected abstract ModelData loadModelData(final FileHandle fileHandle);

	@Override
	public ModelData init(DependencyCollector collector, FileHandle assetFile) {
		ModelData data = loadModelData(assetFile);

		FileType fileType = assetFile.type();
		for (final ModelMaterial modelMaterial : data.materials) {
			if (modelMaterial.textures != null) {
				for (final ModelTexture modelTexture : modelMaterial.textures) {
					collector.addDependency(modelTexture.fileName, fileType, Texture.class);
				}
			}
		}

		return data;
	}

	@Override
	public ModelData processAsync(DependencySupplier provider, FileHandle file, ModelData asyncData,
			ModelProperties properties) {
		return asyncData;
	}

	@Override
	public Model finish(DependencySupplier provider, FileHandle file, ModelData asyncData, ModelProperties properties) {
		final Model result = new Model(asyncData, new DependencyTextureProvider(provider, file.type()));
		// remove the textures from the managed disposables fo ref counting to work!
		Iterator<Disposable> disposables = result.getManagedDisposables().iterator();
		while (disposables.hasNext()) {
			Disposable disposable = disposables.next();
			if (disposable instanceof Texture) {
				disposables.remove();
			}
		}

		return result;
	}

	private static class DependencyTextureProvider implements TextureProvider {
		private final DependencySupplier provider;
		private final FileType fileType;

		public DependencyTextureProvider(DependencySupplier provider, FileType fileType) {
			this.provider = provider;
			this.fileType = fileType;
		}

		@Override
		public Texture load(String fileName) {
			return provider.getDependency(fileName, fileType, Texture.class);
		}
	}
}