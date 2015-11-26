package com.gurella.engine.graphics.vector.sfnt.woff;

import com.badlogic.gdx.utils.ByteArray;

//https://github.com/augustl/js-inflate/blob/master/js-inflate.js
public class Inflate {
	private static final int zip_WSIZE = 32768;		// Sliding Window size
	private static final int zip_STORED_BLOCK = 0;
	//private static final int zip_STATIC_TREES = 1;
	//private static final int zip_DYN_TREES    = 2;

    /* for inflate */
	private static final int zip_lbits = 9; 		// bits in base literal/length lookup table
	private static final int zip_dbits = 6; 		// bits in base distance lookup table
	//private static final int zip_INBUFSIZ = 32768;	// Input buffer size
	//private static final int zip_INBUF_EXTRA = 64;	// Extra buffer
	
	private static final int[] zip_MASK_BITS = new int[]{
	        0x0000,
	        0x0001, 0x0003, 0x0007, 0x000f, 0x001f, 0x003f, 0x007f, 0x00ff,
	        0x01ff, 0x03ff, 0x07ff, 0x0fff, 0x1fff, 0x3fff, 0x7fff, 0xffff};
	    // Tables for deflate from PKZIP's appnote.txt.
	private static final int[] zip_cplens = new int[]{ // Copy lengths for literal codes 257..285
	        3, 4, 5, 6, 7, 8, 9, 10, 11, 13, 15, 17, 19, 23, 27, 31,
	        35, 43, 51, 59, 67, 83, 99, 115, 131, 163, 195, 227, 258, 0, 0};
	    /* note: see note #13 above about the 258 in this list. */
	private static final int[] zip_cplext = new int[]{ // Extra bits for literal codes 257..285
	        0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2,
	        3, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 5, 0, 99, 99}; // 99==invalid
	private static final int[] zip_cpdist = new int[]{ // Copy offsets for distance codes 0..29
	        1, 2, 3, 4, 5, 7, 9, 13, 17, 25, 33, 49, 65, 97, 129, 193,
	        257, 385, 513, 769, 1025, 1537, 2049, 3073, 4097, 6145,
	        8193, 12289, 16385, 24577};
	private static final int[] zip_cpdext = new int[]{ // Extra bits for distance codes
	        0, 0, 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6,
	        7, 7, 8, 8, 9, 9, 10, 10, 11, 11,
	        12, 12, 13, 13};
	private static final int[] zip_border = new int[]{  // Order of the bit length code lengths
	        16, 17, 18, 0, 8, 7, 9, 6, 10, 5, 11, 4, 12, 3, 13, 2, 14, 1, 15};
	
	private byte zip_slide[];
	private int zip_wp;			// current position in slide
	private zip_HuftList zip_fixed_tl = null;	// inflate static
	private zip_HuftList zip_fixed_td;		// inflate static
	private int zip_fixed_bl;	// inflate static
	private int zip_bit_buf;		// bit buffer
	private int zip_bit_len;		// bits in bit buffer
	private int zip_method;
	private boolean zip_eof;
	private int zip_copy_leng;
	private int zip_copy_dist;
	private zip_HuftList zip_tl, zip_td;	// literal/length and distance decoder tables
	private int zip_bl, zip_bd;	// number of bits decoded by tl and td

	private byte[] zip_inflate_data;
	private int zip_inflate_pos;
	
	byte[] inflate(byte[] data) {
        int i, j;

        zip_inflate_start();
        zip_inflate_data = data;
        zip_inflate_pos = 0;

        byte[]buff = new byte[1024];
        ByteArray out = new ByteArray();
        while((i = zip_inflate_internal(buff, 0, buff.length)) > 0) {
            for(j = 0; j < i; j++)
                out.add(buff[j]);
        }
        zip_inflate_data = null; // G.C.

        return out.toArray();
    }

    /*function write_inflated_internal(ws, buff) {
        var bytesInflated = zip_inflate_internal(buff, 0, buff.length);
        if (bytesInflated > 0) {
            var out = "";
            for(j = 0; j < bytesInflated; j++) {
                out += String.fromCharCode(buff[j]);
            }
            ws.write(out);
        }
        return bytesInflated;
    };*/
	
