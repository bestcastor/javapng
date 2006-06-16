package com.sixlegs.png.examples;

import com.sixlegs.png.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

public class ChunkExploder
{
    public static void main(String[] args)
    throws IOException
    {
        explode(new File(args[0]), new File(args[1]));
    }

    public static List explode(File src, final File dir)
    throws IOException
    {
        final List files = new ArrayList();
        (new PngImage(new PngConfig.Builder().warningsFatal(true).build()){
            int index = 0;
            protected PngChunk getChunk(int type) {
                final PngChunk sup = super.getChunk(type);
                return new PngChunk(){
                    public boolean isMultipleOK(int type) {
                        return sup.isMultipleOK(type);
                    }
                    public void read(int type, DataInput in, int length, PngImage png) throws IOException {
                        byte[] bytes = new byte[length];
                        in.readFully(bytes);
                        File dst = new File(dir, getFileName(type, index++));
                        files.add(dst);
                        OutputStream out = new FileOutputStream(dst);
                        try {
                            out.write(bytes);
                        } finally {
                            out.close();
                        }
                        if (!PngChunk.isAncillary(type))
                            sup.read(type, new DataInputStream(new ByteArrayInputStream(bytes)), length, png);                    
                    }
                };                    
            }
            protected BufferedImage createImage(InputStream in) throws IOException {
                byte[] buf = new byte[0x2000];
                File dst = new File(dir, getFileName(PngChunk.IDAT, index++));
                files.add(dst);
                OutputStream out = new FileOutputStream(dst);
                try {
                    pipe(in, out, buf);
                } finally {
                    out.close();
                }
                return null;
            }
        }).read(src);
        return files;
    }

    static String getFileName(int type, int index)
    {
        StringBuffer sb = new StringBuffer();
        String num = String.valueOf(index);
        for (int i = num.length(); i < 4; i++)
            sb.append('0');
        sb.append(num);
        sb.append('-');
        sb.append(PngChunk.getName(type));
        return sb.toString();
    }
     
    static void pipe(InputStream in, OutputStream out, byte[] buf)
    throws IOException
    {
        for (;;) {
            int amt = in.read(buf);
            if (amt < 0)
                break;
            out.write(buf, 0, amt);
        }
    }
}
