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

	protected abstract Node getSunsTree() throws Exception;

    protected void test(String name, PngImageReader ir, BufferedImage bi)
    throws Exception
    {
		System.out.print("================== ");
		System.out.println(name);
		System.out.println("");

		IIOMetadata iiom = ir.getImageMetadata(0);
		Node n = iiom.getAsTree(getFormatName());
		Node sunTree = getSunsTree();

		writeNode(n);

		if (sunTree == null)
		{
			System.out.println("Sun's tree is null; no comparision is done.");
		}
		else
		if (compare(n, sunTree) == false)
		{
			System.out.println(">>>>>>>> Sun's is different! <<<<<<<<");
			System.out.println("");

			writeNode(sunTree);
			fail("Sun's metadata is different.");
		}

		System.out.println("");
	}

	protected void writeNode(Node n)
	throws Exception
	{
		DataWriter dw = new DataWriter();
		dw.setIndentStep(4);
		dw.startDocument();
		writeNode(dw, n);
		dw.endDocument();
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

	// Compares two Nodes to see if they are exactly
	// the same; not a semantic comparision
	protected boolean compare(Node n1, Node n2)
	{
		// Hmmm. Are two null nodes equal?
		if (n1 == null || n2 == null)
			return  false;

		boolean state = true;

		// Compare attributes of current node
		NamedNodeMap map1 = n1.getAttributes();
		NamedNodeMap map2 = n2.getAttributes();

		int len1 = map1.getLength();
		int len2 = map2.getLength();
		if (len1 != len2)
		{
			System.out.println("*** Nodes have different number of attributes");
			System.out.println(n1.getNodeName() + ":" + len1);
			System.out.println(n2.getNodeName() + ":" + len2);
			state = false;
		}

		for (int i=0; i<len1 && i<len2; i++) 
		{
			Node attr1 = map1.item(i);
			Node attr2 = map2.item(i);
			if (!attr1.getNodeName().equals(attr2.getNodeName()))
			{
				System.out.println("*** Attribute names are different");
				System.out.println(n1.getNodeName() + ":" + attr1.getNodeName());
				System.out.println(n2.getNodeName() + ":" + attr2.getNodeName());
				state = false;
			}
			else
			if (!attr1.getNodeValue().equals(attr2.getNodeValue()))
			{
				System.out.println("*** Attribute values are different");
				System.out.println(n1.getNodeName() + ":" + 
						attr1.getNodeName() + ":" + attr1.getNodeValue());
				System.out.println(n2.getNodeName() + ":" + 
						attr2.getNodeName() + ":" + attr2.getNodeValue());
				state = false;
			}
		}

		// Now compare the nodes children
		Node child1 = n1.getFirstChild();
		Node child2 = n2.getFirstChild();
		while (true)
		{
			if (child1 == null && child2 == null)
				break;
			else 
			if (child1 == null ^ child2 == null)
			{
				System.out.println("*** Nodes " +
						n1.getNodeName() +
						" and " +
						n2.getNodeName() +
						" have different number of children");
				return false;
			}

			if (compare(child1, child2) == false)
				state = false;

			child1 = child1.getNextSibling();
			child2 = child2.getNextSibling();
		}

		return state;
	}
}
