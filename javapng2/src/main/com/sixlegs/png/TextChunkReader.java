/*
com.sixlegs.png - Java package to read and display PNG images
Copyright (C) 1998-2005 Chris Nokleberg

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
        chunks.add(new TextChunkImpl(keyword, text, language, translated, type));
    }

    private static class TextChunkImpl
    implements TextChunk
    {
        private String keyword;
        private String text;
        private String language;
        private String translated;
        private int type;
    
        public TextChunkImpl(String keyword, String text, String language, String translated, int type)
        {
            this.keyword = keyword;
            this.text = text;
            this.language = language;
            this.translated = translated;
            this.type = type;
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

        public int getType()
        {
            return type;
        }
    }
}
