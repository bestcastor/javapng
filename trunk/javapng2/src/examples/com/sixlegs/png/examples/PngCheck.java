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
        PngConfig config = new PngConfig();
        config.setReadLimit(PngConfig.READ_UNTIL_DATA);
        (new PngImage(config){
            public void handleWarning(PngWarning e) {
                System.out.println("Warning: " + e.getMessage());
            }
        }).read(new File(args[0]));
    }
}
