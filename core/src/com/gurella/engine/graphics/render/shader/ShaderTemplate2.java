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

public class ShaderTemplate2 {
	private Array<Block> blockStack = new Array<Block>();
	private Array<Block> blocks = new Array<Block>();
	StringBuffer currentValues = new StringBuffer();

	boolean possibleBlockStart;

	char[] endTest = "@end".toCharArray();
	char[] endTemp = new char[4];

	char[] includeTest = "@include".toCharArray();
	char[] includeTemp = new char[8];

	char[] pieceTest = "@piece".toCharArray();
	char[] pieceTemp = new char[6];

	char[] insertpieceTest = "@insertpiece".toCharArray();
	char[] insertpieceTemp = new char[12];

	char[] multiLineCommentStartTest = "/*".toCharArray();
	char[] singleLineCommentStartTest = "//".toCharArray();
	char[] commentStartTemp = new char[2];

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

			if (currentValues.length() > 0 && blockStack.size == 0) {
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

	public void parse(char[] data, int length) {
		Block current = getCurrentBlock();
		BlockType type = current == null ? BlockType.none : current.getType();

		for (int i = 0; i < length; i++) {
			char c = data[i];
			currentValues.append(c);

			switch (type) {
			case singleLineComment:
				if ('\n' == c || '\r' == c) {
					type = pop();
				}
				break;
			case multiLineComment:
				if ('/' == c && currentValues.charAt(currentValues.length() - 2) == '*') {
					type = pop();
				}
				break;
			case include:
			case insertpiece:
				if (')' == c) {
					type = pop();
				}
				break;
			case piece:
				if ('d' == c && testLast(endTest, endTemp)) {
					type = pop();
				}
				break;
			case none:
			case text:
				if ('@' == c) {
					possibleBlockStart = true;
				} else if ('/' == c && testLast(singleLineCommentStartTest, commentStartTemp)) {
					type = startBlock(singleLineCommentStartTest, new SingleLineComment());
				} else if ('*' == c && testLast(multiLineCommentStartTest, commentStartTemp)) {
					type = startBlock(multiLineCommentStartTest, new MultiLineComment());
				} else if (possibleBlockStart) {
					if (testLast(includeTest, includeTemp)) {
						type = startBlock(includeTest, new Include());
					} else if (testLast(pieceTest, pieceTemp)) {
						type = startBlock(pieceTest, new Piece());
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
				pop();
				current = getCurrentBlock();
			}
		} else {
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

	private BlockType pop() {
		Block current = blockStack.peek();
		current.value.append(currentValues);
		currentValues.setLength(0);
		blockStack.pop();
		possibleBlockStart = false;

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
		int start;
		int end;
		StringBuffer value = new StringBuffer();
		private Array<Block> children = new Array<Block>();

		abstract BlockType getType();

		@Override
		public String toString() {
			return value.toString();
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
		InputStream input = ShaderTemplate2.class.getClassLoader()
				.getResourceAsStream("com/gurella/engine/graphics/render/shader/TestParser.glsl");
		ShaderTemplate2 template = new ShaderTemplate2();
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
		}
	}
}
