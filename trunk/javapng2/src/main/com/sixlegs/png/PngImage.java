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
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

public class PngImage
{
    public static final String BACKGROUND_BLUE = "background_blue";
    public static final String BACKGROUND_GRAY = "background_gray";
    public static final String BACKGROUND_GREEN = "background_green";
    public static final String BACKGROUND_INDEX = "background_index";
    public static final String BACKGROUND_RED = "background_red";
    public static final String BIT_DEPTH = "bit_depth";
    public static final String COLOR_TYPE = "color_type";
    public static final String COMPRESSION = "compression";
    public static final String DATA = "data";
    public static final String FILTER = "filter";
    public static final String GAMMA = "gamma";
    public static final String HEIGHT = "height";
    public static final String ICC_PROFILE = "icc_profile";
    public static final String ICC_PROFILE_NAME = "icc_profile_name";
    public static final String INTERLACE = "interlace";
    public static final String PALETTE_ALPHA = "palette_alpha";
    public static final String PALETTE_BLUE = "palette_blue";
    public static final String PALETTE_GREEN = "palette_green";
    public static final String PALETTE_RED = "palette_red";
    public static final String PIXELS_PER_UNIT_X = "pixels_per_unit_x";
    public static final String PIXELS_PER_UNIT_Y = "pixels_per_unit_y";
    public static final String RENDERING_INTENT = "rendering_intent";
    public static final String SIGNIFICANT_BITS = "significant_bits";
    public static final String TEXT_CHUNKS = "text_chunks";
    public static final String TIME = "time";
    public static final String TRANSPARENCY_BLUE = "transparency_blue";
    public static final String TRANSPARENCY_GRAY = "transparency_gray";
    public static final String TRANSPARENCY_GREEN = "transparency_green";
    public static final String TRANSPARENCY_RED = "transparency_red";
    public static final String UNIT_SPECIFIER = "unit_specifier";
    public static final String WIDTH = "width";
    public static final String WHITE_POINT_X = "white_point_x";
    public static final String WHITE_POINT_Y = "white_point_y";
    public static final String RED_X = "red_x";
    public static final String RED_Y = "red_y";
    public static final String BLUE_X = "blue_x";
    public static final String BLUE_Y = "blue_y";
    public static final String GREEN_X = "green_x";
    public static final String GREEN_Y = "green_y";

    public static final int COLOR_TYPE_GRAY = 0;
    public static final int COLOR_TYPE_GRAY_ALPHA = 4;
    public static final int COLOR_TYPE_PALETTE = 3;
    public static final int COLOR_TYPE_RGB = 2;
    public static final int COLOR_TYPE_RGB_ALPHA = 6;
  
    public static final int INTERLACE_NONE = 0;
    public static final int INTERLACE_ADAM7 = 1;

    public static final int FILTER_BASE = 0;
    public static final int FILTER_INTRAPIXEL = 64;

    public static final int COMPRESSION_BASE = 0;  

    public static final int UNIT_UNKNOWN = 0;
    public static final int UNIT_METER = 1;
    public static final int UNIT_PIXEL = 0;
    public static final int UNIT_MICROMETER = 1;
    public static final int UNIT_RADIAN = 2;

    public static final int SRGB_PERCEPTUAL = 0;
    public static final int SRGB_RELATIVE_COLORIMETRIC = 1;
    public static final int SRGB_SATURATION_PRESERVING = 2;
    public static final int SRGB_ABSOLUTE_COLORIMETRIC = 3;
    
    private PngConfig config;
    private Map props = new HashMap();
    private boolean read = false;

    private static final long SIGNATURE = 0x89504E470D0A1A0AL;

    public PngImage()
    {
        this(new BasicPngConfig());
    }

    public PngImage(PngConfig config)
    {
        this.config = config;
    }

    public PngConfig getConfig()
    {
        return config;
    }
    
    public BufferedImage read(File file)
    throws IOException
    {
        return read(new BufferedInputStream(new FileInputStream(file)), true);
    }

