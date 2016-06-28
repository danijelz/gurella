package com.gurella.engine.graphics.render.shader.template;

import com.badlogic.gdx.utils.Array;

public abstract class ShaderTemplateNode {
	Array<ShaderTemplateNode> children = new Array<ShaderTemplateNode>();

	public void addChild(ShaderTemplateNode child) {
		children.add(child);
	}
}
