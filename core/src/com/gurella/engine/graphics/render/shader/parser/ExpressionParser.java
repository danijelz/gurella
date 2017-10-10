package com.gurella.engine.graphics.render.shader.parser;

import com.badlogic.gdx.utils.ObjectMap;

///https://github.com/nickgammon/parser/blob/master/parser.h
//https://github.com/nickgammon/parser/blob/master/parser.cpp
public class ExpressionParser {
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

	private static interface OneArgFunction {
		double eval(double arg);
	}

	private static interface TwoArgFunction {
		double eval(double arg1, double arg2);
	}

	private static interface ThreeArgFunction {
		double eval(double arg1, double arg2, double arg3);
	}

	private static final ObjectMap<String, Double> symbols_ = new ObjectMap<String, Double>();
	private static final ObjectMap<String, OneArgFunction> OneArgumentFunctions = new ObjectMap<String, OneArgFunction>();
	private static final ObjectMap<String, TwoArgFunction> TwoArgumentFunctions = new ObjectMap<String, TwoArgFunction>();
	private static final ObjectMap<String, ThreeArgFunction> ThreeArgumentFunctions = new ObjectMap<String, ThreeArgFunction>();

	CharSequence program_;

	int pWord_;
	int pWordStart_;
	// last token parsed
	TokenType type_;
	String word_;
	double value_;

	// change program and evaluate it
	public double Evaluate(CharSequence program) {
		program_ = program;
		return Evaluate();
	}

	double Evaluate() {
		pWord_ = 0;
		type_ = TokenType.NONE;
		double v = CommaList(true);
		if (type_ != TokenType.END)
			throw new RuntimeException("Unexpected text at end of expression: " + pWordStart_);
		return v;
	}

	// expr1, expr2
	double CommaList(boolean get) {
		double left = Expression(get);
		while (true) {
			switch (type_) {
			case COMMA:
				left = Expression(true);
				break; // discard previous value
			default:
				return left;
			}
		}
	}

	// AND and OR
	double Expression(boolean get) {
		double left = Comparison(get);
		while (true) {
			switch (type_) {
			case AND: {
				double d = Comparison(true); // don't want short-circuit evaluation
				left = (left != 0.0) && (d != 0.0) ? 1 : 0;
			}
				break;
			case OR: {
				double d = Comparison(true); // don't want short-circuit evaluation
				left = (left != 0.0) || (d != 0.0) ? 1 : 0;
			}
				break;
			default:
				return left;
			}
		}
	}

	// LT, GT, LE, EQ etc.
	double Comparison(boolean get) {
		double left = AddSubtract(get);
		while (true) {
			switch (type_) {
			case LT:
				left = left < AddSubtract(true) ? 1.0 : 0.0;
				break;
			case GT:
				left = left > AddSubtract(true) ? 1.0 : 0.0;
				break;
			case LE:
				left = left <= AddSubtract(true) ? 1.0 : 0.0;
				break;
			case GE:
				left = left >= AddSubtract(true) ? 1.0 : 0.0;
				break;
			case EQ:
				left = left == AddSubtract(true) ? 1.0 : 0.0;
				break;
			case NE:
				left = left != AddSubtract(true) ? 1.0 : 0.0;
				break;
			default:
				return left;
			}
		}
	}

	// add and subtract
	double AddSubtract(boolean get) {
		double left = Term(get);
		while (true) {
			switch (type_) {
			case PLUS:
				left += Term(true);
				break;
			case MINUS:
				left -= Term(true);
				break;
			default:
				return left;
			}
		}
	}

	// multiply and divide
	double Term(boolean get) {
		double left = Primary(get);
		while (true) {
			switch (type_) {
			case MULTIPLY:
				left *= Primary(true);
				break;
			case DIVIDE: {
				double d = Primary(true);
				if (d == 0.0)
					throw new RuntimeException("Divide by zero");
				left /= d;
				break;
			}
			default:
				return left;
			}
		}
	}

	// primary (base) tokens
	double Primary(boolean get) {
		if (get) {
			GetToken(false); // one-token lookahead
		}

		switch (type_) {
		case NUMBER: {
			double v = value_;
			GetToken(true); // get next one (one-token lookahead)
			return v;
		}

		case NAME: {
			String word = word_;
			GetToken(true);
			if (type_ == TokenType.LHPAREN) {
				// might be single-argument function (eg. abs (x) )
				OneArgFunction si = OneArgumentFunctions.get(word);
				if (si != null) {
					double v = Expression(true); // get argument
					CheckToken(TokenType.RHPAREN);
					GetToken(true); // get next one (one-token lookahead)
					return si.eval(v); // evaluate function
				}

				// might be double-argument function (eg. roll (6, 2) )
				TwoArgFunction di = TwoArgumentFunctions.get(word);
				if (di != null) {
					double v1 = Expression(true); // get argument 1 (not commalist)
					CheckToken(TokenType.COMMA);
					double v2 = Expression(true); // get argument 2 (not commalist)
					CheckToken(TokenType.RHPAREN);
					GetToken(true); // get next one (one-token lookahead)
					return di.eval(v1, v2); // evaluate function
				}

				// might be double-argument function (eg. roll (6, 2) )
				ThreeArgFunction ti = ThreeArgumentFunctions.get(word);
				if (ti != null) {
					double v1 = Expression(true); // get argument 1 (not commalist)
					CheckToken(TokenType.COMMA);
					double v2 = Expression(true); // get argument 2 (not commalist)
					CheckToken(TokenType.COMMA);
					double v3 = Expression(true); // get argument 3 (not commalist)
					CheckToken(TokenType.RHPAREN);
					GetToken(true); // get next one (one-token lookahead)
					return ti.eval(v1, v2, v3); // evaluate function
				}

				throw new RuntimeException("Function '" + word + "' not implemented.");
			}

			// not a function? must be a symbol in the symbol table
			double v = symbols_.get(word); // get REFERENCE to symbol table entry
			// change table entry with expression? (eg. a = 22, or a = 22)
			switch (type_) {
			// maybe check for NaN or Inf here (see: isinf, isnan functions)
			case ASSIGN:
				v = Expression(true);
				break;
			case ASSIGN_ADD:
				v += Expression(true);
				break;
			case ASSIGN_SUB:
				v -= Expression(true);
				break;
			case ASSIGN_MUL:
				v *= Expression(true);
				break;
			case ASSIGN_DIV: {
				double d = Expression(true);
				if (d == 0.0)
					throw new RuntimeException("Divide by zero");
				v /= d;
				break; // change table entry with expression
			} // end of ASSIGN_DIV
			default:
				break; // do nothing for others
			} // end of switch on type_
			return v; // and return new value
		}

		case MINUS: // unary minus
			return -Primary(true);

		case NOT: // unary not
			return (Primary(true) == 0.0) ? 1.0 : 0.0;

		case LHPAREN: {
			double v = CommaList(true); // inside parens, you could have commas
			CheckToken(TokenType.RHPAREN);
			GetToken(true); // eat the )
			return v;
		}

		default:
			throw new RuntimeException("Unexpected token: " + word_);
		}
	}

