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
    private ImageFactory()
    {
    }

    public static BufferedImage create(PngImage png)
    throws IOException
    {
        PngConfig config = png.getConfig();
        Map props = png.getProperties();
        
        int width     = png.getWidth();
        int height    = png.getHeight();
        int colorType = png.getColorType();
        int bitDepth  = png.getBitDepth();
        int interlace = png.getInterlace();
        int samples   = png.getSamples();

        boolean interlaced = interlace == PngImage.INTERLACE_ADAM7;
        short[] gammaTable = config.getGammaCorrect() ? png.getGammaTable() : null;
        ColorModel colorModel = createColorModel(png, gammaTable);
        WritableRaster raster = colorModel.createCompatibleWritableRaster(width, height);

        PixelProcessor pp = BasicPixelProcessor.getInstance();
        if (colorModel instanceof ComponentColorModel) {
            int[] trans = null;
            if (props.containsKey(PngImage.TRANSPARENCY_GRAY)) {
                trans = new int[]{
                    png.getInt(PngImage.TRANSPARENCY_GRAY),
                };
            } else if (props.containsKey(PngImage.TRANSPARENCY_RED)) {
                trans = new int[]{
                    png.getInt(PngImage.TRANSPARENCY_RED),
                    png.getInt(PngImage.TRANSPARENCY_GREEN),
                    png.getInt(PngImage.TRANSPARENCY_BLUE),
                };
            }
            int shift = (bitDepth == 16 && config.getReduce16()) ? 8 : 0;
            if (shift != 0 || trans != null || gammaTable != null) {
                if (gammaTable == null)
                    gammaTable = getIdentityTable(bitDepth - shift);
                if (trans != null) {
                    pp = new TransGammaPixelProcessor(gammaTable, trans, shift);
                } else {
                    pp = new GammaPixelProcessor(gammaTable, shift);
                }
            }
        }
        if (config.getProgressive() && interlaced)
            pp = new ProgressivePixelProcessor(pp, width, height);

        InputStream in;
        in = new MultiByteArrayInputStream((List)props.remove(PngImage.DATA));
        in = new InflaterInputStream(in, new Inflater(), 0x2000);
        BufferedImage image = new BufferedImage(colorModel, raster, false, null);
        // TODO: if not progressive, initialize to fully transparent?

        Defilterer d = new Defilterer(in, raster, bitDepth, samples, pp);
        if (interlaced) {
            d.defilter(0, 0, 8, 8, (width + 7) / 8, (height + 7) / 8);
            png.handleFrame(image, 6);
            d.defilter(4, 0, 8, 8, (width + 3) / 8, (height + 7) / 8);
            png.handleFrame(image, 5);
            d.defilter(0, 4, 4, 8, (width + 3) / 4, (height + 3) / 8);
            png.handleFrame(image, 4);
            d.defilter(2, 0, 4, 4, (width + 1) / 4, (height + 3) / 4);
            png.handleFrame(image, 3);
            d.defilter(0, 2, 2, 4, (width + 1) / 2, (height + 1) / 4);
            png.handleFrame(image, 2);
            d.defilter(1, 0, 2, 2, width / 2, (height + 1) / 2);
            png.handleFrame(image, 1);
            d.defilter(0, 1, 1, 2, width, height / 2);
            png.handleFrame(image, 0);
        } else {
            d.defilter(0, 0, 1, 1, width, height);
            png.handleFrame(image, 0);
        }
        return image;
    }

    private static ColorModel createColorModel(PngImage png, short[] gammaTable)
    {
        Map props = png.getProperties();
        int colorType = png.getColorType();
        int bitDepth = png.getBitDepth();
        int outputDepth = (bitDepth == 16 && png.getConfig().getReduce16()) ? 8 : bitDepth;

        if (colorType == PngImage.COLOR_TYPE_PALETTE ||
            (colorType == PngImage.COLOR_TYPE_GRAY && bitDepth < 16)) {
            byte[] r = applyGamma((byte[])props.get(PngImage.PALETTE_RED), gammaTable);
            byte[] g = applyGamma((byte[])props.get(PngImage.PALETTE_GREEN), gammaTable);
            byte[] b = applyGamma((byte[])props.get(PngImage.PALETTE_BLUE), gammaTable);
            byte[] a = (byte[])props.get(PngImage.PALETTE_ALPHA);
            if (a != null) {
                return new IndexColorModel(outputDepth, r.length, r, g, b, a);
            } else {
                int trans = -1;
                if (props.containsKey(PngImage.TRANSPARENCY_GRAY)) {
                    trans = png.getInt(PngImage.TRANSPARENCY_GRAY);
                    trans = trans * 255 / ((1 << bitDepth) - 1);
                }
                return new IndexColorModel(outputDepth, r.length, r, g, b, trans);
            }
        } else {
            int dataType = (outputDepth == 16) ?
                DataBuffer.TYPE_USHORT : DataBuffer.TYPE_BYTE;
            int colorSpace =
                (colorType == PngImage.COLOR_TYPE_GRAY ||
                 colorType == PngImage.COLOR_TYPE_GRAY_ALPHA) ?
                ColorSpace.CS_GRAY :
                ColorSpace.CS_sRGB;
            boolean hasAlpha =
                colorType == PngImage.COLOR_TYPE_RGB_ALPHA ||
                colorType == PngImage.COLOR_TYPE_GRAY_ALPHA ||
                props.containsKey(PngImage.TRANSPARENCY_GRAY) ||
                props.containsKey(PngImage.TRANSPARENCY_RED);
            return new ComponentColorModel(ColorSpace.getInstance(colorSpace),
                                           hasAlpha,
                                           false,
                                           hasAlpha ? Transparency.TRANSLUCENT : Transparency.OPAQUE,
                                           dataType);
        }
    }
    
    private static byte[] applyGamma(byte[] palette, short[] gammaTable)
    {
        if (palette == null)
            return null;
        if (gammaTable == null)
            return palette;

        int size = palette.length;
        byte[] copy = new byte[size];
        for (int i = 0; i < size; i++)
            copy[i] = (byte)gammaTable[0xFF & palette[i]];
        return copy;
    }

    private static short[] getIdentityTable(int bitDepth)
    {
        // TODO: cache identity tables?
        int size = 1 << bitDepth;
        short[] table = new short[size];
        for (short i = 0; i < size; i++)
            table[i] = i;
        return table;
    }
}
