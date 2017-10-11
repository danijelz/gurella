package com.gurella.engine.graphics.render.shader.parser;

enum ShaderParserBlockType {
	root(true),

	singleLineComment,
	multiLineComment,
	skipLine,

	include,
	piece(true),
	blockContent(true),
	insertPiece,
	text,

	ifdef(true),
	ifexp(true),
	foreach(true),

	set,
	mul,
	add,
	sub,
	div,
	mod,
	min,
	max,
	define,
	undefine,

	pset,
	pmul,
	padd,
	psub,
	pdiv,
	pmod,
	pmin,
	pmax,
	pdefine,
	pundefine,

	value;
	
	//TODO toInt

	public final boolean composite;

	private ShaderParserBlockType() {
		this.composite = false;
	}

	private ShaderParserBlockType(boolean composite) {
		this.composite = composite;
	}
}