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

import java.awt.Point;
import java.awt.image.*;
import java.io.*;

// TODO: add support for FILTER_TYPE_INTRAPIXEL
class Defilterer
{
    private InputStream in;
    private WritableRaster raster;
    private int bitDepth;
    private int samples;
    private PixelProcessor pp;
    private boolean progressive;
    private int bpp;

    public Defilterer(InputStream in, WritableRaster raster, int bitDepth, int samples, PixelProcessor pp)
    {
        this.in = in;
        this.raster = raster;
        this.bitDepth = bitDepth;
        this.samples = samples;
        this.pp = pp;
        bpp = Math.max(1, (bitDepth * samples) >> 3);
    }

    public void defilter(int xOffset, int yOffset,
                         int xStep, int yStep,
                         int passWidth, int passHeight)
    throws IOException
    {
        if (passWidth == 0 || passHeight == 0)
            return;

        boolean isShort = bitDepth == 16;
        WritableRaster passRow = createInputRaster(bitDepth, samples, raster.getWidth());
        DataBuffer dbuf = passRow.getDataBuffer();
        byte[] byteData = isShort ? null : ((DataBufferByte)dbuf).getData();
        short[] shortData = isShort ? ((DataBufferUShort)dbuf).getData() : null;
        int[] pixel = new int[4];
        
        int bytesPerRow = (bitDepth * samples * passWidth + 7) / 8;
        int rowSize = bytesPerRow + bpp;
        byte[] prev = new byte[rowSize];
        byte[] cur = new byte[rowSize];

        for (int srcY = 0, dstY = yOffset; srcY < passHeight; srcY++, dstY += yStep) {
            int filterType = in.read();
            if (filterType == -1)
                throw new EOFException();
            readFully(in, cur, bpp, bytesPerRow);
            defilter(cur, prev, bpp, filterType);
            if (isShort) {
                for (int c = 0, i = bpp; i < rowSize; c++, i += 2)
                    shortData[c] = (short)((cur[i] << 8) | (0xFF & cur[i + 1]));
            } else {
                System.arraycopy(cur, bpp, byteData, 0, bytesPerRow);
            }
            pp.process(passRow, raster, xOffset, xStep, yStep, dstY, passWidth);
            byte[] tmp = cur;
            cur = prev;
            prev = tmp;
        }
    }

    private static void defilter(byte[] cur, byte[] prev, int bpp, int filterType)
    throws PngException
    {
        int rowSize = cur.length;
        int xc, xp;
        switch (filterType) {
        case 0: // None
            break;
        case 1: // Sub
            for (xc = bpp, xp = 0; xc < rowSize; xc++, xp++)
                cur[xc] = (byte)(cur[xc] + cur[xp]);
            break;
        case 2: // Up
            for (xc = bpp; xc < rowSize; xc++)
                cur[xc] = (byte)(cur[xc] + prev[xc]);
            break;
        case 3: // Average
            for (xc = bpp, xp = 0; xc < rowSize; xc++, xp++)
                cur[xc] = (byte)(cur[xc] + ((0xFF & cur[xp]) + (0xFF & prev[xc])) / 2);
            break;
        case 4: // Paeth
            for (xc = bpp, xp = 0; xc < rowSize; xc++, xp++)
                cur[xc] = (byte)(cur[xc] + paeth(cur[xp], prev[xc], prev[xp]));
            break;
        case 64:
            throw new PngError("Intra-pixel filtering not implemented yet");
        default:
            throw new PngError("Unrecognized filter type " + filterType);
        }
    }

    private static int paeth(byte L, byte u, byte nw)
    {
        int a = 0xFF & L; //  inline byte->int
        int b = 0xFF & u; 
        int c = 0xFF & nw; 
        int p = a + b - c;
        int pa = p - a; if (pa < 0) pa = -pa; // inline Math.abs
        int pb = p - b; if (pb < 0) pb = -pb; 
        int pc = p - c; if (pc < 0) pc = -pc; 
        if (pa <= pb && pa <= pc)
            return a;
        if (pb <= pc)
            return b;
        return c;
    }

    private static void readFully(InputStream in, byte[] b, int off, int len)
    throws IOException
    {
        int total = 0;
        while (total < len) {
            int result = in.read(b, off + total, len - total);
            if (result == -1)
                throw new EOFException();
            total += result;
        }
    }

    private static int[][] bandOffsets = {
        null,
        { 0 },
        { 0, 1 },
        { 0, 1, 2 },
        { 0, 1, 2, 3 },
    };

    private static WritableRaster createInputRaster(int bitDepth, int samples, int width)
    {
        int rowSize = (bitDepth * samples * width + 7) / 8;
        Point origin = new Point(0, 0);
        if ((bitDepth < 8) && (samples == 1)) {
            DataBuffer dbuf = new DataBufferByte(rowSize);
            return Raster.createPackedRaster(dbuf, width, 1, bitDepth, origin);
        } else if (bitDepth <= 8) {
            DataBuffer dbuf = new DataBufferByte(rowSize);
            return Raster.createInterleavedRaster(dbuf, width, 1, rowSize, samples,
                                                  bandOffsets[samples], origin);
        } else {
            DataBuffer dbuf = new DataBufferUShort(rowSize / 2);
            return Raster.createInterleavedRaster(dbuf, width, 1, rowSize / 2, samples,
                                                  bandOffsets[samples], origin);
        }
    }
}