	private int zip_GET_BYTE() {
        if(zip_inflate_data.length == zip_inflate_pos)
            return -1;
        return zip_inflate_data[zip_inflate_pos++];// & 0xff;
    }

    private void zip_NEEDBITS(int n) {
        while(zip_bit_len < n) {
            zip_bit_buf |= zip_GET_BYTE() << zip_bit_len;
            zip_bit_len += 8;
        }
    }

    private int zip_GETBITS(int n) {
        return zip_bit_buf & zip_MASK_BITS[n];
    }

    private void zip_DUMPBITS(int n) {
        zip_bit_buf >>= n;
        zip_bit_len -= n;
    }
    
    private int zip_inflate_codes(byte[] buff, int off, int size) {
        /* inflate (decompress) the codes in a deflated (compressed) block.
           Return an error code or zero if it all goes ok. */
        int e;		// table entry flag/number of extra bits
        zip_HuftNode t;		// (zip_HuftNode) pointer to table entry
        int n;

        if(size == 0)
            return 0;

        // inflate the coded data
        n = 0;
        for(;;) {			// do until end of block
            zip_NEEDBITS(zip_bl);
            t = zip_tl.list[zip_GETBITS(zip_bl)];
            e = t.e;
            while(e > 16) {
                if(e == 99)
                    return -1;
                zip_DUMPBITS(t.b);
                e -= 16;
                zip_NEEDBITS(e);
                t = t.t[zip_GETBITS(e)];
                e = t.e;
            }
            zip_DUMPBITS(t.b);

            if(e == 16) {		// then it's a literal
                zip_wp &= zip_WSIZE - 1;
                buff[off + n++] = zip_slide[zip_wp++] = (byte) t.n;
                if(n == size)
                    return size;
                continue;
            }

            // exit if end of block
            if(e == 15)
                break;

            // it's an EOB or a length

            // get length of block to copy
            zip_NEEDBITS(e);
            zip_copy_leng = t.n + zip_GETBITS(e);
            zip_DUMPBITS(e);

            // decode distance of block to copy
            zip_NEEDBITS(zip_bd);
            t = zip_td.list[zip_GETBITS(zip_bd)];
            e = t.e;

            while(e > 16) {
                if(e == 99)
                    return -1;
                zip_DUMPBITS(t.b);
                e -= 16;
                zip_NEEDBITS(e);
                t = t.t[zip_GETBITS(e)];
                e = t.e;
            }
            zip_DUMPBITS(t.b);
            zip_NEEDBITS(e);
            zip_copy_dist = zip_wp - t.n - zip_GETBITS(e);
            zip_DUMPBITS(e);

            // do the copy
            while(zip_copy_leng > 0 && n < size) {
                zip_copy_leng--;
                zip_copy_dist &= zip_WSIZE - 1;
                zip_wp &= zip_WSIZE - 1;
                buff[off + n++] = zip_slide[zip_wp++] = zip_slide[zip_copy_dist++];
            }

            if(n == size)
                return size;
        }

        zip_method = -1; // done
        return n;
    }
    
    private int zip_inflate_stored(byte[] buff, int off, int size) {
        /* "decompress" an inflated type 0 (stored) block. */
        int n;

        // go to byte boundary
        n = zip_bit_len & 7;
        zip_DUMPBITS(n);

        // get the length and its complement
        zip_NEEDBITS(16);
        n = zip_GETBITS(16);
        zip_DUMPBITS(16);
        zip_NEEDBITS(16);
        if(n != ((~zip_bit_buf) & 0xffff))
            return -1;			// error in compressed data
        zip_DUMPBITS(16);

        // read and output the compressed data
        zip_copy_leng = n;

        n = 0;
        while(zip_copy_leng > 0 && n < size) {
            zip_copy_leng--;
            zip_wp &= zip_WSIZE - 1;
            zip_NEEDBITS(8);
            buff[off + n++] = zip_slide[zip_wp++] = (byte) zip_GETBITS(8);
            zip_DUMPBITS(8);
        }

        if(zip_copy_leng == 0)
            zip_method = -1; // done
        return n;
    }
    
