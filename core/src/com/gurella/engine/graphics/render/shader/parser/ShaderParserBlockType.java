package com.gurella.engine.graphics.render.shader.parser;

enum ShaderParserBlockType {
	singleLineComment,
	multiLineComment,
	include,
	piece,
	blockContent,
	insertPiece,
	text,
	ifdef,
	fordef,
	set,
	mul,
	add,
	sub,
	div,
	mod,
	value,
	none;
}