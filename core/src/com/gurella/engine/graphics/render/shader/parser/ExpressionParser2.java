package com.gurella.engine.graphics.render.shader.parser;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.graphics.render.shader.generator.ShaderGeneratorContext;
import com.gurella.engine.graphics.render.shader.template.ExpressionNode.AndOperation;
import com.gurella.engine.graphics.render.shader.template.ExpressionNode.AssignAddOperation;
import com.gurella.engine.graphics.render.shader.template.ExpressionNode.AssignDivOperation;
import com.gurella.engine.graphics.render.shader.template.ExpressionNode.AssignMulOperation;
import com.gurella.engine.graphics.render.shader.template.ExpressionNode.AssignOperation;
import com.gurella.engine.graphics.render.shader.template.ExpressionNode.AssignSubOperation;
import com.gurella.engine.graphics.render.shader.template.ExpressionNode.CompositeExpression;
import com.gurella.engine.graphics.render.shader.template.ExpressionNode.DivideOperation;
import com.gurella.engine.graphics.render.shader.template.ExpressionNode.EqOperation;
import com.gurella.engine.graphics.render.shader.template.ExpressionNode.GeOperation;
import com.gurella.engine.graphics.render.shader.template.ExpressionNode.GtOperation;
import com.gurella.engine.graphics.render.shader.template.ExpressionNode.LeOperation;
import com.gurella.engine.graphics.render.shader.template.ExpressionNode.LtOperation;
import com.gurella.engine.graphics.render.shader.template.ExpressionNode.MinusOperation;
import com.gurella.engine.graphics.render.shader.template.ExpressionNode.MultiplyOperation;
import com.gurella.engine.graphics.render.shader.template.ExpressionNode.NeOperation;
import com.gurella.engine.graphics.render.shader.template.ExpressionNode.NotOperation;
import com.gurella.engine.graphics.render.shader.template.ExpressionNode.NumberLiteralExpression;
import com.gurella.engine.graphics.render.shader.template.ExpressionNode.OrOperation;
import com.gurella.engine.graphics.render.shader.template.ExpressionNode.PlusOperation;
import com.gurella.engine.graphics.render.shader.template.ExpressionNode.ShaderTemplateExpression;
import com.gurella.engine.graphics.render.shader.template.ExpressionNode.UnaryMinusOperation;
import com.gurella.engine.graphics.render.shader.template.ExpressionNode.VarExpression;

//https://github.com/nickgammon/parser/blob/master/parser.h
//https://github.com/nickgammon/parser/blob/master/parser.cpp
public class ExpressionParser2 {
	private static final ObjectMap<String, OneArgFunctionFactory> oneArgumentFunctions = new ObjectMap<String, OneArgFunctionFactory>();
	private static final ObjectMap<String, TwoArgFunctionFactory> twoArgumentFunctions = new ObjectMap<String, TwoArgFunctionFactory>();
	private static final ObjectMap<String, ThreeArgFunctionFactory> threeArgumentFunctions = new ObjectMap<String, ThreeArgFunctionFactory>();

	CharSequence program;

	int pWord;
	int pWordStart;

	// last token parsed
	TokenType type;
	String word;

	float value;

	// change program and evaluate it
	public ShaderTemplateExpression parse(CharSequence program) {
		this.program = program;
		pWord = 0;
		type = TokenType.NONE;
		ShaderTemplateExpression v = commaList(true);

		if (v == null) {
			throw new RuntimeException("Unexpected text at end of expression: " + pWordStart);
		}

		return v;
	}

	// expr1, expr2
	ShaderTemplateExpression commaList(boolean get) {
		Array<ShaderTemplateExpression> expressions = new Array<ShaderTemplateExpression>();
		expressions.add(expression(get));

		while (true) {
			switch (type) {
			case COMMA:
				expressions.add(expression(true));
				break; // discard previous value
			default:
				switch (expressions.size) {
				case 0:
					return null;
				case 1:
					return expressions.get(0);
				default:
					return new CompositeExpression(expressions);
				}
			}
		}
	}

