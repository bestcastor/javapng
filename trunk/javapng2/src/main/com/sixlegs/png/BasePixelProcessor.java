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

abstract class BasePixelProcessor
extends PixelProcessor
{
    final protected WritableRaster dst;
    final protected int[] row;
    final protected int samples;
    
    public BasePixelProcessor(WritableRaster dst)
    {
        this.dst = dst;
        row = new int[dst.getNumBands() * dst.getWidth()]; // TODO: too big?
        samples = dst.getNumBands();
    }

    protected void transfer(int xOffset, int xStep, int y, int width)
    {
        if (xStep == 1) {
            dst.setPixels(xOffset, y, width, 1, row);
        } else {
            int dstX = xOffset;
            for (int index = 0, total = samples * width; index < total; index += samples) {
                for (int i = 0; i < samples; i++)
                    row[i] = row[index + i];
                dst.setPixel(dstX, y, row);
                dstX += xStep;
            }
        }
    }
}
