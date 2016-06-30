package com.gurella.engine.graphics.render.shader.generator;

import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.gurella.engine.graphics.render.material.Material;
import com.gurella.engine.graphics.render.shader.template.PieceNode;
import com.gurella.engine.graphics.render.shader.template.ShaderTemplate;

public class ShaderGeneratorContext {
	private final ObjectSet<String> defines = new ObjectSet<String>();
	private final ObjectIntMap<String> values = new ObjectIntMap<String>();
	
	private ShaderTemplate template; 
	private Material material;
	
	public boolean isDefined(String property) {
		return defines.contains(property);
	}
	
	public PieceNode getPiece(String pieceName) {
		return template.getPiece(pieceName);
	}
}
