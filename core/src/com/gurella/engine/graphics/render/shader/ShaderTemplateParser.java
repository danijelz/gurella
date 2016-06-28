package com.gurella.engine.graphics.render.shader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.SerializationException;
import com.badlogic.gdx.utils.StreamUtils;

public class ShaderTemplateParser {
	private Array<Block> blockStack = new Array<Block>();
	private Array<Block> blocks = new Array<Block>();
	private StringBuffer currentValues = new StringBuffer();

	private int possibleBlockStart = -1;
	private int maxBlockTestChar = 12;
	private boolean parenthesisOpened = false;

	private char[] endTest = "@end".toCharArray();
	private char[] endTemp = new char[4];

	private char[] includeTest = "@include".toCharArray();
	private char[] includeTemp = new char[8];

	private char[] pieceTest = "@piece".toCharArray();
	private char[] pieceTemp = new char[6];

	private char[] insertpieceTest = "@insertpiece".toCharArray();
	private char[] insertpieceTemp = new char[12];

	private char[] ifdefTest = "@ifdef".toCharArray();
	private char[] ifdefTemp = new char[6];

	private char[] multiLineCommentStartTest = "/*".toCharArray();
	private char[] singleLineCommentStartTest = "//".toCharArray();
	private char[] commentStartTemp = new char[2];

	public void parse(Reader reader) {
		try {
			int size = 10;
			char[] data = new char[size];
			while (true) {
				int length = reader.read(data);
				if (length < 1) {
					break;
				} else {
					parse(data, length);
				}
			}

			if (!areCurrentValuesEmpty(0) && blockStack.size == 0) {
				Text text = new Text();
				text.value.append(currentValues);
				blocks.add(text);
			}
		} catch (IOException ex) {
			throw new SerializationException(ex);
		} finally {
			StreamUtils.closeQuietly(reader);
		}
	}