	// AND and OR
	ShaderTemplateExpression expression(boolean get) {
		ShaderTemplateExpression left = comparison(get);
		while (true) {
			switch (type) {
			case AND: {
				ShaderTemplateExpression right = comparison(true); // don't want short-circuit evaluation
				return new AndOperation(left, right);
			}
			case OR: {
				ShaderTemplateExpression right = comparison(true); // don't want short-circuit evaluation
				return new OrOperation(left, right);
			}
			default:
				return left;
			}
		}
	}

	// LT, GT, LE, EQ etc.
	ShaderTemplateExpression comparison(boolean get) {
		ShaderTemplateExpression left = addSubtract(get);
		while (true) {
			switch (type) {
			case LT:
				return new LtOperation(left, addSubtract(true));
			case GT:
				return new GtOperation(left, addSubtract(true));
			case LE:
				return new LeOperation(left, addSubtract(true));
			case GE:
				return new GeOperation(left, addSubtract(true));
			case EQ:
				return new EqOperation(left, addSubtract(true));
			case NE:
				return new NeOperation(left, addSubtract(true));
			default:
				return left;
			}
		}
	}

	// add and subtract
	ShaderTemplateExpression addSubtract(boolean get) {
		ShaderTemplateExpression left = term(get);
		while (true) {
			switch (type) {
			case PLUS:
				return new PlusOperation(left, term(true));
			case MINUS:
				return new MinusOperation(left, term(true));
			default:
				return left;
			}
		}
	}

	// multiply and divide
	ShaderTemplateExpression term(boolean get) {
		ShaderTemplateExpression left = primary(get);
		while (true) {
			switch (type) {
			case MULTIPLY:
				return new MultiplyOperation(left, primary(true));
			case DIVIDE: {
				return new DivideOperation(left, primary(true));
			}
			default:
				return left;
			}
		}
	}

	// primary (base) tokens
	ShaderTemplateExpression primary(boolean get) {
		if (get) {
			getToken(false); // one-token lookahead
		}

		switch (type) {
		case NUMBER: {
			float v = value;
			getToken(true); // get next one (one-token lookahead)
			return new NumberLiteralExpression(v);
		}

		case NAME: {
			String word = this.word;
			getToken(true);
			if (type == TokenType.LHPAREN) {
				// might be single-argument function (eg. abs (x) )
				OneArgFunctionFactory si = oneArgumentFunctions.get(word);
				if (si != null) {
					ShaderTemplateExpression v = expression(true); // get argument
					checkToken(TokenType.RHPAREN);
					getToken(true); // get next one (one-token lookahead)
					return si.create(v); // evaluate function
				}

				// might be double-argument function (eg. roll (6, 2) )
				TwoArgFunctionFactory di = twoArgumentFunctions.get(word);
				if (di != null) {
					ShaderTemplateExpression v1 = expression(true); // get argument 1 (not commalist)
					checkToken(TokenType.COMMA);
					ShaderTemplateExpression v2 = expression(true); // get argument 2 (not commalist)
					checkToken(TokenType.RHPAREN);
					getToken(true); // get next one (one-token lookahead)
					return di.create(v1, v2); // evaluate function
				}

				// might be double-argument function (eg. roll (6, 2) )
				ThreeArgFunctionFactory ti = threeArgumentFunctions.get(word);
				if (ti != null) {
					ShaderTemplateExpression v1 = expression(true); // get argument 1 (not commalist)
					checkToken(TokenType.COMMA);
					ShaderTemplateExpression v2 = expression(true); // get argument 2 (not commalist)
					checkToken(TokenType.COMMA);
					ShaderTemplateExpression v3 = expression(true); // get argument 3 (not commalist)
					checkToken(TokenType.RHPAREN);
					getToken(true); // get next one (one-token lookahead)
					return ti.create(v1, v2, v3); // evaluate function
				}

				throw new RuntimeException("Function '" + word + "' not implemented.");
			}

			// not a function? must be a symbol in the symbol table
			// change table entry with expression? (eg. a = 22, or a = 22)
			switch (type) {
			// maybe check for NaN or Inf here (see: isinf, isnan functions)
			case ASSIGN:
				return new AssignOperation(expression(true), word);
			case ASSIGN_ADD:
				return new AssignAddOperation(expression(true), word);
			case ASSIGN_SUB:
				return new AssignSubOperation(expression(true), word);
			case ASSIGN_MUL:
				return new AssignMulOperation(expression(true), word);
			case ASSIGN_DIV: {
				return new AssignDivOperation(expression(true), word);
			}
			default:
				break; // do nothing for others
			}

			return new VarExpression(word); // and return new value
		}

		case MINUS:
			return new UnaryMinusOperation(primary(true));

		case NOT:
			return new NotOperation(primary(true));

		case LHPAREN: {
			ShaderTemplateExpression v = commaList(true); // inside parens, you could have commas
			checkToken(TokenType.RHPAREN);
			getToken(true); // eat the )
			return v;
		}

		default:
			throw new RuntimeException("Unexpected token: " + word);
		}
	}

