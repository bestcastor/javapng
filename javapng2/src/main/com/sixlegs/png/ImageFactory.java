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

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

class ImageFactory
{
    private ImageFactory()
    {
    }

    public static BufferedImage create(PngConfig config, Map props)
    throws IOException
    {
        if (config.getMetadataOnly())
            return null;

        int width     = PngImage.getInt(props, PngImage.WIDTH);
        int height    = PngImage.getInt(props, PngImage.HEIGHT);
        int colorType = PngImage.getInt(props, PngImage.COLOR_TYPE);
        int bitDepth  = PngImage.getInt(props, PngImage.BIT_DEPTH);
        int interlace = PngImage.getInt(props, PngImage.INTERLACE);

        int samples = getSamples(colorType);
        // int outputDepth = (bitDepth == 16) ? 8 : bitDepth;
        int outputDepth = bitDepth;
        int pixelSizeInBits = samples * outputDepth;
        int rowSizeInBits = width * pixelSizeInBits;
        int extraBits = rowSizeInBits % 8;
        if (extraBits > 0)
            rowSizeInBits += (8 - extraBits);
        int rowSizeInBytes = rowSizeInBits >> 3;
        int totalBytes = height * rowSizeInBytes;

        byte[] buffer = new byte[totalBytes];
        System.err.println("w=" + width + " h=" + height + " samples=" + samples + " bytes=" + totalBytes);

        List data = (List)props.remove(PngImage.DATA);
        InputStream in;
        in = new MultiByteArrayInputStream(data);
        in = new InflaterInputStream(in, new Inflater(), 0x2000);
        in = new UnfilterInputStream(in, colorType, bitDepth, interlace != 0);
        readFully(in, buffer, 0, buffer.length); // TODO: interlacing

        // TODO: color models, etc.
        return null;
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

    private static int getSamples(int colorType)
    {
        switch (colorType) {
        case PngImage.COLOR_TYPE_RGB:        return 3;
        case PngImage.COLOR_TYPE_GRAY_ALPHA: return 2;
        case PngImage.COLOR_TYPE_RGB_ALPHA:  return 4;
        }
        return 1;
    }
}
