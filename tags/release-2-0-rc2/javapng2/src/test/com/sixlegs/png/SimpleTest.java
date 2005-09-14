package com.sixlegs.png;

import com.sixlegs.png.examples.BeforeDataHook;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Map;
import junit.framework.*;

public class SimpleTest
extends PngTestCase
{
    public void testRecolorMonochrome()
    throws Exception
    {
        PngImage png = new PngImage(new BeforeDataHook(){
            public void process(PngImage png) {
                if (png.getBitDepth() == 1 &&
                    png.getColorType() == PngConstants.COLOR_TYPE_GRAY) {
                    Map props = png.getProperties();
                    props.put(PngConstants.COLOR_TYPE,
                              new Integer(PngConstants.COLOR_TYPE_PALETTE));
                    props.put(PngConstants.PALETTE, new byte[]{
                        (byte)255, 0, 0,
                        (byte)255, (byte)255, 0
                    });
                }
            }
        });
        InputStream in = getClass().getResourceAsStream("/images/suite/basn0g01.png");
        BufferedImage img = png.read(in, true);
        javax.imageio.ImageIO.write(img, "PNG", File.createTempFile("recolor", ".png"));
    }

    public void testPrivateChunk()
    throws Exception
    {
        PngImage png = readResource("/images/misc/anigif.png", new MyPngConfig());
        byte[] bytes = (byte[])png.getProperty(MyPngConfig.ORIGINAL_GIF);

        assertEquals("MSOFFICE9.0", new String(bytes, 0, 11, "US-ASCII"));
        
        File file = File.createTempFile("msog", ".gif");
        OutputStream out = new FileOutputStream(file);
        out.write(bytes, 11, bytes.length - 11);
        out.close();

        BufferedImage image = javax.imageio.ImageIO.read(file);
        assertEquals(32, image.getWidth());
        assertEquals(32, image.getHeight());        
    }

    private static class MyPngConfig
    extends PngConfig
    {
        private static final String ORIGINAL_GIF = "original_gif";
        private static final int msOG = PngChunk.getType("msOG");

        private static final PngChunk CHUNK = new PngChunk(){
            public void read(int type, PngInputStream in, PngImage png) throws IOException {
                byte[] bytes = new byte[in.getRemaining()];
                in.readFully(bytes);
                png.getProperties().put(ORIGINAL_GIF, bytes);
            }
        };
        
        public PngChunk getChunk(PngImage png, int type)
        {
            return (type == msOG) ? CHUNK : super.getChunk(png, type);
        }
    }

    public void testRead()
    throws Exception
    {
        PngImage png = readResource("/images/misc/cc1.png");
        assertEquals(138, png.getWidth());
        assertEquals(180, png.getHeight());
    }

    public void testReadAncillary()
    throws Exception
    {
        PngImage png = readResource("/images/misc/anigif.png");
        assertEquals(32, png.getWidth());
        assertEquals(32, png.getHeight());
    }
    
    public void testErrors()
    throws Exception
    {
        errorHelper("/images/suite/x00n0g01.png");
        errorHelper("/images/suite/xcrn0g04.png");
        errorHelper("/images/suite/xlfn0g04.png");
    }

    public void errorHelper(String path)
    throws Exception
    {
        try {
            readResource(path);
            fail("Expected exception");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private PngImage readResource(String path)
    throws IOException
    {
        return readResource(path, new PngConfig());
    }

    private PngImage readResource(String path, PngConfig config)
    throws IOException
    {
        PngImage png = new PngImage(config);
        InputStream in = getClass().getResourceAsStream(path);
        png.read(in, true);
        return png;
    }

    public SimpleTest(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        return getSuite(SimpleTest.class);
    }
}
