package com.sixlegs.png;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import javax.imageio.*;
import junit.framework.*;

// TODO: reduce reliance on ImageIO for checksum calc
public class TestSimple
extends PngTestCase
{
    private static final int msOG_type = PngConstants.getChunkType("msOG");
    private static final int heRB_type = PngConstants.getChunkType("heRB");
    private byte[] buf = new byte[0x2000];

    public void testNonFatalWarning()
    throws Exception
    {
        new PngImage().read(getClass().getResourceAsStream("/images/broken/gama_zero.png"), true);
    }

    public void testReadSuggestedPalette()
    throws Exception
    {
        PngImage png = readResource("/images/misc/ps2n2c16.png");
        SuggestedPalette splt =
            (SuggestedPalette)((List)png.getProperty(PngConstants.SUGGESTED_PALETTES)).get(0);
        assertEquals("six-cube", splt.getName());
        assertEquals(16, splt.getSampleDepth());
        assertEquals(216, splt.getSampleCount());
        short[] pixel = new short[4];
        splt.getSample(1, pixel);
        assertEquals(0, pixel[0]);
        assertEquals(0, pixel[1]);
        assertEquals(51, pixel[2]);
        assertEquals(255, pixel[3]);
        assertEquals(0, splt.getFrequency(0));
        assertEquals(0, splt.getFrequency(1));
    }

    public void testGetBackground()
    throws Exception
    {
        assertEquals(Color.yellow, readResource("/images/suite/bgyn6a16.png").getBackground());
        assertEquals(Color.black, readResource("/images/suite/bgbn4a08.png").getBackground());
        assertEquals(Color.white, readResource("/images/suite/bgwn6a08.png").getBackground());
        assertEquals(new Color(0xABABAB), readResource("/images/suite/tbgn3p08.png").getBackground());
        assertNull(readResource("/images/suite/basn0g01.png").getBackground());
    }

    public void testReadExceptMultipleDataChunks()
    throws Exception
    {
        readResource("/images/suite/oi4n0g16.png",
                     new PngImage(new PngConfig.Builder().readLimit(PngConfig.READ_EXCEPT_DATA).build()));
    }

    public void testReadTextChunk()
    throws Exception
    {
        PngImage png = readResource("/images/suite/ct1n0g04.png");
        TextChunk title = png.getTextChunk("Title");
        assertEquals("Title", title.getKeyword());
        assertNull(title.getTranslatedKeyword());
        assertNull(title.getLanguage());
        assertEquals("PngSuite", title.getText());
        assertEquals(PngConstants.tEXt, title.getType());

        assertNull(png.getTextChunk("foobar"));
    }

    public void testSubsampling()
    throws Exception
    {
        subsamplingHelper("/images/misc/penguin.png", 3, 3, 923164955L);
        subsamplingHelper("/images/misc/pngtest.png", 3, 3, 1930297805L);
        try {
            readResource("/images/suite/s02n3p01.png",
                         new PngImage(new PngConfig.Builder().sourceSubsampling(3, 3, 2, 2).build()));
            fail("expected exception");
        } catch (IllegalStateException ignore) { }
    }

    private BufferedImage subsamplingHelper(String path, int xsub, int ysub, long expect)
    throws Exception
    {
        PngImage png = new PngImage(new PngConfig.Builder()
                                    .sourceSubsampling(xsub, ysub, 0, 0)
                                    .build());
        BufferedImage image = png.read(getClass().getResourceAsStream(path), true);
        assertChecksum(expect, image, "subsample");
        return image;
    }

    public void testSourceRegions()
    throws Exception
    {
        regionHelper("/images/misc/penguin.png", new Rectangle(75, 0, 105, 125), 490287408L);
        regionHelper("/images/misc/pngtest.png", new Rectangle(10, 20, 30, 40), 2689440455L);
        try {
            regionHelper("/images/misc/pngtest.png", new Rectangle(10, 20, 100, 100), 0L);
            fail("expected exception");
        } catch (IllegalStateException ignore) { }
    }

    private void regionHelper(String path, Rectangle region, long expect)
    throws Exception
    {
        PngImage png = new PngImage(new PngConfig.Builder().sourceRegion(region).build());
        BufferedImage image = png.read(getClass().getResourceAsStream(path), true);
        assertEquals(region.width, image.getWidth());
        assertEquals(region.height, image.getHeight());
        assertChecksum(expect, image, "region");
    }

    private void assertChecksum(long expect, BufferedImage image, String desc)
    throws Exception
    {
        File file = File.createTempFile(desc, ".png");
        javax.imageio.ImageIO.write(image, "PNG", file);
        assertEquals(expect, getChecksum(new java.util.zip.CRC32(), file, buf));
        file.delete();
    }
    
    public void testRecolorMonochrome()
    throws Exception
    {
        PngImage png = new PngImage(){
            protected BufferedImage createImage(InputStream in, Dimension size) throws IOException {
                if (getBitDepth() == 1 &&
                    getColorType() == PngConstants.COLOR_TYPE_GRAY) {
                    Map props = getProperties();
                    props.put(PngConstants.COLOR_TYPE,
                              new Integer(PngConstants.COLOR_TYPE_PALETTE));
                    props.put(PngConstants.PALETTE, new byte[]{
                        (byte)255, 0, 0,
                        (byte)255, (byte)255, 0
                    });
                }
                return super.createImage(in, size);
            }
        };
        InputStream in = getClass().getResourceAsStream("/images/suite/basn0g01.png");
        File file = File.createTempFile("recolor", ".png");
        javax.imageio.ImageIO.write(png.read(in, true), "PNG", file);
        assertEquals(2661639413L, getChecksum(new java.util.zip.CRC32(), file, buf));
        new PngImage().read(file); // test reading from a file
        file.delete();
    }

    abstract private static class PrivateChunkReader
    extends PngImage
    {
        private final int type;
        public PrivateChunkReader(int type) { this.type = type; }

        abstract protected void readChunk(DataInput in) throws IOException;
        @Override protected void readChunk(int type, DataInput in, long off, int len) throws IOException {
            if (type == this.type) {
                readChunk(in);
            } else {
                super.readChunk(type, in, off, len);
            }
        }
    }

    public void testDataInputMethods()
    throws Exception
    {
        readResource("/images/misc/herbio.png", new PrivateChunkReader(heRB_type){
            protected void readChunk(DataInput in) throws IOException {
                assertEquals(true, in.readBoolean());
                assertEquals(false, in.readBoolean());
                assertEquals(250, in.readUnsignedByte());
                assertEquals((byte)250, in.readByte());
                assertEquals(50000, in.readUnsignedShort());
                assertEquals((short)50000, in.readShort());
                assertEquals('Z', in.readChar());
                assertEquals((float)Math.PI, in.readFloat());
                assertEquals(Math.PI, in.readDouble());
                assertEquals("Chris", in.readLine());
                assertEquals("Nokleberg", in.readUTF());
                assertEquals(2000000000, in.readInt());
            }
        });
        readResource("/images/misc/herbio.png", new PrivateChunkReader(heRB_type){
            protected void readChunk(DataInput in) throws IOException {
                skipFully(in, 39);
                assertEquals(2000000000, in.readInt());
            }
        });
        try {
            readResource("/images/misc/herbio.png", new PrivateChunkReader(heRB_type){
                protected void readChunk(DataInput in) throws IOException {
                    skipFully(in, 40);
                    in.readInt();
                    fail("expected exception");
                }
            });
        } catch (EOFException ignore) { }
        try {
            readResource("/images/misc/herbio.png", new PrivateChunkReader(heRB_type){
                protected void readChunk(DataInput in) throws IOException {
                    skipFully(in, 42);
                    in.readShort();
                    fail("expected exception");
                }
            });
        } catch (EOFException ignore) { }
        try {
            readResource("/images/misc/herbio.png", new PrivateChunkReader(heRB_type){
                protected void readChunk(DataInput in) throws IOException {
                    skipFully(in, 43);
                    in.readByte();
                    fail("expected exception");
                }
            });
        } catch (EOFException ignore) { }
    }

    public void testPrivateChunk()
    throws Exception
    {
        final String ORIGINAL_GIF = "original_gif";
        PngImage png = readResource("/images/misc/anigif.png", new PngImage(){
            @Override protected void readChunk(int type, DataInput in, long off, int len) throws IOException {
                if (type == msOG_type) {
                    byte[] bytes = new byte[len];
                    in.readFully(bytes);
                    getProperties().put(ORIGINAL_GIF, bytes);
                } else {
                    super.readChunk(type, in, off, len);
                }
            }
        });
        byte[] bytes = (byte[])png.getProperty(ORIGINAL_GIF);

        assertEquals("MSOFFICE9.0", new String(bytes, 0, 11, "US-ASCII"));
        
        File file = File.createTempFile("msog", ".gif");
        OutputStream out = new FileOutputStream(file);
        out.write(bytes, 11, bytes.length - 11);
        out.close();
        assertEquals(916473047L, getChecksum(new java.util.zip.CRC32(), file, buf));
        file.delete();
    }

    public void testRead()
    throws Exception
    {
        PngImage png = readResource("/images/misc/cc1.png");
        assertEquals(138, png.getWidth());
        assertEquals(180, png.getHeight());
    }

    public void testProgressBar()
    throws Exception
    {
        final List progress = new ArrayList();
        readResource("/images/misc/cc1.png", new PngImage(){
            protected boolean handleProgress(BufferedImage image, float pct) {
                progress.add(new Float(pct));
                return pct < 60f;
            }
        });
        assertEquals(Arrays.asList(new Float[]{
            new Float(5.5555553f),
            new Float(10.555555f),
            new Float(15.555555f),
            new Float(20.555555f),
            new Float(25.555555f),
            new Float(30.555555f),
            new Float(35.555557f),
            new Float(40.555557f),
            new Float(45.555557f),
            new Float(50.555557f),
            new Float(55.555557f),
            new Float(60.555557f),
        }), progress);
    }

    public void testAbort()
    throws Exception
    {
        readResource("/images/suite/basi3p04.png", new PngImage(){
            protected boolean handleFrame(BufferedImage image, int framesLeft) {
                if (framesLeft != 6)
                    throw new IllegalStateException("Should have aborted after first frame");
                return false;
            }
        });
    }

    public void testReadAncillary()
    throws Exception
    {
        PngImage png = readResource("/images/misc/anigif.png");
        assertEquals(32, png.getWidth());
        assertEquals(32, png.getHeight());
        // TODO: check gif chunks
    }

    public void testSkipCriticalChunk()
    throws Exception
    {
        try {
            readResource("/images/misc/penguin.png", new PngImage(){
                @Override protected void readChunk(int type, DataInput in, long off, int len) throws IOException {
                    if (type == PngConstants.PLTE)
                        return;
                    super.readChunk(type, in, off, len);
                }
            });
            fail("expected exception");
        } catch (IllegalStateException ignore) { }
    }
    
    public void testCoverage()
    throws Exception
    {
        try {
            new PngImage().read(null, true);
            fail("expected exception");
        } catch (NullPointerException ignore) { }

        try {
            new PngImage().getWidth();
            fail("expected exception");
        } catch (IllegalStateException ignore) { }

        try {
            new PngConfig.Builder().sourceRegion(new Rectangle(-1, 0, 1, 1));
            fail("expected exception");
        } catch (IllegalArgumentException ignore) { }

        try {
            new PngConfig.Builder().sourceSubsampling(0, 2, 0, 0);
            fail("expected exception");
        } catch (IllegalArgumentException ignore) { }
        
        try {
            new PngConfig.Builder()
                .sourceRegion(new Rectangle(0, 0, 10, 10))
                .progressive(true)
                .build();
            fail("expected exception");
        } catch (IllegalStateException ignore) { }

        try {
            new PngConfig.Builder()
                .sourceSubsampling(2, 2, 0, 0)
                .progressive(true)
                .build();
            fail("expected exception");
        } catch (IllegalStateException ignore) { }
        
        assertTrue(PngConstants.isReserved(PngConstants.getChunkType("HErB")));
        assertTrue(PngConstants.isSafeToCopy(PngConstants.getChunkType("HERb")));

        readResource("/images/suite/tbbn2c16.png",
                     new PngImage(new PngConfig.Builder().gammaCorrect(false).build()));
        
        readResource("/images/suite/basi0g01.png",
                     new PngImage(new PngConfig.Builder().progressive(true).build()));
                     
        PngConfig readHeader = new PngConfig.Builder()
            .sourceRegion(null) // for coverage
            .readLimit(PngConfig.READ_HEADER)
            .build();
        assertEquals(32, readResource("/images/suite/basn0g01.png", new PngImage(readHeader)).getWidth());
        assertEquals(PngConfig.READ_HEADER, new PngConfig.Builder(readHeader).build().getReadLimit());

        PngConfig readUntilData = new PngConfig.Builder().readLimit(PngConfig.READ_UNTIL_DATA).build();
        assertNotNull(readResource("/images/suite/basn3p01.png", new PngImage(readUntilData)).getProperty(PngConstants.PALETTE));

        PngConfig readExceptData = new PngConfig.Builder().readLimit(PngConfig.READ_EXCEPT_DATA).build();
        assertEquals(32, readResource("/images/suite/basn0g01.png", new PngImage(readExceptData)).getWidth());
    }    

    public void testErrors()
    throws Exception
    {
        errorHelper("/images/broken/x00n0g01.png");
        errorHelper("/images/broken/xcrn0g04.png");
        errorHelper("/images/broken/xlfn0g04.png");

        errorHelper("/images/broken/bkgd_after_idat.png");
        errorHelper("/images/broken/chrm_after_idat.png");
        errorHelper("/images/broken/chrm_after_plte.png");
        errorHelper("/images/broken/chunk_crc.png");
        errorHelper("/images/broken/chunk_length.png");
        errorHelper("/images/broken/chunk_private_critical.png");
        errorHelper("/images/broken/chunk_type.png");
        errorHelper("/images/broken/gama_after_idat.png");
        errorHelper("/images/broken/gama_after_plte.png");
        errorHelper("/images/broken/gama_zero.png");
        errorHelper("/images/broken/hist_after_idat.png");
        errorHelper("/images/broken/hist_before_plte.png");
        errorHelper("/images/broken/iccp_after_idat.png");
        errorHelper("/images/broken/iccp_after_plte.png");
        errorHelper("/images/broken/ihdr_16bit_palette.png");
        errorHelper("/images/broken/ihdr_1bit_alpha.png");
        errorHelper("/images/broken/ihdr_bit_depth.png");
        errorHelper("/images/broken/ihdr_color_type.png");
        errorHelper("/images/broken/ihdr_compression_method.png");
        errorHelper("/images/broken/ihdr_filter_method.png");
        errorHelper("/images/broken/ihdr_image_size.png");
        errorHelper("/images/broken/ihdr_interlace_method.png");
        errorHelper("/images/broken/itxt_compression_flag.png");
        errorHelper("/images/broken/itxt_compression_method.png");
        errorHelper("/images/broken/itxt_keyword_length.png");
        errorHelper("/images/broken/itxt_keyword_length_2.png");
        errorHelper("/images/broken/missing_idat.png");
        errorHelper("/images/broken/missing_ihdr.png");
        errorHelper("/images/broken/missing_plte.png");
        errorHelper("/images/broken/missing_plte_2.png");
        errorHelper("/images/broken/multiple_bkgd.png");
        errorHelper("/images/broken/multiple_chrm.png");
        errorHelper("/images/broken/multiple_gama.png");
        errorHelper("/images/broken/multiple_hist.png");
        errorHelper("/images/broken/multiple_iccp.png");
        errorHelper("/images/broken/multiple_ihdr.png");
        errorHelper("/images/broken/multiple_offs.png");
        errorHelper("/images/broken/multiple_pcal.png");
        errorHelper("/images/broken/multiple_phys.png");
        errorHelper("/images/broken/multiple_plte.png");
        errorHelper("/images/broken/multiple_sbit.png");
        errorHelper("/images/broken/multiple_scal.png");
        errorHelper("/images/broken/multiple_srgb.png");
        errorHelper("/images/broken/multiple_ster.png");
        errorHelper("/images/broken/multiple_time.png");
        errorHelper("/images/broken/multiple_trns.png");
        errorHelper("/images/broken/nonconsecutive_idat.png");
        errorHelper("/images/broken/offs_after_idat.png");
        errorHelper("/images/broken/pcal_after_idat.png");
        errorHelper("/images/broken/phys_after_idat.png");
        errorHelper("/images/broken/plte_after_idat.png");
        errorHelper("/images/broken/plte_empty.png");
        errorHelper("/images/broken/plte_in_grayscale.png");
        errorHelper("/images/broken/plte_length_mod_three.png");
        errorHelper("/images/broken/plte_too_many_entries.png");
        errorHelper("/images/broken/plte_too_many_entries_2.png");
        errorHelper("/images/broken/sbit_after_idat.png");
        errorHelper("/images/broken/sbit_after_plte.png");
        errorHelper("/images/broken/scal_after_idat.png");
        errorHelper("/images/broken/splt_after_idat.png");
        errorHelper("/images/broken/srgb_after_idat.png");
        errorHelper("/images/broken/srgb_after_plte.png");
        errorHelper("/images/broken/ster_after_idat.png");
        errorHelper("/images/broken/time_value_range.png");
        errorHelper("/images/broken/trns_after_idat.png");
        errorHelper("/images/broken/trns_bad_color_type.png");
        errorHelper("/images/broken/trns_too_many_entries.png");
        errorHelper("/images/broken/phys_unit_specifier.png");
        errorHelper("/images/broken/offs_unit_specifier.png");
        errorHelper("/images/broken/scal_unit_specifier.png");
        errorHelper("/images/broken/scal_floating_point.png");
        errorHelper("/images/broken/scal_negative.png");
        errorHelper("/images/broken/scal_zero.png");
        errorHelper("/images/broken/sbit_sample_depth.png");
        errorHelper("/images/broken/sbit_sample_depth_2.png");
        errorHelper("/images/broken/ster_mode.png");
        errorHelper("/images/broken/splt_sample_depth.png");
        errorHelper("/images/broken/splt_length_mod_6.png");
        errorHelper("/images/broken/splt_length_mod_10.png");
        errorHelper("/images/broken/splt_duplicate_name.png");
        errorHelper("/images/broken/ztxt_compression_method.png");
        errorHelper("/images/broken/ztxt_data_format.png");

        errorHelper("/images/broken/length_ihdr.png");
        errorHelper("/images/broken/length_iend.png");
        errorHelper("/images/broken/length_ster.png");
        errorHelper("/images/broken/length_srgb.png");
        errorHelper("/images/broken/length_gama.png");
        errorHelper("/images/broken/length_phys.png");
        errorHelper("/images/broken/length_time.png");
        errorHelper("/images/broken/length_offs.png");
        errorHelper("/images/broken/length_chrm.png");
        errorHelper("/images/broken/length_gifg.png");
        errorHelper("/images/broken/length_trns_gray.png");
        errorHelper("/images/broken/length_trns_rgb.png");
        errorHelper("/images/broken/length_trns_palette.png");
        errorHelper("/images/broken/length_hist.png");
        errorHelper("/images/broken/length_sbit.png");
        errorHelper("/images/broken/length_sbit_2.png");
        errorHelper("/images/broken/length_bkgd_gray.png");
        errorHelper("/images/broken/length_bkgd_rgb.png");
        errorHelper("/images/broken/length_bkgd_palette.png");
        errorHelper("/images/broken/truncate_zlib.png");
        errorHelper("/images/broken/truncate_zlib_2.png");
        errorHelper("/images/broken/truncate_idat_0.png");
        errorHelper("/images/broken/truncate_idat_1.png");
        errorHelper("/images/broken/unknown_filter_type.png");
        errorHelper("/images/broken/text_trailing_null.png");

        errorHelper("/images/broken/private_compression_method.png");
        errorHelper("/images/broken/private_filter_method.png");
        errorHelper("/images/broken/private_interlace_method.png");
        errorHelper("/images/broken/private_filter_type.png");
    }

    public void errorHelper(String path)
    throws Exception
    {
        try {
            readResource(path);
            fail("Expected exception in " + path);
        } catch (Exception e) {
            System.err.println(new File(path).getName() + ": " + e.getMessage());
            // StackTraceElement stack = e.getStackTrace()[0];
            // System.err.println("\t" + stack.getFileName() + ":" + stack.getLineNumber());
            if (e.getMessage() == null)
                e.printStackTrace(System.err);
        }
    }

    private static final PngConfig WARNINGS_FATAL =
        new PngConfig.Builder().warningsFatal(true).build();

    private PngImage readResource(String path)
    throws IOException
    {
        return readResource(path, new PngImage(WARNINGS_FATAL));
    }

    private PngImage readResource(String path, PngImage png)
    throws IOException
    {
        InputStream in = getClass().getResourceAsStream(path);
        png.read(in, true);
        return png;
    }

    public TestSimple(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        return getSuite(TestSimple.class);
    }
}
