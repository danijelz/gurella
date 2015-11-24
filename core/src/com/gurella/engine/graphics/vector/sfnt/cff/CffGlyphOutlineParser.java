package com.gurella.engine.graphics.vector.sfnt.cff;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.gurella.engine.graphics.vector.Path;

public class CffGlyphOutlineParser {
	private int defaultWidthX;
	private int nominalWidthX;
	private Array<byte[]> charStrings;
	private int globalBias;
	private Array<byte[]> globalSubrutines; 
	private int privateBias;
	private Array<byte[]> privateSubrutines; 
	
	private float width;
	private Path p;
	private IntArray stack = new IntArray();
	private int nStems;
	private boolean haveWidth;
	private boolean open;
	private int x, y, c1x, c1y, c2x, c2y;
	
	CffGlyphOutlineParser(CffTable cffTable) {
		CffPrivateDict privateDict = cffTable.privateDict;
		defaultWidthX = privateDict.getDefaultWidthX().intValue();
		nominalWidthX = privateDict.getNominalWidthX().intValue();
		
		charStrings = cffTable.charStringsIndexTable.values;
		
		CffSubrIndexSubTable globalSubrIndexTable = cffTable.globalSubrIndexTable;
		globalBias = globalSubrIndexTable.bias;
		globalSubrutines = cffTable.globalSubrIndexTable.values;
		
		CffSubrIndexSubTable privateSubrIndexTable = cffTable.privateSubrIndexTable;
		privateBias = privateSubrIndexTable.bias;
		privateSubrutines = cffTable.privateSubrIndexTable.values;
	}
	
	Path createOutline(int glyphId) {
		p = new Path();
		width = 0;
		stack.clear();
		nStems = 0;
		haveWidth = false;
		open = false;
		x = 0; 
		y = 0; 
		c1x = 0; 
		c1y = 0; 
		c2x = 0; 
		c2y = 0; 
		byte[] code = charStrings.get(glyphId);
		parse(code);
		return p;
	}
	
