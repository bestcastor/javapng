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

import java.io.*;
import java.util.*;

class RegisteredChunks
extends PngChunk
{
    private static TimeZone TIME_ZONE = TimeZone.getTimeZone("UTC");

    public boolean isMultipleOK(int type)
    {
        switch (type) {
        case PngChunk.sPLT:
        case PngChunk.iTXt:
        case PngChunk.tEXt:
        case PngChunk.zTXt:
            return true;
        }
        return false;
    }

    public void read(int type, DataInput in, int length, PngImage png)
    throws IOException
    {
        Map props = png.getProperties();
        switch (type) {
        case PngChunk.IHDR: read_IHDR(in, length, props); break;
        case PngChunk.IEND: checkLength(length, 0); break;
        case PngChunk.PLTE: read_PLTE(in, length, props, png); break;
        case PngChunk.bKGD: read_bKGD(in, length, props, png); break;
        case PngChunk.tRNS: read_tRNS(in, length, props, png); break;
        case PngChunk.sBIT: read_sBIT(in, length, props, png); break;
        case PngChunk.cHRM: read_cHRM(in, length, props); break;
        case PngChunk.gAMA: read_gAMA(in, length, props); break;
        case PngChunk.hIST: read_hIST(in, length, props); break;
        case PngChunk.iCCP: read_iCCP(in, length, props); break;
        case PngChunk.pHYs: read_pHYs(in, length, props); break;
        case PngChunk.sRGB: read_sRGB(in, length, props); break;
        case PngChunk.tIME: read_tIME(in, length, props); break;
        case PngChunk.sPLT: read_sPLT(in, length, props); break;
        case PngChunk.gIFg: read_gIFg(in, length, props); break;
        case PngChunk.oFFs: read_oFFs(in, length, props); break;
        case PngChunk.sCAL: read_sCAL(in, length, props); break;
        case PngChunk.sTER: read_sTER(in, length, props); break;
            // case PngChunk.gIFx: read_gIFx(in, length, props); break;
        case PngChunk.iTXt:
        case PngChunk.tEXt:
        case PngChunk.zTXt:
            readText(type, in, length, props);
            break;
        }
    }

    private void read_IHDR(DataInput in, int length, Map props)
    throws IOException
    {
        checkLength(length, 13);
        int width = in.readInt();
        int height = in.readInt();
        if (width <= 0 || height <= 0)
            throw new PngException("Bad image size: " + width + "x" + height, true);

        byte bitDepth = in.readByte();
        switch (bitDepth) {
        case 1: case 2: case 4: case 8: case 16: break;
        default: throw new PngException("Bad bit depth: " + bitDepth, true);
        }

        byte[] sbits = null;
        int colorType = in.readUnsignedByte();
        switch (colorType) {
        case PngConstants.COLOR_TYPE_RGB:
        case PngConstants.COLOR_TYPE_GRAY: 
            break;
        case PngConstants.COLOR_TYPE_PALETTE: 
            if (bitDepth == 16)
                throw new PngException("Bad bit depth for color type " + colorType + ": " + bitDepth, true);
            break;
        case PngConstants.COLOR_TYPE_GRAY_ALPHA: 
        case PngConstants.COLOR_TYPE_RGB_ALPHA: 
            if (bitDepth <= 4)
                throw new PngException("Bad bit depth for color type " + colorType + ": " + bitDepth, true);
            break;
        default:
            throw new PngException("Bad color type: " + colorType, true);
        }

        int compression = in.readUnsignedByte();
        if (compression != PngConstants.COMPRESSION_BASE) 
            throw new PngException("Unrecognized compression method: " + compression, true);

        int filter = in.readUnsignedByte();
        if (filter != PngConstants.FILTER_BASE)
            throw new PngException("Unrecognized filter method: " + filter, true);

        int interlace = in.readUnsignedByte();
        switch (interlace) {
        case PngConstants.INTERLACE_NONE:
        case PngConstants.INTERLACE_ADAM7:
            break;
        default:
            throw new PngException("Unrecognized interlace method: " + interlace, true);
        }

        props.put(PngConstants.WIDTH, Integers.valueOf(width));
        props.put(PngConstants.HEIGHT, Integers.valueOf(height));
        props.put(PngConstants.BIT_DEPTH, Integers.valueOf(bitDepth));
        props.put(PngConstants.INTERLACE, Integers.valueOf(interlace));
        props.put(PngConstants.COMPRESSION, Integers.valueOf(compression));
        props.put(PngConstants.FILTER, Integers.valueOf(filter));
        props.put(PngConstants.COLOR_TYPE, Integers.valueOf(colorType));
    }

    private void read_PLTE(DataInput in, int length, Map props, PngImage png)
    throws IOException
    {
        if (length == 0)
            throw new PngException("PLTE chunk cannot be empty", true);
        if (length % 3 != 0)
            throw new PngException("PLTE chunk length indivisible by 3: " + length, true);
        int size = length / 3;
        if (size > 256)
            throw new PngException("Too many palette entries: " + size, true);
        switch (png.getColorType()) {
        case PngConstants.COLOR_TYPE_PALETTE:
            if (size > (2 << (png.getBitDepth() - 1)))
                throw new PngException("Too many palette entries: " + size, true);
            break;
        case PngConstants.COLOR_TYPE_GRAY:
        case PngConstants.COLOR_TYPE_GRAY_ALPHA:
            throw new PngException("PLTE chunk found in grayscale image", false);
        }
        byte[] palette = new byte[length];
        in.readFully(palette);
        props.put(PngConstants.PALETTE, palette);
    }

    private void read_tRNS(DataInput in, int length, Map props, PngImage png)
    throws IOException
    {
        switch (png.getColorType()) {
        case PngConstants.COLOR_TYPE_GRAY:
            checkLength(length, 2);
            props.put(PngConstants.TRANSPARENCY, new int[]{ in.readUnsignedShort() });
            break;
        case PngConstants.COLOR_TYPE_RGB:
            checkLength(length, 6);
            props.put(PngConstants.TRANSPARENCY, new int[]{
                in.readUnsignedShort(),
                in.readUnsignedShort(),
                in.readUnsignedShort(),
            });
            break;
        case PngConstants.COLOR_TYPE_PALETTE:
            int paletteSize = ((byte[])props.get(PngConstants.PALETTE)).length / 3;
            if (length > paletteSize)
                throw new PngException("Too many transparency palette entries (" + length + " > " + paletteSize + ")", true);
            byte[] trans = new byte[length];
            in.readFully(trans);
            props.put(PngConstants.PALETTE_ALPHA, trans);
            break;
        default:
            throw new PngException("tRNS prohibited for color type " + png.getColorType(), true);
        }
    }

    private void read_bKGD(DataInput in, int length, Map props, PngImage png)
    throws IOException
    {
        int[] background;
        switch (png.getColorType()) {
        case PngConstants.COLOR_TYPE_PALETTE:
            checkLength(length, 1);
            background = new int[]{ in.readUnsignedByte() };
            break;
        case PngConstants.COLOR_TYPE_GRAY:
        case PngConstants.COLOR_TYPE_GRAY_ALPHA:
            checkLength(length, 2);
            background = new int[]{ in.readUnsignedShort() };
            break;
        default:
            // truecolor
            checkLength(length, 6);
            background = new int[]{
                in.readUnsignedShort(),
                in.readUnsignedShort(),
                in.readUnsignedShort(),
            };
        }
        props.put(PngConstants.BACKGROUND, background);
    }

    private void read_cHRM(DataInput in, int length, Map props)
    throws IOException
    {
        checkLength(length, 32);
        float[] array = new float[8];
        for (int i = 0; i < 8; i++)
            array[i] = in.readInt() / 100000f;
        if (!props.containsKey(PngConstants.CHROMATICITY))
            props.put(PngConstants.CHROMATICITY, array);
    }

    private void read_gAMA(DataInput in, int length, Map props)
    throws IOException
    {
        checkLength(length, 4);
        int gamma = in.readInt();
        if (gamma == 0)
            throw new PngException("Meaningless zero gAMA chunk value", false);
        if (!props.containsKey(PngConstants.RENDERING_INTENT))
            props.put(PngConstants.GAMMA, new Float(gamma / 100000f));
    }

    private void read_hIST(DataInput in, int length, Map props)
    throws IOException
    {
        int paletteSize = ((byte[])props.get(PngConstants.PALETTE)).length / 3;
        checkLength(length, paletteSize * 2);
        int[] array = new int[paletteSize];
        for (int i = 0; i < paletteSize; i++)
            array[i] = in.readUnsignedShort();
        props.put(PngConstants.HISTOGRAM, array);
    }

    private void read_iCCP(DataInput in, int length, Map props)
    throws IOException
    {
        String name = PngUtils.readKeyword(in, length);
        byte[] data = PngUtils.readCompressed(in, length - name.length() - 1, true);
        props.put(PngConstants.ICC_PROFILE_NAME, name);
        props.put(PngConstants.ICC_PROFILE, data);
    }

    private void read_pHYs(DataInput in, int length, Map props)
    throws IOException
    {
        checkLength(length, 9);
        int pixelsPerUnitX = in.readInt();
        int pixelsPerUnitY = in.readInt();
        int unit = in.readUnsignedByte();
        if (unit != PngConstants.UNIT_UNKNOWN && unit != PngConstants.UNIT_METER)
            throw new PngException("Illegal pHYs chunk unit specifier: " + unit, false);
        props.put(PngConstants.PIXELS_PER_UNIT_X, Integers.valueOf(pixelsPerUnitX));
        props.put(PngConstants.PIXELS_PER_UNIT_Y, Integers.valueOf(pixelsPerUnitY));
        props.put(PngConstants.UNIT, Integers.valueOf(unit));
    }

    private void read_sBIT(DataInput in, int length, Map props, PngImage png)
    throws IOException
    {
        boolean paletted = png.getColorType() == PngConstants.COLOR_TYPE_PALETTE;
        int count = paletted ? 3 : png.getSamples();
        checkLength(length, count);
        int depth = paletted ? 8 : png.getBitDepth();
        byte[] array = new byte[count];
        for (int i = 0; i < count; i++) {
            byte bits = in.readByte();
            if (bits <= 0 || bits > depth)
                throw new PngException("Illegal sBIT sample depth", false);
            array[i] = bits;
        }
        props.put(PngConstants.SIGNIFICANT_BITS, array);
    }

    private void read_sRGB(DataInput in, int length, Map props)
    throws IOException
    {
        checkLength(length, 1);
        int intent = in.readByte();
        props.put(PngConstants.RENDERING_INTENT, Integers.valueOf(intent));
        props.put(PngConstants.GAMMA, new Float(0.45455));
        props.put(PngConstants.CHROMATICITY, new float[]{
            0.3127f, 0.329f, 0.64f, 0.33f, 0.3f, 0.6f, 0.15f, 0.06f,
        });
    }

    private void read_tIME(DataInput in, int length, Map props)
    throws IOException
    {
        checkLength(length, 7);
        Calendar cal = Calendar.getInstance(TIME_ZONE);
        cal.set(in.readUnsignedShort(),
                check(in.readUnsignedByte(), 1, 12) - 1,
                check(in.readUnsignedByte(), 1, 31),
                check(in.readUnsignedByte(), 0, 23),
                check(in.readUnsignedByte(), 0, 59),
                check(in.readUnsignedByte(), 0, 60));
        props.put(PngConstants.TIME, cal.getTime());
    }

    private static int check(int value, int min, int max)
    throws PngException
    {
        if (value < min || value > max)
            throw new PngException("tIME value out of bounds", false);
        return value;
    }

    private void read_sPLT(DataInput in, int length, Map props)
    throws IOException
    {
        String name = PngUtils.readKeyword(in, length);
        int sampleDepth = in.readByte();
        if (sampleDepth != 8 && sampleDepth != 16)
            throw new PngException("Sample depth must be 8 or 16", false);
        
        length -= (name.length() + 2);
        if ((length % ((sampleDepth == 8) ? 6 : 10)) != 0)
            throw new PngException("Incorrect sPLT data length for given sample depth", false);
        byte[] bytes = new byte[length];
        in.readFully(bytes);

        List palettes = (List)props.get(PngConstants.SUGGESTED_PALETTES);
        if (palettes == null)
            props.put(PngConstants.SUGGESTED_PALETTES, palettes = new ArrayList());
        for (Iterator it = palettes.iterator(); it.hasNext();) {
            if (name.equals(((SuggestedPalette)it.next()).getName()))
                throw new PngException("Duplicate suggested palette name " + name, false);
        }
        palettes.add(new SuggestedPaletteImpl(name, sampleDepth, bytes));
    }

    private void readText(int type, DataInput in, int length, Map props)
    throws IOException
    {
        byte[] bytes = new byte[length];
        in.readFully(bytes);
        DataInputStream data = new DataInputStream(new ByteArrayInputStream(bytes));

        String keyword = PngUtils.readKeyword(data, length);
        String enc = PngUtils.ISO_8859_1;
        boolean compressed = false;
        boolean readMethod = true;
        String language = null;
        String translated = null;
        switch (type) {
        case tEXt:
            break;
        case zTXt:
            compressed = true;
            break;
        case iTXt:
            enc = PngUtils.UTF_8;
            int flag = data.readByte();
            int method = data.readByte();
            if (flag == 1) {
                compressed = true;
                readMethod = false;
                if (method != 0)
                    throw new PngException("Unrecognized " + PngChunk.getName(type) + " compression method: " + method, false);
            } else if (flag != 0) {
                throw new PngException("Illegal " + PngChunk.getName(type) + " compression flag: " + flag, false);
            }
            language = PngUtils.readString(data, data.available(), PngUtils.US_ASCII);
            // TODO: split language on hyphens, check that each component is 1-8 caharacters
            translated = PngUtils.readString(data, data.available(), PngUtils.UTF_8);
            // TODO: check for line breaks?
        }

        String text;
        if (compressed) {
            text = new String(PngUtils.readCompressed(data, data.available(), readMethod), enc);
        } else {
            text = new String(bytes, bytes.length - data.available(), data.available(), enc);
        }
        List chunks = (List)props.get(PngConstants.TEXT_CHUNKS);
        if (chunks == null)
            props.put(PngConstants.TEXT_CHUNKS, chunks = new ArrayList());
        chunks.add(new TextChunkImpl(keyword, text, language, translated, type));
    }

    private void read_gIFg(DataInput in, int length, Map props)
    throws IOException
    {
        checkLength(length, 4);
        int disposalMethod = in.readUnsignedByte();
        int userInputFlag = in.readUnsignedByte();
        int delayTime = in.readUnsignedShort();
        props.put(PngConstants.GIF_DISPOSAL_METHOD, Integers.valueOf(disposalMethod));
        props.put(PngConstants.GIF_USER_INPUT_FLAG, Integers.valueOf(userInputFlag));
        props.put(PngConstants.GIF_DELAY_TIME, Integers.valueOf(delayTime));
    }

    private void read_oFFs(DataInput in, int length, Map props)
    throws IOException
    {
        checkLength(length, 9);
        int x = in.readInt();
        int y = in.readInt();
        int unit = in.readByte();
        if (unit != PngConstants.POSITION_UNIT_PIXEL &&
            unit != PngConstants.POSITION_UNIT_MICROMETER)
            throw new PngException("Illegal oFFs chunk unit specifier: " + unit, false);
        props.put(PngConstants.POSITION_X, Integers.valueOf(x));
        props.put(PngConstants.POSITION_Y, Integers.valueOf(y));
        props.put(PngConstants.POSITION_UNIT, Integers.valueOf(unit));
    }

    private void read_sCAL(DataInput in, int length, Map props)
    throws IOException
    {
        byte[] bytes = new byte[length];
        in.readFully(bytes);
        DataInputStream data = new DataInputStream(new ByteArrayInputStream(bytes));
        
        int unit = data.readByte();
        double width = PngUtils.readFloatingPoint(data, data.available());
        double height = PngUtils.readFloatingPoint(data, data.available());
        props.put(PngConstants.SCALE_UNIT, Integers.valueOf(unit));
        props.put(PngConstants.PIXEL_WIDTH, new Double(width));
        props.put(PngConstants.PIXEL_HEIGHT, new Double(height));
    }

    private void read_sTER(DataInput in, int length, Map props)
    throws IOException
    {
        int mode = in.readByte();
        switch (mode) {
        case PngConstants.STEREO_MODE_CROSS:
        case PngConstants.STEREO_MODE_DIVERGING:
            props.put(PngConstants.STEREO_MODE, Integers.valueOf(mode));
            break;
        default:
            throw new PngException("Unknown sTER mode: " + mode, false);
        }
    }

    private static void checkLength(int length, int correct)
    throws PngException
    {
        if (length != correct)
            throw new PngException("Bad chunk length: " + length + " (expected " + correct + ")", true);
    }
}
