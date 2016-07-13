package com.gurella.engine.graphics.render.shader.parser;

import static com.gurella.engine.graphics.render.shader.parser.ShaderParserBlockType.add;
import static com.gurella.engine.graphics.render.shader.parser.ShaderParserBlockType.div;
import static com.gurella.engine.graphics.render.shader.parser.ShaderParserBlockType.foreach;
import static com.gurella.engine.graphics.render.shader.parser.ShaderParserBlockType.ifdef;
import static com.gurella.engine.graphics.render.shader.parser.ShaderParserBlockType.ifexp;
import static com.gurella.engine.graphics.render.shader.parser.ShaderParserBlockType.include;
import static com.gurella.engine.graphics.render.shader.parser.ShaderParserBlockType.insertPiece;
import static com.gurella.engine.graphics.render.shader.parser.ShaderParserBlockType.max;
import static com.gurella.engine.graphics.render.shader.parser.ShaderParserBlockType.min;
import static com.gurella.engine.graphics.render.shader.parser.ShaderParserBlockType.mod;
import static com.gurella.engine.graphics.render.shader.parser.ShaderParserBlockType.mul;
import static com.gurella.engine.graphics.render.shader.parser.ShaderParserBlockType.piece;
import static com.gurella.engine.graphics.render.shader.parser.ShaderParserBlockType.set;
import static com.gurella.engine.graphics.render.shader.parser.ShaderParserBlockType.sub;
import static com.gurella.engine.graphics.render.shader.parser.ShaderParserBlockType.value;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntMap;

class ShaderTemplateTokens {
	static final char[] endToken = "@end".toCharArray();
	static final char[] includeToken = "@include".toCharArray();
	static final char[] pieceToken = "@piece".toCharArray();
	static final char[] insertpieceToken = "@insertpiece".toCharArray();
	static final char[] ifdefToken = "@ifdef".toCharArray();
	static final char[] ifexpToken = "@ifexp".toCharArray();
	static final char[] forToken = "@for".toCharArray();
	static final char[] setToken = "@set".toCharArray();
	static final char[] mulToken = "@mul".toCharArray();
	static final char[] addToken = "@add".toCharArray();
	static final char[] subToken = "@sub".toCharArray();
	static final char[] divToken = "@div".toCharArray();
	static final char[] modToken = "@mod".toCharArray();
	static final char[] minToken = "@min".toCharArray();
	static final char[] maxToken = "@max".toCharArray();
	static final char[] valueToken = "@value".toCharArray();
	static final char[] multiLineCommentToken = "/*".toCharArray();
	static final char[] singleLineCommentToken = "//".toCharArray();
	static final char[] skipLineCommentToken = "@@".toCharArray();

	static final int minTokenLength = endToken.length;
	static final int maxTokenLength = insertpieceToken.length;

	static final IntMap<ContentTokenInfo> contenetTokens = new IntMap<ContentTokenInfo>();
	static {
		putToken(includeToken, include);
		putToken(pieceToken, piece);
		putToken(insertpieceToken, insertPiece);
		putToken(ifdefToken, ifdef);
		putToken(ifexpToken, ifexp);
		putToken(forToken, foreach);
		putToken(setToken, set);
		putToken(mulToken, mul);
		putToken(addToken, add);
		putToken(subToken, sub);
		putToken(divToken, div);
		putToken(modToken, mod);
		putToken(minToken, min);
		putToken(maxToken, max);
		putToken(valueToken, value);
	}

	private static void putToken(char[] token, ShaderParserBlockType blockType) {
		if (contenetTokens.put(tokenHashCode(token), new ContentTokenInfo(token, blockType)) != null) {
			throw new GdxRuntimeException("Hash collision!");
		}
	}

	private static int tokenHashCode(char[] token) {
		int result = 1;
		for (int i = 0, n = token.length; i < n; i++) {
			result = 31 * result + token[i];
		}
		return result;
	}

	static class ContentTokenInfo {
		char[] token;
		ShaderParserBlockType blockType;

		public ContentTokenInfo(char[] token, ShaderParserBlockType blockType) {
			this.token = token;
			this.blockType = blockType;
		}
	}
}
