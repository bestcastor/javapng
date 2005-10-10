package com.sixlegs.png.iio;

import java.awt.image.BufferedImage;
import javax.imageio.metadata.*;
import junit.framework.Test;
import org.w3c.dom.*;
import com.megginson.sax.DataWriter;
import org.xml.sax.helpers.AttributesImpl;

abstract public class MetadataTestCase
extends IIOTestCase
{
	public MetadataTestCase(String name) 
	{
		super(name);
	}

    protected void test(String name, PngImageReader ir, BufferedImage bi)
    throws Exception
    {
		System.out.print("================== ");
		System.out.println(name);
		System.out.println("");

		IIOMetadata iiom = ir.getImageMetadata(0);
		Node n = iiom.getAsTree(getFormatName());

		DataWriter dw = new DataWriter();
		dw.setIndentStep(4);
		dw.startDocument();
		writeNode(dw, n);
		dw.endDocument();

		System.out.println("");
    }

    abstract protected String getFormatName();

	private void writeNode(DataWriter dw, Node n)
	throws Exception
	{
		AttributesImpl attr = new AttributesImpl();
		NamedNodeMap map = n.getAttributes();
		if (map != null)
		{
			int len = map.getLength();
			for (int i=0; i<len; i++)
			{
				Node a = map.item(i);
				attr.addAttribute("", a.getNodeName(), "", "", a.getNodeValue());
			}
		}

		dw.startElement("", n.getNodeName(), "", attr);

		Node child = n.getFirstChild();
		while (child != null)
		{
			writeNode(dw, child);
			child = child.getNextSibling();
		}

		dw.endElement(n.getNodeName());
	}
}