    public BufferedImage read(InputStream in, boolean close)
    throws IOException
    {
        try {
            read = true;
            props.clear();
            CRCInputStream crc = new CRCInputStream(in, new byte[0x2000]);
            PngInputStream data = new PngInputStream(crc);
            long sig = data.readLong();
            if (sig != SIGNATURE) {
                throw new PngError("Improper signature, expected 0x" +
                                   Long.toHexString(SIGNATURE).toUpperCase() + ", got 0x" +
                                   Long.toHexString(sig).toUpperCase());
            }
            Set seen = new HashSet();
            int state = STATE_START;
            while (state != STATE_END) {
                int length = data.readInt();
                if (length < 0)
                    throw new PngError("Bad chunk length: " + length);
                crc.reset();

                int type = data.readInt();
                String name = PngChunk.typeToString(type);
                
                if (PngChunk.isPrivate(type) && !PngChunk.isAncillary(type))
                    throw new PngError("Private critical chunk encountered: " + name);
                for (int i = 0; i < 4; i++) {
                    int c = 0xFF & (type >>> (8 * i));
                    if (c < 65 || (c > 90 && c < 97) || c > 122)
                        throw new PngError("Corrupted chunk type: " + name);
                }

                state = updateState(state, type, name);
                // System.err.println("read chunk " + name + ", state=" + state);
                PngChunk chunk = config.getChunk(type);

                if (chunk == null) {
                    if (!PngChunk.isAncillary(type))
                        throw new PngError("Critical chunk " + name + " cannot be skipped");
                    data.skipFully(length);
                } else {
                    try {
                        Integer key = Integers.valueOf(type);
                        if (!chunk.isMultipleOK()) {
                            if (seen.contains(key)) {
                                String msg = "Multiple " + name + " chunks are not allowed";
                                if (PngChunk.isAncillary(type)) {
                                    data.skipFully(length);
                                    throw new PngWarning(msg);
                                }
                                throw new PngError(msg);
                            } else {
                                seen.add(key);
                            }
                        }
                        chunk.read(data, length, this);
                    } catch (PngWarning warning) {
                        config.handleWarning(warning);
                    }
                }
                long calcChecksum = crc.getValue();
                long fileChecksum = data.readUnsignedInt();
                if (calcChecksum != fileChecksum)
                    throw new PngError("Bad CRC value for " + name + " chunk");
            }
            // TODO
            if (getColorType() == COLOR_TYPE_PALETTE && props.get(PALETTE_RED) == null)
                throw new PngError("Required PLTE chunk not found");
            if (config.getMetadataOnly())
                return null;
            return ImageFactory.create(this);
        } catch (PngError e) {
            throw e;
        } finally {
            if (close)
                in.close();
        }
    }

    public void handleFrame(BufferedImage image, int framesLeft)
    {
    }

    private static final int STATE_START = 0;
    private static final int STATE_SAW_IHDR = 1;
    private static final int STATE_SAW_IHDR_NO_PLTE = 2;
    private static final int STATE_SAW_PLTE = 3;
    private static final int STATE_IN_IDAT = 4;
    private static final int STATE_AFTER_IDAT = 5;
    private static final int STATE_END = 6;

    private int updateState(int state, int type, String name)
    throws IOException
    {
        switch (state) {
        case STATE_START:
            if (type != PngChunk.IHDR)
            return STATE_SAW_IHDR;
        case STATE_SAW_IHDR:
        case STATE_SAW_IHDR_NO_PLTE:
            switch (type) {
            case PngChunk.PLTE:
                if (state == STATE_SAW_IHDR_NO_PLTE)
                    throw new PngError("IHDR chunk must be first chunk");
                return STATE_SAW_PLTE;
            case PngChunk.IDAT:
                // TODO: move "Required PLTE chunk not found here"
                return config.getMetadataOnly() ? STATE_END : STATE_IN_IDAT;
            case PngChunk.bKGD:
            case PngChunk.hIST:
            case PngChunk.tRNS:
                return STATE_SAW_IHDR_NO_PLTE;
            default:
                return STATE_SAW_IHDR;
            }
        case STATE_SAW_PLTE:
            switch (type) {
            case PngChunk.cHRM:
            case PngChunk.gAMA:
            case PngChunk.iCCP:
            case PngChunk.sBIT:
            case PngChunk.sRGB:
                throw new PngException(name + " cannot appear after PLTE");
            case PngChunk.IDAT:
                return config.getMetadataOnly() ? STATE_END : STATE_IN_IDAT;
            case PngChunk.IEND:
                throw new PngException("Required data chunk(s) not found");
            default:
                return STATE_SAW_PLTE;
            }
        default:
            switch (type) {
            case PngChunk.PLTE:
            case PngChunk.cHRM:
            case PngChunk.gAMA:
            case PngChunk.iCCP:
            case PngChunk.sBIT:
            case PngChunk.sRGB:
            case PngChunk.bKGD:
            case PngChunk.hIST:
            case PngChunk.tRNS:
            case PngChunk.pHYs:
            case PngChunk.sPLT:
                throw new PngException(name + " cannot appear after IDAT");
            }
            switch (state) {
            case STATE_IN_IDAT:
                switch (type) {
                case PngChunk.IEND:
                    return STATE_END;
                case PngChunk.IDAT:
                    return STATE_IN_IDAT;
                default:
                    return STATE_AFTER_IDAT;
                }
            case STATE_AFTER_IDAT:
                switch (type) {
                case PngChunk.IEND:
                    return STATE_END;
                case PngChunk.IDAT:
                    throw new PngException("IDAT chunks must be consecutive");
                default:
                    return STATE_AFTER_IDAT;
                }
            }
        }
        // impossible
        throw new Error();
    }

