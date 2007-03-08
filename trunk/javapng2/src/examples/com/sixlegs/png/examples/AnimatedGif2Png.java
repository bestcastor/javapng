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
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.zip.*;
import javax.imageio.*;
import javax.imageio.metadata.*;
import javax.imageio.stream.FileImageInputStream;
import org.w3c.dom.Node;

public class AnimatedGif2Png
{
    private static final int MIN_DELAY = 75; // ms
    
    public static void main(String[] args)
    throws IOException
    {
        convert(new File(args[0]), new File(args[1]));
    }

    public static void convert(File in, File out)
    throws IOException
    {
        ImageReader imageReader = ImageIO.getImageReadersByFormatName("GIF").next();
        imageReader.setInput(new FileImageInputStream(in));
        int index = 0;
        Set<Integer> entries = new HashSet<Integer>();
        List<Frame> frames = new ArrayList<Frame>();
        int[] prev = null;
        boolean different = false;
        try {
            for (;;) {
                // TODO: get palette from metadata instead of decoding image?
                ColorModel colorModel = imageReader.read(index).getColorModel();
                IndexColorModel icm = (IndexColorModel)colorModel;
                int[] palette = new int[icm.getMapSize()];
                icm.getRGBs(palette);
                for (int i = 0; i < palette.length; i++)
                    entries.add(palette[i]);
                if (!different && prev != null && !Arrays.equals(palette, prev))
                    different = true;
                prev = palette;
                
                IIOMetadata metadata = imageReader.getImageMetadata(index);
                Node node = metadata.getAsTree(metadata.getNativeMetadataFormatName());
                Node desc = getChild(node, "ImageDescriptor");
                Node gce = getChild(node, "GraphicControlExtension");
                frames.add(new Frame(palette,
                                     new Rectangle(Integer.parseInt(getAttr(desc, "imageLeftPosition")),
                                                   Integer.parseInt(getAttr(desc, "imageTopPosition")),
                                                   Integer.parseInt(getAttr(desc, "imageWidth")),
                                                   Integer.parseInt(getAttr(desc, "imageHeight"))),
                                     Math.max(10 * Integer.parseInt(getAttr(gce, "delayTime")), MIN_DELAY),
                                     mapDisposal(getAttr(gce, "disposalMethod"))));
                index++;
            }
        } catch (IndexOutOfBoundsException e) {
            // no more frames
        }

        Frame first = frames.get(0);
        PngWriter w = new PngWriter(out);
        if (entries.size() <= 256) {
            if (different)
                // this should be rare, implement it later
                throw new UnsupportedOperationException("implement palette remapping");
            writePaletted(w, imageReader, frames);
        } else {
            writeTruecolor(w, imageReader, frames);
        }
        imageReader.dispose();
    }

    private static void writePaletted(PngWriter w, ImageReader imageReader, List<Frame> frames)
    throws IOException
    {
        Frame first = frames.get(0);
        w.start(first.bounds.getSize(), PngConstants.COLOR_TYPE_PALETTE, first.palette, frames.size());

        // TODO: if palette is <= 64 entries, use smaller bit depth
        ByteArrayOutputStream raw = new ByteArrayOutputStream();
        byte[] row = new byte[first.bounds.width];
        int index = 0;
        for (Frame frame : frames) {
            BufferedImage image = imageReader.read(index++);
            raw.reset();
            DeflaterOutputStream defl = new DeflaterOutputStream(raw);
            for (int y = 0, h = frame.bounds.height; y < h; y++) {
                image.getRaster().getDataElements(0, y, frame.bounds.width, 1, row);
                defl.write(0); // filter byte
                defl.write(row, 0, frame.bounds.width);
            }
            defl.close();
            w.frame(frame, raw.toByteArray());
        }
        w.finish();
    }

    private static void writeTruecolor(PngWriter w, ImageReader imageReader, List<Frame> frames)
    throws IOException
    {
        // TODO: could use tRNS instead of alpha channel in some cases for better compression
        Frame first = frames.get(0);
        w.start(first.bounds.getSize(), PngConstants.COLOR_TYPE_RGB_ALPHA, null, frames.size());
        BufferedImage canvas =
            new BufferedImage(first.bounds.width, first.bounds.height, BufferedImage.TYPE_INT_ARGB);
        byte[] buf = new byte[0x2000];
        int index = 0;
        File temp = File.createTempFile("frame", ".png");
        for (Frame frame : frames) {
            BufferedImage image = imageReader.read(index++);
            BufferedImage subcanvas = canvas.getSubimage(0, 0, frame.bounds.width, frame.bounds.height);
            Graphics2D g = subcanvas.createGraphics();
            g.drawImage(image, null, null);
            g.dispose();
            ImageIO.write(subcanvas, "PNG", temp);
            w.frame(frame, extractData(temp, buf));
        }
        temp.delete();
        w.finish();
    }

