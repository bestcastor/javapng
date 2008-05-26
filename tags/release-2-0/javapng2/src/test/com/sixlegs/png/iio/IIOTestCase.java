package com.sixlegs.png.iio;

import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Constructor;
import java.util.*;
import javax.imageio.*;
import javax.imageio.metadata.*;
import javax.imageio.stream.ImageInputStream;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

abstract public class IIOTestCase
extends TestCase
{
    public static final List SUITE = Collections.unmodifiableList(Arrays.asList(transform(new String[]{
        "basi0g01", "basi0g02", "basi0g04", "basi0g08",
        "basi0g16", "basi2c08", "basi2c16", "basi3p01",
        "basi3p02", "basi3p04", "basi3p08", "basi4a08",
        "basi4a16", "basi6a08", "basi6a16", "basn0g01",
        "basn0g02", "basn0g04", "basn0g08", "basn0g16",
        "basn2c08", "basn2c16", "basn3p01", "basn3p02",
        "basn3p04", "basn3p08", "basn4a08", "basn4a16",
        "basn6a08", "basn6a16", "bgai4a08", "bgai4a16",
        "bgan6a08", "bgan6a16", "bgbn4a08", "bggn4a16",
        "bgwn6a08", "bgyn6a16", "ccwn2c08", "ccwn3p08",
        "cdfn2c08", "cdhn2c08", "cdsn2c08", "cdun2c08",
        "ch1n3p04", "ch2n3p08", "cm0n0g04", "cm7n0g04",
        "cm9n0g04", "cs3n2c16", "cs3n3p08", "cs5n2c08",
        "cs5n3p08", "cs8n2c08", "cs8n3p08", "ct0n0g04",
        "ct1n0g04", "ctzn0g04", "f00n0g08", "f00n2c08",
        "f01n0g08", "f01n2c08", "f02n0g08", "f02n2c08",
        "f03n0g08", "f03n2c08", "f04n0g08", "f04n2c08",
        "g03n0g16", "g03n2c08", "g03n3p04", "g04n0g16",
        "g04n2c08", "g04n3p04", "g05n0g16", "g05n2c08",
        "g05n3p04", "g07n0g16", "g07n2c08", "g07n3p04",
        "g10n0g16", "g10n2c08", "g10n3p04", "g25n0g16",
        "g25n2c08", "g25n3p04", "oi1n0g16", "oi1n2c16",
        "oi2n0g16", "oi2n2c16", "oi4n0g16", "oi4n2c16",
        "oi9n0g16", "oi9n2c16", "pp0n2c16", "pp0n6a08",
        "ps1n0g08", "ps1n2c16", "ps2n0g08", "ps2n2c16",
        "s01i3p01", "s01n3p01", "s02i3p01", "s02n3p01",
        "s03i3p01", "s03n3p01", "s04i3p01", "s04n3p01",
        "s05i3p02", "s05n3p02", "s06i3p02", "s06n3p02",
        "s07i3p02", "s07n3p02", "s08i3p02", "s08n3p02",
        "s09i3p02", "s09n3p02", "s32i3p04", "s32n3p04",
        "s33i3p04", "s33n3p04", "s34i3p04", "s34n3p04",
        "s35i3p04", "s35n3p04", "s36i3p04", "s36n3p04",
        "s37i3p04", "s37n3p04", "s38i3p04", "s38n3p04",
        "s39i3p04", "s39n3p04", "s40i3p04", "s40n3p04",
        "tbbn1g04", "tbbn2c16", "tbbn3p08", "tbgn2c16",
        "tbgn3p08", "tbrn2c08", "tbwn1g16", "tbwn3p08",
        "tbyn3p08", "tp0n1g08", "tp0n2c08", "tp0n3p08",
        "tp1n3p08", "z00n2c08", "z03n2c08", "z06n2c08",
        "z09n2c08",
    }, "/images/suite/", ".png")));

    public static String[] transform(String[] array, String before, String after)
    {
        for (int i = 0; i < array.length; i++)
            array[i] = before + array[i] + after;
        return array;
    }

    protected String name;

    public IIOTestCase(String name)
    {
        super("test");
        this.name = name;
    }

    public static Test createSuite(Class subclass)
    throws Exception
    {
        return createSuite(subclass, SUITE);
    }
            
    public static Test createSuite(Class subclass, Collection images)
    throws Exception
    {
        TestSuite suite = new TestSuite();
        Constructor cstruct = subclass.getConstructor(new Class[]{ String.class });
        for (Iterator it = images.iterator(); it.hasNext();) {
            suite.addTest((IIOTestCase)cstruct.newInstance(new Object[]{ (String)it.next() }));
        }
        return suite;
    }

	protected ImageReader sunIR = null;

    public void test()
    throws Exception
    {
		// Initialise sun's plugin
		sunIR = new com.sun.imageio.plugins.png.PNGImageReader(
				new com.sun.imageio.plugins.png.PNGImageReaderSpi());
		sunIR.setInput(ImageIO.createImageInputStream(
					getClass().getResourceAsStream(name)));

        InputStream in = getClass().getResourceAsStream(name);
		ImageInputStream iis = ImageIO.createImageInputStream(in);
        PngImageReaderSpi spi = new PngImageReaderSpi();
        assertTrue(spi.canDecodeInput(iis));
		PngImageReader ir = new PngImageReader(spi);
		ir.setInput(iis);
		BufferedImage bi = ir.read(0);
		assertTrue(bi != null);
        test(name, ir, bi);
    }

    public String toString()
    {
        return name + "(" + getClass().getName() + ")";
    }

    abstract protected void test(String name, PngImageReader ir, BufferedImage bi) throws Exception;
}
