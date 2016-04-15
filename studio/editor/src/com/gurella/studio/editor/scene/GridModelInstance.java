package com.gurella.studio.editor.scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;

public class GridModelInstance implements Disposable, RenderableProvider {
	private Model model;
	private ModelInstance instance;

	public GridModelInstance() {
		ModelBuilder builder = new ModelBuilder();
		model = builder.createLineGrid(20, 20, 0.5f, 0.5f, new Material(ColorAttribute.createDiffuse(Color.WHITE)),
				Usage.Position | Usage.ColorUnpacked);
		instance = new ModelInstance(model);
	}

	@Override
	public void dispose() {
		if (model != null) {
			model.dispose();
		}
	}

	@Override
	public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
		instance.getRenderables(renderables, pool);
	}
}
