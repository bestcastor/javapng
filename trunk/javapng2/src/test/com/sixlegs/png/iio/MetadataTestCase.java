package com.sixlegs.png.iio;

import java.awt.image.BufferedImage;
import javax.imageio.metadata.*;
import junit.framework.Test;
import org.w3c.dom.*;

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
		System.out.println("==================================================");
		System.out.println("                                                  ");
		IIOMetadata iiom = ir.getImageMetadata(0);
		Node n = iiom.getAsTree(getFormatName());
		displayMetadata(n, 0);
		System.out.println("                                                  ");
    }

    abstract protected String getFormatName();

	private void indent(int level) 
	{
		for (int i = 0; i < level; i++) 
			System.out.print("\t");
	} 

	private void displayMetadata(Node node, int level) 
	{
		indent(level); // emit open tag
		System.out.print("<" + node.getNodeName());
		NamedNodeMap map = node.getAttributes();
		if (map != null) { // print attribute values
			int length = map.getLength();
			for (int i = 0; i < length; i++) {
				Node attr = map.item(i);
				System.out.print(" " + attr.getNodeName() +
								 "=\"" + attr.getNodeValue() + "\"");
			}
		}

		Node child = node.getFirstChild();
		if (child != null) {
			System.out.println(">"); // close current tag
			while (child != null) { // emit child tags recursively
				displayMetadata(child, level + 1);
				child = child.getNextSibling();
			}
			indent(level); // emit close tag
			System.out.println("</" + node.getNodeName() + ">");
		} else {
			System.out.println("/>");
        }
	}
}
