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
import static com.gurella.engine.graphics.render.shader.parser.ShaderParserBlockType.none;
import static com.gurella.engine.graphics.render.shader.parser.ShaderParserBlockType.piece;
import static com.gurella.engine.graphics.render.shader.parser.ShaderParserBlockType.set;
import static com.gurella.engine.graphics.render.shader.parser.ShaderParserBlockType.singleLineComment;
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

	private static final char[] endToken = "@end".toCharArray();
	private static final char[] includeToken = "@include".toCharArray();
	private static final char[] pieceToken = "@piece".toCharArray();
	private static final char[] insertpieceToken = "@insertpiece".toCharArray();
	private static final char[] ifdefToken = "@ifdef".toCharArray();
	private static final char[] ifexpToken = "@ifexp".toCharArray();
	private static final char[] forToken = "@for".toCharArray();
	private static final char[] setToken = "@set".toCharArray();
	private static final char[] mulToken = "@mul".toCharArray();
	private static final char[] addToken = "@add".toCharArray();
	private static final char[] subToken = "@sub".toCharArray();
	private static final char[] divToken = "@div".toCharArray();
	private static final char[] modToken = "@mod".toCharArray();
	private static final char[] minToken = "@min".toCharArray();
	private static final char[] maxToken = "@max".toCharArray();
	private static final char[] valueToken = "@value".toCharArray();
	private static final char[] multiLineCommentStartToken = "/*".toCharArray();
	private static final char[] singleLineCommentStartToken = "//".toCharArray();

	private static final int minTokenLength = endToken.length;
	private static final int maxTokenLength = insertpieceToken.length;

	private final char[] data = new char[dataSize];

	private BooleanExpressionParser booleanExpressionParser = new BooleanExpressionParser();

	private Array<ShaderParserBlock> blockStack = new Array<ShaderParserBlock>();
	private Array<ShaderParserBlock> rootBlocks = new Array<ShaderParserBlock>();
	private Array<ShaderParserBlock> allBlocks = new Array<ShaderParserBlock>();

	private StringBuffer currentText = new StringBuffer();

	private int potencialBlockStart = -1;
	private boolean parenthesisOpened;
	private int numIfdefExpressionParenthesis;

	ShaderParserBlockType type = none;

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
					currentText.setLength(currentText.length() - 1);
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
					currentText.setLength(currentText.length() - 1);
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
					currentText.setLength(currentText.length() - 1);
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
					checkBlockStart(c);
				}
				break;
			case none:
			case text:
				checkBlockStart(c);
				break;
			default:
				break;
			}
		}
	}

	private boolean isBlockClosed(char c) {
		return potencialBlockStart > -1 && 'd' == c && testToken(endToken);
	}

	private void checkBlockStart(char c) {
		switch (c) {
		case '@':
			potencialBlockStart = currentText.length() - 1;
			return;
		case '/':
			if (testToken(singleLineCommentStartToken)) {
				potencialBlockStart = -1;
				startBlock(singleLineCommentStartToken, singleLineComment);
			}
			return;
		case '*':
			if (testToken(multiLineCommentStartToken)) {
				potencialBlockStart = -1;
				startBlock(multiLineCommentStartToken, multiLineComment);
			}
			return;
		default:
			int length = currentText.length();
			if (length < minTokenLength) {
				return;
			} else if (length - potencialBlockStart > maxTokenLength) {
				potencialBlockStart = -1;
				return;
			}

			if (blockStack.size == 0 && testToken(includeToken)) {
				startBlock(includeToken, include);
			} else if (blockStack.size == 0 && testToken(pieceToken)) {
				startBlock(pieceToken, piece);
			} else if (testToken(ifexpToken)) {
				startBlock(ifexpToken, ifexp);
			} else if (testToken(insertpieceToken)) {
				startBlock(insertpieceToken, insertPiece);
			} else if (testToken(ifdefToken)) {
				numIfdefExpressionParenthesis = 1;
				startBlock(ifdefToken, ifdef);
			} else if (testToken(forToken)) {
				startBlock(forToken, foreach);
			} else if (testToken(setToken)) {
				startBlock(setToken, set);
			} else if (testToken(mulToken)) {
				startBlock(mulToken, mul);
			} else if (testToken(addToken)) {
				startBlock(addToken, add);
			} else if (testToken(subToken)) {
				startBlock(subToken, sub);
			} else if (testToken(divToken)) {
				startBlock(divToken, div);
			} else if (testToken(modToken)) {
				startBlock(modToken, mod);
			} else if (testToken(minToken)) {
				startBlock(minToken, min);
			} else if (testToken(maxToken)) {
				startBlock(maxToken, max);
			} else if (testToken(valueToken)) {
				startBlock(valueToken, value);
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

		if (currLen == testLen) {
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
		type = current == null ? none : current.type;
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

	private boolean testToken(char[] token) {
		int currLen = currentText.length();
		int testLen = token.length;
		if (currLen < testLen) {
			return false;
		}

		for (int i = 0, n = currLen - testLen; i < testLen; i++) {
			if (token[i] != currentText.charAt(n + i)) {
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
