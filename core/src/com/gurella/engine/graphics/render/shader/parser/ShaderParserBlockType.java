package com.gurella.engine.graphics.render.shader.parser;

enum ShaderParserBlockType {
	singleLineComment,
	multiLineComment,
	skipLine,
	include,
	piece,
	blockContent,
	insertPiece,
	text,
	ifdef,
	ifexp,
	foreach,
	set,
	mul,
	add,
	sub,
	div,
	mod,
	min,
	max,
	value,
	none;
}