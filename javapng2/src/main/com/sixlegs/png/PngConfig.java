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

/**
 * Customizable parameters
 * used by {@link PngImage} when decoding an image.
 */
public class PngConfig
{
    public static final int READ_ALL = 0;
    public static final int READ_HEADER = 1;
    public static final int READ_UNTIL_DATA = 2;
    public static final int READ_EXCEPT_DATA = 3;

    private int readLimit = READ_ALL;
    private float defaultGamma = 0.45455f;
    private float displayExponent = 2.2f;
    private float userExponent = 1.0f;
    private boolean warningsFatal;
    private boolean progressive;
    private boolean reduce16 = true;
    private boolean gammaCorrect = true;
    
    /**
     * If true, 16-bit samples are reduced to 8-bit samples by
     * shifting to the right by 8 bits.
     * Default is true.
     */
    public boolean getReduce16()
    {
        return reduce16;
    }

    /**
     * TODO
     */
    public void setReduce16(boolean reduce16)
    {
        this.reduce16 = reduce16;
    }

    /**
     * The default gamma value to use if the image does not contain
     * an explicit gamma value. Initially set to 1/45455.
     */
    public float getDefaultGamma()
    {
        return defaultGamma;
    }

    /**
     * TODO
     */
    public void setDefaultGamma(float defaultGamma)
    {
        this.defaultGamma = defaultGamma;
    }
    
    /**
     * If true, decoded images will be gamma corrected.
     * Return false if your application will perform the gamma
     * correctly manually.
     * Default is true.
     * @see PngImage#getGamma
     * @see PngImage#getGammaTable
     */
    public boolean getGammaCorrect()
    {
        return gammaCorrect;
    }

    /**
     * TODO
     */
    public void setGammaCorrect(boolean gammaCorrect)
    {
        this.gammaCorrect = gammaCorrect;
    }

    /**
     * If true, enables progressive display for interlaced images.
     * Each received pixel is expanded (replicated) to fill a rectangle
     * covering the yet-to-be-transmitted pixel positions below and to the right
     * of the received pixel. This produces a "fade-in" effect as the new image
     * gradually replaces the old, at the cost of some additional processing time.
     * Default is false.
     * @see PngImage#handleFrame
     */
    public boolean getProgressive()
    {
        return progressive;
    }

    /**
     * TODO
     */
    public void setProgressive(boolean progressive)
    {
        this.progressive = progressive;
    }

    /**
     * The default display exponent. Depends on monitor and gamma lookup
     * table settings, if any. The default value of 2.2 should
     * work well with most PC displays. If the operating system has
     * a gamma lookup table (e.g. Macintosh) the display exponent should be lower.
     */
    public float getDisplayExponent()
    {
        return displayExponent;
    }

    /**
     * TODO
     */
    public void setDisplayExponent(float displayExponent)
    {
        this.displayExponent = displayExponent;
    }
    
    /**
     * The user gamma exponent. The ideal setting depends on the user's
     * particular viewing conditions. Set to greater than 1.0 to darken the mid-level
     * tones, or less than 1.0 to lighten them. Default is 1.0.
     */
    public float getUserExponent()
    {
        return userExponent;
    }

    /**
     * TODO
     */
    public void setUserExponent(float userExponent)
    {
        this.userExponent = userExponent;
    }

    /**
     * Callback for customized handling of warnings. Whenever a non-fatal
     * error is found, an instance of {@link PngWarning} is created and
     * passed to this method. To signal that the exception should be treated
     * as a fatal exception, an implementation should re-throw the exception.
     * @throws PngWarning if the warning should be treated as fatal
     */
    public void handleWarning(PngWarning e)
    throws PngWarning
    {
        if (warningsFatal)
            throw e;
    }

//     /**
//      * If true, image data will not be decoded.
//      * Instead, {@link PngImage#read(java.io.File)} and
//      * {@link PngImage#read(java.io.InputStream, boolean)} will return
//      * null after reading all of the image metadata.
//      * {@link BasicPngConfig} defaults to false.
//      */
    public int getReadLimit()
    {
        return readLimit;
    }

    /**
     * TODO
     */
    public void setReadLimit(int readLimit)
    {
        this.readLimit = readLimit;
    }

    /**
     * TODO
     */
    public boolean getWarningsFatal()
    {
        return warningsFatal;
    }

    /**
     * TODO
     */
    public void setWarningsFatal(boolean warningsFatal)
    {
        this.warningsFatal = warningsFatal;
    }

    private static final PngChunk IHDR = loadChunk(PngChunk.IHDR);
    private static final PngChunk PLTE = loadChunk(PngChunk.PLTE);
    private static final PngChunk IEND = loadChunk(PngChunk.IEND);
    private static final PngChunk bKGD = loadChunk(PngChunk.bKGD);
    private static final PngChunk cHRM = loadChunk(PngChunk.cHRM);
    private static final PngChunk gAMA = loadChunk(PngChunk.gAMA);
    private static final PngChunk pHYs = loadChunk(PngChunk.pHYs);
    private static final PngChunk sBIT = loadChunk(PngChunk.sBIT);
    private static final PngChunk sRGB = loadChunk(PngChunk.sRGB);
    private static final PngChunk tIME = loadChunk(PngChunk.tIME);
    private static final PngChunk tRNS = loadChunk(PngChunk.tRNS);
    private static final PngChunk text = loadChunk("com.sixlegs.png.TextChunkReader");

    static PngChunk loadChunk(int chunk)
    {
        return loadChunk("com.sixlegs.png.Chunk_" + PngChunk.getName(chunk));
    }

    static PngChunk loadChunk(String className)
    {
        try {
            return (PngChunk)Class.forName(className).newInstance();
        } catch (ClassNotFoundException e) {
            return null;
        } catch (IllegalAccessException e) {
            throw new Error(e.getMessage());
        } catch (InstantiationException e) {
            throw new Error(e.getMessage());
        }
    }

    /**
     * Returns a {@link PngChunk} implementation for the given chunk type.
     * The returned chunk object will be responsible for reading the
     * binary chunk data and populating the property map of the {@link PngImage}
     * as appropriate. If <code>null</code> is returned, the chunk is skipped. Any chunk
     * Note that skipping certain critical chunks will guarantee an eventual
     * exception.
     * <p>
     * {@link BasicPngConfig} has a default implementation for all of the chunk
     * types defined in Version 1.2 of the PNG Specification except
     * {@link PngChunk#hIST hIST}, {@link PngChunk#iCCP iCCP}, and
     * {@link PngChunk#sPLT sPLT}. Those three are added by {@link CompletePngConfig}.
     * @param png the image requesting the chunk
     * @param type the chunk type
     */
    public PngChunk getChunk(PngImage png, int type)
    {
        switch (type) {
        case PngChunk.IHDR: return IHDR;
        case PngChunk.PLTE: return PLTE;
        case PngChunk.IEND: return IEND;
        case PngChunk.bKGD: return bKGD;
        case PngChunk.cHRM: return cHRM;
        case PngChunk.gAMA: return gAMA;
        case PngChunk.pHYs: return pHYs;
        case PngChunk.sBIT: return sBIT;
        case PngChunk.sRGB: return sRGB;
        case PngChunk.tIME: return tIME;
        case PngChunk.tRNS: return tRNS;
        case PngChunk.iTXt:
        case PngChunk.tEXt:
        case PngChunk.zTXt:
            return text;
        }
        return null;
    }
}
