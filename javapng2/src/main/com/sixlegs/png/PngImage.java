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

// TODO: progressive rendering
public class PngImage
{
    public static final String BACKGROUND = "background";
    public static final String BACKGROUND_INDEX = "backgroundIndex";
    public static final String BACKGROUND_LOW_BYTES = "backgroundLowBytes";
    public static final String BIT_DEPTH = "bitDepth";
    public static final String COLOR_TYPE = "colorType";
    public static final String COMPRESSION = "compression";
    public static final String FILTER = "filter";
    public static final String HEIGHT = "height";
    public static final String INTERLACE = "interlace";
    public static final String PALETTE = "palette";
    public static final String PALETTE_SIZE = "paletteSize";
    public static final String SIGNIFICANT_BITS = "significantBits";
    public static final String TEXT_CHUNKS = "textChunks";
    public static final String TRANSPARENCY = "transparency";
    public static final String TRANSPARENCY_LOW_BYTES = "transparencyLowBytes";
    public static final String TRANSPARENCY_SIZE = "transparencySize";
    public static final String WIDTH = "width";

    /* package */ static final String DATA = "data";

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
    private Map props = new TreeMap();
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
            CRCInputStream crc = new CRCInputStream(in, new byte[0x2000]);
            PngInputStream data = new PngInputStream(crc);
            long sig = data.readLong();
            if (sig != SIGNATURE) {
                throw new PngError("Improper signature, expected 0x" +
                                   Long.toHexString(SIGNATURE).toUpperCase() + ", got " +
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
                System.err.println("read chunk " + name + ", state=" + state);
                PngChunk chunk = config.getChunk(type);

                if (chunk == null) {
                    if (!PngChunk.isAncillary(type))
                        throw new PngError("Critical chunk " + name + " cannot be skipped");
                    data.skipFully(length);
                } else {
                    Integer key = Integers.valueOf(type);
                    if (!chunk.isMultipleOK()) {
                        if (seen.contains(key)) {
                            String msg = "Multiple " + name + " chunks are not allowed";
                            if (PngChunk.isAncillary(type))
                                throw new PngWarning(msg);
                            throw new PngError(msg);
                        } else {
                            seen.add(key);
                        }
                    }
                    chunk.read(data, length, props, config);
                }
                long calcChecksum = crc.getValue();
                long fileChecksum = data.readUnsignedInt();
                if (calcChecksum != fileChecksum)
                    throw new PngError("Bad CRC value for " + name + " chunk");
            }
            return new ImageFactory().create(config, props);
        } finally {
            if (close)
                in.close();
        }
    }

    private static final int STATE_START = 0;
    private static final int STATE_SAW_IHDR = 1;
    private static final int STATE_SAW_PLTE = 2;
    private static final int STATE_IN_IDAT = 3;
    private static final int STATE_AFTER_IDAT = 4;
    private static final int STATE_END = 5;

    private int updateState(int state, int type, String name)
    throws IOException
    {
        switch (state) {
        case STATE_START:
            if (type != PngChunk.IHDR)
                throw new PngError("IHDR chunk must be first chunk");
            return STATE_SAW_IHDR;
        case STATE_SAW_IHDR:
            switch (type) {
            case PngChunk.PLTE:
                return STATE_SAW_PLTE;
            case PngChunk.IDAT:
                return STATE_IN_IDAT;
            case PngChunk.bKGD:
            case PngChunk.hIST:
            case PngChunk.tRNS:
                throw new PngException(name + " cannot appear before PLTE");
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
                return STATE_IN_IDAT;
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

    public int getInterlaceType()
    {
        return getInt(INTERLACE);
    }

    public int getColorType()
    {
        return getInt(COLOR_TYPE);
    }

    public boolean hasAlphaChannel()
    {
        switch (getColorType()) {
        case COLOR_TYPE_GRAY_ALPHA:
        case COLOR_TYPE_RGB_ALPHA:
            return true;
        default:
            return false;
        }
    }

    public boolean isGrayscale()
    {
        switch (getColorType()) {
        case COLOR_TYPE_GRAY:
        case COLOR_TYPE_GRAY_ALPHA:
            return true;
        default:
            return false;
        }
    }

    public boolean isIndexedColor()
    {
        return getColorType() == COLOR_TYPE_PALETTE;
    }

    public Color getBackground()
    {
        return (Color)getProperty(BACKGROUND);
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

    /*
    public TextChunk getTextChunk(String key)
    {
        Map map = (Map)getProperty(TEXT_CHUNKS);
        return (map != null) ? (TextChunk)map.get(key) : null;
    }
    */

    private int getInt(String name)
    {
        return getInt(props, name);
    }

    // package-protected
    static int getInt(Map map, String name)
    {
        return ((Number)map.get(name)).intValue();
    }

    private void assertRead()
    {
        if (!read)
            throw new IllegalStateException("Image has not been read");
    }
}
