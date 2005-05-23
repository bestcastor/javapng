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

/**
 * TODO
 */
abstract public class PngChunk
{
    /** Image header */
    public static final int IHDR = 0x49484452;
    /** Palette */
    public static final int PLTE = 0x504c5445;
    /** Image data */
    public static final int IDAT = 0x49444154;
    /** Image trailer */
    public static final int IEND = 0x49454e44;

    /** Background color */
    public static final int bKGD = 0x624b4744;
    /** Primary chromaticities */
    public static final int cHRM = 0x6348524d;
    /** Image gamma */
    public static final int gAMA = 0x67414d41;
    /** Palette histogram */
    public static final int hIST = 0x68495354;
    /** Embedded ICC profile */
    public static final int iCCP = 0x69434350;
    /** International textual data */
    public static final int iTXt = 0x69545874;
    /** Physical pixel dimensions */
    public static final int pHYs = 0x70485973;
    /** Significant bits */
    public static final int sBIT = 0x73424954;
    /** Suggested palette */
    public static final int sPLT = 0x73504c54;
    /** Standard RGB color space */
    public static final int sRGB = 0x73524742;
    /** Textual data */
    public static final int tEXt = 0x74455874;
    /** Image last-modification time */
    public static final int tIME = 0x74494d45;
    /** Transparency */
    public static final int tRNS = 0x74524e53;
    /** Compressed textual data */
    public static final int zTXt = 0x7a545874;

    /** Image offset */
    public static final int oFFs = 0x6f464673;
    /** Calibration of pixel values */
    public static final int pCAL = 0x7043414c;
    /** Physical scale of image subject */
    public static final int sCAL = 0x7343414c;
    /** GIF Graphic Control Extension */
    public static final int gIFg = 0x67494667;
    /** GIF Application Extension */
    public static final int gIFx = 0x67494678;

    /**
     * TODO
     */
    abstract public void read(PngInputStream in, PngImage png) throws IOException;

    /**
     * TODO
     */
    public boolean isMultipleOK()
    {
        return false;
    }

    /**
     * Returns <code>true</code> if the given type has the ancillary bit set
     * (the first letter is lowercase).
     * An ancillary chunk is once which is not strictly necessary
     * in order to meaningfully display the contents of the file.
     * @param type the chunk type
     */
    public static boolean isAncillary(int type)
    {
        return ((type & 0x20000000) != 0);
    }

    /**
     * Returns <code>true</code> if the given type has the private bit set
     * (the second letter is lowercase).
     * All unregistered chunk types should have this bit set.
     * @param type the chunk type
     */
    public static boolean isPrivate(int type)
    {
        return ((type & 0x00200000) != 0);
    }

    /**
     * Returns <code>true</code> if the given type has the reserved bit set
     * (the third letter is lowercase).
     * The meaning of this bit is currently undefined, but reserved for future use.
     * Images conforming to the current version of the PNG specification must
     * not have this bit set.
     * @param type the chunk type
     */
    public static boolean isReserved(int type)
    {
        return ((type & 0x00002000) != 0);
    }

    /**
     * Returns <code>true</code> if the given type has the safe-to-copy bit set
     * (the fourth letter is lowercase).
     * Chunks marked as safe-to-copy may be copied to a modified PNG file
     * whether or not the software recognizes the chunk type.
     * @param type the chunk type
     */
    public static boolean isSafeToCopy(int type)
    {
        return ((type & 0x00000020) != 0);
    }

    /**
     * TODO
     */
    public static String typeToString(int x)
    {
        return ("" + 
                (char)((x >>> 24) & 0xFF) + 
                (char)((x >>> 16) & 0xFF) + 
                (char)((x >>>  8) & 0xFF) + 
                (char)((x       ) & 0xFF));
    }

    /**
     * TODO
     */
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
            throw new PngError("Bad chunk length: " + length + " (expected " + correct + ")");
    }
}
