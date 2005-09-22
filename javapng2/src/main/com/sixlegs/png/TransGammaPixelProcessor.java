/*
com.sixlegs.png - Java package to read and display PNG images
Copyright (C) 1998-2005 Chris Nokleberg

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

final class TransGammaPixelProcessor
extends BasicPixelProcessor
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
    
    public boolean process(Raster src, int xOffset, int xStep, int yStep, int y, int width)
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
        return true;
    }
}
