package com.gurella.engine.graphics.render.shader.template;

public class InsertPieceNode extends ShaderTemplateNode {
	String pieceName;

	public InsertPieceNode(String pieceName) {
		this.pieceName = pieceName;
	}

	@Override
	protected String toStringValue() {
		return pieceName;
	}

	@Override
	protected void generate(ShaderTemplate template, StringBuilder builder) {
		PieceNode piece = template.getPiece(pieceName);
		if (piece != null) {
			piece.generate(template, builder);
		}
	}
}
