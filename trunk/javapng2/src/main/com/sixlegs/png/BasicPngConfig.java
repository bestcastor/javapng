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

public class BasicPngConfig
implements PngConfig
{
    private int defaultGamma = 45455;
    private double displayExponent = 2.2;
    private double userExponent = 1.0;
    private boolean metadataOnly;
    private boolean warningsFatal;
    private boolean progressive;
    private boolean reduce16 = true;

    public BasicPngConfig()
    {
    }

    public boolean getReduce16()
    {
        return reduce16;
    }

    public void setReduce16(boolean reduce16)
    {
        this.reduce16 = reduce16;
    }

    public int getDefaultGamma()
    {
        return defaultGamma;
    }

    public void setDefaultGamma(int defaultGamma)
    {
        this.defaultGamma = defaultGamma;
    }
    
    public boolean getProgressive()
    {
        return progressive;
    }

    public void setProgressive()
    {
        this.progressive = progressive;
    }

    public double getDisplayExponent()
    {
        return displayExponent;
    }

    public void setDisplayExponent(double displayExponent)
    {
        this.displayExponent = displayExponent;
    }
    
    public double getUserExponent()
    {
        return userExponent;
    }

    public void setUserExponent(double userExponent)
    {
        this.userExponent = userExponent;
    }

    public void handleException(PngException e)
    throws PngException
    {
        if (warningsFatal || (e instanceof PngError))
            throw e;
    }

    public boolean getMetadataOnly()
    {
        return metadataOnly;
    }

    public void setMetadataOnly(boolean metadataOnly)
    {
        this.metadataOnly = metadataOnly;
    }

    public boolean getWarningsFatal()
    {
        return warningsFatal;
    }

    public void setWarningsFatal(boolean warningsFatal)
    {
        this.warningsFatal = warningsFatal;
    }
    
    private static final PngChunk IHDR = new Chunk_IHDR();
    private static final PngChunk PLTE = new Chunk_PLTE();
    private static final PngChunk IDAT = new Chunk_IDAT();
    private static final PngChunk IEND = new Chunk_IEND();
    private static final PngChunk bKGD = new Chunk_bKGD();
    private static final PngChunk tRNS = new Chunk_tRNS();
    private static final PngChunk gAMA = new Chunk_gAMA();

    public PngChunk getChunk(int type)
    {
        switch (type) {
        case PngChunk.IHDR: return IHDR;
        case PngChunk.PLTE: return PLTE;
        case PngChunk.IDAT: return metadataOnly ? null : IDAT;
        case PngChunk.IEND: return IEND;
        case PngChunk.bKGD: return bKGD;
        case PngChunk.tRNS: return tRNS;
        case PngChunk.gAMA: return gAMA;
            /*
        case PngChunk.cHRM: return cHRM;
        case PngChunk.hIST: return hIST;
        case PngChunk.pHYs: return pHYs;
        case PngChunk.sBIT: return sBIT;
        case PngChunk.tEXt: return tEXt;
        case PngChunk.tIME: return tIME;
        case PngChunk.zTXt: return zTXt;
        case PngChunk.sRGB: return sRGB;
        case PngChunk.sPLT: return sPLT;
        case PngChunk.oFFs: return oFFs;
        case PngChunk.sCAL: return sCAL;
        case PngChunk.iCCP: return iCCP;
        case PngChunk.pCAL: return pCAL;
        case PngChunk.iTXt: return iTXt;
        case PngChunk.gIFg: return gIFg;
        case PngChunk.gIFx: return gIFx;
            */
        default:
            return null;
        }
    }
}
