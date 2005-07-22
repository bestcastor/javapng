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

/**
 * TODO
 */
public class ExtendedPngConfig
extends CompletePngConfig
{
    private static final PngChunk gIFg = new Chunk_gIFg();
    private static final PngChunk gIFx = new Chunk_gIFx();
    private static final PngChunk oFFs = new Chunk_oFFs();
    private static final PngChunk pCAL = new Chunk_pCAL();
    private static final PngChunk sCAL = new Chunk_sCAL();
    
    public PngChunk getChunk(PngImage png, int type)
    {
        switch (type) {
        case PngChunk.gIFg: return gIFg;
        case PngChunk.gIFx: return gIFx;
        case PngChunk.oFFs: return oFFs;
        case PngChunk.pCAL: return pCAL;
        case PngChunk.sCAL: return sCAL;
        }
        return super.getChunk(png, type);
    }
}
