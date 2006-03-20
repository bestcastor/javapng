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

final class ProgressUpdater
extends PixelProcessor
{
    private static final int STEP_PERCENT = 5;
    
    final private PngImage png;
    final private BufferedImage image;
    final private PixelProcessor pp;
    final private int total;
    final private int step;
    private int count;
    private int mod;
    
    public ProgressUpdater(PngImage png, BufferedImage image, PixelProcessor pp)
    {
        this.png = png;
        this.image = image;
        this.pp = pp;
        total = calcTotalPixels(png);
        step = Math.max(1, total * STEP_PERCENT / 100);
    }

    private static int calcTotalPixels(PngImage png)
    {
        int w = png.getWidth();
        int h = png.getHeight();
        if (png.isInterlaced()) {
            int minPass = png.getConfig().getSourceMinProgressivePass();
            int maxPass = png.getConfig().getSourceMaxProgressivePass();
            if (minPass > 0 && maxPass < 6) {
                int total = 0;
                for (int i = minPass; i <= maxPass; i++) {
                    switch (i) {
                    case 0: total += ((w + 7) / 8) * ((h + 7) / 8); break;
                    case 1: total += ((w + 3) / 8) * ((h + 7) / 8); break;
                    case 2: total += ((w + 3) / 4) * ((h + 3) / 8); break;
                    case 3: total += ((w + 1) / 4) * ((h + 3) / 4); break;
                    case 4: total += ((w + 1) / 2) * ((h + 1) / 4); break;
                    case 5: total += (w / 2) * ((h + 1) / 2); break;
                    case 6: total += w * (h / 2); break;
                    }
                }
                return total;
            }
        }
        return w * h;
    }

    public boolean process(Raster src, int xOffset, int xStep, int yStep, int y, int width)
    {
        if (!pp.process(src, xOffset, xStep, yStep, y, width))
            return false;
        mod += width;
        count += width;
        if (mod > step) {
            mod %= step;
            return png.handleProgress(image, 100f * count / total);
        } else {
            return true;
        }
    }
}
