package com.gurella.engine.graphics.render.shader.parser;

import java.util.HashMap;

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

	TokenType GetToken (boolean ignoreSign)
	  {
	  /*word_.erase (0, std::string::npos);
	  
	  // skip spaces
	  while (*pWord_ && isspace (*pWord_))
	    ++pWord_;

	  pWordStart_ = pWord_;   // remember where word_ starts *now*
	  
	  // look out for unterminated statements and things
	  if (*pWord_ == 0 &&  // we have EOF
	      type_ == END)  // after already detecting it
	    throw std::runtime_error ("Unexpected end of expression.");

	  unsigned char cFirstCharacter = *pWord_;        // first character in new word_
	  
	  if (cFirstCharacter == 0)    // stop at end of file
	    {
	    word_ = "<end of expression>";
	    return type_ = END;
	    }
	  
	  unsigned char cNextCharacter  = *(pWord_ + 1);  // 2nd character in new word_
	  
	  // look for number
	  // can be: + or - followed by a decimal point
	  // or: + or - followed by a digit
	  // or: starting with a digit
	  // or: decimal point followed by a digit
	  if ((!ignoreSign &&
		   (cFirstCharacter == '+' || cFirstCharacter == '-') &&
		   (isdigit (cNextCharacter) || cNextCharacter == '.')
		   )
		  || isdigit (cFirstCharacter)
		  // allow decimal numbers without a leading 0. e.g. ".5"
		  // Dennis Jones 01-30-2009
		  || (cFirstCharacter == '.' && isdigit (cNextCharacter)) )
		  {
	    // skip sign for now
	    if ((cFirstCharacter == '+' || cFirstCharacter == '-'))
	      pWord_++;
	    while (isdigit (*pWord_) || *pWord_ == '.')
	      pWord_++;
	    
	    // allow for 1.53158e+15
	    if (*pWord_ == 'e' || *pWord_ == 'E')
	      {
	      pWord_++; // skip 'e'
	      if ((*pWord_  == '+' || *pWord_  == '-'))
	        pWord_++; // skip sign after e
	      while (isdigit (*pWord_))  // now digits after e
	        pWord_++;      
	      }
	    
	    word_ = std::string (pWordStart_, pWord_ - pWordStart_);
	    
	    std::istringstream is (word_);
	    // parse std::string into double value
	    is >> value_;
	      
	    if (is.fail () && !is.eof ())
	      throw std::runtime_error ("Bad numeric literal: " + word_);
	    return type_ = NUMBER;
	    }   // end of number found

	  // special test for 2-character sequences: <= >= == !=
	  // also +=, -=, /=, *=
	  if (cNextCharacter == '=')
	    {
	    switch (cFirstCharacter)
	      {
	      // comparisons
	      case '=': type_ = EQ;   break;
	      case '<': type_ = LE;   break;
	      case '>': type_ = GE;   break;
	      case '!': type_ = NE;   break;
	      // assignments
	      case '+': type_ = ASSIGN_ADD;   break;
	      case '-': type_ = ASSIGN_SUB;   break;
	      case '*': type_ = ASSIGN_MUL;   break;
	      case '/': type_ = ASSIGN_DIV;   break;
	      // none of the above
	      default:  type_ = NONE; break;
	      } // end of switch on cFirstCharacter
	    
	    if (type_ != NONE)
	      {
	      word_ = std::string (pWordStart_, 2);
	      pWord_ += 2;   // skip both characters
	      return type_;
	      } // end of found one    
	    } // end of *=
	  
	  switch (cFirstCharacter)
	    {
	    case '&': if (cNextCharacter == '&')    // &&
	                {
	                word_ = std::string (pWordStart_, 2);
	                pWord_ += 2;   // skip both characters
	                return type_ = AND;
	                }
	              break;
	   case '|': if (cNextCharacter == '|')   // ||
	                {
	                word_ = std::string (pWordStart_, 2);
	                pWord_ += 2;   // skip both characters
	                return type_ = OR;
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
	      word_ = std::string (pWordStart_, 1);
	      ++pWord_;   // skip it
	      return type_ = TokenType (cFirstCharacter);
	    } // end of switch on cFirstCharacter
	  
	  if (!isalpha (cFirstCharacter))
	    {
	    if (cFirstCharacter < ' ')
	      {
	      std::ostringstream s;
	      s << "Unexpected character (decimal " << int (cFirstCharacter) << ")";
	      throw std::runtime_error (s.str ());    
	      }
	    else
	      throw std::runtime_error ("Unexpected character: " + std::string (1, cFirstCharacter));
	    }
	  
	  // we have a word (starting with A-Z) - pull it out
	  while (isalnum (*pWord_) || *pWord_ == '_')
	    ++pWord_;
	  
	  word_ = std::string (pWordStart_, pWord_ - pWordStart_);
	  return type_ = NAME;*/
		return null;
	  }

	void CheckToken(TokenType wanted) {
		if (type_ != wanted) {
			throw new RuntimeException("'" + wanted + "' expected.");
		}
	}
}
