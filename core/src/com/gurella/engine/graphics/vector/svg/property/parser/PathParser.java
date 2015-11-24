package com.gurella.engine.graphics.vector.svg.property.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.badlogic.gdx.utils.Array;
import com.gurella.engine.graphics.vector.Path;
import com.gurella.engine.graphics.vector.svg.property.PropertyParser;

public class PathParser implements PropertyParser<Path> {
	public static final PathParser instance = new PathParser();
	
	private final Matcher matcher = Pattern.compile("([MmLlHhVvAaQqTtCcSsZz])|([-+]?((\\d*\\.\\d+)|(\\d+))([eE][-+]?\\d+)?)").matcher("");
	private final Array<String> tokens = new Array<String>();
	
	private PathParser() {
	}

	@Override
	public synchronized Path parse(String strValue) {
		try {
			return parseSafely(strValue);
		} finally {
			tokens.clear();
		}
	}

	private Path parseSafely(String strValue) {
		matcher.reset(strValue);
		while (matcher.find()) {
			tokens.add(matcher.group());
		}

		Path path = Path.obtain();
		char currentComand = 'Z';
		
		while (tokens.size != 0) {
			String currentToken = tokens.removeIndex(0);
			
			char initChar = currentToken.charAt(0);
			
			if ((initChar >= 'A' && initChar <= 'Z') || (initChar >= 'a' && initChar <= 'z')) {
				currentComand = initChar;
			} else {
				tokens.insert(0, currentToken);
			}

			switch (currentComand) {
			case 'M':
				path.moveTo(nextFloat(), nextFloat());
				currentComand = 'L';
				break;
			case 'm':
				path.moveToRel(nextFloat(), nextFloat());
				currentComand = 'l';
				break;
			case 'L':
				path.lineTo(nextFloat(), nextFloat());
				break;
			case 'l':
				path.lineToRel(nextFloat(), nextFloat());
				break;
			case 'H':
				path.horizontalLineTo(nextFloat());
				break;
			case 'h':
				path.horizontalLineToRel(nextFloat());
				break;
			case 'V':
				path.verticalLineTo(nextFloat());
				break;
			case 'v':
				path.verticalLineToRel(nextFloat());
				break;
			case 'A':
				path.arcTo(nextFloat(), nextFloat(), nextFloat(), nextFloat() == 1f, nextFloat() == 1f, nextFloat(), nextFloat());
				break;
			case 'a':
				path.arcToRel(nextFloat(), nextFloat(), nextFloat(), nextFloat() == 1f, nextFloat() == 1f, nextFloat(), nextFloat());
				break;
			case 'Q':
				path.quadTo(nextFloat(), nextFloat(), nextFloat(), nextFloat());
				break;
			case 'q':
				path.quadToRel(nextFloat(), nextFloat(), nextFloat(), nextFloat());
				break;
			case 'T':
				path.quadSmoothTo(nextFloat(), nextFloat());
				break;
			case 't':
				path.quadSmoothToRel(nextFloat(), nextFloat());
				break;
			case 'C':
				path.cubicTo(nextFloat(), nextFloat(), nextFloat(), nextFloat(), nextFloat(), nextFloat());
				break;
			case 'c':
				path.cubicToRel(nextFloat(), nextFloat(), nextFloat(), nextFloat(), nextFloat(), nextFloat());
				break;
			case 'S':
				path.cubicSmoothTo(nextFloat(), nextFloat(), nextFloat(), nextFloat());
				break;
			case 's':
				path.cubicSmoothToRel(nextFloat(), nextFloat(), nextFloat(), nextFloat());
				break;
			case 'Z':
			case 'z':
				path.close();
				break;
			default:
				throw new RuntimeException("Invalid path element");
			}
		}

		return path;
	}
	
	private float nextFloat() {
		return Float.parseFloat(tokens.removeIndex(0));
	}
}