    private static class PngWriter
    {
        private final DataOutputStream data;
        private final ChunkWriter chunk = new ChunkWriter();
        private int seq;
        
        public PngWriter(File out)
        throws IOException
        {
            this.data = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(out)));
        }

        public void start(Dimension size, int colorType, int[] palette, int numFrames)
        throws IOException
        {
            data.writeLong(0x89504E470D0A1A0AL);

            chunk.start(PngConstants.IHDR);
            chunk.writeInt(size.width);
            chunk.writeInt(size.height);
            chunk.writeByte(8); // bit depth
            chunk.writeByte(colorType);
            chunk.writeByte(PngConstants.COMPRESSION_BASE);
            chunk.writeByte(PngConstants.FILTER_BASE);
            chunk.writeByte(PngConstants.INTERLACE_NONE); // TODO: does ImageIO write interlaced?
            int ihdr_crc = chunk.finish(data);
            int plte_crc = 0;

            if (colorType == PngConstants.COLOR_TYPE_PALETTE) {
                chunk.start(PngConstants.PLTE);
                int numTrans = 0;
                int maxTrans = 0;
                for (int i = 0; i < palette.length; i++) {
                    Color color = new Color(palette[i], true);
                    chunk.writeByte(color.getRed());
                    chunk.writeByte(color.getGreen());
                    chunk.writeByte(color.getBlue());
                    if (color.getAlpha() != 0xFF) {
                        numTrans++;
                        maxTrans = i;
                    }
                }
                plte_crc = chunk.finish(data);

                if (numTrans > 0) {
                    chunk.start(PngConstants.tRNS);
                    for (int i = 0; i <= maxTrans; i++)
                        chunk.writeByte(new Color(palette[i]).getAlpha());
                    chunk.finish(data);
                }
            }

            chunk.start(AnimatedPngImage.acTl);
            chunk.writeInt(numFrames);
            chunk.writeInt(0); // TODO: numIterations
            chunk.writeInt(ihdr_crc);
            chunk.writeInt(plte_crc);
            chunk.finish(data);
        }

        public void frame(Frame frame, byte[] bytes)
        throws IOException
        {
            chunk.start(AnimatedPngImage.fcTl);
            chunk.writeInt(seq++);
            chunk.writeInt(frame.bounds.width);
            chunk.writeInt(frame.bounds.height);
            chunk.writeInt(frame.bounds.x);
            chunk.writeInt(frame.bounds.y);
            chunk.writeShort(frame.delayTime);
            chunk.writeShort(1000);
            chunk.writeByte(frame.disposalMethod);
            chunk.finish(data);

            if (seq == 1) {
                chunk.start(PngConstants.IDAT);
            } else {
                chunk.start(AnimatedPngImage.fdAt);
                chunk.writeInt(seq++);
            }
            chunk.write(bytes);
            chunk.finish(data);
        }

        public void finish()
        throws IOException
        {
            chunk.start(PngConstants.IEND);
            chunk.finish(data);
            data.close();
        }
    }

    private static class ChunkWriter
    extends DataOutputStream
    {
        public ChunkWriter()
        {
            super(new ByteArrayOutputStream());
        }

        public void start(int type)
        throws IOException
        {
            ((ByteArrayOutputStream)out).reset();
            writeInt(type);
        }

        public int finish(DataOutput data)
        throws IOException
        {
            byte[] bytes = ((ByteArrayOutputStream)out).toByteArray();
            int crc = crc(bytes);
            data.writeInt(bytes.length - 4);
            data.write(bytes);
            data.writeInt(crc);
            return crc;
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

    private static class Frame
    {
        final int[] palette;
        final Rectangle bounds;
        final int delayTime;
        final int disposalMethod;

        public Frame(int[] palette, Rectangle bounds, int delayTime, int disposalMethod)
        {
            this.palette = palette;
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