    public int getWidth()
    {
        return getInt(WIDTH);
    }

    public int getHeight()
    {
        return getInt(HEIGHT);
    }

    public int getBitDepth()
    {
        return getInt(BIT_DEPTH);
    }

    public int getInterlace()
    {
        return getInt(INTERLACE);
    }

    public int getColorType()
    {
        return getInt(COLOR_TYPE);
    }

    // package protected
    int getSamples()
    {
        switch (getColorType()) {
        case COLOR_TYPE_GRAY_ALPHA: return 2;
        case COLOR_TYPE_RGB:        return 3;
        case COLOR_TYPE_RGB_ALPHA:  return 4;
        }
        return 1;
    }

    public float getGamma()
    {
        assertRead();
        if (props.containsKey(PngImage.GAMMA))
            return ((Number)props.get(PngImage.GAMMA)).floatValue();
        return config.getDefaultGamma();
    }

    public short[] getGammaTable()
    {
        assertRead();
        double gamma = getGamma();
        int bitDepth = getBitDepth();
        int size = 1 << ((bitDepth == 16 && !config.getReduce16()) ? 16 : 8);
        short[] gammaTable = new short[size];
        double decodingExponent =
            (double)config.getUserExponent() / (gamma * (double)config.getDisplayExponent());
        for (int i = 0; i < size; i++)
            gammaTable[i] = (short)(Math.pow((double)i / (size - 1), decodingExponent) * (size - 1));
        return gammaTable;
    }

    // TODO: gamma-correct background?
    public Color getBackground()
    {
        assertRead();
        switch (getColorType()) {
        case COLOR_TYPE_PALETTE:
            if (!props.containsKey(BACKGROUND_INDEX))
                return null;
            int index = getInt(BACKGROUND_INDEX);
            return new Color(0xFF & ((byte[])props.get(PALETTE_RED))[index],
                             0xFF & ((byte[])props.get(PALETTE_GREEN))[index],
                             0xFF & ((byte[])props.get(PALETTE_BLUE))[index]);
        case COLOR_TYPE_GRAY:
        case COLOR_TYPE_GRAY_ALPHA:
            if (!props.containsKey(BACKGROUND_GRAY))
                return null;
            int gray = getInt(BACKGROUND_GRAY) * 255 / ((1 << getBitDepth()) - 1);
            return new Color(gray, gray, gray);
            
        default:
            if (!props.containsKey(BACKGROUND_RED))
                return null;
            int r = getInt(BACKGROUND_RED);
            int g = getInt(BACKGROUND_GREEN);
            int b = getInt(BACKGROUND_BLUE);
            if (getBitDepth() == 16) {
                return new Color(r >> 8, g >> 8, b >> 8);
            } else {
                return new Color(r, g, b);
            }
        }
    }

    public Object getProperty(String name)
    {
        assertRead();
        return props.get(name);
    }

    public Map getProperties()
    {
        assertRead();
        return props;
    }

    public TextChunk getTextChunk(String key)
    {
        List list = (List)getProperty(TEXT_CHUNKS);
        if (key != null && list != null) {
            for (Iterator it = list.iterator(); it.hasNext();) {
                TextChunk chunk = (TextChunk)it.next();
                if (chunk.getKeyword().equals(key))
                    return chunk;
            }
        }
        return null;
    }

    // package-protected
    int getInt(String name)
    {
        assertRead();
        return ((Number)props.get(name)).intValue();
    }

    private void assertRead()
    {
        if (!read)
            throw new IllegalStateException("Image has not been read");
    }
}