	private boolean areCurrentValuesEmpty(int end) {
		int length = currentValues.length();
		if (length == 0) {
			return true;
		}

		for (int i = 0; i < length - end; i++) {
			if (!Character.isWhitespace(currentValues.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	public void parse(char[] data, int length) {
		Block current = getCurrentBlock();
		BlockType type = current == null ? BlockType.none : current.getType();

		for (int i = 0; i < length; i++) {
			char c = data[i];
			currentValues.append(c);

			switch (type) {
			case singleLineComment:
				if ('\n' == c || '\r' == c) {
					type = pop(1);
				}
				break;
			case multiLineComment:
				if ('/' == c && currentValues.charAt(currentValues.length() - 2) == '*') {
					type = pop(2);
				}
				break;
			case include:
			case insertPiece:
				if (parenthesisOpened) {
					if (')' == c) {
						type = pop(1);
					}
				} else {
					currentValues.setLength(currentValues.length() - 1);
					if ('(' == c) {
						parenthesisOpened = true;
					}
				}
				break;
			case ifdef:
				if (parenthesisOpened) {
					if (')' == c) {
						type = push(1, new IfdefContent());
					}
				} else {
					currentValues.setLength(currentValues.length() - 1);
					if ('(' == c) {
						parenthesisOpened = true;
					}
				}
				break;
			case piece:
				if (possibleBlockStart > -1 && 'd' == c && testLast(endTest, endTemp)) {
					type = pop(4);
				} else {
					type = checkBlockStart(type, c);
				}
				break;
			case ifdefContent:
				if (possibleBlockStart > -1 && 'd' == c && testLast(endTest, endTemp)) {
					type = pop(4);
					type = pop(0);
				} else {
					type = checkBlockStart(type, c);
				}
				break;
			case none:
			case text:
				type = checkBlockStart(type, c);
				break;
			default:
				break;
			}
		}
	}

	private BlockType checkBlockStart(BlockType type, char c) {
		switch (c) {
		case '@':
			possibleBlockStart = currentValues.length() - 1;
			return type;
		case '/':
			if (testLast(singleLineCommentStartTest, commentStartTemp)) {
				return startBlock(singleLineCommentStartTest, new SingleLineComment());
			} else {
				return type;
			}
		case '*':
			if (testLast(multiLineCommentStartTest, commentStartTemp)) {
				return startBlock(multiLineCommentStartTest, new MultiLineComment());
			} else {
				return type;
			}
		default:
			if (currentValues.length() - possibleBlockStart > maxBlockTestChar) {
				possibleBlockStart = -1;
				return type;
			} else if (testLast(includeTest, includeTemp)) {
				parenthesisOpened = false;
				return startBlock(includeTest, new Include());
			} else if (testLast(pieceTest, pieceTemp)) {
				return startBlock(pieceTest, new Piece());
			} else if (testLast(insertpieceTest, insertpieceTemp)) {
				parenthesisOpened = false;
				return startBlock(insertpieceTest, new InsertPiece());
			} else if (testLast(ifdefTest, ifdefTemp)) {
				parenthesisOpened = false;
				return startBlock(ifdefTest, new Ifdef());
			} else {
				return type;
			}
		}
	}

	private BlockType startBlock(char[] startedType, Block newBlock) {
		int currLen = currentValues.length();
		int testLen = startedType.length;
		Block current = getCurrentBlock();

		if (currLen == testLen) {
			if (current != null) {
				pop(0);
				current = getCurrentBlock();
			}
		} else if (!areCurrentValuesEmpty(testLen)) {
			Text text = new Text();
			text.value.append(currentValues, 0, currLen - testLen);
			if (current == null) {
				blocks.add(text);
			} else {
				current.children.add(text);
			}
		}

		currentValues.setLength(0);
		blockStack.add(newBlock);
		if (current == null) {
			blocks.add(newBlock);
		} else {
			current.children.add(newBlock);
		}

		return newBlock.getType();
	}

	private Block getCurrentBlock() {
		return blockStack.size == 0 ? null : blockStack.peek();
	}

	private BlockType pop(int valuesSub) {
		Block current = blockStack.peek();
		BlockType type = current.getType();

		if (type == BlockType.piece || type == BlockType.ifdefContent) {
			Text text = new Text();
			text.value.append(currentValues, 0, currentValues.length() - valuesSub);
			current.children.add(text);
		} else {
			current.value.append(currentValues, 0, currentValues.length() - valuesSub);
		}

		currentValues.setLength(0);
		blockStack.pop();
		possibleBlockStart = -1;

		current = getCurrentBlock();
		return current == null ? BlockType.none : current.getType();
	}

	private BlockType push(int valuesSub, Block newBlock) {
		Block current = blockStack.peek();
		current.value.append(currentValues, 0, currentValues.length() - valuesSub);
		currentValues.setLength(0);
		blockStack.add(newBlock);
		current.children.add(newBlock);
		possibleBlockStart = -1;
		return newBlock.getType();
	}

	private boolean testLast(char[] testVal, char[] temp) {
		int currLen = currentValues.length();
		int testLen = testVal.length;
		if (currLen < testLen) {
			return false;
		}

		currentValues.getChars(currLen - testLen, currLen, temp, 0);
		return Arrays.equals(testVal, temp);
	}

	private enum BlockType {
		singleLineComment,
		multiLineComment,
		include,
		piece,
		insertPiece,
		text,
		ifdef,
		ifdefExpression,
		ifdefContent,
		none;
	}

	private static abstract class Block {
		StringBuffer value = new StringBuffer();
		private Array<Block> children = new Array<Block>();

		abstract BlockType getType();

		@Override
		public String toString() {
			return toString(0);
		}

		public String toString(int indent) {
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < indent; i++) {
				builder.append('\t');
			}

			builder.append(getType().name());
			builder.append(": {");
			builder.append(toStringValue());
			builder.append(toStringChildren(indent + 1));

			if (children.size > 0) {
				builder.append("\n");
				for (int i = 0; i < indent; i++) {
					builder.append('\t');
				}
			}

			builder.append("}");
			return builder.toString();
		}

		protected String toStringValue() {
			return value.toString();
		}

		private String toStringChildren(int indent) {
			if (children.size == 0) {
				return "";
			}

			StringBuilder builder = new StringBuilder();
			for (Block child : children) {
				builder.append("\n");
				builder.append(child.toString(indent));
			}
			return builder.toString();
		}
	}

	private static class SingleLineComment extends Block {
		@Override
		BlockType getType() {
			return BlockType.singleLineComment;
		}
	}

	private static class MultiLineComment extends Block {
		@Override
		BlockType getType() {
			return BlockType.multiLineComment;
		}
	}

	private static class Include extends Block {
		@Override
		BlockType getType() {
			return BlockType.include;
		}
	}

	private static class InsertPiece extends Block {
		@Override
		BlockType getType() {
			return BlockType.insertPiece;
		}
	}

	private static class Piece extends Block {
		@Override
		BlockType getType() {
			return BlockType.piece;
		}
	}

	private static class Text extends Block {
		@Override
		BlockType getType() {
			return BlockType.text;
		}

		@Override
		protected String toStringValue() {
			return super.toStringValue().replace("\n", "\\n");
		}
	}

	private static class Ifdef extends Block {
		@Override
		BlockType getType() {
			return BlockType.ifdef;
		}
	}

	private static class IfdefContent extends Block {
		@Override
		BlockType getType() {
			return BlockType.ifdefContent;
		}
	}

	public static void main(String[] args) {
		InputStream input = ShaderTemplateParser.class.getClassLoader()
				.getResourceAsStream("com/gurella/engine/graphics/render/shader/TestParser.glsl");
		ShaderTemplateParser template = new ShaderTemplateParser();
		try {
			template.parse(new InputStreamReader(input, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		template.printBlocks();
	}

	private void printBlocks() {
		for (Block block : blocks) {
			System.out.print(block.toString());
			System.out.print("\n");
		}
	}
}