	TokenType GetToken(boolean ignoreSign) {
		word_ = null;

		// skip spaces
		while (pWord_ > -1 && Character.isWhitespace(programChar(pWord_))) {
			++pWord_;
		}

		pWordStart_ = pWord_; // remember where word_ starts *now*

		// look out for unterminated statements and things
		// we have EOF after already detecting it
		if (pWord_ == 0 && type_ == TokenType.END) {
			throw new RuntimeException("Unexpected end of expression.");
		}

		char cFirstCharacter = programChar(pWord_); // first character in new word_

		if (cFirstCharacter == 0) // stop at end of file
		{
			word_ = "<end of expression>";
			return type_ = TokenType.END;
		}

		char cNextCharacter = programChar(pWord_ + 1); // 2nd character in new word_

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
				pWord_++;
			}

			char c = programChar(pWord_);
			while (Character.isDigit(c) || c == '.') {
				pWord_++;
				c = programChar(pWord_);
			}

			// allow for 1.53158e+15
			c = programChar(pWord_);
			if (c == 'e' || c == 'E') {
				pWord_++; // skip 'e'
				c = programChar(pWord_);
				if ((c == '+' || c == '-')) {
					pWord_++; // skip sign after e
				}

				// now digits after e
				while (Character.isDigit(programChar(pWord_)))
					pWord_++;
			}

			word_ = program_.subSequence(pWordStart_, pWordStart_ + pWord_ - pWordStart_).toString();
			value_ = Double.valueOf(word_);

			return type_ = TokenType.NUMBER;
		} // end of number found

		// special test for 2-character sequences: <= >= == !=
		// also +=, -=, /=, *=
		if (cNextCharacter == '=') {
			switch (cFirstCharacter) {
			// comparisons
			case '=':
				type_ = TokenType.EQ;
				break;
			case '<':
				type_ = TokenType.LE;
				break;
			case '>':
				type_ = TokenType.GE;
				break;
			case '!':
				type_ = TokenType.NE;
				break;
			// assignments
			case '+':
				type_ = TokenType.ASSIGN_ADD;
				break;
			case '-':
				type_ = TokenType.ASSIGN_SUB;
				break;
			case '*':
				type_ = TokenType.ASSIGN_MUL;
				break;
			case '/':
				type_ = TokenType.ASSIGN_DIV;
				break;
			// none of the above
			default:
				type_ = TokenType.NONE;
				break;
			} // end of switch on cFirstCharacter

			if (type_ != TokenType.NONE) {
				word_ = program_.subSequence(pWordStart_, pWordStart_ + 2).toString();
				pWord_ += 2; // skip both characters
				return type_;
			} // end of found one    
		} // end of *=

		switch (cFirstCharacter) {
		case '&':
			if (cNextCharacter == '&') // &&
			{
				word_ = program_.subSequence(pWordStart_, pWordStart_ + 2).toString();
				pWord_ += 2; // skip both characters
				return type_ = TokenType.AND;
			}
			break;
		case '|':
			if (cNextCharacter == '|') // ||
			{
				word_ = program_.subSequence(pWordStart_, pWordStart_ + 2).toString();
				pWord_ += 2; // skip both characters
				return type_ = TokenType.OR;
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
			word_ = program_.subSequence(pWordStart_, pWordStart_ + 1).toString();
			++pWord_; // skip it
			return type_ = TokenType.get(cFirstCharacter);
		} // end of switch on cFirstCharacter

		if (!Character.isLetter(cFirstCharacter)) {
			throw new RuntimeException("Unexpected character: " + cFirstCharacter);
		}

		// we have a word (starting with A-Z) - pull it out
		char c = programChar(pWord_);
		while (Character.isLetterOrDigit(c) || c == '_') {
			++pWord_;
		}

		word_ = program_.subSequence(pWordStart_, pWordStart_ + pWord_ - pWordStart_).toString();
		return type_ = TokenType.NAME;
	}

	private char programChar(int index) {
		return program_.length() > index ? program_.charAt(index) : 0;
	}

	void CheckToken(TokenType wanted) {
		if (type_ != wanted) {
			throw new RuntimeException("'" + wanted + "' expected.");
		}
	}

	public static void main(String[] args) {
		System.out.println(new ExpressionParser().Evaluate("(2 + 2) * 3"));
	}
}
