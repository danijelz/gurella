package com.gurella.engine.graphics.vector.sfnt.truetype.glyf;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.ByteArray;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.ShortArray;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.graphics.vector.Path;

class GlyfOtline implements Poolable {
	final IntArray endPointOfContours = new IntArray();
	final ByteArray flags = new ByteArray();
	final ShortArray xCoordinates = new ShortArray();
	final ShortArray yCoordinates = new ShortArray();
	
	static GlyfOtline obtain() {
		return Pools.obtain(GlyfOtline.class);
	}
	
	private int getContourCount() {
		return endPointOfContours.size;
	}
	
	private int getEndPointOfContour(int contourIndex) {
		return endPointOfContours.get(contourIndex);
	}

	private boolean isOnCurve(int pointIndex) {
		return (flags.get(pointIndex) & GlyfTableConstants.onCurve) != 0;
	}
	
	void merge(GlyfOtline other) {
		endPointOfContours.addAll(other.endPointOfContours);
		flags.addAll(other.flags);
		xCoordinates.addAll(other.xCoordinates);
		yCoordinates.addAll(other.yCoordinates);
	}
	
	Path createOutlinePath() {
		//embolden3(500f, 1);
		
		Path path = Path.obtain();
		int contourStart = 0;
		int contourEnd;
		
		for (int i = 0; i < getContourCount(); i++) {
			contourEnd = getEndPointOfContour(i);
			if (contourEnd - contourStart > 0) {
				appendContour(path, contourStart, contourEnd - contourStart + 1);
			}
			contourStart = contourEnd + 1;
        }
		
		if(getOrienatation() == Orientation.TRUETYPE) {
			path.reverse();
		}
		
		Path unmodifiable = path.unmodifiable();
		path.free();
		return unmodifiable;
	}
	
	private void appendContour(Path path, int startIndex, int count) {
        int offset = 0;

        while (offset < count) {
        	int point0Index = startIndex + offset % count;
        	int point0x = xCoordinates.get(point0Index);
        	int point0y = yCoordinates.get(point0Index);
        	boolean point0OnCurve = isOnCurve(point0Index);
        	
        	int point1Index = startIndex + (offset + 1) % count;
        	int point1x = xCoordinates.get(point1Index);
        	int point1y = yCoordinates.get(point1Index);
        	boolean point1OnCurve = isOnCurve(point1Index);
        	
        	int point2Index = startIndex + (offset + 2) % count;
        	int point2x = xCoordinates.get(point2Index);
        	int point2y = yCoordinates.get(point2Index);
        	boolean point2OnCurve = isOnCurve(point2Index);

            if (offset == 0) {
            	path.moveTo(point0x, point0y);
            }

            if (point0OnCurve && point1OnCurve) {
            	if (point1x == point0x) { 
            		// This is a vertical line
            		path.verticalLineTo(point1y);
				} else if (point1y == point0y) { 
					// This is a horizontal
					path.horizontalLineTo(point1x);
				} else {
					path.lineTo(point1x, point1y);
				}
				offset++;
            } else if (point0OnCurve && !point1OnCurve && point2OnCurve) {
            	// This is a curve with no implied points
            	path.quadTo(point1x, point1y, point2x, point2y);
				offset += 2;
            } else if (point0OnCurve && !point1OnCurve && !point2OnCurve) {
            	// This is a curve with one implied point
            	path.quadTo(point1x, point1y, midValue(point1x, point2x), midValue(point1y, point2y));
				offset += 2;
            } else if (!point0OnCurve && !point1OnCurve) {
            	// This is a curve with two implied points
            	path.quadSmoothTo(midValue(point0x, point1x), midValue(point0y, point1y));
				offset++;
            } else if (!point0OnCurve && point1OnCurve) {
            	path.quadSmoothTo(point1x, point1y);
				offset++;
            } else {
            	Gdx.app.debug("GlyfTable", "Invalid contour state.");
                break;
            }
        }
        
        path.close();
	}
    
    private static float midValue(float a, float b) {
        return a + (b - a) / 2.0f;
    }
    
    ///////////////////////////////// freetype
    
	public BoundingBox getControlBounds(BoundingBox out) {
		if (flags.size == 0) {
			out.min.setZero();
			out.max.setZero();
		} else {
			for (int i = 0; i < flags.size; i++) {
				out.ext(xCoordinates.get(i), yCoordinates.get(i), 0);
			}
		}

		return out;
	}
	
