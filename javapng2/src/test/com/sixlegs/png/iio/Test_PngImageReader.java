package com.sixlegs.png.iio;

// import junit.framework.*;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Assert;

// import other stuff
import java.awt.image.*;
import java.io.File;
import java.util.*;
import javax.imageio.*;
import javax.imageio.stream.*;
import javax.imageio.metadata.*;
import java.io.*;

import com.sixlegs.png.iio.PngImageReader;
import com.sixlegs.png.iio.PngImageReaderSpi;

import org.w3c.dom.*;

public class Test_PngImageReader extends TestCase {
	static final String dir = "images/suite/";
	static final String logFile = "iiometadata";

	private PrintWriter out;

	/* Dynamicaly select the tests using reflection.
	 * Note: make all tests public. 
	 * This is better because you can change it by simply modifying this
	 * method.
	 */
	public static Test suite() {
		return new TestSuite(Test_PngImageReader.class);
	}

	/* Constructor */
	public Test_PngImageReader(String name) 
	throws Exception
	{
		super(name);

		// Clear the log file
		out = new PrintWriter(new BufferedWriter(new FileWriter(logFile, false)));
		out.close();
		out = null;
	}

	/* Code to set up test scaffold for each test */
	protected void setUp() 
	throws Exception
	{
		out = new PrintWriter(new BufferedWriter(new FileWriter(logFile, true)));
	}
	
	/* Code to destroy the scaffold after each test */
	protected void tearDown() {
		out.close();
		out = null;
	}

	private void doTst(String fName)
	throws Exception 
	{
		String fileName = dir + fName;
		File f = new File(fileName);
		ImageInputStream iis = ImageIO.createImageInputStream(f);
		ImageReader ir = new PngImageReader(new PngImageReaderSpi());
		ir.setInput(iis);
		BufferedImage bi = ir.read(0);
		printImageReaderDetails(ir);
		assertTrue(bi != null);
	}

	private void printImageReaderDetails(ImageReader ir)
	throws Exception
	{
		out.println("==================================================");
		out.println("                                                  ");
		IIOMetadata iiom = ir.getImageMetadata(0);
		Node n = iiom.getAsTree("com.sixlegs.png.iio.PngImageMetadata_v1");
		displayMetadata(n);
		out.println("                                                  ");
	}


	private void displayMetadata(Node root) 
	{
		displayMetadata(root, 0);
	}

	private void indent(int level) 
	{
		for (int i = 0; i < level; i++) 
			out.print("\t");
	} 

	private void displayMetadata(Node node, int level) 
	{
		indent(level); // emit open tag
		out.print("<" + node.getNodeName());
		NamedNodeMap map = node.getAttributes();
		if (map != null)  // print attribute values
		{
			int length = map.getLength();
			for (int i = 0; i < length; i++) 
			{
				Node attr = map.item(i);
				out.print(" " + attr.getNodeName() +
								 "=\"" + attr.getNodeValue() + "\"");
			}
		}

		Node child = node.getFirstChild();
		if (child != null) 
		{
			out.println(">"); // close current tag
			while (child != null) // emit child tags recursively
			{
				displayMetadata(child, level + 1);
				child = child.getNextSibling();
			}
			indent(level); // emit close tag
			out.println("</" + node.getNodeName() + ">");
		} else 
			out.println("/>");
	}

	/* ---------------------------------------- Tests start here */

