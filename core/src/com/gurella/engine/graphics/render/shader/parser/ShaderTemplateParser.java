package com.gurella.engine.graphics.render.shader.parser;

import static com.gurella.engine.graphics.render.shader.parser.ShaderParserBlockType.blockContent;
import static com.gurella.engine.graphics.render.shader.parser.ShaderParserBlockType.ifdef;
import static com.gurella.engine.graphics.render.shader.parser.ShaderParserBlockType.include;
import static com.gurella.engine.graphics.render.shader.parser.ShaderParserBlockType.multiLineComment;
import static com.gurella.engine.graphics.render.shader.parser.ShaderParserBlockType.piece;
import static com.gurella.engine.graphics.render.shader.parser.ShaderParserBlockType.root;
import static com.gurella.engine.graphics.render.shader.parser.ShaderParserBlockType.singleLineComment;
import static com.gurella.engine.graphics.render.shader.parser.ShaderParserBlockType.skipLine;
import static com.gurella.engine.graphics.render.shader.parser.ShaderParserBlockType.text;
import static com.gurella.engine.graphics.render.shader.parser.ShaderTemplateTokens.contenetTokens;
import static com.gurella.engine.graphics.render.shader.parser.ShaderTemplateTokens.endToken;
import static com.gurella.engine.graphics.render.shader.parser.ShaderTemplateTokens.maxTokenLength;
import static com.gurella.engine.graphics.render.shader.parser.ShaderTemplateTokens.minTokenLength;
import static com.gurella.engine.graphics.render.shader.parser.ShaderTemplateTokens.multiLineCommentToken;
import static com.gurella.engine.graphics.render.shader.parser.ShaderTemplateTokens.singleLineCommentToken;
import static com.gurella.engine.graphics.render.shader.parser.ShaderTemplateTokens.skipLineCommentToken;

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
import com.gurella.engine.graphics.render.shader.parser.ShaderTemplateTokens.ContentTokenInfo;
import com.gurella.engine.graphics.render.shader.template.ShaderTemplate;
import com.gurella.engine.pool.PoolService;

public class ShaderTemplateParser implements Poolable {
	private static final int dataSize = 1024;
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
		if (mustAttachTextNode(0)) {
			ShaderParserBlock textSourceBlock = obtainBlock(text);
			textSourceBlock.value.append(currentText);
			rootBlocks.add(textSourceBlock);
		}
	}

	private ShaderParserBlock obtainBlock(ShaderParserBlockType type) {
		ShaderParserBlock textSourceBlock = PoolService.obtain(ShaderParserBlock.class);
		allBlocks.add(textSourceBlock);
		textSourceBlock.type = type;
		textSourceBlock.booleanExpressionParser = booleanExpressionParser;
		return textSourceBlock;
	}

	private boolean mustAttachTextNode(int end) {
		return type.composite && currentText.length() - end > 0;
	}

	public void parse(char[] data, int length) {
		for (int i = 0; i < length; i++) {
			char c = data[i];
			currentText.append(c);

			switch (type) {
			case singleLineComment:
				if ('\n' == c || '\r' == c) {
					pop(1);
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
				if (skipLineEnded) {
					int currentLength = currentText.length();
					if (linebreak && currentText.charAt(currentLength - 1) != currentText.charAt(currentLength - 2)) {
						pop(0);
					} else {
						pop(1);
						i--;
					}
				} else if (linebreak) {
					skipLineEnded = true;
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
			case define:
			case undefine:
			case pset:
			case pmul:
			case padd:
			case psub:
			case pdiv:
			case pmod:
			case pmin:
			case pmax:
			case pdefine:
			case pundefine:
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
					checkBlockStart(c);
				}
				break;
			case root:
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
			int currentLength = currentText.length();
			if (testToken(skipLineCommentToken)) {
				potencialBlockStart = -1;
				skipLineEnded = false;
				startBlock(skipLineCommentToken, skipLine);
			} else {
				potencialBlockStart = currentLength - 1;
			}
			return;
		case '/':
			if (testToken(singleLineCommentToken)) {
				potencialBlockStart = -1;
				startBlock(singleLineCommentToken, singleLineComment);
			}
			return;
		case '*':
			if (testToken(multiLineCommentToken)) {
				potencialBlockStart = -1;
				startBlock(multiLineCommentToken, multiLineComment);
			}
			return;
		default:
			if (potencialBlockStart == -1) {
				return;
			}

			int length = currentText.length();
			if (length < minTokenLength) {
				return;
			} else if (length - potencialBlockStart > maxTokenLength) {
				potencialBlockStart = -1;
				return;
			}

			int seqHashCode = getPotencialTokenHashCode();
			ContentTokenInfo contentTokenInfo = contenetTokens.get(seqHashCode);
			if (contentTokenInfo == null) {
				return;
			}

			ShaderParserBlockType blockType = contentTokenInfo.blockType;
			if ((blockType == include || blockType == piece) && blockStack.size != 0) {
				return;
			} else if (blockType == ifdef) {
				numIfdefExpressionParenthesis = 1;
			}

			startBlock(contentTokenInfo.token, blockType);
		}
	}

	private void startBlock(char[] token, ShaderParserBlockType blockType) {
		parenthesisOpened = false;
		ShaderParserBlock newBlock = obtainBlock(blockType);
		int currLen = currentText.length();
		int tokenLen = token.length;
		ShaderParserBlock current = getCurrentBlock();

		if (mustAttachTextNode(tokenLen)) {
			ShaderParserBlock textSourceBlock = obtainBlock(text);
			textSourceBlock.value.append(currentText, 0, currLen - tokenLen);
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

	private int getPotencialTokenHashCode() {
		int result = 1;
		for (int i = potencialBlockStart, n = currentText.length(); i < n; i++) {
			result = 31 * result + currentText.charAt(i);
		}
		return result;
	}

	private ShaderParserBlock getCurrentBlock() {
		return blockStack.size == 0 ? null : blockStack.peek();
	}

	private void pop(int valuesSub) {
		ShaderParserBlock current = blockStack.peek();

		if (mustAttachTextNode(0)) {
			ShaderParserBlock textSourceBlock = obtainBlock(text);
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
		ShaderParserBlock newBlock = obtainBlock(blockType);
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
		int tokenLen = token.length;
		if (currLen < tokenLen) {
			return false;
		}

		for (int i = 0, n = currLen - tokenLen; i < tokenLen; i++) {
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
		skipLineEnded = false;
		type = root;
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
