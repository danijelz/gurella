package com.gurella.engine.graphics.render;

import java.util.Comparator;

import com.badlogic.gdx.utils.Array;
import com.gurella.engine.graphics.render.renderable.Renderable;

public class RenderBin {
	int priority;
	String name;
	Comparator<Renderable> comparator;
	Array<Renderable> renderables = new Array<Renderable>();
}