    private int zip_inflate_fixed(byte[] buff, int off, int size) {
        /* decompress an inflated type 1 (fixed Huffman codes) block.  We should
           either replace this with a custom decoder, or at least precompute the
           Huffman tables. */
    	int zip_fixed_bd = 0;
        // if first time, set up tables for fixed blocks
        if(zip_fixed_tl == null) {
            int i;			// temporary variable
            int[] l = new int[288];	// length list for huft_build
            zip_HuftBuild h;	// zip_HuftBuild

            // literal table
            for(i = 0; i < 144; i++)
                l[i] = 8;
            for(; i < 256; i++)
                l[i] = 9;
            for(; i < 280; i++)
                l[i] = 7;
            for(; i < 288; i++)	// make a complete, but wrong code set
                l[i] = 8;
            zip_fixed_bl = 7;

            h = new zip_HuftBuild().build(l, 288, 257, zip_cplens, zip_cplext, zip_fixed_bl);
            if(h.status != 0) {
                throw new IllegalStateException("HufBuild error: "+h.status);
            }
            zip_fixed_tl = h.root;
            zip_fixed_bl = h.m;

            // distance table
            for(i = 0; i < 30; i++)	// make an incomplete code set
                l[i] = 5;
            zip_fixed_bd = 5;

            h = new zip_HuftBuild().build(l, 30, 0, zip_cpdist, zip_cpdext, zip_fixed_bd);
            if(h.status > 1) {
                zip_fixed_tl = null;
                throw new IllegalStateException("HufBuild error: "+h.status);
            }
            zip_fixed_td = h.root;
            zip_fixed_bd = h.m;
        }

        zip_tl = zip_fixed_tl;
        zip_td = zip_fixed_td;
        zip_bl = zip_fixed_bl;
        zip_bd = zip_fixed_bd;
        return zip_inflate_codes(buff, off, size);
    }
    
    private int zip_inflate_dynamic(byte[] buff, int off, int size) {
        // decompress an inflated type 2 (dynamic Huffman codes) block.
    	int i;		// temporary variables
        int j;
        int l;		// last length
        int n;		// number of lengths to get
        zip_HuftNode t;		// (zip_HuftNode) literal/length code table
        int nb;		// number of bit length codes
        int nl;		// number of literal/length codes
        int nd;		// number of distance codes
        int[] ll = new int[286+30]; // literal/length and distance code lengths
        zip_HuftBuild h;		// (zip_HuftBuild)

        for(i = 0; i < ll.length; i++)
            ll[i] = 0;

        // read in table lengths
        zip_NEEDBITS(5);
        nl = 257 + zip_GETBITS(5);	// number of literal/length codes
        zip_DUMPBITS(5);
        zip_NEEDBITS(5);
        nd = 1 + zip_GETBITS(5);	// number of distance codes
        zip_DUMPBITS(5);
        zip_NEEDBITS(4);
        nb = 4 + zip_GETBITS(4);	// number of bit length codes
        zip_DUMPBITS(4);
        if(nl > 286 || nd > 30)
            return -1;		// bad lengths

        // read in bit-length-code lengths
        for(j = 0; j < nb; j++)
        {
            zip_NEEDBITS(3);
            ll[zip_border[j]] = zip_GETBITS(3);
            zip_DUMPBITS(3);
        }
        for(; j < 19; j++)
            ll[zip_border[j]] = 0;

        // build decoding table for trees--single level, 7 bit lookup
        zip_bl = 7;
        h = new zip_HuftBuild().build(ll, 19, 19, null, null, zip_bl);
        if(h.status != 0)
            return -1;	// incomplete code set

        zip_tl = h.root;
        zip_bl = h.m;

        // read in literal and distance code lengths
        n = nl + nd;
        i = l = 0;
        while(i < n) {
            zip_NEEDBITS(zip_bl);
            t = zip_tl.list[zip_GETBITS(zip_bl)];
            j = t.b;
            zip_DUMPBITS(j);
            j = t.n;
            if(j < 16)		// length of code in bits (0..15)
                ll[i++] = l = j;	// save last length in l
            else if(j == 16) {	// repeat last length 3 to 6 times
                zip_NEEDBITS(2);
                j = 3 + zip_GETBITS(2);
                zip_DUMPBITS(2);
                if(i + j > n)
                    return -1;
                while(j-- > 0)
                    ll[i++] = l;
            } else if(j == 17) {	// 3 to 10 zero length codes
                zip_NEEDBITS(3);
                j = 3 + zip_GETBITS(3);
                zip_DUMPBITS(3);
                if(i + j > n)
                    return -1;
                while(j-- > 0)
                    ll[i++] = 0;
                l = 0;
            } else {		// j == 18: 11 to 138 zero length codes
                zip_NEEDBITS(7);
                j = 11 + zip_GETBITS(7);
                zip_DUMPBITS(7);
                if(i + j > n)
                    return -1;
                while(j-- > 0)
                    ll[i++] = 0;
                l = 0;
            }
        }

        // build the decoding tables for literal/length and distance codes
        zip_bl = zip_lbits;
        h = new zip_HuftBuild().build(ll, nl, 257, zip_cplens, zip_cplext, zip_bl);
        if(zip_bl == 0)	// no literals or lengths
            h.status = 1;
        if(h.status != 0) {
            if(h.status == 1)
                // **incomplete literal tree**
            return -1;		// incomplete code set
        }
        zip_tl = h.root;
        zip_bl = h.m;

        for(i = 0; i < nd; i++)
            ll[i] = ll[i + nl];
        zip_bd = zip_dbits;
        h = new zip_HuftBuild().build(ll, nd, 0, zip_cpdist, zip_cpdext, zip_bd);
        zip_td = h.root;
        zip_bd = h.m;

        if(zip_bd == 0 && nl > 257) {   // lengths but no distances
            // **incomplete distance tree**
            return -1;
        }

        if(h.status == 1) {
            // **incomplete distance tree**
        }
        if(h.status != 0)
            return -1;

        // decompress until an end-of-block code
        return zip_inflate_codes(buff, off, size);
    }
    
