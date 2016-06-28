package com.gurella.engine.graphics.render.shader.parser;

enum ShaderParserBlockType {
	singleLineComment,
	multiLineComment,
	include,
	piece,
	insertPiece,
	text,
	ifdef,
	ifdefContent,
	none;
}