package com.gurella.engine.asset2.loader.rendertarget;

import com.badlogic.gdx.files.FileHandle;
import com.gurella.engine.asset2.loader.DependencyProvider;
import com.gurella.engine.asset2.loader.json.SelializedJsonLoader;
import com.gurella.engine.asset2.loader.json.SelializedJsonProperties;
import com.gurella.engine.graphics.render.RenderTarget;

public class RenderTargetLoader extends SelializedJsonLoader<RenderTarget> {
	public RenderTargetLoader() {
		super(RenderTarget.class);
	}

	@Override
	public RenderTarget finish(DependencyProvider provider, FileHandle file, RenderTarget asyncData,
			SelializedJsonProperties properties) {
		RenderTarget renderTarget = super.finish(provider, file, asyncData, properties);
		renderTarget.init();
		return renderTarget;
	}
}
