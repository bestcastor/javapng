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

import java.awt.Dimension;
import java.awt.image.WritableRaster;

final class LowPassDestination
extends RasterDestination
{
    private final int xsub;
    private final int ysub;
    private final int xoff;
    private final int yoff;
    private final int sourceHeight;
    private final WritableRaster buffer;
    private final int[] pixelBuffer;
    private final int samples;

    private final int xradius;
    private final int yradius;
    private final int blockWidth;
    private final int blockHeight;
    private final boolean hasAlpha;
    private final float[][] kernel;
    private int ydiff;
    
    public LowPassDestination(WritableRaster raster, Dimension size,
                              int xsub, int ysub, int xoff, int yoff)
    {
        super(raster, size.width);
        this.xsub = xsub;
        this.ysub = ysub;
        this.xoff = xoff;
        this.yoff = yoff;
        this.sourceHeight = size.height;

        float[] xkernel = makeKernel(xsub);
        float[] ykernel = makeKernel(ysub);
        kernel = makeKernel(xkernel, ykernel);
        
        blockWidth = xkernel.length;
        blockHeight = ykernel.length;
        xradius = (blockWidth - 1) / 2;
        yradius = (blockHeight - 1) / 2;
        
        samples = raster.getNumBands();
        hasAlpha = samples % 2 == 0;
        buffer = raster.createCompatibleWritableRaster(sourceWidth, blockHeight);
        pixelBuffer = new int[samples * blockWidth * blockHeight];
        ydiff = yradius;
    }

    private static float[] makeKernel(int r)
    {
        float radius = r;
        float sigma = radius / 3;
        int rows = r * 2 + 1;
        float[] matrix = new float[rows];
        float sigma22 = 2 * sigma * sigma;
        float sigmaPi2 = 2 * (float)Math.PI * sigma;
        float sqrtSigmaPi2 = (float)Math.sqrt(sigmaPi2);
        float total = 0;
        for (int row = -r, index = 0; row <= r; row++, index++) {
            float dist = row * row;
            total += matrix[index] = (float)Math.exp(-dist / sigma22) / sqrtSigmaPi2;
        }
        for (int i = 0; i < rows; i++)
            matrix[i] /= total;
        return matrix;
    }

    private static float[][] makeKernel(float[] xkernel, float[] ykernel)
    {
        int w = xkernel.length;
        int h = ykernel.length;
        float[][] kernel = new float[h][w];
        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++)
                kernel[y][x] = xkernel[x] * ykernel[y];
        return kernel;
    }

    public void setPixels(int x, int y, int w, int[] pixels)
    {
        if (hasAlpha) {
            // multiply by alpha
            for (int j = 0, len = w * samples; j < len; j += samples) {
                int alpha = pixels[j + samples - 1];
                for (int i = 0; i < samples - 1; i++)
                    pixels[i + j] = (pixels[i + j] * alpha) / 255;
            }
        }
        buffer.setPixels(x, y + ydiff, w, 1, pixels);
        if (y >= yradius)
            filterRow(y - yradius);
    }

    public void done()
    {
        for (int y = sourceHeight - yradius; y < sourceHeight; y++)
            filterRow(y);
    }

    private void filterRow(int y)
    {
        if (((y - yoff) % ysub) != 0)
            return;

        int ydst = (y - yoff) / ysub;
        int by = y - yradius;
        int byClip = (by >= 0) ? by : 0;
        int byDiff = byClip - by;
        int by2 = by + blockHeight;
        int bhClip = ((by2 < sourceHeight) ? by2 : sourceHeight) - byClip;

        for (int xsrc = xoff, xdst = 0; xsrc < sourceWidth; xsrc += xsub, xdst++) {
            int bx = xsrc - xradius;
            int bxClip = (bx >= 0) ? bx : 0;
            int bxDiff = bxClip - bx;
            int bx2 = bx + blockWidth;
            int bwClip = ((bx2 < sourceWidth) ? bx2 : sourceWidth) - bxClip;

            buffer.getPixels(bxClip, byClip + ydiff, bwClip, bhClip, pixelBuffer);
            for (int sampleIndex = 0; sampleIndex < samples; sampleIndex++) {
                int index = sampleIndex;
                float sum = 0;
                float total = 0;
                for (int j = 0; j < bhClip; j++) {
                    for (int i = 0; i < bwClip; i++) {
                        float value = kernel[j + byDiff][i + bxDiff];
                        sum += value * pixelBuffer[index];
                        total += value;
                        index += samples;
                    }
                }
                pixelBuffer[sampleIndex] = (int)Math.round(sum / total);
            }
            if (hasAlpha) {
                // divide by alpha
                int alpha = pixelBuffer[samples - 1];
                if (alpha > 0) {
                    for (int i = 0; i < samples - 1; i++)
                        pixelBuffer[i] = (pixelBuffer[i] * 255) / alpha;
                }
            }
            raster.setPixel(xdst, ydst, pixelBuffer);
        }
        buffer.setRect(0, -ysub, buffer);
        ydiff -= ysub;
    }

    public void setPixel(int x, int y, int[] pixel)
    {
        // should only be called if interlaced, which is incompatible with blurring (without a full buffer)
        throw new UnsupportedOperationException();
    }

    public void getPixel(int x, int y, int[] pixel)
    {
        throw new UnsupportedOperationException();
    }
}
