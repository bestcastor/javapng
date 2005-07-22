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

class TextChunkReader
extends PngChunk
{
    public boolean isMultipleOK(int type)
    {
        return true;
    }
    
    public void read(int type, PngInputStream in, PngImage png)
    throws IOException
    {
        String keyword = PngUtils.readKeyword(in);
        String enc = PngUtils.ISO_8859_1;
        boolean compressed = false;
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
            int flag = in.readByte();
            int method = in.readByte();
            if (flag == 1) {
                compressed = true;
                if (method != 0)
                    throw new PngWarning("Unrecognized " + this + " compression method: " + method);
            } else if (flag != 0) {
                throw new PngWarning("Illegal " + this + " compression flag: " + flag);
            }
            language = PngUtils.readString(in, PngUtils.US_ASCII);
            translated = PngUtils.readString(in, PngUtils.UTF_8);
        }

        byte[] data;
        if (compressed) {
            data = PngUtils.readCompressed(in, in.getRemaining());
        } else {
            data = new byte[in.getRemaining()];
            in.readFully(data);
        }
        String text = new String(data, enc);
        Map props = png.getProperties();
        List chunks = (List)props.get(PngConstants.TEXT_CHUNKS);
        if (chunks == null)
            props.put(PngConstants.TEXT_CHUNKS, chunks = new ArrayList());
        chunks.add(new TextChunkImpl(keyword, text, language, translated));
    }

    private static class TextChunkImpl
    implements TextChunk
    {
        private String keyword;
        private String text;
        private String language;
        private String translated;
    
        public TextChunkImpl(String keyword, String text, String language, String translated)
        {
            this.keyword = keyword;
            this.text = text;
            this.language = language;
            this.translated = translated;
        }
    
        public String getKeyword()
        {
            return keyword;
        }

        public String getTranslatedKeyword()
        {
            return translated;
        }

        public String getLanguage()
        {
            return language;
        }

        public String getText()
        {
            return text;
        }
    }
}
