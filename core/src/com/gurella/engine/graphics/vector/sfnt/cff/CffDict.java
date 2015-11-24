package com.gurella.engine.graphics.vector.sfnt.cff;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;

abstract class CffDict {
	static final String[] dictLookup = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", ".", "E", "E-", null, "-"};
	
	private StringBuilder tempStringBuilder = new StringBuilder();
	IntMap<CffDictEntry> entries = new IntMap<CffDictEntry>();
	
	//TODO should read from RandomAccessFile
	public CffDict(byte[] valueData) {
		Array<Number> operands = new Array<Number>();
		
		for (int i = 0; i < valueData.length; i++) {
			int op = valueData[i] & 0xff;
			
			// The first byte for each dict item distinguishes between operator (key) and operand (value).
	        // Values <= 21 are operators.
			if (op <= 21) {
	            // Two-byte operators have an initial escape byte of 12.
	            if (op == 12) {
	                op = 1200 + (valueData[++i] & 0xff);
	            }
	            entries.put(op, new CffDictEntry(op, operands));
	            operands = new Array<Number>();
	        } else {
	            // Since the operands (values) come before the operators (keys), we store all operands in a list
	            // until we encounter an operator.
	        	int b1, b2, b3, b4;
			    if (op == 28) {
			        b1 = valueData[++i] & 0xff;
			        b2 = valueData[++i] & 0xff;
			        operands.add(b1 << 8 | b2);
			    } else if (op == 29) {
			        b1 = valueData[++i] & 0xff;
			        b2 = valueData[++i] & 0xff;
			        b3 = valueData[++i] & 0xff;
			        b4 = valueData[++i] & 0xff;
			        operands.add(b1 << 24 | b2 << 16 | b3 << 8 | b4);
			    } else if (op == 30) {
			    	i += parseFloatOperand(operands, valueData, i);
			    } else if (op >= 32 && op <= 246) {
			    	operands.add(op - 139);
			    } else if (op >= 247 && op <= 250) {
			        b1 = valueData[++i] & 0xff;
			        operands.add((op - 247) * 256 + b1 + 108);
			    } else if (op >= 251 && op <= 254) {
			        b1 = valueData[++i] & 0xff;
			        operands.add(-(op - 251) * 256 - b1 - 108);
			    } else {
			    	throw new IllegalArgumentException("Invalid b0 " + op);
			    }
	        }
		}
		
		resolveValues();
	}
	
	
	int parseFloatOperand(Array<Number> operands, byte[] valueData, int i) {
		tempStringBuilder.delete(0, tempStringBuilder.length());
		int eof = 15;
	    int count = 0;
	    
	    while (true) {
	        int b = valueData[++count] & 0xff;
	        int n1 = b >> 4;
			int n2 = b & 15;

	        if (n1 == eof) {
	            break;
	        }
	        tempStringBuilder.append(dictLookup[n1]);

	        if (n2 == eof) {
	            break;
	        }
	        tempStringBuilder.append(dictLookup[n2]);
	    }
	    
	    operands.add(Float.valueOf(tempStringBuilder.toString()));
	    return count;
	}
	
	void resolveValues() {
		for(CffDictEntryType cffDictEntryType : getDictEntryTypes()) {
			int operator = cffDictEntryType.getId();
			CffDictEntry dictEntry = entries.get(operator);
			if(dictEntry == null) {
				dictEntry = new CffDictEntry(operator, cffDictEntryType.getDefaultValue());
				entries.put(operator, dictEntry);
			}
		}
	}
	
	abstract CffDictEntryType[] getDictEntryTypes();
	
	public CffDictEntry entry(int operator) {
		return entries.get(operator);
	}

	public Float floatValue(int operator) {
		return entries.get(operator).getFloat();
	}

	public Integer integerValue(int operator) {
		return entries.get(operator).getInteger();
	}
	
	public Integer[] rangeValue(int operator) {
		return entries.get(operator).getRange();
	}

	public Number numberValue(int operator) {
		return entries.get(operator).getNumber();
	}

	public float[] floatArrayValue(int operator) {
		return entries.get(operator).getFloatArray();
	}

	public float[] deltaValue(int operator) {
		return entries.get(operator).getDelta();
	}

	public Integer sidValue(int operator) {
		return entries.get(operator).getSID();
	}

	public Boolean booleanValue(int operator) {
		return entries.get(operator).getBoolean();
	}
	
	interface CffDictEntryType {
		int getId();
		
		Number[] getDefaultValue();
	}
}
