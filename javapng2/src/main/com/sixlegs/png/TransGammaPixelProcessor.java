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

final class TransGammaPixelProcessor
extends BasePixelProcessor
{
    final private short[] gammaTable;
    final private int[] trans;
    final private int shift;
    final private int max;
    final private int samplesNoAlpha;
    
    public TransGammaPixelProcessor(WritableRaster dst, short[] gammaTable, int[] trans, int shift)
    {
        super(dst);
        this.gammaTable = gammaTable;
        this.trans = trans;
        this.shift = shift;
        max = gammaTable.length - 1;
        samplesNoAlpha = samples - 1;
        if (samplesNoAlpha % 2 == 0)
            throw new IllegalStateException("Expecting alpha channel");
    }
    
    public void process(Raster src, int xOffset, int xStep, int yStep, int y, int width)
    {
        for (int srcX = 0, dstX = xOffset; srcX < width; srcX++) {
            src.getPixel(srcX, 0, row);
            int transCount = 0;
            for (;;) {
                if (row[transCount] != trans[transCount]) {
                    row[samplesNoAlpha] = max;
                    break;
                } else if (++transCount == samplesNoAlpha) {
                    row[samplesNoAlpha] = 0;
                    break;
                }
            }
            for (int i = 0; i < samplesNoAlpha; i++)
                row[i] = 0xFFFF & gammaTable[row[i] >> shift];
            dst.setPixel(dstX, y, row);
            dstX += xStep;
        }
    }
}
