package com.gurella.engine.graphics.render.shader.parser;

import static com.gurella.engine.graphics.render.shader.parser.ShaderParserBlockType.add;
import static com.gurella.engine.graphics.render.shader.parser.ShaderParserBlockType.blockContent;
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
import static com.gurella.engine.graphics.render.shader.parser.ShaderParserBlockType.multiLineComment;
import static com.gurella.engine.graphics.render.shader.parser.ShaderParserBlockType.piece;
import static com.gurella.engine.graphics.render.shader.parser.ShaderParserBlockType.root;
import static com.gurella.engine.graphics.render.shader.parser.ShaderParserBlockType.set;
import static com.gurella.engine.graphics.render.shader.parser.ShaderParserBlockType.singleLineComment;
import static com.gurella.engine.graphics.render.shader.parser.ShaderParserBlockType.skipLine;
import static com.gurella.engine.graphics.render.shader.parser.ShaderParserBlockType.sub;
import static com.gurella.engine.graphics.render.shader.parser.ShaderParserBlockType.text;
import static com.gurella.engine.graphics.render.shader.parser.ShaderParserBlockType.value;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.SerializationException;
import com.badlogic.gdx.utils.StreamUtils;
import com.gurella.engine.graphics.render.shader.generator.ShaderGeneratorContext;
import com.gurella.engine.graphics.render.shader.template.ShaderTemplate;
import com.gurella.engine.pool.PoolService;

public class ShaderTemplateParser implements Poolable {
	private static final int dataSize = 10;

	private static final char[] endKeyword = "@end".toCharArray();
	private static final char[] includeKeyword = "@include".toCharArray();
	private static final char[] pieceKeyword = "@piece".toCharArray();
	private static final char[] insertpieceKeyword = "@insertpiece".toCharArray();
	private static final char[] ifdefKeyword = "@ifdef".toCharArray();
	private static final char[] ifexpKeyword = "@ifexp".toCharArray();
	private static final char[] forKeyword = "@for".toCharArray();
	private static final char[] setKeyword = "@set".toCharArray();
	private static final char[] mulKeyword = "@mul".toCharArray();
	private static final char[] addKeyword = "@add".toCharArray();
	private static final char[] subKeyword = "@sub".toCharArray();
	private static final char[] divKeyword = "@div".toCharArray();
	private static final char[] modKeyword = "@mod".toCharArray();
	private static final char[] minKeyword = "@min".toCharArray();
	private static final char[] maxKeyword = "@max".toCharArray();
	private static final char[] valueKeyword = "@value".toCharArray();
	private static final char[] skipLineKeyword = "@skip".toCharArray();
	private static final char[] multiLineCommentStartKeyword = "/*".toCharArray();
	private static final char[] singleLineCommentStartKeyword = "//".toCharArray();

	private static final int minKeywordLength = endKeyword.length;
	private static final int maxKeywordLength = insertpieceKeyword.length;

	private final char[] data = new char[dataSize];

	private BooleanExpressionParser booleanExpressionParser = new BooleanExpressionParser();

	private Array<ShaderParserBlock> blockStack = new Array<ShaderParserBlock>();
	private Array<ShaderParserBlock> rootBlocks = new Array<ShaderParserBlock>();
	private Array<ShaderParserBlock> allBlocks = new Array<ShaderParserBlock>();

	private StringBuffer currentText = new StringBuffer();

	private int potencialBlockStart = -1;
	private boolean parenthesisOpened;
	private int numIfdefExpressionParenthesis;
	private boolean skipLineEnded;

	ShaderParserBlockType type = root;

	public ShaderTemplate parse(Reader reader) {
		try {
			return parseSafely(reader);
		} catch (IOException ex) {
			throw new SerializationException(ex);
		} finally {
			StreamUtils.closeQuietly(reader);
		}
	}

	protected ShaderTemplate parseSafely(Reader reader) throws IOException {
		while (true) {
			int length = reader.read(data);
			if (length < 1) {
				finish();
				return extractShaderTemplate();
			} else {
				parse(data, length);
			}
		}
	}

	private ShaderTemplate extractShaderTemplate() {
		ShaderTemplate shaderTemplate = new ShaderTemplate();
		for (int i = 0, n = rootBlocks.size; i < n; i++) {
			rootBlocks.get(i).initTemplate(shaderTemplate);
		}
		return shaderTemplate;
	}

	protected void finish() {
		if (!areCurrentValuesEmpty(0) && blockStack.size == 0) {
			ShaderParserBlock textSourceBlock = obtainShaderParserBlock(text);
			textSourceBlock.value.append(currentText);
			rootBlocks.add(textSourceBlock);
		}
	}

