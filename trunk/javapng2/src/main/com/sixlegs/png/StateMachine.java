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

class StateMachine
{
    public static final int STATE_START = 0;
    public static final int STATE_SAW_IHDR = 1;
    public static final int STATE_SAW_IHDR_NO_PLTE = 2;
    public static final int STATE_SAW_PLTE = 3;
    public static final int STATE_IN_IDAT = 4;
    public static final int STATE_AFTER_IDAT = 5;
    public static final int STATE_END = 6;

    private PngImage png;
    private int state = STATE_START;
    private int type;

    public StateMachine(PngImage png)
    {
        this.png = png;
    }

    public int getState()
    {
        return state;
    }

    public int getType()
    {
        return type;
    }

    public void nextState(int type)
    throws PngError
    {
        state = nextState(png, state, this.type = type);
    }
        
    private static int nextState(PngImage png, int state, int type)
    throws PngError
    {
        if (PngChunk.isPrivate(type) && !PngChunk.isAncillary(type))
            throw new PngError("Private critical chunk encountered: " + PngChunk.getName(type));
        for (int i = 0; i < 4; i++) {
            int c = 0xFF & (type >>> (8 * i));
            if (c < 65 || (c > 90 && c < 97) || c > 122)
                throw new PngError("Corrupted chunk type: " + PngChunk.getName(type));
        }
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
                errorIfPaletted(png);
                return STATE_IN_IDAT;
            case PngChunk.bKGD:
                return STATE_SAW_IHDR_NO_PLTE;
            case PngChunk.tRNS:
                errorIfPaletted(png);
                return STATE_SAW_IHDR_NO_PLTE;
            case PngChunk.hIST:
                throw new PngError("PLTE must precede hIST");
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
                throw new PngError(PngChunk.getName(type) + " cannot appear after PLTE");
            case PngChunk.IDAT:
                return STATE_IN_IDAT;
            case PngChunk.IEND:
                throw new PngError("Required data chunk(s) not found");
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
                throw new PngError(PngChunk.getName(type) + " cannot appear after IDAT");
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
                    throw new PngError("IDAT chunks must be consecutive");
                default:
                    return STATE_AFTER_IDAT;
                }
            }
        }
        // impossible
        throw new Error();
    }

    private static void errorIfPaletted(PngImage png)
    throws PngError
    {
        if (png.getColorType() == PngConstants.COLOR_TYPE_PALETTE)
            throw new PngError("Required PLTE chunk not found");
    }
}
