package com.sixlegs.png;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import junit.framework.*;

// TODO: reduce reliance on ImageIO for checksum calc
public class TestSimple
extends PngTestCase
{
    private byte[] buf = new byte[0x2000];

    public void testSubsampling()
    throws Exception
    {
        subsamplingHelper("/images/misc/penguin.png", 3, 3, 923164955L);
        subsamplingHelper("/images/misc/pngtest.png", 3, 3, 1930297805L);
    }

    private void subsamplingHelper(String path, int xsub, int ysub, long expect)
    throws Exception
    {
        PngImage png = new PngImage(new PngConfig.Builder().sourceSubsampling(xsub, ysub, 0, 0).build());
        BufferedImage image = png.read(getClass().getResourceAsStream(path), true);
        assertChecksum(expect, image, "subsample");
    }

    public void testSourceRegions()
    throws Exception
    {
        regionHelper("/images/misc/penguin.png", new Rectangle(75, 0, 105, 125), 490287408L);
        regionHelper("/images/misc/pngtest.png", new Rectangle(10, 20, 30, 40), 2689440455L);
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
            protected BufferedImage createImage(InputStream in) throws IOException {
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
                return super.createImage(in);
            }
        };
        InputStream in = getClass().getResourceAsStream("/images/suite/basn0g01.png");
        File file = File.createTempFile("recolor", ".png");
        javax.imageio.ImageIO.write(png.read(in, true), "PNG", file);
        assertEquals(2661639413L, getChecksum(new java.util.zip.CRC32(), file, buf));
        file.delete();
    }

    public void testPrivateChunk()
    throws Exception
    {
        final String ORIGINAL_GIF = "original_gif";
        final int msOG_type = PngConstants.getChunkType("msOG");

        PngImage png = readResource("/images/misc/anigif.png", new PngImage(){
            protected boolean readChunk(int type, DataInput in, int length) throws IOException {
                if (type == msOG_type) {
                    byte[] bytes = new byte[length];
                    in.readFully(bytes);
                    getProperties().put(ORIGINAL_GIF, bytes);
                    return true;
                }
                return super.readChunk(type, in, length);
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
    
    public void testCoverage()
    throws Exception
    {
        assertTrue(PngConstants.isReserved(PngConstants.getChunkType("HErB")));
        assertTrue(PngConstants.isSafeToCopy(PngConstants.getChunkType("HERb")));
        
        PngConfig progressive = new PngConfig.Builder().progressive(true).build();
        readResource("/images/suite/basi0g01.png", new PngImage(progressive));
                     
        PngConfig readHeader = new PngConfig.Builder().readLimit(PngConfig.READ_HEADER).build();
        assertEquals(32, readResource("/images/suite/basn0g01.png", new PngImage(readHeader)).getWidth());

        PngConfig readUntilData = new PngConfig.Builder().readLimit(PngConfig.READ_UNTIL_DATA).build();
        assertNotNull(readResource("/images/suite/basn3p01.png", new PngImage(readUntilData)).getProperty(PngConstants.PALETTE));
    }    

    public void testErrors()
    throws Exception
    {
        errorHelper("/images/suite/x00n0g01.png");
        errorHelper("/images/suite/xcrn0g04.png");
        errorHelper("/images/suite/xlfn0g04.png");

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
        // errorHelper("/images/broken/multiple_pcal.png");
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
        errorHelper("/images/broken/length_trns_gray.png");
        errorHelper("/images/broken/length_trns_rgb.png");
        errorHelper("/images/broken/length_trns_palette.png");
        errorHelper("/images/broken/length_hist.png");
        errorHelper("/images/broken/length_bkgd_gray.png");
        errorHelper("/images/broken/length_bkgd_rgb.png");
        errorHelper("/images/broken/length_bkgd_palette.png");
        
        errorHelper("/images/broken/truncate_idat.png");
        errorHelper("/images/broken/truncate_idat_2.png");
    }

    public void errorHelper(String path)
    throws Exception
    {
        try {
            readResource(path);
            fail("Expected exception");
        } catch (Exception e) {
            System.err.println(new File(path).getName() + ": " + e.getMessage());
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