	private ShaderParserBlock obtainShaderParserBlock(ShaderParserBlockType type) {
		ShaderParserBlock textSourceBlock = PoolService.obtain(ShaderParserBlock.class);
		allBlocks.add(textSourceBlock);
		textSourceBlock.type = type;
		textSourceBlock.booleanExpressionParser = booleanExpressionParser;
		return textSourceBlock;
	}

	private boolean areCurrentValuesEmpty(int end) {
		return currentText.length() - end < 1;
	}

	public void parse(char[] data, int length) {
		for (int i = 0; i < length; i++) {
			char c = data[i];
			currentText.append(c);

			switch (type) {
			case singleLineComment:
				if ('\n' == c || '\r' == c) {
					pop(0);
					i--;
				}
				break;
			case multiLineComment:
				if ('/' == c && currentText.charAt(currentText.length() - 2) == '*') {
					pop(2);
				}
				break;
			case skipLine:
				boolean linebreak = '\n' == c || '\r' == c;
				if (linebreak && !skipLineEnded) {
					skipLineEnded = true;
				} else if (!linebreak && skipLineEnded) {
					pop(0);
					i--;
				}
				break;
			case include:
			case insertPiece:
			case set:
			case mul:
			case add:
			case sub:
			case div:
			case mod:
			case min:
			case max:
			case value:
				if (parenthesisOpened) {
					if (')' == c) {
						pop(1);
					}
				} else {
					currentText.setLength(0);
					if ('(' == c) {
						parenthesisOpened = true;
					}
				}
				break;
			case ifdef:
				if (parenthesisOpened) {
					if ('(' == c) {
						numIfdefExpressionParenthesis++;
					} else if (')' == c) {
						numIfdefExpressionParenthesis--;
						if (numIfdefExpressionParenthesis == 0) {
							push(1, blockContent);
						}
					}
				} else {
					currentText.setLength(0);
					if ('(' == c) {
						parenthesisOpened = true;
					}
				}
				break;
			case ifexp:
			case foreach:
			case piece:
				if (parenthesisOpened) {
					if (')' == c) {
						push(1, blockContent);
					}
				} else {
					currentText.setLength(0);
					if ('(' == c) {
						parenthesisOpened = true;
					}
				}
				break;
			case blockContent:
				if (isBlockClosed(c)) {
					pop(4);
					pop(0);
				} else {
					tryStartBlock(c);
				}
				break;
			case root:
			case text:
				tryStartBlock(c);
				break;
			default:
				break;
			}
		}
	}

	private boolean isBlockClosed(char c) {
		return potencialBlockStart > -1 && 'd' == c && testKeyword(endKeyword);
	}

	private void tryStartBlock(char c) {
		switch (c) {
		case '@':
			potencialBlockStart = currentText.length() - 1;
			return;
		case '/':
			if (testKeyword(singleLineCommentStartKeyword)) {
				potencialBlockStart = -1;
				startBlock(singleLineCommentStartKeyword, singleLineComment);
			}
			return;
		case '*':
			if (testKeyword(multiLineCommentStartKeyword)) {
				potencialBlockStart = -1;
				startBlock(multiLineCommentStartKeyword, multiLineComment);
			}
			return;
		default:
			int length = currentText.length();
			if (length < minKeywordLength) {
				return;
			} else if (length - potencialBlockStart > maxKeywordLength) {
				potencialBlockStart = -1;
				return;
			}

			if (blockStack.size == 0 && testKeyword(includeKeyword)) {
				startBlock(includeKeyword, include);
			} else if (blockStack.size == 0 && testKeyword(pieceKeyword)) {
				startBlock(pieceKeyword, piece);
			} else if (testKeyword(ifexpKeyword)) {
				startBlock(ifexpKeyword, ifexp);
			} else if (testKeyword(insertpieceKeyword)) {
				startBlock(insertpieceKeyword, insertPiece);
			} else if (testKeyword(ifdefKeyword)) {
				numIfdefExpressionParenthesis = 1;
				startBlock(ifdefKeyword, ifdef);
			} else if (testKeyword(forKeyword)) {
				startBlock(forKeyword, foreach);
			} else if (testKeyword(setKeyword)) {
				startBlock(setKeyword, set);
			} else if (testKeyword(mulKeyword)) {
				startBlock(mulKeyword, mul);
			} else if (testKeyword(addKeyword)) {
				startBlock(addKeyword, add);
			} else if (testKeyword(subKeyword)) {
				startBlock(subKeyword, sub);
			} else if (testKeyword(divKeyword)) {
				startBlock(divKeyword, div);
			} else if (testKeyword(modKeyword)) {
				startBlock(modKeyword, mod);
			} else if (testKeyword(minKeyword)) {
				startBlock(minKeyword, min);
			} else if (testKeyword(maxKeyword)) {
				startBlock(maxKeyword, max);
			} else if (testKeyword(valueKeyword)) {
				startBlock(valueKeyword, value);
			} else if (testKeyword(skipLineKeyword)) {
				skipLineEnded = false;
				startBlock(skipLineKeyword, skipLine);
			}

			return;
		}
	}

