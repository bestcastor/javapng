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

final class GammaPixelProcessor
extends BasePixelProcessor
{
    final private short[] gammaTable;
    final private int shift;
    final private int samplesNoAlpha;
    final private boolean hasAlpha;
    final private boolean shiftAlpha;
    
    public GammaPixelProcessor(WritableRaster dst, short[] gammaTable, int shift)
    {
        super(dst);
        this.gammaTable = gammaTable;
        this.shift = shift;
        hasAlpha = samples % 2 == 0;
        samplesNoAlpha = hasAlpha ? samples - 1 : samples; // don't change alpha channel
        shiftAlpha = hasAlpha && shift > 0;
    }
    
    public void process(Raster src, int xOffset, int xStep, int yStep, int y, int width)
    {
        src.getPixels(0, 0, width, 1, row);
        int total = samples * width;
        for (int i = 0; i < samplesNoAlpha; i++)
            for (int index = i; index < total; index += samples)
                row[index] = 0xFFFF & gammaTable[row[index] >> shift];
        if (shiftAlpha)
            for (int index = samplesNoAlpha; index < total; index += samples)
                row[index] >>= shift;
        transfer(xOffset, xStep, y, width);
    }
}
