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

import java.awt.image.*;

class GammaPixelProcessor
extends PixelProcessor
{
    private int[] gammaTable;
    
    public GammaPixelProcessor(int[] gammaTable)
    {
        this.gammaTable = gammaTable;
    }
    
    public void process(Raster src, WritableRaster dst,
                        int xOffset, int xStep, int yStep, int y, int width)
    {
        int[] pixel = src.getPixel(0, 0, (int[])null);
        int samples = pixel.length;
        if (samples % 2 == 0)
            samples--; // don't change alpha channel
        for (int srcX = 0, dstX = xOffset; srcX < width; srcX++) {
            src.getPixel(srcX, 0, pixel);
            for (int i = 0; i < samples; i++)
                pixel[i] = gammaTable[pixel[i]];
            dst.setPixel(dstX, y, pixel);
            dstX += xStep;
        }
    }
}
