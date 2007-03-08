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
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.zip.*;
import javax.imageio.*;
import javax.imageio.metadata.*;
import javax.imageio.stream.FileImageInputStream;
import org.w3c.dom.Node;

public class AnimatedGif2Png
{
    public static void main(String[] args)
    throws IOException
    {
        convert(new File(args[0]), new File(args[1]));
    }

    public static void convert(final File in, final File out)
    throws IOException
    {
        ImageReader imageReader = ImageIO.getImageReadersByFormatName("GIF").next();
        imageReader.setInput(new FileImageInputStream(in));
        int index = 0;
        final List<Frame> frames = new ArrayList<Frame>();
        try {
            for (;;) {
                BufferedImage image = imageReader.read(index);
                File temp = File.createTempFile("frame", ".png");
                ImageIO.write(image, "PNG", temp);
                IIOMetadata metadata = imageReader.getImageMetadata(index);
                Node node = metadata.getAsTree(metadata.getNativeMetadataFormatName());
                Node desc = getChild(node, "ImageDescriptor");
                Node gce = getChild(node, "GraphicControlExtension");
                frames.add(new Frame(temp,
                                     new Rectangle(Integer.parseInt(getAttr(desc, "imageLeftPosition")),
                                                   Integer.parseInt(getAttr(desc, "imageTopPosition")),
                                                   Integer.parseInt(getAttr(desc, "imageWidth")),
                                                   Integer.parseInt(getAttr(desc, "imageHeight"))),
                                     Integer.parseInt(getAttr(gce, "delayTime")),
                                     mapDisposal(getAttr(gce, "disposalMethod"))));
                index++;
            }
        } catch (IndexOutOfBoundsException e) {
            // no more frames
        }

        PngImage png = new PngImage() {
            private long dataStart;
            private long dataEnd;
            private long chunkEnd;
                
            @Override protected boolean readChunk(int type, DataInput in, long offset, int length)
            throws IOException
            {
                chunkEnd = offset + length + 4;
                if (dataEnd == 0) {
                    if (dataStart > 0)
                        dataEnd = offset - 8;
                }
                if (type == PngConstants.IEND)
                    finish();
                return super.readChunk(type, in, offset, length);
            }

            @Override protected BufferedImage createImage(InputStream in)
            throws IOException
            {
                dataStart = chunkEnd;
                return null;
            }
                
            private void finish()
            throws IOException
            {
                System.err.println("writing " + out);
                RandomAccessFile rf = new RandomAccessFile(frames.get(0).file, "r");
                byte[] buf = new byte[0x2000];
                DataOutputStream os = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(out)));

                copy(rf, 0, dataStart, os, buf);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                DataOutput data = new DataOutputStream(baos);
                data.writeInt(AnimatedPngImage.acTl);
                data.writeInt(frames.size());
                data.writeInt(0); // TODO: numIterations
                data.writeInt(((Number)getProperty("ihdr_crc")).intValue());
                data.writeInt(((Number)getProperty("plte_crc")).intValue());
                data.writeInt(crc(baos.toByteArray()));
                os.writeInt(baos.size() - 8);
                os.write(baos.toByteArray());

                int seq = 0;
                for (Frame frame : frames) {
                    baos.reset();
                    data = new DataOutputStream(baos);
                    data.writeInt(AnimatedPngImage.fcTl);
                    data.writeInt(seq++);
                    data.writeInt(frame.bounds.width);
                    data.writeInt(frame.bounds.height);
                    data.writeInt(frame.bounds.x);
                    data.writeInt(frame.bounds.y);
                    data.writeShort(frame.delayTime);
                    data.writeShort(0);
                    data.writeByte(frame.disposalMethod);
                    data.writeInt(crc(baos.toByteArray()));
                    os.writeInt(baos.size() - 8);
                    os.write(baos.toByteArray());
                    
                    baos.reset();
                    data = new DataOutputStream(baos);
                    if (seq == 1) {
                        data.writeInt(PngConstants.IDAT);
                    } else {
                        data.writeInt(AnimatedPngImage.fdAt);
                        data.writeInt(seq++);
                    }
                    data.write(extractData(frame.file, buf));
                    data.writeInt(crc(baos.toByteArray()));
                    os.writeInt(baos.size() - 8);
                    os.write(baos.toByteArray());
                }
                copy(rf, dataEnd, chunkEnd - dataEnd, os, buf);
                rf.close();
                os.close();
            }
        };
        png.read(frames.get(0).file);

        for (Frame frame : frames)
            frame.file.delete();
    }

    private static void copy(RandomAccessFile rf, long off, long len, OutputStream out, byte[] buf)
    throws IOException
    {
        rf.seek(off);
        while (len > 0) {
            int amt = (int)Math.min(len, buf.length);
            rf.readFully(buf, 0, amt);
            out.write(buf, 0, amt);
            len -= amt;
        }
    }

    private static Node getChild(Node node, String name)
    {
        for (node = node.getFirstChild(); node != null; node = node.getNextSibling())
            if (name.equals(node.getNodeName()))
                break;
        return node;
    }
    
    private static String getAttr(Node element, String name)
    {
        return element.getAttributes().getNamedItem(name).getNodeValue();
    }

    private static int mapDisposal(String gifDisposalMethod)
    {
        if (gifDisposalMethod.equals("restoreToBackgroundColor"))
            return FrameControl.DISPOSE_BACKGROUND;
        if (gifDisposalMethod.equals("restoreToPrevious"))
            return FrameControl.DISPOSE_PREVIOUS;
        return FrameControl.DISPOSE_NONE | 8; // turn on blend
    }

    private static byte[] extractData(File file, final byte[] buf)
    throws IOException
    {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        (new PngImage(new PngConfig.Builder().readLimit(PngConfig.READ_EXCEPT_METADATA).build()) {
            @Override protected BufferedImage createImage(InputStream in) throws IOException {
                pipe(in, out, buf);
                return null;
            }
        }).read(file);
        return out.toByteArray();
    }

    private static int crc(byte[] bytes)
    throws IOException
    {
        CheckedOutputStream checked = new CheckedOutputStream(new NullOutputStream(), new CRC32());
        DataOutputStream data = new DataOutputStream(checked);
        data.write(bytes);
        data.flush();
        return (int)checked.getChecksum().getValue();
    }

    private static void pipe(InputStream in, OutputStream out, byte[] buf)
    throws IOException
    {
        for (;;) {
            int amt = in.read(buf);
            if (amt < 0)
                break;
            out.write(buf, 0, amt);
        }
    }

    private static class Frame
    {
        final File file;
        final Rectangle bounds;
        final int delayTime;
        final int disposalMethod;

        public Frame(File file, Rectangle bounds, int delayTime, int disposalMethod)
        {
            this.file = file;
            this.bounds = bounds;
            this.delayTime = delayTime;
            this.disposalMethod = disposalMethod;
        }
    }

    private static class NullOutputStream extends OutputStream
    {
        public void write(int b) { }
        public void write(byte[] b) { }
        public void write(byte[] b, int off, int len) { }
    }
}