	public void testImage_basi0g01()
	throws Exception
	{
		doTst( "basi0g01.png" );
	}
	public void testImage_basi0g02()
	throws Exception
	{
		doTst( "basi0g02.png" );
	}
	public void testImage_basi0g04()
	throws Exception
	{
		doTst( "basi0g04.png" );
	}
	public void testImage_basi0g08()
	throws Exception
	{
		doTst( "basi0g08.png" );
	}
	public void testImage_basi0g16()
	throws Exception
	{
		doTst( "basi0g16.png" );
	}
	public void testImage_basi2c08()
	throws Exception
	{
		doTst( "basi2c08.png" );
	}
	public void testImage_basi2c16()
	throws Exception
	{
		doTst( "basi2c16.png" );
	}
	public void testImage_basi3p01()
	throws Exception
	{
		doTst( "basi3p01.png" );
	}
	public void testImage_basi3p02()
	throws Exception
	{
		doTst( "basi3p02.png" );
	}
	public void testImage_basi3p04()
	throws Exception
	{
		doTst( "basi3p04.png" );
	}
	public void testImage_basi3p08()
	throws Exception
	{
		doTst( "basi3p08.png" );
	}
	public void testImage_basi4a08()
	throws Exception
	{
		doTst( "basi4a08.png" );
	}
	public void testImage_basi4a16()
	throws Exception
	{
		doTst( "basi4a16.png" );
	}
	public void testImage_basi6a08()
	throws Exception
	{
		doTst( "basi6a08.png" );
	}
	public void testImage_basi6a16()
	throws Exception
	{
		doTst( "basi6a16.png" );
	}
	public void testImage_basn0g01()
	throws Exception
	{
		doTst( "basn0g01.png" );
	}
	public void testImage_basn0g02()
	throws Exception
	{
		doTst( "basn0g02.png" );
	}
	public void testImage_basn0g04()
	throws Exception
	{
		doTst( "basn0g04.png" );
	}
	public void testImage_basn0g08()
	throws Exception
	{
		doTst( "basn0g08.png" );
	}
	public void testImage_basn0g16()
	throws Exception
	{
		doTst( "basn0g16.png" );
	}
	public void testImage_basn2c08()
	throws Exception
	{
		doTst( "basn2c08.png" );
	}
	public void testImage_basn2c16()
	throws Exception
	{
		doTst( "basn2c16.png" );
	}
	public void testImage_basn3p01()
	throws Exception
	{
		doTst( "basn3p01.png" );
	}
	public void testImage_basn3p02()
	throws Exception
	{
		doTst( "basn3p02.png" );
	}
	public void testImage_basn3p04()
	throws Exception
	{
		doTst( "basn3p04.png" );
	}
	public void testImage_basn3p08()
	throws Exception
	{
		doTst( "basn3p08.png" );
	}
	public void testImage_basn4a08()
	throws Exception
	{
		doTst( "basn4a08.png" );
	}
	public void testImage_basn4a16()
	throws Exception
	{
		doTst( "basn4a16.png" );
	}
	public void testImage_basn6a08()
	throws Exception
	{
		doTst( "basn6a08.png" );
	}
	public void testImage_basn6a16()
	throws Exception
	{
		doTst( "basn6a16.png" );
	}
	public void testImage_bgai4a08()
	throws Exception
	{
		doTst( "bgai4a08.png" );
	}
	public void testImage_bgai4a16()
	throws Exception
	{
		doTst( "bgai4a16.png" );
	}
	public void testImage_bgan6a08()
	throws Exception
	{
		doTst( "bgan6a08.png" );
	}
	public void testImage_bgan6a16()
	throws Exception
	{
		doTst( "bgan6a16.png" );
	}
	public void testImage_bgbn4a08()
	throws Exception
	{
		doTst( "bgbn4a08.png" );
	}
	public void testImage_bggn4a16()
	throws Exception
	{
		doTst( "bggn4a16.png" );
	}
	public void testImage_bgwn6a08()
	throws Exception
	{
		doTst( "bgwn6a08.png" );
	}
	public void testImage_bgyn6a16()
	throws Exception
	{
		doTst( "bgyn6a16.png" );
	}
	public void testImage_ccwn2c08()
	throws Exception
	{
		doTst( "ccwn2c08.png" );
	}
	public void testImage_ccwn3p08()
	throws Exception
	{
		doTst( "ccwn3p08.png" );
	}
	public void testImage_cdfn2c08()
	throws Exception
	{
		doTst( "cdfn2c08.png" );
	}
	public void testImage_cdhn2c08()
	throws Exception
	{
		doTst( "cdhn2c08.png" );
	}
	public void testImage_cdsn2c08()
	throws Exception
	{
		doTst( "cdsn2c08.png" );
	}
	public void testImage_cdun2c08()
	throws Exception
	{
		doTst( "cdun2c08.png" );
	}
	public void testImage_ch1n3p04()
	throws Exception
	{
		doTst( "ch1n3p04.png" );
	}
	public void testImage_ch2n3p08()
	throws Exception
	{
		doTst( "ch2n3p08.png" );
	}
	public void testImage_cm0n0g04()
	throws Exception
	{
		doTst( "cm0n0g04.png" );
	}
	public void testImage_cm7n0g04()
	throws Exception
	{
		doTst( "cm7n0g04.png" );
	}
	public void testImage_cm9n0g04()
	throws Exception
	{
		doTst( "cm9n0g04.png" );
	}
	public void testImage_cs3n2c16()
	throws Exception
	{
		doTst( "cs3n2c16.png" );
	}
	public void testImage_cs3n3p08()
	throws Exception
	{
		doTst( "cs3n3p08.png" );
	}
	public void testImage_cs5n2c08()
	throws Exception
	{
		doTst( "cs5n2c08.png" );
	}
	public void testImage_cs5n3p08()
	throws Exception
	{
		doTst( "cs5n3p08.png" );
	}
	public void testImage_cs8n2c08()
	throws Exception
	{
		doTst( "cs8n2c08.png" );
	}
	public void testImage_cs8n3p08()
	throws Exception
	{
		doTst( "cs8n3p08.png" );
	}
	public void testImage_ct0n0g04()
	throws Exception
	{
		doTst( "ct0n0g04.png" );
	}
	public void testImage_ct1n0g04()
	throws Exception
	{
		doTst( "ct1n0g04.png" );
	}
	public void testImage_ctzn0g04()
	throws Exception
	{
		doTst( "ctzn0g04.png" );
	}
	public void testImage_f00n0g08()
	throws Exception
	{
		doTst( "f00n0g08.png" );
	}
	public void testImage_f00n2c08()
	throws Exception
	{
		doTst( "f00n2c08.png" );
	}
	public void testImage_f01n0g08()
	throws Exception
	{
		doTst( "f01n0g08.png" );
	}
	public void testImage_f01n2c08()
	throws Exception
	{
		doTst( "f01n2c08.png" );
	}
	public void testImage_f02n0g08()
	throws Exception
	{
		doTst( "f02n0g08.png" );
	}
	public void testImage_f02n2c08()
	throws Exception
	{
		doTst( "f02n2c08.png" );
	}
	public void testImage_f03n0g08()
	throws Exception
	{
		doTst( "f03n0g08.png" );
	}
	public void testImage_f03n2c08()
	throws Exception
	{
		doTst( "f03n2c08.png" );
	}
	public void testImage_f04n0g08()
	throws Exception
	{
		doTst( "f04n0g08.png" );
	}
	public void testImage_f04n2c08()
	throws Exception
	{
		doTst( "f04n2c08.png" );
	}
	public void testImage_g03n0g16()
	throws Exception
	{
		doTst( "g03n0g16.png" );
	}
	public void testImage_g03n2c08()
	throws Exception
	{
		doTst( "g03n2c08.png" );
	}
	public void testImage_g03n3p04()
	throws Exception
	{
		doTst( "g03n3p04.png" );
	}
	public void testImage_g04n0g16()
	throws Exception
	{
		doTst( "g04n0g16.png" );
	}
	public void testImage_g04n2c08()
	throws Exception
	{
		doTst( "g04n2c08.png" );
	}
	public void testImage_g04n3p04()
	throws Exception
	{
		doTst( "g04n3p04.png" );
	}
	public void testImage_g05n0g16()
	throws Exception
	{
		doTst( "g05n0g16.png" );
	}
	public void testImage_g05n2c08()
	throws Exception
	{
		doTst( "g05n2c08.png" );
	}
	public void testImage_g05n3p04()
	throws Exception
	{
		doTst( "g05n3p04.png" );
	}
	public void testImage_g07n0g16()
	throws Exception
	{
		doTst( "g07n0g16.png" );
	}
	public void testImage_g07n2c08()
	throws Exception
	{
		doTst( "g07n2c08.png" );
	}
	public void testImage_g07n3p04()
	throws Exception
	{
		doTst( "g07n3p04.png" );
	}
	public void testImage_g10n0g16()
	throws Exception
	{
		doTst( "g10n0g16.png" );
	}
	public void testImage_g10n2c08()
	throws Exception
	{
		doTst( "g10n2c08.png" );
	}
	public void testImage_g10n3p04()
	throws Exception
	{
		doTst( "g10n3p04.png" );
	}
	public void testImage_g25n0g16()
	throws Exception
	{
		doTst( "g25n0g16.png" );
	}
	public void testImage_g25n2c08()
	throws Exception
	{
		doTst( "g25n2c08.png" );
	}
	public void testImage_g25n3p04()
	throws Exception
	{
		doTst( "g25n3p04.png" );
	}
	public void testImage_oi1n0g16()
	throws Exception
	{
		doTst( "oi1n0g16.png" );
	}
	public void testImage_oi1n2c16()
	throws Exception
	{
		doTst( "oi1n2c16.png" );
	}
	public void testImage_oi2n0g16()
	throws Exception
	{
		doTst( "oi2n0g16.png" );
	}
	public void testImage_oi2n2c16()
	throws Exception
	{
		doTst( "oi2n2c16.png" );
	}
	public void testImage_oi4n0g16()
	throws Exception
	{
		doTst( "oi4n0g16.png" );
	}
	public void testImage_oi4n2c16()
	throws Exception
	{
		doTst( "oi4n2c16.png" );
	}
	public void testImage_oi9n0g16()
	throws Exception
	{
		doTst( "oi9n0g16.png" );
	}
	public void testImage_oi9n2c16()
	throws Exception
	{
		doTst( "oi9n2c16.png" );
	}
	public void testImage_pp0n2c16()
	throws Exception
	{
		doTst( "pp0n2c16.png" );
	}
	public void testImage_pp0n6a08()
	throws Exception
	{
		doTst( "pp0n6a08.png" );
	}
	public void testImage_ps1n0g08()
	throws Exception
	{
		doTst( "ps1n0g08.png" );
	}
	public void testImage_ps1n2c16()
	throws Exception
	{
		doTst( "ps1n2c16.png" );
	}
	public void testImage_ps2n0g08()
	throws Exception
	{
		doTst( "ps2n0g08.png" );
	}
	public void testImage_ps2n2c16()
	throws Exception
	{
		doTst( "ps2n2c16.png" );
	}
	public void testImage_s01i3p01()
	throws Exception
	{
		doTst( "s01i3p01.png" );
	}
	public void testImage_s01n3p01()
	throws Exception
	{
		doTst( "s01n3p01.png" );
	}
	public void testImage_s02i3p01()
	throws Exception
	{
		doTst( "s02i3p01.png" );
	}
	public void testImage_s02n3p01()
	throws Exception
	{
		doTst( "s02n3p01.png" );
	}
	public void testImage_s03i3p01()
	throws Exception
	{
		doTst( "s03i3p01.png" );
	}
	public void testImage_s03n3p01()
	throws Exception
	{
		doTst( "s03n3p01.png" );
	}
	public void testImage_s04i3p01()
	throws Exception
	{
		doTst( "s04i3p01.png" );
	}
	public void testImage_s04n3p01()
	throws Exception
	{
		doTst( "s04n3p01.png" );
	}
	public void testImage_s05i3p02()
	throws Exception
	{
		doTst( "s05i3p02.png" );
	}
	public void testImage_s05n3p02()
	throws Exception
	{
		doTst( "s05n3p02.png" );
	}
	public void testImage_s06i3p02()
	throws Exception
	{
		doTst( "s06i3p02.png" );
	}
	public void testImage_s06n3p02()
	throws Exception
	{
		doTst( "s06n3p02.png" );
	}
	public void testImage_s07i3p02()
	throws Exception
	{
		doTst( "s07i3p02.png" );
	}
	public void testImage_s07n3p02()
	throws Exception
	{
		doTst( "s07n3p02.png" );
	}
	public void testImage_s08i3p02()
	throws Exception
	{
		doTst( "s08i3p02.png" );
	}
	public void testImage_s08n3p02()
	throws Exception
	{
		doTst( "s08n3p02.png" );
	}
	public void testImage_s09i3p02()
	throws Exception
	{
		doTst( "s09i3p02.png" );
	}
	public void testImage_s09n3p02()
	throws Exception
	{
		doTst( "s09n3p02.png" );
	}
	public void testImage_s32i3p04()
	throws Exception
	{
		doTst( "s32i3p04.png" );
	}
	public void testImage_s32n3p04()
	throws Exception
	{
		doTst( "s32n3p04.png" );
	}
	public void testImage_s33i3p04()
	throws Exception
	{
		doTst( "s33i3p04.png" );
	}
	public void testImage_s33n3p04()
	throws Exception
	{
		doTst( "s33n3p04.png" );
	}
	public void testImage_s34i3p04()
	throws Exception
	{
		doTst( "s34i3p04.png" );
	}
	public void testImage_s34n3p04()
	throws Exception
	{
		doTst( "s34n3p04.png" );
	}
	public void testImage_s35i3p04()
	throws Exception
	{
		doTst( "s35i3p04.png" );
	}
	public void testImage_s35n3p04()
	throws Exception
	{
		doTst( "s35n3p04.png" );
	}
	public void testImage_s36i3p04()
	throws Exception
	{
		doTst( "s36i3p04.png" );
	}
	public void testImage_s36n3p04()
	throws Exception
	{
		doTst( "s36n3p04.png" );
	}
	public void testImage_s37i3p04()
	throws Exception
	{
		doTst( "s37i3p04.png" );
	}
	public void testImage_s37n3p04()
	throws Exception
	{
		doTst( "s37n3p04.png" );
	}
	public void testImage_s38i3p04()
	throws Exception
	{
		doTst( "s38i3p04.png" );
	}
	public void testImage_s38n3p04()
	throws Exception
	{
		doTst( "s38n3p04.png" );
	}
	public void testImage_s39i3p04()
	throws Exception
	{
		doTst( "s39i3p04.png" );
	}
	public void testImage_s39n3p04()
	throws Exception
	{
		doTst( "s39n3p04.png" );
	}
	public void testImage_s40i3p04()
	throws Exception
	{
		doTst( "s40i3p04.png" );
	}
	public void testImage_s40n3p04()
	throws Exception
	{
		doTst( "s40n3p04.png" );
	}
	public void testImage_tbbn1g04()
	throws Exception
	{
		doTst( "tbbn1g04.png" );
	}
	public void testImage_tbbn2c16()
	throws Exception
	{
		doTst( "tbbn2c16.png" );
	}
	public void testImage_tbbn3p08()
	throws Exception
	{
		doTst( "tbbn3p08.png" );
	}
	public void testImage_tbgn2c16()
	throws Exception
	{
		doTst( "tbgn2c16.png" );
	}
	public void testImage_tbgn3p08()
	throws Exception
	{
		doTst( "tbgn3p08.png" );
	}
	public void testImage_tbrn2c08()
	throws Exception
	{
		doTst( "tbrn2c08.png" );
	}
	public void testImage_tbwn1g16()
	throws Exception
	{
		doTst( "tbwn1g16.png" );
	}
	public void testImage_tbwn3p08()
	throws Exception
	{
		doTst( "tbwn3p08.png" );
	}
	public void testImage_tbyn3p08()
	throws Exception
	{
		doTst( "tbyn3p08.png" );
	}
	public void testImage_tp0n1g08()
	throws Exception
	{
		doTst( "tp0n1g08.png" );
	}
	public void testImage_tp0n2c08()
	throws Exception
	{
		doTst( "tp0n2c08.png" );
	}
	public void testImage_tp0n3p08()
	throws Exception
	{
		doTst( "tp0n3p08.png" );
	}
	public void testImage_tp1n3p08()
	throws Exception
	{
		doTst( "tp1n3p08.png" );
	}
	public void testImage_z00n2c08()
	throws Exception
	{
		doTst( "z00n2c08.png" );
	}
	public void testImage_z03n2c08()
	throws Exception
	{
		doTst( "z03n2c08.png" );
	}
	public void testImage_z06n2c08()
	throws Exception
	{
		doTst( "z06n2c08.png" );
	}
	public void testImage_z09n2c08()
	throws Exception
	{
		doTst( "z09n2c08.png" );
	}
}
