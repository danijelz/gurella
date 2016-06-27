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

	private char[] endTest = "@end".toCharArray();
	private char[] endTemp = new char[4];

	private char[] includeTest = "@include".toCharArray();
	private char[] includeTemp = new char[8];

	private char[] pieceTest = "@piece".toCharArray();
	private char[] pieceTemp = new char[6];

	private char[] insertpieceTest = "@insertpiece".toCharArray();
	private char[] insertpieceTemp = new char[12];

	private char[] multiLineCommentStartTest = "/*".toCharArray();
	private char[] singleLineCommentStartTest = "//".toCharArray();
	private char[] commentStartTemp = new char[2];

	public void parse(Reader reader) {
		try {
			int size = 1024;
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
			case insertpiece:
				if (')' == c) {
					type = pop(1);
				}
				break;
			case piece:
				if ('d' == c && testLast(endTest, endTemp)) {
					type = pop(4);
				}
				break;
			case none:
			case text:
				if ('@' == c) {
					possibleBlockStart = currentValues.length() - 1;
				} else if ('/' == c && testLast(singleLineCommentStartTest, commentStartTemp)) {
					type = startBlock(singleLineCommentStartTest, new SingleLineComment());
				} else if ('*' == c && testLast(multiLineCommentStartTest, commentStartTemp)) {
					type = startBlock(multiLineCommentStartTest, new MultiLineComment());
				} else if (possibleBlockStart > -1) {
					if (testLast(includeTest, includeTemp)) {
						type = startBlock(includeTest, new Include());
					} else if (testLast(pieceTest, pieceTemp)) {
						type = startBlock(pieceTest, new Piece());
					} else if (testLast(insertpieceTest, insertpieceTemp)) {
						type = startBlock(insertpieceTest, new InsertPiece());
					} else if (currentValues.length() - possibleBlockStart > maxBlockTestChar) {
						possibleBlockStart = -1;
					}
				}
				break;
			default:
				break;
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
			blocks.add(text);
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
		current.value.append(currentValues, 0, currentValues.length() - valuesSub);
		currentValues.setLength(0);
		blockStack.pop();
		possibleBlockStart = -1;

		current = getCurrentBlock();
		return current == null ? BlockType.none : current.getType();
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
		singleLineComment, multiLineComment, include, piece, insertpiece, text, none;
	}

	private static abstract class Block {
		StringBuffer value = new StringBuffer();
		private Array<Block> children = new Array<Block>();

		abstract BlockType getType();

		@Override
		public String toString() {
			return getType().name() + ": '" + value.toString() + "'" + toStringChildren();
		}

		private String toStringChildren() {
			if (children.size == 0) {
				return "";
			}

			StringBuilder builder = new StringBuilder();
			for (Block child : children) {
				builder.append("\n\t");
				builder.append(child.toString());
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
			return BlockType.insertpiece;
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
