// Copyright (C) 1998-2004 Chris Nokleberg
// Please see included LICENSE.TXT

package com.sixlegs.image.png;

import java.io.IOException;

class Chunk
implements Cloneable
{
    /* package */ int length;
    /* package */ int type;

    protected PngImage img;
    protected ExDataInputStream in_data;

    Chunk(int type)
    {
        this.type = type;
    }

    Chunk copy()
    {
        try {
            return (Chunk)clone();
        } catch (CloneNotSupportedException e) { 
            return null;
        }
    }

    boolean isAncillary()
    {
        return ((type & 0x20000000) != 0);
    }

    final boolean isPrivate ()
    {
        return ((type & 0x00200000) != 0);
    }

    final boolean isReservedSet ()
    {
        return ((type & 0x00002000) != 0);
    }

    final boolean isSafeToCopy ()
    {
        return ((type & 0x00000020) != 0);
    }

    final boolean isUnknown ()
    {
        return getClass() == Chunk.class;
    }

    int bytesRemaining()
    {
        return Math.max(0, length + 4 - img.data.in_idat.count());
    }

    protected boolean multipleOK() { return true; }
    protected boolean beforeIDAT() { return false; }
  
    static String typeToString(int x)
    {
        return ("" + 
                (char)((x >>> 24) & 0xFF) + 
                (char)((x >>> 16) & 0xFF) + 
                (char)((x >>>  8) & 0xFF) + 
                (char)((x       ) & 0xFF));
    }

    static int stringToType(String id)
    {
        return ((((int)id.charAt(0) & 0xFF) << 24) | 
                (((int)id.charAt(1) & 0xFF) << 16) | 
                (((int)id.charAt(2) & 0xFF) <<  8) | 
                (((int)id.charAt(3) & 0xFF)      ));
    }

    final void badLength(int correct)
    throws PngException
    {
        throw new PngException("Bad " + typeToString(type) +
                               " chunk length: " + in_data.unsign(length) +
                               " (expected " + correct + ")");
    }

    final void badLength()
    throws PngException
    {
        throw new PngException("Bad " + typeToString(type) +
                               " chunk length: " + in_data.unsign(length));
    }

    protected void readData()
    throws IOException
    {
        in_data.skipBytes(length);
    }

    static final int IHDR = stringToType("IHDR");
    static final int PLTE = stringToType("PLTE");
    static final int IDAT = stringToType("IDAT");
    static final int IEND = stringToType("IEND");
    static final int bKGD = stringToType("bKGD");
    static final int cHRM = stringToType("cHRM");
    static final int gAMA = stringToType("gAMA");
    static final int hIST = stringToType("hIST");
    static final int pHYs = stringToType("pHYs");
    static final int sBIT = stringToType("sBIT");
    static final int tEXt = stringToType("tEXt");
    static final int tIME = stringToType("tIME");
    static final int tRNS = stringToType("tRNS");
    static final int zTXt = stringToType("zTXt");
    static final int sRGB = stringToType("sRGB");
    static final int sPLT = stringToType("sPLT");
    static final int oFFs = stringToType("oFFs");
    static final int sCAL = stringToType("sCAL");
    static final int iCCP = stringToType("iCCP");
    static final int pCAL = stringToType("pCAL");
    static final int iTXt = stringToType("iTXt");
    static final int gIFg = stringToType("gIFg");
    static final int gIFx = stringToType("gIFx");
}