	private void startBlock(char[] startedType, ShaderParserBlockType blockType) {
		parenthesisOpened = false;
		ShaderParserBlock newBlock = obtainShaderParserBlock(blockType);
		int currLen = currentText.length();
		int testLen = startedType.length;
		ShaderParserBlock current = getCurrentBlock();

		if (!type.composite) {
			if (current != null) {
				pop(0);
				current = getCurrentBlock();
			}
		} else if (!areCurrentValuesEmpty(testLen)) {
			ShaderParserBlock textSourceBlock = obtainShaderParserBlock(text);
			textSourceBlock.value.append(currentText, 0, currLen - testLen);
			if (current == null) {
				rootBlocks.add(textSourceBlock);
			} else {
				current.children.add(textSourceBlock);
			}
		}

		currentText.setLength(0);
		blockStack.add(newBlock);
		if (current == null) {
			rootBlocks.add(newBlock);
		} else {
			current.children.add(newBlock);
		}

		type = newBlock.type;
	}

	private ShaderParserBlock getCurrentBlock() {
		return blockStack.size == 0 ? null : blockStack.peek();
	}

	private void pop(int valuesSub) {
		ShaderParserBlock current = blockStack.peek();

		if (type == blockContent) {
			ShaderParserBlock textSourceBlock = obtainShaderParserBlock(text);
			textSourceBlock.value.append(currentText, 0, currentText.length() - valuesSub);
			current.children.add(textSourceBlock);
		} else {
			current.value.append(currentText, 0, currentText.length() - valuesSub);
		}

		currentText.setLength(0);
		blockStack.pop();
		potencialBlockStart = -1;

		current = getCurrentBlock();
		type = current == null ? root : current.type;
	}

	private void push(int valuesSub, ShaderParserBlockType blockType) {
		ShaderParserBlock newBlock = obtainShaderParserBlock(blockType);
		ShaderParserBlock current = blockStack.peek();
		current.value.append(currentText, 0, currentText.length() - valuesSub);
		currentText.setLength(0);
		blockStack.add(newBlock);
		current.children.add(newBlock);
		potencialBlockStart = -1;
		type = newBlock.type;
	}

	private boolean testKeyword(char[] keyword) {
		int currLen = currentText.length();
		int testLen = keyword.length;
		if (currLen < testLen) {
			return false;
		}

		for (int i = 0, n = currLen - testLen; i < testLen; i++) {
			if (keyword[i] != currentText.charAt(n + i)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public void reset() {
		blockStack.clear();
		rootBlocks.clear();
		PoolService.freeAll(allBlocks);
		allBlocks.clear();

		currentText.setLength(0);

		potencialBlockStart = -1;
		parenthesisOpened = false;
	}

	public static void main(String[] args) {
		String name = "com/gurella/engine/graphics/render/shader/parser/TestParser.glsl";
		InputStream input = ShaderTemplateParser.class.getClassLoader().getResourceAsStream(name);
		ShaderTemplateParser parser = new ShaderTemplateParser();

		try {
			parser.parse(new InputStreamReader(input, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		parser.printBlocks();
		System.out.print("\n\n\n\n\n");
		ShaderTemplate template = parser.extractShaderTemplate();
		System.out.print(template.toString());
		System.out.print("\n\n\n\n\n");
		ShaderGeneratorContext context = new ShaderGeneratorContext();
		context.init(template);
		context.define("abc");
		context.define("bbca");
		context.setValue("dddVar", 2);
		template.generate(context);
		System.out.println("----------------------");
		System.out.print(context.getShaderSource(true));
		parser.reset();
	}

	private void printBlocks() {
		for (ShaderParserBlock shaderParserBlock : rootBlocks) {
			System.out.print(shaderParserBlock.toString());
			System.out.print("\n");
		}
	}
}