    private void zip_inflate_start() {
        if(zip_slide == null)
            zip_slide = new byte[2 * zip_WSIZE];
        zip_wp = 0;
        zip_bit_buf = 0;
        zip_bit_len = 0;
        zip_method = -1;
        zip_eof = false;
        zip_copy_leng = zip_copy_dist = 0;
        zip_tl = null;
    }

    private int zip_inflate_internal(byte[] buff, int off, int size) {
        // decompress an inflated entry
        int n, i;

        n = 0;
        while(n < size) {
            if(zip_eof && zip_method == -1)
                return n;

            if(zip_copy_leng > 0) {
                if(zip_method != zip_STORED_BLOCK) {
                    // STATIC_TREES or DYN_TREES
                    while(zip_copy_leng > 0 && n < size) {
                        zip_copy_leng--;
                        zip_copy_dist &= zip_WSIZE - 1;
                        zip_wp &= zip_WSIZE - 1;
                        buff[off + n++] = zip_slide[zip_wp++] =
                            zip_slide[zip_copy_dist++];
                    }
                } else {
                    while(zip_copy_leng > 0 && n < size) {
                        zip_copy_leng--;
                        zip_wp &= zip_WSIZE - 1;
                        zip_NEEDBITS(8);
                        buff[off + n++] = zip_slide[zip_wp++] = (byte) zip_GETBITS(8);
                        zip_DUMPBITS(8);
                    }
                    if(zip_copy_leng == 0)
                        zip_method = -1; // done
                }
                if(n == size)
                    return n;
            }

            if(zip_method == -1) {
                if(zip_eof)
                    break;

                // read in last block bit
                zip_NEEDBITS(1);
                if(zip_GETBITS(1) != 0)
                    zip_eof = true;
                zip_DUMPBITS(1);

                // read in block type
                zip_NEEDBITS(2);
                zip_method = zip_GETBITS(2);
                zip_DUMPBITS(2);
                zip_tl = null;
                zip_copy_leng = 0;
            }

            switch(zip_method) {
            case 0: // zip_STORED_BLOCK
                i = zip_inflate_stored(buff, off + n, size - n);
                break;

            case 1: // zip_STATIC_TREES
                if(zip_tl != null)
                    i = zip_inflate_codes(buff, off + n, size - n);
                else
                    i = zip_inflate_fixed(buff, off + n, size - n);
                break;

            case 2: // zip_DYN_TREES
                if(zip_tl != null)
                    i = zip_inflate_codes(buff, off + n, size - n);
                else
                    i = zip_inflate_dynamic(buff, off + n, size - n);
                break;

            default: // error
                i = -1;
                break;
            }

            if(i == -1) {
                if(zip_eof)
                    return 0;
                return -1;
            }
            n += i;
        }
        return n;
    }
    
