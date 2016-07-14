package com.gurella.engine.graphics.render.shader.template;

import com.gurella.engine.graphics.render.shader.generator.ShaderGeneratorContext;

public class InsertPieceNode extends ShaderTemplateNode {
	String pieceName;

	public InsertPieceNode(String pieceName) {
		this.pieceName = pieceName.trim();
	}

	@Override
	protected String toStringValue() {
		return pieceName;
	}

	@Override
	protected void generate(ShaderGeneratorContext context) {
		PieceNode piece = context.getPiece(pieceName);
		if (piece != null) {
			piece.generateChildren(context);
		}
	}
}
