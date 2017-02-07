package com.gurella.engine.asset.loader.rendertarget;

import com.badlogic.gdx.files.FileHandle;
import com.gurella.engine.asset.loader.DependencySupplier;
import com.gurella.engine.asset.loader.json.SelializedJsonLoader;
import com.gurella.engine.asset.loader.json.SelializedJsonProperties;
import com.gurella.engine.graphics.render.RenderTarget;

public class RenderTargetLoader extends SelializedJsonLoader<RenderTarget> {
	public RenderTargetLoader() {
		super(RenderTarget.class);
	}

	@Override
	public RenderTarget finish(DependencySupplier provider, FileHandle file, RenderTarget asyncData,
			SelializedJsonProperties properties) {
		RenderTarget renderTarget = super.finish(provider, file, asyncData, properties);
		renderTarget.init();
		return renderTarget;
	}
}