	private void parse(byte[] code) {
		int b1, b2, b3, b4;
		int codeIndex;
        byte[] subrCode;
        
        int i = 0;
        
        while (i < code.length) {
            int v = code[i] & 0xff;
            i += 1;
            switch (v) {
            case 1: // hstem
                parseStems();
                break;
            case 3: // vstem
                parseStems();
                break;
            case 4: // vmoveto
                if (stack.size > 1 && !haveWidth) {
                    width = stack.removeIndex(0) + nominalWidthX;
                    haveWidth = true;
                }
                y += stack.pop();
                newContour(x, -y);
                break;
            case 5: // rlineto
                while (stack.size > 0) {
                    x += stack.removeIndex(0);
                    y += stack.removeIndex(0);
                    p.lineTo(x, -y);
                }
                break;
            case 6: // hlineto
                while (stack.size > 0) {
                    x += stack.removeIndex(0);
                    p.lineTo(x, -y);
                    if (stack.size == 0) {
                        break;
                    }
                    y += stack.removeIndex(0);
                    p.lineTo(x, -y);
                }
                break;
            case 7: // vlineto
                while (stack.size > 0) {
                    y += stack.removeIndex(0);
                    p.lineTo(x, -y);
                    if (stack.size == 0) {
                        break;
                    }
                    x += stack.removeIndex(0);
                    p.lineTo(x, -y);
                }
                break;
            case 8: // rrcurveto
                while (stack.size > 0) {
                    c1x = x + stack.removeIndex(0);
                    c1y = y + stack.removeIndex(0);
                    c2x = c1x + stack.removeIndex(0);
                    c2y = c1y + stack.removeIndex(0);
                    x = c2x + stack.removeIndex(0);
                    y = c2y + stack.removeIndex(0);
                    p.cubicTo(c1x, -c1y, c2x, -c2y, x, -y);
                }
                break;
            case 10: // callsubr
                codeIndex = stack.pop() + privateBias;
                subrCode = privateSubrutines.get(codeIndex);
                if (subrCode != null) {
                    parse(subrCode);
                }
                break;
            case 11: // return
                return;
            case 12: // escape
                v = code[i];
                i += 1;
                break;
            case 14: // endchar
                if (stack.size > 0 && !haveWidth) {
                    width = stack.removeIndex(0) + nominalWidthX;
                    haveWidth = true;
                }
                if (open) {
                    p.close();
                    open = false;
                }
                break;
            case 18: // hstemhm
                parseStems();
                break;
            case 19: // hintmask
            case 20: // cntrmask
                parseStems();
                i += (nStems + 7) >> 3;
                break;
            case 21: // rmoveto
                if (stack.size > 2 && !haveWidth) {
                    width = stack.removeIndex(0) + nominalWidthX;
                    haveWidth = true;
                }
                y += stack.pop();
                x += stack.pop();
                newContour(x, -y);
                break;
            case 22: // hmoveto
                if (stack.size > 1 && !haveWidth) {
                    width = stack.removeIndex(0) + nominalWidthX;
                    haveWidth = true;
                }
                x += stack.pop();
                newContour(x, -y);
                break;
            case 23: // vstemhm
                parseStems();
                break;
            case 24: // rcurveline
                while (stack.size > 2) {
                    c1x = x + stack.removeIndex(0);
                    c1y = y + stack.removeIndex(0);
                    c2x = c1x + stack.removeIndex(0);
                    c2y = c1y + stack.removeIndex(0);
                    x = c2x + stack.removeIndex(0);
                    y = c2y + stack.removeIndex(0);
                    p.cubicTo(c1x, -c1y, c2x, -c2y, x, -y);
                }
                x += stack.removeIndex(0);
                y += stack.removeIndex(0);
                p.lineTo(x, -y);
                break;
            case 25: // rlinecurve
                while (stack.size > 6) {
                    x += stack.removeIndex(0);
                    y += stack.removeIndex(0);
                    p.lineTo(x, -y);
                }
                c1x = x + stack.removeIndex(0);
                c1y = y + stack.removeIndex(0);
                c2x = c1x + stack.removeIndex(0);
                c2y = c1y + stack.removeIndex(0);
                x = c2x + stack.removeIndex(0);
                y = c2y + stack.removeIndex(0);
                p.cubicTo(c1x, -c1y, c2x, -c2y, x, -y);
                break;
            case 26: // vvcurveto
                if (stack.size % 2 != 0) {
                    x += stack.removeIndex(0);
                }
                while (stack.size > 0) {
                    c1x = x;
                    c1y = y + stack.removeIndex(0);
                    c2x = c1x + stack.removeIndex(0);
                    c2y = c1y + stack.removeIndex(0);
                    x = c2x;
                    y = c2y + stack.removeIndex(0);
                    p.cubicTo(c1x, -c1y, c2x, -c2y, x, -y);
                }
                break;
            case 27: // hhcurveto
                if (stack.size % 2 != 0) {
                    y += stack.removeIndex(0);
                }
                while (stack.size > 0) {
                    c1x = x + stack.removeIndex(0);
                    c1y = y;
                    c2x = c1x + stack.removeIndex(0);
                    c2y = c1y + stack.removeIndex(0);
                    x = c2x + stack.removeIndex(0);
                    y = c2y;
                    p.cubicTo(c1x, -c1y, c2x, -c2y, x, -y);
                }
                break;
            case 28: // shortint
                b1 = code[i] & 0xff;
                b2 = code[i + 1] & 0xff;
                stack.add(((b1 << 24) | (b2 << 16)) >> 16);
                i += 2;
                break;
            case 29: // callgsubr
                codeIndex = stack.pop() + globalBias;
                subrCode = globalSubrutines.get(codeIndex);
                if (subrCode != null) {
                    parse(subrCode);
                }
                break;
            case 30: // vhcurveto
                while (stack.size > 0) {
                    c1x = x;
                    c1y = y + stack.removeIndex(0);
                    c2x = c1x + stack.removeIndex(0);
                    c2y = c1y + stack.removeIndex(0);
                    x = c2x + stack.removeIndex(0);
                    y = c2y + (stack.size == 1 ? stack.removeIndex(0) : 0);
                    p.cubicTo(c1x, -c1y, c2x, -c2y, x, -y);
                    if (stack.size == 0) {
                        break;
                    }
                    c1x = x + stack.removeIndex(0);
                    c1y = y;
                    c2x = c1x + stack.removeIndex(0);
                    c2y = c1y + stack.removeIndex(0);
                    y = c2y + stack.removeIndex(0);
                    x = c2x + (stack.size == 1 ? stack.removeIndex(0) : 0);
                    p.cubicTo(c1x, -c1y, c2x, -c2y, x, -y);
                }
                break;
            case 31: // hvcurveto
                while (stack.size > 0) {
                    c1x = x + stack.removeIndex(0);
                    c1y = y;
                    c2x = c1x + stack.removeIndex(0);
                    c2y = c1y + stack.removeIndex(0);
                    y = c2y + stack.removeIndex(0);
                    x = c2x + (stack.size == 1 ? stack.removeIndex(0) : 0);
                    p.cubicTo(c1x, -c1y, c2x, -c2y, x, -y);
                    if (stack.size == 0) {
                        break;
                    }
                    c1x = x;
                    c1y = y + stack.removeIndex(0);
                    c2x = c1x + stack.removeIndex(0);
                    c2y = c1y + stack.removeIndex(0);
                    x = c2x + stack.removeIndex(0);
                    y = c2y + (stack.size == 1 ? stack.removeIndex(0) : 0);
                    p.cubicTo(c1x, -c1y, c2x, -c2y, x, -y);
                }
                break;
            default:
                if (v < 32) {
                    Gdx.app.log(CffGlyphOutlineParser.class.getName(), "Unknown operator " + v);
                } else if (v < 247) {
                    stack.add(v - 139);
                } else if (v < 251) {
                    b1 = code[i] & 0xff;
                    i += 1;
                    stack.add((v - 247) * 256 + b1 + 108);
                } else if (v < 255) {
                    b1 = code[i] & 0xff;
                    i += 1;
                    stack.add(-(v - 251) * 256 - b1 - 108);
                } else {
                    b1 = code[i] & 0xff;
                    b2 = code[i + 1] & 0xff;
                    b3 = code[i + 2] & 0xff;
                    b4 = code[i + 3] & 0xff;
                    i += 4;
                    stack.add(((b1 << 24) | (b2 << 16) | (b3 << 8) | b4) / 65536);
                }
            }
        }
    }
	
	private void parseStems() {
        // The number of stem operators on the stack is always even.
        // If the value is uneven, that means a width is specified.
		boolean hasWidthArg = stack.size % 2 != 0;
        if (hasWidthArg && !haveWidth) {
            width = stack.removeIndex(0) + nominalWidthX;
        }
        nStems += stack.size >> 1;
        stack.clear();
        haveWidth = true;
    }
	
	private void newContour(float x, float y) {
        if (open) {
            p.close();
        }

        p.moveTo(x, y);
        open = true;
    }
}
