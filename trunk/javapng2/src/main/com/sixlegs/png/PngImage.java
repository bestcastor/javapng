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
            CRCInputStream crc = new CRCInputStream(in, new byte[0x1000]);
            CountingInputStream count = new CountingInputStream(crc);
            PngInputStream data = new PngInputStream(count);
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
                    count.setCount(0);
                    data.setLength(length);
                    try {
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
                        chunk.read(data, this);
                        if (data.getRemaining() != 0)
                            throw new PngError(chunk + " read " + count.getCount() + " bytes, expected " + length);
                    } catch (PngWarning warning) {
                        data.skipFully(length - count.getCount());
                        config.handleWarning(warning);
                    }
                }
                long calcChecksum = crc.getValue();
                long fileChecksum = data.readUnsignedInt();
                if (calcChecksum != fileChecksum)
                    throw new PngError("Bad CRC value for " + name + " chunk");
            }
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
                if (getColorType() == PngConstants.COLOR_TYPE_PALETTE)
                    throw new PngError("Required PLTE chunk not found");
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
            case PngChunk.oFFs:
            case PngChunk.pCAL:
            case PngChunk.sCAL:
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
        return getInt(PngConstants.WIDTH);
    }

    public int getHeight()
    {
        return getInt(PngConstants.HEIGHT);
    }

    public int getBitDepth()
    {
        return getInt(PngConstants.BIT_DEPTH);
    }

    public int getInterlace()
    {
        return getInt(PngConstants.INTERLACE);
    }

    public int getColorType()
    {
        return getInt(PngConstants.COLOR_TYPE);
    }

    // package protected
    int getSamples()
    {
        switch (getColorType()) {
        case PngConstants.COLOR_TYPE_GRAY_ALPHA: return 2;
        case PngConstants.COLOR_TYPE_RGB:        return 3;
        case PngConstants.COLOR_TYPE_RGB_ALPHA:  return 4;
        }
        return 1;
    }

    public float getGamma()
    {
        assertRead();
        if (props.containsKey(PngConstants.GAMMA))
            return ((Number)props.get(PngConstants.GAMMA)).floatValue();
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
        case PngConstants.COLOR_TYPE_PALETTE:
            if (!props.containsKey(PngConstants.BACKGROUND_INDEX))
                return null;
            int index = getInt(PngConstants.BACKGROUND_INDEX);
            return new Color(0xFF & ((byte[])props.get(PngConstants.PALETTE_RED))[index],
                             0xFF & ((byte[])props.get(PngConstants.PALETTE_GREEN))[index],
                             0xFF & ((byte[])props.get(PngConstants.PALETTE_BLUE))[index]);
        case PngConstants.COLOR_TYPE_GRAY:
        case PngConstants.COLOR_TYPE_GRAY_ALPHA:
            if (!props.containsKey(PngConstants.BACKGROUND_GRAY))
                return null;
            int gray = getInt(PngConstants.BACKGROUND_GRAY) * 255 / ((1 << getBitDepth()) - 1);
            return new Color(gray, gray, gray);
            
        default:
            if (!props.containsKey(PngConstants.BACKGROUND_RED))
                return null;
            int r = getInt(PngConstants.BACKGROUND_RED);
            int g = getInt(PngConstants.BACKGROUND_GREEN);
            int b = getInt(PngConstants.BACKGROUND_BLUE);
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
        List list = (List)getProperty(PngConstants.TEXT_CHUNKS);
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
