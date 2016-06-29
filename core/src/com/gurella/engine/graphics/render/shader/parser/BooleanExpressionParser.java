package com.gurella.engine.graphics.render.shader.parser;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.graphics.render.shader.template.BooleanExpression;
import com.gurella.engine.graphics.render.shader.template.BooleanExpression.And;
import com.gurella.engine.graphics.render.shader.template.BooleanExpression.Not;
import com.gurella.engine.graphics.render.shader.template.BooleanExpression.Or;
import com.gurella.engine.graphics.render.shader.template.BooleanExpression.Terminal;
import com.gurella.engine.pool.PoolService;

class BooleanExpressionParser implements Poolable {
	private int index = 0;
	private int len = 0;
	private CharSequence input;
	private StringBuilder lastProperty = new StringBuilder();

	private BooleanExpressionParser.Symbol symbol;
	private BooleanExpression root;

	static BooleanExpression parse(CharSequence input) {
		BooleanExpressionParser parser = PoolService.obtain(BooleanExpressionParser.class);
		BooleanExpression expression = parser.parseExpression(input);
		PoolService.free(parser);
		return expression;
	}

	BooleanExpression parseExpression(CharSequence input) {
		this.input = input;
		len = input.length();
		expression();
		return root;
	}

	private void expression() {
		term();
		while (symbol == Symbol.OR) {
			Or or = new Or();
			or.setLeft(root);
			term();
			or.setRight(root);
			root = or;
		}
	}

	private void term() {
		factor();
		while (symbol == Symbol.AND) {
			And and = new And();
			and.setLeft(root);
			factor();
			and.setRight(root);
			root = and;
		}
	}

	private void factor() {
		symbol = nextSymbol();
		if (symbol == Symbol.PROPERTY) {
			root = new Terminal(lastProperty.toString());
			symbol = nextSymbol();
		} else if (symbol == Symbol.NOT) {
			factor();
			Not not = new Not(root);
			root = not;
		} else if (symbol == Symbol.LEFT) {
			expression();
			symbol = nextSymbol();
		} else {
			throw new RuntimeException("Expression Malformed");
		}
	}

	public BooleanExpressionParser.Symbol nextSymbol() {
		while (index < len) {
			char c = input.charAt(index++);
			switch (c) {
			case '(':
				return Symbol.LEFT;
			case ')':
				return Symbol.RIGHT;
			case '&':
				return Symbol.AND;
			case '|':
				return Symbol.OR;
			case '!':
				return Symbol.NOT;
			case (char) 0x0009:
			case (char) 0x000A:
			case (char) 0x000B:
			case (char) 0x000C:
			case (char) 0x000D:
			case (char) 0x0020:
			case (char) 0x0085:
			case (char) 0x00A0:
			case (char) 0x1680:
			case (char) 0x180E:
			case (char) 0x2000:
			case (char) 0x2001:
			case (char) 0x2002:
			case (char) 0x2003:
			case (char) 0x2004:
			case (char) 0x2005:
			case (char) 0x2006:
			case (char) 0x2007:
			case (char) 0x2008:
			case (char) 0x2009:
			case (char) 0x200A:
			case (char) 0x200B:
			case (char) 0x200C:
			case (char) 0x200D:
			case (char) 0x2028:
			case (char) 0x2029:
			case (char) 0x202F:
			case (char) 0x205F:
			case (char) 0x2060:
			case (char) 0x3000:
			case (char) 0xFEFF:
				continue;
			default:
				lastProperty.setLength(0);
				lastProperty.append(c);
				extractProperty();
				return Symbol.PROPERTY;
			}
		}

		return Symbol.EOF;
	}

	protected void extractProperty() {
		while (index < len) {
			char c = input.charAt(index++);
			switch (c) {
			case '(':
			case ')':
			case '&':
			case '|':
			case '!':
				index--;
				return;
			case (char) 0x0009:
			case (char) 0x000A:
			case (char) 0x000B:
			case (char) 0x000C:
			case (char) 0x000D:
			case (char) 0x0020:
			case (char) 0x0085:
			case (char) 0x00A0:
			case (char) 0x1680:
			case (char) 0x180E:
			case (char) 0x2000:
			case (char) 0x2001:
			case (char) 0x2002:
			case (char) 0x2003:
			case (char) 0x2004:
			case (char) 0x2005:
			case (char) 0x2006:
			case (char) 0x2007:
			case (char) 0x2008:
			case (char) 0x2009:
			case (char) 0x200A:
			case (char) 0x200B:
			case (char) 0x200C:
			case (char) 0x200D:
			case (char) 0x2028:
			case (char) 0x2029:
			case (char) 0x202F:
			case (char) 0x205F:
			case (char) 0x2060:
			case (char) 0x3000:
			case (char) 0xFEFF:
				return;
			default:
				lastProperty.append(c);
			}
		}
	}

	@Override
	public void reset() {
		index = 0;
		input = null;
		symbol = null;
		root = null;
	}

	private enum Symbol {
		EOF,

		OR,
		AND,
		NOT,

		PROPERTY,

		LEFT,
		RIGHT;
	}

	public static void main(String[] args) {
		BooleanExpressionParser parser = new BooleanExpressionParser();
		System.out.println(parser.parseExpression("true & ((true | false) & !(true & false))").toString());
		parser.reset();
		System.out.println(parser.parseExpression("\n\nA & ((B | C) & !\n(!DEF & !GHI))").toString());
	}
}