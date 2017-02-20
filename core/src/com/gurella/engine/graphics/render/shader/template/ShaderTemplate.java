package com.gurella.engine.graphics.render.shader.template;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.gurella.engine.graphics.render.shader.generator.ShaderGeneratorContext;
import com.gurella.engine.utils.ImmutableArray;

public class ShaderTemplate extends ShaderTemplateNode {
	private final Array<String> _dependencies = new Array<String>();
	public final ImmutableArray<String> dependencies = new ImmutableArray<String>(_dependencies);
	private final ObjectMap<String, PieceNode> piecesByName = new ObjectMap<String, PieceNode>();

	public void addDependency(String dependencyPath) {
		_dependencies.add(dependencyPath);
	}

	public void addPieces(ShaderTemplate template) {
		for (Entry<String, PieceNode> entry : template.piecesByName) {
			if (!piecesByName.containsKey(entry.key)) {
				piecesByName.put(entry.key, entry.value);
			}
		}
	}

	public void addPiece(PieceNode piece) {
		piecesByName.put(piece.name, piece);
	}

	@Override
	public void generate(ShaderGeneratorContext context) {
		preprocessChildren(context);
		generateChildren(context);
	}

	public PieceNode getPiece(String pieceName) {
		return piecesByName.get(pieceName);
	}

	@Override
	protected String toStringValue() {
		return "";
	}
}
