/*
com.sixlegs.png - Java package to read and display PNG images
Copyright (C) 1998-2006 Chris Nokleberg

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

package com.sixlegs.png.examples;

import com.sixlegs.png.*;
import java.awt.image.*;
import java.io.*;

// TODO: this is a work in progress
public class Png2AnimatedPng
{
    public static void main(final String[] args) throws Exception {
        final DataOutputStream out = new DataOutputStream(new BufferedOutputStream(System.out));
        final PngConfig config = new PngConfig.Builder().readLimit(PngConfig.READ_EXCEPT_DATA).build();
        out.writeLong(0x89504E470D0A1A0AL); // signature
        (new PngImage(config) {
            private ChunkWriter chunk = new ChunkWriter();
            private int seq = 0;
            protected boolean readChunk(int type, DataInput in, long offset, int length) throws IOException {
                byte[] data = new byte[length];
                in.readFully(data);

                if (type == PngConstants.IEND) {
                    for (int i = 1; i < args.length; i++) {
                        nextFrame();
                        (new PngImage(config) {
                            protected boolean readChunk(int type, DataInput in, long offset, int length) throws IOException {
                                if (type == PngConstants.IDAT) {
                                    byte[] data = new byte[length];
                                    in.readFully(data);
                                    chunk.start(AnimatedPngImage.fdAT);
                                    chunk.writeInt(seq++);
                                    chunk.write(data);
                                    chunk.finish(out);
                                    return true;
                                } else {
                                    return super.readChunk(type, in, offset, length);
                                }
                            }
                        }).read(new File(args[i]));
                    }
                }

                chunk.start(type);
                chunk.write(data);
                chunk.finish(out);
                boolean result = super.readChunk(type, new DataInputStream(new ByteArrayInputStream(data)), offset, length);

                if (type == PngConstants.IHDR) {
                    chunk.start(AnimatedPngImage.acTL);
                    chunk.writeInt(args.length);
                    chunk.writeInt(0);
                    chunk.finish(out);
                    nextFrame();
                }
                return result;
            }

            private void nextFrame() throws IOException {
                chunk.start(AnimatedPngImage.fcTL);
                chunk.writeInt(seq++);
                chunk.writeInt(getWidth());
                chunk.writeInt(getHeight());
                chunk.writeInt(0);
                chunk.writeInt(0);
                chunk.writeShort(500); // 1/2 second
                chunk.writeShort(1000);
                chunk.writeByte(1); // blend = false, dispose = none
                chunk.finish(out);
            }
        }).read(new File(args[0]));
        out.close();
    }
}