    private static class zip_HuftBuild {
    	private static final int BMAX = 16;   // maximum bit length of any code
    	private static final int N_MAX = 288; // maximum number of codes in any set
    	private int status = 0;	// 0: success, 1: incomplete table, 2: bad input
        private zip_HuftList root = null;	// (zip_HuftList) starting table
        private int m = 0;		// maximum lookup bits, returns actual
        
        zip_HuftBuild build(
        		int[] b,	// code lengths in bits (all assumed <= BMAX)
                int n,	// number of codes (assumed <= N_MAX)
                int s,	// number of simple-valued codes (0..s-1)
                int[] d,	// list of base values for non-simple codes
                int[] e,	// list of extra bits for non-simple codes
                int mm	// maximum lookup bits
        		) {
        	int a;			// counter for codes of length k
        	int[] c = new int[BMAX+1];	// bit length count table
        	int el;			// length of EOB code (value 256)
        	int f;			// i repeats in table every f entries
        	int g;			// maximum code length
        	int h;			// table level
        	int i;			// counter, current code
        	int j;			// counter
        	int k;			// number of bits in current code
        	int[] lx = new int[BMAX+1];	// stack of bits per table
        	int[] p;			// pointer into c[], b[], or v[]
        	int pidx;		// index of p
        	zip_HuftNode[] q;			// (zip_HuftNode) points to current table
            zip_HuftNode r = new zip_HuftNode(); // table entry for structure assignment
            zip_HuftNode[][] u = new zip_HuftNode[BMAX][]; // zip_HuftNode[BMAX][]  table stack
            int[] v = new int[N_MAX]; // values in order of bit length
            int w;
            int[] x = new int[BMAX+1];// bit offsets, then code stack
            int xp;			// pointer into x or c
            int y;			// number of dummy codes added
            int z;			// number of entries in current table
            int o;
            zip_HuftList tail;		// (zip_HuftList)

            tail = this.root = null;
            for(i = 0; i < c.length; i++)
                c[i] = 0;
            for(i = 0; i < lx.length; i++)
                lx[i] = 0;
            for(i = 0; i < u.length; i++)
                u[i] = null;
            for(i = 0; i < v.length; i++)
                v[i] = 0;
            for(i = 0; i < x.length; i++)
                x[i] = 0;

            // Generate counts for each bit length
            el = n > 256 ? b[256] : BMAX; // set length of EOB code, if any
            p = b; pidx = 0;
            i = n;
            do {
                c[p[pidx]]++;	// assume all entries <= BMAX
                pidx++;
            } while(--i > 0);
            if(c[0] == n) {	// null input--all zero length codes
                this.root = null;
                this.m = 0;
                this.status = 0;
                return this;
            }

            // Find minimum and maximum length, bound *m by those
            for(j = 1; j <= BMAX; j++)
                if(c[j] != 0)
                    break;
            k = j;			// minimum code length
            if(mm < j)
                mm = j;
            for(i = BMAX; i != 0; i--)
                if(c[i] != 0)
                    break;
            g = i;			// maximum code length
            if(mm > i)
                mm = i;

            // Adjust last length count to fill out codes, if needed
            for(y = 1 << j; j < i; j++, y <<= 1)
                if((y -= c[j]) < 0) {
                    this.status = 2;	// bad input: more codes than bits
                    this.m = mm;
                    return this;
                }
            if((y -= c[i]) < 0) {
                this.status = 2;
                this.m = mm;
                return this;
            }
            c[i] += y;

            // Generate starting offsets into the value table for each length
            x[1] = j = 0;
            p = c;
            pidx = 1;
            xp = 2;
            while(--i > 0)		// note that i == g from above
                x[xp++] = (j += p[pidx++]);

            // Make a table of values in order of bit lengths
            p = b; pidx = 0;
            i = 0;
            do {
                if((j = p[pidx++]) != 0)
                    v[x[j]++] = i;
            } while(++i < n);
            n = x[g];			// set n to length of v

            // Generate the Huffman codes and for each, make the table entries
            x[0] = i = 0;		// first Huffman code is zero
            p = v; pidx = 0;		// grab values in bit order
            h = -1;			// no tables yet--level -1
            w = lx[0] = 0;		// no bits decoded yet
            q = null;			// ditto
            z = 0;			// ditto

            // go through the bit lengths (k already is bits in shortest code)
            for(; k <= g; k++) {
                a = c[k];
                while(a-- > 0) {
                    // here i is the Huffman code of length k bits for value p[pidx]
                    // make tables up to required level
                    while(k > w + lx[1 + h]) {
                        w += lx[1 + h]; // add bits already decoded
                        h++;

                        // compute minimum size table less than or equal to *m bits
                        z = (z = g - w) > mm ? mm : z; // upper limit
                        if((f = 1 << (j = k - w)) > a + 1) { // try a k-w bit table
                            // too few codes for k-w bit table
                            f -= a + 1;	// deduct codes from patterns left
                            xp = k;
                            while(++j < z) { // try smaller tables up to z bits
                                if((f <<= 1) <= c[++xp])
                                    break;	// enough codes to use up j bits
                                f -= c[xp];	// else deduct codes from patterns
                            }
                        }
                        if(w + j > el && w < el)
                            j = el - w;	// make EOB code end at table
                        z = 1 << j;	// table entries for j-bit table
                        lx[1 + h] = j; // set table size in stack

                        // allocate and link in new table
                        q = new zip_HuftNode[z];
                        for(o = 0; o < z; o++) {
                            q[o] = new zip_HuftNode();
                        }

                        if(tail == null)
                            tail = this.root = new zip_HuftList();
                        else
                            tail = new zip_HuftList();
                        tail.list = q;
                        u[h] = q;	// table starts after link

                        /* connect to last table, if there is one */
                        if(h > 0) {
                            x[h] = i;		// save pattern for backing up
                            r.b = lx[h];	// bits to dump before this table
                            r.e = 16 + j;	// bits in this table
                            r.t = q;		// pointer to this table
                            j = (i & ((1 << w) - 1)) >> (w - lx[h]);
                            u[h-1][j].e = r.e;
                            u[h-1][j].b = r.b;
                            u[h-1][j].n = r.n;
                            u[h-1][j].t = r.t;
                        }
                    }

                    // set up table entry in r
                    r.b = k - w;
                    if(pidx >= n)
                        r.e = 99;		// out of values--invalid code
                    else if(p[pidx] < s) {
                        r.e = (p[pidx] < 256 ? 16 : 15); // 256 is end-of-block code
                        r.n = p[pidx++];	// simple code is just the value
                    } else {
                        r.e = e[p[pidx] - s];	// non-simple--look up in lists
                        r.n = d[p[pidx++] - s];
                    }

                    // fill code-like entries with r //
                    f = 1 << (k - w);
                    for(j = i >> w; j < z; j += f) {
                        q[j].e = r.e;
                        q[j].b = r.b;
                        q[j].n = r.n;
                        q[j].t = r.t;
                    }

                    // backwards increment the k-bit code i
                    for(j = 1 << (k - 1); (i & j) != 0; j >>= 1)
                        i ^= j;
                    i ^= j;

                    // backup over finished tables
                    while((i & ((1 << w) - 1)) != x[h]) {
                        w -= lx[h];		// don't need to update q
                        h--;
                    }
                }
            }

            /* return actual size of base table */
            this.m = lx[1];

            /* Return true (1) if we were given an incomplete table */
            this.status = ((y != 0 && g != 1) ? 1 : 0);
            
            return this;
        }
    }
    
    private static class zip_HuftList {
    	zip_HuftNode[] list;
    }
    
    private static class zip_HuftNode {
        int e = 0; // number of extra bits or operation
        int b = 0; // number of bits in this code or subcode

        // union
        int n = 0; // literal, length base, or distance base
        zip_HuftNode[] t; // (zip_HuftNode) pointer to next level of table
    }
}