	public void embolden3(float xstrength, float ystrength) {
		int v_prev, v_first, v_next, v_cur;
		int c, n, first;

		xstrength /= 2;
		ystrength /= 2;
		if (xstrength == 0 && ystrength == 0)
			return;

		Orientation orientation = getOrienatation();
		if (orientation == Orientation.NONE) {
			return;
		}

		first = 0;
		for (c = 0; c < endPointOfContours.size; c++) {
			float inx, iny, outx, outy, shiftx, shifty;
			float l_in, l_out, l, q, d;
			int last = endPointOfContours.get(c);

			v_first = first;
			v_prev = last;
			v_cur = v_first;

			/* compute incoming normalized vector */
			inx = xCoordinates.get(v_cur) - xCoordinates.get(v_prev);
			iny = -yCoordinates.get(v_cur) + yCoordinates.get(v_prev);
			l_in = (float) Math.sqrt(inx * inx + iny * iny);
			if (l_in != 0) {
				inx = (inx/l_in);
				iny = (iny/l_in);
			}

			for (n = first; n <= last; n++) {
				if (n < last)
					v_next = n + 1;
				else
					v_next = v_first;

				/* compute outgoing normalized vector */
				outx = xCoordinates.get(v_next) - xCoordinates.get(v_cur);
				outy = -yCoordinates.get(v_next) + yCoordinates.get(v_cur);
				l_out = (float) Math.sqrt(outx * outx + outy * outy);
				if (l_out != 0) {
					outx = (outx/l_out);
					outy = (outy/l_out);
				}

				d = inx * outx + iny * outy;

				/* shift only if turn is less than ~160 degrees */
				if (d > -0xF000L) {
					d = d + 15f;

					/* shift components are aligned along lateral bisector */
					/* and directed according to the outline orientation. */
					shiftx = iny + outy;
					shifty = inx + outx;

					if (orientation == Orientation.TRUETYPE) {
						shiftx = -shiftx;
					} else {
						shifty = -shifty;
					}

					/*
					 * restrict shift magnitude to better handle collapsing
					 * segments
					 */
					q = outx * iny - outy * inx;
					if (orientation == Orientation.TRUETYPE) {
						q = -q;
					}

					l = Math.min(l_in, l_out);

					/*
					 * non-strict inequalities avoid divide-by-zero when q == l
					 * == 0
					 */
					if (xstrength * q <=  d * l) {
						shiftx = ((shiftx * xstrength) / d);
					} else {
						shiftx = ((shiftx * l) / q);
					}

					if (ystrength * q <=  d * l) {
						shifty = ((shifty * ystrength) / d);
					} else {
						shifty = ((shifty * l) / q);
					}
				} else {
					shiftx = shifty = 0;
				}

				xCoordinates.set(n, (short) (xCoordinates.get(v_cur) + xstrength + shiftx));
				yCoordinates.set(n, (short) (yCoordinates.get(v_cur) + ystrength + shifty));

				inx = outx;
				iny = outy;
				l_in = l_out;
				v_cur = v_next;
			}

			first = last + 1;
		}
	}
    
	public void embolden(float xstrength, float ystrength) {
		int v_prev, v_first, v_next, v_cur;
		int c, n, first;

		xstrength /= 2;
		ystrength /= 2;
		if (xstrength == 0 && ystrength == 0)
			return;

		Orientation orientation = getOrienatation();
		if (orientation == Orientation.NONE) {
			return;
		}

		first = 0;
		for (c = 0; c < endPointOfContours.size; c++) {
			int inx, iny, outx, outy, shiftx, shifty;
			float l_in, l_out, l, q, d;
			int last = endPointOfContours.get(c);

			v_first = first;
			v_prev = last;
			v_cur = v_first;

			/* compute incoming normalized vector */
			inx = xCoordinates.get(v_cur) - xCoordinates.get(v_prev);
			iny = yCoordinates.get(v_cur) - yCoordinates.get(v_prev);
			l_in = (float) Math.sqrt(inx * inx + iny * iny);
			if (l_in != 0) {
				inx = (int) (inx/l_in);
				iny = (int) (iny/l_in);
			}

			for (n = first; n <= last; n++) {
				if (n < last)
					v_next = n + 1;
				else
					v_next = v_first;

				/* compute outgoing normalized vector */
				outx = xCoordinates.get(v_next) - xCoordinates.get(v_cur);
				outy = yCoordinates.get(v_next) - yCoordinates.get(v_cur);
				l_out = (float) Math.sqrt(outx * outx + outy * outy);
				if (l_out != 0) {
					outx = (int) (outx/l_out);
					outy = (int) (outy/l_out);
				}

				d = inx * outx + iny * outy;

				/* shift only if turn is less than ~160 degrees */
				if (d > -0xF000L) {
					d = d + 0x10000L;

					/* shift components are aligned along lateral bisector */
					/* and directed according to the outline orientation. */
					shiftx = iny + outy;
					shifty = inx + outx;

					if (orientation == Orientation.TRUETYPE) {
						shiftx = -shiftx;
					} else {
						shifty = -shifty;
					}

					/*
					 * restrict shift magnitude to better handle collapsing segments
					 */
					q = outx * iny - outy * inx;
					if (orientation == Orientation.TRUETYPE) {
						q = -q;
					}

					l = Math.min(l_in, l_out);

					/*
					 * non-strict inequalities avoid divide-by-zero when q == l == 0
					 */
					if (xstrength * q <=  d * l) {
						shiftx = (int) ((shiftx * xstrength) / d);
					} else {
						shiftx = (int) ((shiftx * l) / q);
					}

					if (ystrength * q <=  d * l) {
						shifty = (int) ((shifty * ystrength) / d);
					} else {
						shifty = (int) ((shifty * l) / q);
					}
				} else {
					shiftx = shifty = 0;
				}

				xCoordinates.set(n, (short) (xCoordinates.get(v_cur) + xstrength + shiftx));
				yCoordinates.set(n, (short) (yCoordinates.get(v_cur) + ystrength + shifty));

				inx = outx;
				iny = outy;
				l_in = l_out;
				v_cur = v_next;
			}

			first = last + 1;
		}
	}
	
