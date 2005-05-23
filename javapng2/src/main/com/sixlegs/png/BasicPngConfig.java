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

import java.awt.image.BufferedImage;

/**
 * TODO
 */
public class BasicPngConfig
implements PngConfig
{
    private float defaultGamma = 0.45455f;
    private float displayExponent = 2.2f;
    private float userExponent = 1.0f;
    private boolean metadataOnly;
    private boolean warningsFatal;
    private boolean progressive;
    private boolean keepRawData;
    private boolean reduce16 = true;
    private boolean gammaCorrect = true;
    
    public boolean getKeepRawData()
    {
        return keepRawData;
    }

    /**
     * TODO
     */
    public void setKeepRawData(boolean keepRawData)
    {
        this.keepRawData = keepRawData;
    }

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

    public void handleWarning(PngWarning e)
    throws PngWarning
    {
        if (warningsFatal)
            throw e;
    }

    public boolean getMetadataOnly()
    {
        return metadataOnly;
    }

    /**
     * TODO
     */
    public void setMetadataOnly(boolean metadataOnly)
    {
        this.metadataOnly = metadataOnly;
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
    
    private static final PngChunk IHDR = new Chunk_IHDR();
    private static final PngChunk PLTE = new Chunk_PLTE();
    private static final PngChunk IDAT = new Chunk_IDAT();
    private static final PngChunk IEND = new Chunk_IEND();
    private static final PngChunk bKGD = new Chunk_bKGD();
    private static final PngChunk cHRM = new Chunk_cHRM();
    private static final PngChunk gAMA = new Chunk_gAMA();
    private static final PngChunk iTXt = new Chunk_iTXt();
    private static final PngChunk pHYs = new Chunk_pHYs();
    private static final PngChunk sBIT = new Chunk_sBIT();
    private static final PngChunk sRGB = new Chunk_sRGB();
    private static final PngChunk tEXt = new Chunk_tEXt();
    private static final PngChunk tIME = new Chunk_tIME();
    private static final PngChunk tRNS = new Chunk_tRNS();
    private static final PngChunk zTXt = new Chunk_zTXt();

    public PngChunk getChunk(int type)
    {
        switch (type) {
        case PngChunk.IHDR: return IHDR;
        case PngChunk.PLTE: return PLTE;
        case PngChunk.IDAT: return IDAT;
        case PngChunk.IEND: return IEND;
        case PngChunk.bKGD: return bKGD;
        case PngChunk.cHRM: return cHRM;
        case PngChunk.gAMA: return gAMA;
        case PngChunk.iTXt: return iTXt;
        case PngChunk.pHYs: return pHYs;
        case PngChunk.sBIT: return sBIT;
        case PngChunk.sRGB: return sRGB;
        case PngChunk.tEXt: return tEXt;
        case PngChunk.tIME: return tIME;
        case PngChunk.tRNS: return tRNS;
        case PngChunk.zTXt: return zTXt;
        }
        return null;
    }
}
