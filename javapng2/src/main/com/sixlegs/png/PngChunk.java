/*
com.sixlegs.image.png - Java package to read and display PNG images
Copyright (C) 1998-2005 Chris Nokleberg

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Library General Public
License as published by the Free Software Foundation; either
version 2 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Library General Public License for more details.

You should have received a copy of the GNU Library General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
*/

package com.sixlegs.png;

import java.io.*;
import java.util.Map;

abstract public class PngChunk
{
    public static final int IHDR = 0x49484452;
    public static final int PLTE = 0x504c5445;
    public static final int IDAT = 0x49444154;
    public static final int IEND = 0x49454e44;

    public static final int bKGD = 0x624b4744;
    public static final int cHRM = 0x6348524d;
    public static final int gAMA = 0x67414d41;
    public static final int hIST = 0x68495354;
    public static final int iCCP = 0x69434350;
    public static final int iTXt = 0x69545874;
    public static final int pHYs = 0x70485973;
    public static final int sBIT = 0x73424954;
    public static final int sPLT = 0x73504c54;
    public static final int sRGB = 0x73524742;
    public static final int tEXt = 0x74455874;
    public static final int tIME = 0x74494d45;
    public static final int tRNS = 0x74524e53;
    public static final int zTXt = 0x7a545874;

    public static final int oFFs = 0x6f464673;
    public static final int pCAL = 0x7043414c;
    public static final int sCAL = 0x7343414c;
    public static final int gIFg = 0x67494667;
    public static final int gIFx = 0x67494678;

    private int type;
    
    public PngChunk(int type)
    {
        this.type = type;
    }

    public int getType()
    {
        return type;
    }

    public boolean isMultipleOK()
    {
        return false;
    }

    public static boolean isAncillary(int type)
    {
        return ((type & 0x20000000) != 0);
    }

    public static boolean isPrivate(int type)
    {
        return ((type & 0x00200000) != 0);
    }

    public static boolean isReservedSet(int type)
    {
        return ((type & 0x00002000) != 0);
    }

    public static boolean isSafeToCopy(int type)
    {
        return ((type & 0x00000020) != 0);
    }

    public String toString()
    {
        return typeToString(type);
    }

    public static String typeToString(int x)
    {
        return ("" + 
                (char)((x >>> 24) & 0xFF) + 
                (char)((x >>> 16) & 0xFF) + 
                (char)((x >>>  8) & 0xFF) + 
                (char)((x       ) & 0xFF));
    }

    public static int stringToType(String id)
    {
        return ((((int)id.charAt(0) & 0xFF) << 24) | 
                (((int)id.charAt(1) & 0xFF) << 16) | 
                (((int)id.charAt(2) & 0xFF) <<  8) | 
                (((int)id.charAt(3) & 0xFF)      ));
    }

    protected void checkLength(int length, int correct)
    throws PngError
    {
        if (length != correct)
            throw new PngError("Bad " + typeToString(type) +
                               " chunk length: " + length + " (expected " + correct + ")");
    }

    abstract public void read(PngInputStream in, PngImage png) throws IOException;
}
