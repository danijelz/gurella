package com.gurella.engine.graphics.render.shader.parser;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.graphics.render.shader.template.BooleanExpression;
import com.gurella.engine.graphics.render.shader.template.BooleanExpression.And;
import com.gurella.engine.graphics.render.shader.template.BooleanExpression.Not;
import com.gurella.engine.graphics.render.shader.template.BooleanExpression.Or;
import com.gurella.engine.graphics.render.shader.template.BooleanExpression.Property;

//https://unnikked.ga/how-to-evaluate-a-boolean-expression/
class BooleanExpressionParser implements Poolable {
	private int index = 0;
	private int len = 0;
	private CharSequence input;
	private StringBuilder lastProperty = new StringBuilder();

	private Symbol symbol;
	private BooleanExpression root;

	BooleanExpression parse(CharSequence input) {
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
		switch (symbol) {
		case PROPERTY:
			root = new Property(lastProperty.toString());
			symbol = nextSymbol();
			return;
		case NOT:
			factor();
			Not not = new Not(root);
			root = not;
			return;
		case LEFT:
			expression();
			symbol = nextSymbol();
			return;
		default:
			throw new RuntimeException("Expression Malformed: " + input);
		}
	}

	public Symbol nextSymbol() {
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
		System.out.println(parser.parse("true & ((true | false) & !(true & false))").toString());
		parser.reset();
		System.out.println(parser.parse("\n\nA & ((B | C) & !\n(!DEF & !GHI))").toString());
	}
}