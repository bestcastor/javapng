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

class ProgressivePixelProcessor
extends PixelProcessor
{
    private PixelProcessor pp;
    
    public ProgressivePixelProcessor(PixelProcessor pp)
    {
        this.pp = pp;
    }
    
    public void process(Raster src, WritableRaster dst,
                        int xOffset, int xStep, int yStep, int y, int width)
    {
        // run non-progressive processor first
        pp.process(src, dst, xOffset, xStep, yStep, y, width);

        // then replicate pixels across entire step
        int[] pixel = src.getPixel(0, 0, (int[])null);
        for (int srcX = 0, dstX = xOffset; srcX < width; srcX++) {
            dst.getPixel(dstX, y, pixel);
            for (int i = 0; i < xStep; i++) {
                for (int j = 0; j < yStep; j++) {
                    dst.setPixel(dstX + i, y + j, pixel);
                }
            }
            dstX += xStep;
        }
    }
}
