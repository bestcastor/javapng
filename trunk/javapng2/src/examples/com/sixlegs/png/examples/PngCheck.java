package com.sixlegs.png.examples;

import com.sixlegs.png.*;
import java.io.IOException;
import java.io.File;
import java.util.*;

public class PngCheck
{
    public static void main(String[] args)
    throws IOException
    {
        final List errors = new ArrayList();
        BasicPngConfig config = new BasicPngConfig(){
            public void handleWarning(PngWarning e) {
                System.out.println("Warning: " + e.getMessage());
            }
        };
        config.setMetadataOnly(true);
        new PngImage(config).read(new File(args[0]));
    }
}
