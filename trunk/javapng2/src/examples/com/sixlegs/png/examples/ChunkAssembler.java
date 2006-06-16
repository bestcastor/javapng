package com.sixlegs.png.examples;

import com.sixlegs.png.*;
import java.io.*;
import java.util.*;
import java.util.zip.*;

public class ChunkAssembler
{
    public static void main(String[] args)
    throws IOException
    {
        List files = new ArrayList(Arrays.asList(args));
        File dst = (File)files.remove(files.size() - 1);
        assemble(files, dst);
    }

    public static void assemble(List files, File dst)
    throws IOException
    {
        byte[] buf = new byte[0x2000];
        FileOutputStream out = new FileOutputStream(dst);
        CheckedOutputStream checked = new CheckedOutputStream(out, new CRC32());
        DataOutputStream data = new DataOutputStream(checked);
        try {
            data.writeLong(0x89504E470D0A1A0AL);
            for (Iterator it = files.iterator(); it.hasNext();) {
                File src = (File)it.next();
                String name = src.getName();
                int type = PngChunk.getType(name.substring(name.length() - 4));
                data.writeInt((int)src.length());
                data.flush();
                checked.getChecksum().reset();
                data.writeInt(type);
                InputStream in = new FileInputStream(src);
                try {
                    ChunkExploder.pipe(in, data, buf);
                } finally {
                    in.close();
                }
                data.flush();
                data.writeInt((int)checked.getChecksum().getValue());
            }
        } finally {
            data.close();
        }
    }
}