	TokenType getToken(boolean ignoreSign) {
		word = null;

		// skip spaces
		while (pWord > -1 && Character.isWhitespace(programChar(pWord))) {
			++pWord;
		}

		pWordStart = pWord; // remember where word_ starts *now*

		// look out for unterminated statements and things
		// we have EOF after already detecting it
		if (pWord == 0 && type == TokenType.END) {
			throw new RuntimeException("Unexpected end of expression.");
		}

		char cFirstCharacter = programChar(pWord); // first character in new word_

		// stop at end of file
		if (cFirstCharacter == 0) {
			word = "<end of expression>";
			return type = TokenType.END;
		}

		char cNextCharacter = programChar(pWord + 1); // 2nd character in new word_

		// look for number
		// can be: + or - followed by a decimal point
		// or: + or - followed by a digit
		// or: starting with a digit
		// or: decimal point followed by a digit
		// allow decimal numbers without a leading 0. e.g. ".5"
		// Dennis Jones 01-30-2009
		if ((!ignoreSign && (cFirstCharacter == '+' || cFirstCharacter == '-')
				&& (Character.isDigit(cNextCharacter) || cNextCharacter == '.')) || Character.isDigit(cFirstCharacter)
				|| (cFirstCharacter == '.' && Character.isDigit(cNextCharacter))) {
			// skip sign for now
			if ((cFirstCharacter == '+' || cFirstCharacter == '-')) {
				pWord++;
			}

			char c = programChar(pWord);
			while (Character.isDigit(c) || c == '.') {
				pWord++;
				c = programChar(pWord);
			}

			// allow for 1.53158e+15
			c = programChar(pWord);
			if (c == 'e' || c == 'E') {
				pWord++; // skip 'e'
				c = programChar(pWord);
				if ((c == '+' || c == '-')) {
					pWord++; // skip sign after e
				}

				// now digits after e
				while (Character.isDigit(programChar(pWord))) {
					pWord++;
				}
			}

			word = program.subSequence(pWordStart, pWordStart + pWord - pWordStart).toString();
			value = Float.parseFloat(word);

			return type = TokenType.NUMBER;
		}

		// special test for 2-character sequences: <= >= == !=
		// also +=, -=, /=, *=
		if (cNextCharacter == '=') {
			switch (cFirstCharacter) {
			// comparisons
			case '=':
				type = TokenType.EQ;
				break;
			case '<':
				type = TokenType.LE;
				break;
			case '>':
				type = TokenType.GE;
				break;
			case '!':
				type = TokenType.NE;
				break;
			// assignments
			case '+':
				type = TokenType.ASSIGN_ADD;
				break;
			case '-':
				type = TokenType.ASSIGN_SUB;
				break;
			case '*':
				type = TokenType.ASSIGN_MUL;
				break;
			case '/':
				type = TokenType.ASSIGN_DIV;
				break;
			// none of the above
			default:
				type = TokenType.NONE;
				break;
			}

			if (type != TokenType.NONE) {
				word = program.subSequence(pWordStart, pWordStart + 2).toString();
				pWord += 2; // skip both characters
				return type;
			}
		}

		switch (cFirstCharacter) {
		case '&':
			// &&
			if (cNextCharacter == '&') {
				word = program.subSequence(pWordStart, pWordStart + 2).toString();
				pWord += 2; // skip both characters
				return type = TokenType.AND;
			}
			break;
		case '|':
			// ||
			if (cNextCharacter == '|') {
				word = program.subSequence(pWordStart, pWordStart + 2).toString();
				pWord += 2; // skip both characters
				return type = TokenType.OR;
			}
			break;
		// single-character symboles
		case '=':
		case '<':
		case '>':
		case '+':
		case '-':
		case '/':
		case '*':
		case '(':
		case ')':
		case ',':
		case '!':
			word = program.subSequence(pWordStart, pWordStart + 1).toString();
			++pWord; // skip it
			return type = TokenType.get(cFirstCharacter);
		}

		if (!Character.isLetter(cFirstCharacter)) {
			throw new RuntimeException("Unexpected character: " + cFirstCharacter);
		}

		// we have a word (starting with A-Z) - pull it out
		char c = programChar(pWord);
		while (Character.isLetterOrDigit(c) || c == '_') {
			++pWord;
			c = programChar(pWord);
		}

		word = program.subSequence(pWordStart, pWordStart + pWord - pWordStart).toString();
		return type = TokenType.NAME;
	}