	public void embolden2(float xstrength, float ystrength) {
		int v_prev, v_first, v_next, v_cur;
		int c, n, first;
		Orientation orientation;

		xstrength /= 2;
		ystrength /= 2;
		if (xstrength == 0 && ystrength == 0)
			return;

		orientation = getOrienatation();
		if (orientation == Orientation.NONE) {
			return;
		}

		first = 0;
		for (c = 0; c < endPointOfContours.size; c++) {
			int inx, iny, outx, outy, shiftx, shifty;
			float l_in, l_out, l, q, d;
			int last = endPointOfContours.get(c);

			v_first = first;
			v_prev = last;
			v_cur = v_first;

			 //compute incoming normalized vector 
			inx = xCoordinates.get(v_cur) - xCoordinates.get(v_prev);
			iny = yCoordinates.get(v_cur) - yCoordinates.get(v_prev);
			l_in = (float) Math.sqrt(inx * inx + iny * iny);
			if (l_in != 0) {
				inx = FT_DivFix(inx, l_in);
				iny = FT_DivFix(iny, l_in);
			}

			for (n = first; n <= last; n++) {
				if (n < last)
					v_next = n + 1;
				else
					v_next = v_first;

				 //compute outgoing normalized vector 
				outx = xCoordinates.get(v_next) - xCoordinates.get(v_cur);
				outy = yCoordinates.get(v_next) - yCoordinates.get(v_cur);
				l_out = (float) Math.sqrt(outx * outx + outy * outy);
				if (l_out != 0) {
					outx = FT_DivFix(outx, l_out);
					outy = FT_DivFix(outy, l_out);
				}

				d = FT_MulFix(inx, outx) + FT_MulFix(iny, outy);

				 //shift only if turn is less than ~160 degrees 
				if (d > -0xF000L) {
					d = d + 0x10000L;

					 //shift components are aligned along lateral bisector 
					 //and directed according to the outline orientation. 
					shiftx = iny + outy;
					shifty = inx + outx;

					if (orientation == Orientation.TRUETYPE)
						shiftx = -shiftx;
					else
						shifty = -shifty;

					
					 //* restrict shift magnitude to better handle collapsing
					 //* segments
					q = FT_MulFix(outx, iny) - FT_MulFix(outy, inx);
					if (orientation == Orientation.TRUETYPE)
						q = -q;

					l = Math.min(l_in, l_out);

					
					 //* non-strict inequalities avoid divide-by-zero when q == l
					 //* == 0
					if (FT_MulFix(xstrength, q) <= FT_MulFix(d, l))
						shiftx = FT_MulDiv(shiftx, xstrength, d);
					else
						shiftx = FT_MulDiv(shiftx, l, q);

					if (FT_MulFix(ystrength, q) <= FT_MulFix(d, l))
						shifty = FT_MulDiv(shifty, ystrength, d);
					else
						shifty = FT_MulDiv(shifty, l, q);
				} else
					shiftx = shifty = 0;

				xCoordinates.set(n, (short) (xCoordinates.get(v_cur) + xstrength + shiftx));
				yCoordinates.set(n, (short) (yCoordinates.get(v_cur) + ystrength + shifty));

				inx = outx;
				iny = outy;
				l_in = l_out;
				v_cur = v_next;
			}

			first = last + 1;
		}
	}
	
