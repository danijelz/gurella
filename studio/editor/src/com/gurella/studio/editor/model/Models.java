package com.gurella.studio.editor.model;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.ModelFactory;

public class Models {
	private static final ObjectMap<Class<?>, Model<?>> resolvedModels = new ObjectMap<Class<?>, Model<?>>();
	private static final Array<ModelFactory> modelFactories = new Array<ModelFactory>();
}
