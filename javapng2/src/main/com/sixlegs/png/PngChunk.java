/*
com.sixlegs.png - Java package to read and display PNG images
Copyright (C) 1998-2006 Chris Nokleberg

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA

Linking this library statically or dynamically with other modules is
making a combined work based on this library.  Thus, the terms and
conditions of the GNU General Public License cover the whole
combination.

As a special exception, the copyright holders of this library give you
permission to link this library with independent modules to produce an
executable, regardless of the license terms of these independent
modules, and to copy and distribute the resulting executable under
terms of your choice, provided that you also meet, for each linked
independent module, the terms and conditions of the license of that
module.  An independent module is a module which is not derived from
or based on this library.  If you modify this library, you may extend
this exception to your version of the library, but you are not
obligated to do so.  If you do not wish to do so, delete this
exception statement from your version.
*/

package com.sixlegs.png;

import java.io.*;

/**
 * Individual chunks in a PNG image are read by implementations of this
 * class. The {@code PngChunk} instance used to read a particular chunk
 * is returned by {@link PngImage#getChunk}. In addition, the type of
 * chunk being read is passed to the {@link #read} method, to make it
 * possible for a single {@code PngChunk} implementation to handle
 * reading multiple chunk types.
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
    /** Indicator of Stereo Image */
    public static final int sTER = 0x73544552;
    

    /**
     * Read the chunk data from the image data stream, storing properties
     * into the {@code PngImage} instance. Subclasses are required to read
     * or skip the exact length of the chunk data.
     * @param type the chunk type
     * @param in the chunk data
     * @param length the length of the chunk in bytes
     * @param png the image into which to store chunk-specific properties
     */
    abstract public void read(int type, DataInput in, int length, PngImage png) throws IOException;

    /**
     * Returns {@code true} if the given chunk type is
     * allowed to occur multiple times within a single image. The default
     * implementation always returns {@code false}.
     * @param type the chunk type
     * @return whether multiple instances of the chunk type are allowed
     */
    public boolean isMultipleOK(int type)
    {
        return false;
    }

    /**
     * Returns {@code true} if the given chunk type has the ancillary bit set
     * (the first letter is lowercase).
     * An ancillary chunk is once which is not strictly necessary
     * in order to meaningfully display the contents of the file.
     * @param type the chunk type
     * @return whether the chunk type ancillary bit is set
     */
    public static boolean isAncillary(int type)
    {
        return ((type & 0x20000000) != 0);
    }

    /**
     * Returns {@code true} if the given chunk type has the private bit set
     * (the second letter is lowercase).
     * All unregistered chunk types should have this bit set.
     * @param type the chunk type
     * @return whether the chunk type private bit is set
     */
    public static boolean isPrivate(int type)
    {
        return ((type & 0x00200000) != 0);
    }

    /**
     * Returns {@code true} if the given chunk type has the reserved bit set
     * (the third letter is lowercase).
     * The meaning of this bit is currently undefined, but reserved for future use.
     * Images conforming to the current version of the PNG specification must
     * not have this bit set.
     * @param type the chunk type
     * @return whether the chunk type reserved bit is set
     */
    public static boolean isReserved(int type)
    {
        return ((type & 0x00002000) != 0);
    }

    /**
     * Returns {@code true} if the given chunk type has the safe-to-copy bit set
     * (the fourth letter is lowercase).
     * Chunks marked as safe-to-copy may be copied to a modified PNG file
     * whether or not the software recognizes the chunk type.
     * @param type the chunk type
     * @return whether the chunk safe-to-copy bit is set
     */
    public static boolean isSafeToCopy(int type)
    {
        return ((type & 0x00000020) != 0);
    }

    /**
     * Returns the four-character ASCII name corresponding to the given
     * chunk type. For example, {@code PngChunk.getName(PngChunk.IHDR)} will
     * return {@code "IHDR"}.
     * @param type the chunk type
     * @return the four-character ASCII chunk name
     */
    public static String getName(int type)
    {
        return ("" + 
                (char)((type >>> 24) & 0xFF) + 
                (char)((type >>> 16) & 0xFF) + 
                (char)((type >>>  8) & 0xFF) + 
                (char)((type       ) & 0xFF));
    }

    /**
     * Returns the chunk type corresponding to the given four-character
     * ASCII chunk name.
     * @param name the four-character ASCII chunk name
     * @return the chunk type
     * @throws NullPointerException if {@code name} is null
     * @throws IndexOutOfBoundsException if {@code name} has less than four characters
     */
    public static int getType(String name)
    {
        return ((((int)name.charAt(0) & 0xFF) << 24) | 
                (((int)name.charAt(1) & 0xFF) << 16) | 
                (((int)name.charAt(2) & 0xFF) <<  8) | 
                (((int)name.charAt(3) & 0xFF)      ));
    }
}
