package com.gurella.engine.graphics.render.shader.parser;

import static com.gurella.engine.graphics.render.shader.parser.ShaderParserBlockType.multiLineComment;
import static com.gurella.engine.graphics.render.shader.parser.ShaderParserBlockType.text;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.graphics.render.shader.template.AddNode;
import com.gurella.engine.graphics.render.shader.template.DivNode;
import com.gurella.engine.graphics.render.shader.template.ForNode;
import com.gurella.engine.graphics.render.shader.template.IfdefNode;
import com.gurella.engine.graphics.render.shader.template.IfexpNode;
import com.gurella.engine.graphics.render.shader.template.InsertPieceNode;
import com.gurella.engine.graphics.render.shader.template.MaxNode;
import com.gurella.engine.graphics.render.shader.template.MinNode;
import com.gurella.engine.graphics.render.shader.template.ModNode;
import com.gurella.engine.graphics.render.shader.template.MulNode;
import com.gurella.engine.graphics.render.shader.template.PieceNode;
import com.gurella.engine.graphics.render.shader.template.SetNode;
import com.gurella.engine.graphics.render.shader.template.ShaderTemplate;
import com.gurella.engine.graphics.render.shader.template.ShaderTemplateNode;
import com.gurella.engine.graphics.render.shader.template.SubNode;
import com.gurella.engine.graphics.render.shader.template.ValueNode;

class ShaderParserBlock implements Poolable {
	ShaderParserBlockType type;
	BooleanExpressionParser booleanExpressionParser;

	StringBuffer value = new StringBuffer();
	Array<ShaderParserBlock> children = new Array<ShaderParserBlock>();

	void initTemplate(ShaderTemplateNode node) {
		switch (type) {
		case singleLineComment:
		case multiLineComment:
		case skipLine:
		case root:
			return;
		case include:
			if (node instanceof ShaderTemplate) {
				((ShaderTemplate) node).addDependency(value.toString());
			}
			return;
		case insertPiece:
			node.addChild(new InsertPieceNode(value.toString()));
			return;
		case set:
			node.addChild(new SetNode(value.toString()));
			return;
		case add:
			node.addChild(new AddNode(value.toString()));
			return;
		case sub:
			node.addChild(new SubNode(value.toString()));
			return;
		case mul:
			node.addChild(new MulNode(value.toString()));
			return;
		case div:
			node.addChild(new DivNode(value.toString()));
			return;
		case mod:
			node.addChild(new ModNode(value.toString()));
			return;
		case max:
			node.addChild(new MaxNode(value.toString()));
			return;
		case min:
			node.addChild(new MinNode(value.toString()));
			return;
		case value:
			node.addChild(new ValueNode(value.toString()));
			return;
		case text:
			if (value.length() > 0) {
				node.addText(value.toString());
			}
			return;
		case piece:
			if (node instanceof ShaderTemplate) {
				PieceNode piece = new PieceNode(value.toString());
				((ShaderTemplate) node).addPiece(piece);
				initTemplateChildren(piece);
			}
			return;
		case blockContent:
			initTemplateChildren(node);
			return;
		case ifdef:
			IfdefNode ifdef = new IfdefNode(booleanExpressionParser.parse(value));
			booleanExpressionParser.reset();
			node.addChild(ifdef);
			initTemplateChildren(ifdef);
			return;
		case ifexp:
			IfexpNode ifexp = new IfexpNode(value.toString());
			node.addChild(ifexp);
			initTemplateChildren(ifexp);
			return;
		case foreach:
			ForNode forNode = new ForNode(value.toString());
			node.addChild(forNode);
			initTemplateChildren(forNode);
			return;
		default:
			throw new IllegalArgumentException();
		}
	}

	private void initTemplateChildren(ShaderTemplateNode node) {
		for (int i = 0, n = children.size; i < n; i++) {
			ShaderParserBlock child = children.get(i);
			child.initTemplate(node);
		}
	}

	@Override
	public void reset() {
		type = null;
		booleanExpressionParser = null;
		value.setLength(0);
		children.clear();
	}

	@Override
	public String toString() {
		return toString(0);
	}

	public String toString(int indent) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < indent; i++) {
			builder.append('\t');
		}

		builder.append(type.name());
		builder.append(": {");
		builder.append(toStringValue());
		builder.append(toStringChildren(indent + 1));

		if (children.size > 0) {
			builder.append('\n');
			for (int i = 0; i < indent; i++) {
				builder.append('\t');
			}
		}

		builder.append("}");
		return builder.toString();
	}

	protected String toStringValue() {
		return type == text || type == multiLineComment ? value.toString().replaceAll("(\r\n|\n|\r)", "\\\\n")
				: value.toString();
	}

	private String toStringChildren(int indent) {
		if (children.size == 0) {
			return "";
		}

		StringBuilder builder = new StringBuilder();
		for (ShaderParserBlock child : children) {
			builder.append("\n");
			builder.append(child.toString(indent));
		}
		return builder.toString();
	}
}