	private int FT_MulDiv(long a, float b, float c) {
		return (int) ((a*b)/c);
//		long  s;
//		  
//		  
//		  /* TODO: this function does not allow 64-bit arguments */
//		  if ( a == 0 || b == c )
//		  return (int) a;
		  
//		  s  = a; a = FT_ABS( a );
//		  s ^= b; b = FT_ABS( b );
//		  s ^= c; c = FT_ABS( c );
//		  
//		  if ( a <= 46340L && b <= 46340L && c <= 176095L && c > 0 )
//		  a = ( a * b + ( c >> 1 ) ) / c;
//		  
//		  else if ( (int)c > 0 )
//		  {
//		  long  temp, temp2;
//		  
//		  
//		  ft_multo64( (int)a, (int)b, temp );
//		  
//		  temp2.hi = 0;
//		  temp2.lo = (FT_UInt32)(c >> 1);
//		  FT_Add64( &temp, &temp2, &temp );
//		  a = ft_div64by32( temp.hi, temp.lo, (FT_Int32)c );
//		  }
//		  else
//		  a = 0x7FFFFFFFL;
//		  
//		  return ( s < 0 ? -a : a );
		// TODO Auto-generated method stub
	}

	private int FT_DivFix(float a, float b) {
//		int s;
//		long q;
//
//		s = 1;
//		if (a < 0) {
//			a = -a;
//			s = -1;
//		}
//		if (b < 0) {
//			b = -b;
//			s = -s;
//		}
//
//		if (b == 0) {
//			q = 0x7FFFFFFFL;
//		} else {
//			q = (long) ((((long) a << 16) + ((int) b >> 1)) / b);
//		}
//
//		return (int) (s < 0 ? -q : q);
		// TODO Auto-generated method stub
		return (int) ((a*0x10000)/b);
	}

	private float FT_MulFix(float a, float b) {

		/*int s = 1;
		long c;

		if (a < 0) {
			a = -a;
			s = -1;
		}

		if (b < 0) {
			b = -b;
			s = -s;
		}

		c = ((long) (a * b + 0x8000L)) >> 16;

		return (int) ((s > 0) ? c : -c);*/
		return (int) ((a*b)/0x10000);
	}

	private Orientation getOrienatation() {
		BoundingBox cbox = Pools.obtain(BoundingBox.class);
		getControlBounds(cbox);

		int xShift, yShift;
		float area = 0;

		xShift = FT_MSB(Math.abs((int) cbox.max.x) | Math.abs((int) cbox.min.x)) - 14;
		xShift = Math.max(xShift, 0);

		yShift = FT_MSB((int) (cbox.max.y - cbox.min.y)) - 14;
		yShift = Math.max(yShift, 0);

		int first = 0;
		for (int c = 0; c < endPointOfContours.size; c++) {
			int last = endPointOfContours.get(c);

			int prev = last;

			for (int cur = first; cur <= last; cur++) {
				area += ((yCoordinates.get(cur) - yCoordinates.get(prev)) >> yShift)
						* ((xCoordinates.get(cur) + xCoordinates.get(prev)) >> xShift);
				prev = cur;
			}

			first = last + 1;
		}

		if (area > 0) {
			return Orientation.TRUETYPE;
		} else if (area < 0) {
			return Orientation.POSTSCRIPT;
		} else {
			return Orientation.NONE;
		}
	}
    
    private static int FT_MSB(int x)  {
    	return 31 - __builtin_clz(x);
    }
    
    //http://blog.stephencleary.com/2010/10/implementing-gccs-builtin-functions.html
    //TODO test http://www.go4expert.com/articles/builtin-gcc-functions-builtinclz-t29238/
    private static int __builtin_clz(int x) {
      // This uses a binary search (counting down) algorithm from Hacker's Delight.
       int temp = x;
       int y;
       int n = 32;
       y = temp >> 16;  if (y != 0) {n = n -16;  temp = y;}
       y = temp >> 8;  if (y != 0) {n = n - 8;  temp = y;}
       y = temp >> 4;  if (y != 0) {n = n - 4;  temp = y;}
       y = temp >> 2;  if (y != 0) {n = n - 2;  temp = y;}
       y = temp >> 1;  if (y != 0) return n - 2;
       return n - temp;
    }
	
	@Override
	public void reset() {
		endPointOfContours.clear();
		flags.clear();
		xCoordinates.clear();
		yCoordinates.clear();
	}
	
	void free() {
		Pools.free(this);
	}
	
	private enum Orientation {
		TRUETYPE, POSTSCRIPT, NONE;
	}
}