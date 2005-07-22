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

import java.io.*;
import java.util.*;

class Chunk_gIFx
extends PngChunk
{
    public void read(int type, PngInputStream in, PngImage png)
    throws IOException
    {
        byte[] id = new byte[8];
        in.readFully(id);
        byte[] authCode = new byte[3];
        in.readFully(authCode);
        byte[] data = new byte[in.getRemaining()];
        in.readFully(data);

        Map props = png.getProperties();
        List extensions = (List)props.get(ExtendedPngConstants.GIF_APPLICATION_EXTENSIONS);
        if (extensions == null) {
            props.put(ExtendedPngConstants.GIF_APPLICATION_EXTENSIONS,
                      extensions = new ArrayList());
        }
        extensions.add(new ExtensionImpl(new String(id, "US-ASCII"), authCode, data));
    }

    private static class ExtensionImpl
    implements GifApplicationExtension
    {
        private String id;
        private byte[] authCode;
        private byte[] data;

        public ExtensionImpl(String id, byte[] authCode, byte[] data)
        {
            this.id = id;
            this.authCode = authCode;
            this.data = data;
        }

        public String getId()
        {
            return id;
        }

        public byte[] getAuthenticationCode()
        {
            return (byte[])authCode.clone();
        }
        
        public byte[] getApplicationData()
        {
            return (byte[])data.clone();
        }
    }
}
