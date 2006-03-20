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

import java.awt.image.*;

final class ProgressivePixelProcessor
extends BasicPixelProcessor
{
    final private PixelProcessor pp;
    final private int imgWidth;
    final private int imgHeight;
    
    public ProgressivePixelProcessor(BasicPixelProcessor pp, int imgWidth, int imgHeight)
    {
        super(pp.dst, pp.row);
        this.pp = pp;
        this.imgWidth = imgWidth;
        this.imgHeight = imgHeight;
    }
    
    public boolean process(Raster src, int xOffset, int xStep, int yStep, int y, int width)
    {
        // run non-progressive processor first
        if (!pp.process(src, xOffset, xStep, yStep, y, width))
            return false;

        // then replicate pixels across entire block
        int blockHeight = xStep;
        int blockWidth = xStep - xOffset;
        if (blockWidth > 1 || blockHeight > 1) {
            int yMax = Math.min(y + blockHeight, imgHeight);
            for (int srcX = 0, dstX = xOffset; srcX < width; srcX++) {
                dst.getPixel(dstX, y, row);
                int xMax = Math.min(dstX + blockWidth, imgWidth);
                for (int i = dstX; i < xMax; i++) {
                    for (int j = y; j < yMax; j++) {
                        dst.setPixel(i, j, row);
                    }
                }
                dstX += xStep;
            }
        }
        return true;
    }
}
