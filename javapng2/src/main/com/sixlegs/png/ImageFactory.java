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

import java.awt.Color;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import java.awt.Point;

class ImageFactory
{
    private int[][] bandOffsets = {
        null,
        { 0 },
        { 0, 1 },
        { 0, 1, 2 },
        { 0, 1, 2, 3 },
    };

    public ImageFactory()
    {
    }

    public BufferedImage create(PngConfig config, Map props)
    throws IOException
    {
        if (config.getMetadataOnly())
            return null;

        int width     = PngImage.getInt(props, PngImage.WIDTH);
        int height    = PngImage.getInt(props, PngImage.HEIGHT);
        int colorType = PngImage.getInt(props, PngImage.COLOR_TYPE);
        int bitDepth  = PngImage.getInt(props, PngImage.BIT_DEPTH);
        int interlace = PngImage.getInt(props, PngImage.INTERLACE);

        boolean interlaced = interlace == PngImage.INTERLACE_ADAM7;
        int samples = getSamples(colorType);
        int rowSize = (bitDepth * samples * width + 7) / 8;
        int[] palette = (int[])props.get(PngImage.PALETTE);
        ColorModel colorModel = createColorModel(colorType, bitDepth, palette);
        WritableRaster raster = createRaster(bitDepth, samples, width, height, rowSize);

        InputStream in;
        in = new MultiByteArrayInputStream((List)props.remove(PngImage.DATA));
        in = new InflaterInputStream(in, new Inflater(), 0x2000);

        Defilterer d = new Defilterer(in, raster, bitDepth, samples);
        if (interlaced) {
            d.defilter(0, 0, 8, 8, (width + 7) / 8, (height + 7) / 8);
            d.defilter(4, 0, 8, 8, (width + 3) / 8, (height + 7) / 8);
            d.defilter(0, 4, 4, 8, (width + 3) / 4, (height + 3) / 8);
            d.defilter(2, 0, 4, 4, (width + 1) / 4, (height + 3) / 4);
            d.defilter(0, 2, 2, 4, (width + 1) / 2, (height + 1) / 4);
            d.defilter(1, 0, 2, 2, width / 2, (height + 1) / 2);
            d.defilter(0, 1, 1, 2, width, height / 2);
        } else {
            d.defilter(0, 0, 1, 1, width, height);
        }
        return new BufferedImage(colorModel, raster, false, null);
    }

    private ColorModel createColorModel(int colorType, int bitDepth, int[] palette)
    {
        int colorSpace = ColorSpace.CS_sRGB;
        boolean hasAlpha = false;
        switch (colorType) {
        case PngImage.COLOR_TYPE_PALETTE:
            return new IndexColorModel(bitDepth,
                                       palette.length,
                                       palette,
                                       0,
                                       true, // TODO: any advantage to sometimes using false?
                                       -1,
                                       DataBuffer.TYPE_BYTE);
        case PngImage.COLOR_TYPE_RGB_ALPHA:
            hasAlpha = true;
            break;
        case PngImage.COLOR_TYPE_GRAY:
        case PngImage.COLOR_TYPE_GRAY_ALPHA:
            colorSpace = ColorSpace.CS_GRAY;
            hasAlpha = (colorType == PngImage.COLOR_TYPE_GRAY_ALPHA);
            if (bitDepth < 8) {
                int size = (int)Math.pow(2, bitDepth);
                palette = new int[size];
                for (int i = 0; i < size; i++) {
                    int g = i * 255 / (size - 1);
                    palette[i] = (g << 16) | (g << 8) | g;
                }
                return new IndexColorModel(bitDepth,
                                           palette.length,
                                           palette,
                                           0,
                                           false,
                                           -1, // TODO: transgray!
                                           DataBuffer.TYPE_BYTE);
            }
        }
        int dataType = (bitDepth == 16) ? DataBuffer.TYPE_USHORT : DataBuffer.TYPE_BYTE;
        return new ComponentColorModel(ColorSpace.getInstance(colorSpace),
                                       hasAlpha,
                                       false,
                                       hasAlpha ? Transparency.TRANSLUCENT : Transparency.OPAQUE,
                                       dataType);
    }

    private WritableRaster createRaster(int bitDepth, int samples, int width, int height, int rowSize)
    {
        DataBuffer dbuf;
        Point origin = new Point(0, 0);
        if ((bitDepth < 8) && (samples == 1)) {
            dbuf = new DataBufferByte(height * rowSize);
            return Raster.createPackedRaster(dbuf,
                                             width,
                                             height,
                                             bitDepth,
                                             origin);
        } else if (bitDepth <= 8) {
            dbuf = new DataBufferByte(height * rowSize);
            return Raster.createInterleavedRaster(dbuf,
                                                  width,
                                                  height,
                                                  rowSize,
                                                  samples,
                                                  bandOffsets[samples],
                                                  origin);
        } else {
            dbuf = new DataBufferUShort(height * rowSize / 2);
            return Raster.createInterleavedRaster(dbuf,
                                                  width,
                                                  height,
                                                  rowSize / 2,
                                                  samples,
                                                  bandOffsets[samples],
                                                  origin);
        }
    }
    
    private static int getSamples(int colorType)
    {
        switch (colorType) {
        case PngImage.COLOR_TYPE_GRAY_ALPHA: return 2;
        case PngImage.COLOR_TYPE_RGB:        return 3;
        case PngImage.COLOR_TYPE_RGB_ALPHA:  return 4;
        }
        return 1;
    }
}