	private char programChar(int index) {
		return program.length() > index ? program.charAt(index) : 0;
	}

	void checkToken(TokenType wanted) {
		if (type != wanted) {
			throw new RuntimeException("'" + wanted + "' expected.");
		}
	}

	enum TokenType {
		NONE,
		NAME,
		NUMBER,
		END,
		PLUS, // ='+',
		MINUS, // ='-',
		MULTIPLY, // ='*',
		DIVIDE, // ='/',
		ASSIGN, // ='=',
		LHPAREN, // ='(',
		RHPAREN, // =')',
		COMMA, // =',',
		NOT, // ='!',

		// comparisons
		LT, // ='<',
		GT, // ='>',
		LE, // <=
		GE, // >=
		EQ, // ==
		NE, // !=
		AND, // &&
		OR, // ||

		// special assignments
		ASSIGN_ADD, // +=
		ASSIGN_SUB, // +-
		ASSIGN_MUL, // +*
		ASSIGN_DIV // +/
		;

		public static TokenType get(char character) {
			switch (character) {
			case '=':
				return ASSIGN;
			case '<':
				return LT;
			case '>':
				return GT;
			case '+':
				return PLUS;
			case '-':
				return MINUS;
			case '/':
				return DIVIDE;
			case '*':
				return MULTIPLY;
			case '(':
				return LHPAREN;
			case ')':
				return RHPAREN;
			case ',':
				return COMMA;
			case '!':
				return NOT;
			default:
				break;
			}
			// TODO Auto-generated method stub
			return null;
		}
	}

	private static interface OneArgFunctionFactory {
		ShaderTemplateExpression create(ShaderTemplateExpression arg);
	}

	private static interface TwoArgFunctionFactory {
		ShaderTemplateExpression create(ShaderTemplateExpression arg1, ShaderTemplateExpression arg2);
	}

	private static interface ThreeArgFunctionFactory {
		ShaderTemplateExpression create(ShaderTemplateExpression arg1, ShaderTemplateExpression arg2,
				ShaderTemplateExpression arg3);
	}

	public static void main(String[] args) {
		System.out.println(
				new ExpressionParser2().parse("a = 1, (2 + 2) * 3 + a").evaluate(new ShaderGeneratorContext()));
	}
}
