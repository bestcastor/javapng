## Introduction ##

As a means of increasing test coverage, I started working on suite of images to cover all the possible error cases. It has subsequently also been used to improve the excellent [pngcheck](http://www.libpng.org/pub/png/apps/pngcheck.html) tool.

The code to generate the images is checked in:
[BrokenGenerator.java](http://javapng.googlecode.com/svn/trunk/javapng2/src/test/BrokenGenerator.java). In general I try to pick a simple image
from [PngSuite](http://www.schaik.com/pngsuite/) and mutate it by swapping chunks, adding in new chunks, etc. Contributions are welcome.

## Download ##

All releases are tagged with the [BrokenSuite label](http://code.google.com/p/javapng/downloads/list?q=label:BrokenSuite).

## Images ##

This table lists all of the images in the latest release and the corresponding errors (the message of the exception that will be thrown).

| x00n0g01.png | Bad image size: 0x0 |
|:-------------|:--------------------|
| xcrn0g04.png | Improper signature, expected 0x89504e470d0a1a0a, got 0x89504e470d0d0a1a |
| xlfn0g04.png | Improper signature, expected 0x89504e470d0a1a0a, got 0x89504e470a0a0a1a |
| bkgd\_after\_idat.png | bKGD cannot appear after IDAT |
| chrm\_after\_idat.png | cHRM cannot appear after IDAT |
| chrm\_after\_plte.png | cHRM cannot appear after PLTE |
| chunk\_crc.png | Bad CRC value for IHDR chunk |
| chunk\_length.png | Bad chunk length: 4294967276 |
| chunk\_private\_critical.png | Private critical chunk encountered: GaMA |
| chunk\_type.png | Corrupted chunk type: 0x67614d5f |
| gama\_after\_idat.png | gAMA cannot appear after IDAT |
| gama\_after\_plte.png | gAMA cannot appear after PLTE |
| gama\_zero.png | Meaningless zero gAMA chunk value |
| hist\_after\_idat.png | hIST cannot appear after IDAT |
| hist\_before\_plte.png | PLTE must precede hIST |
| iccp\_after\_idat.png | iCCP cannot appear after IDAT |
| iccp\_after\_plte.png | iCCP cannot appear after PLTE |
| ihdr\_16bit\_palette.png | Bad bit depth for color type 3: 16 |
| ihdr\_1bit\_alpha.png | Bad bit depth for color type 6: 1 |
| ihdr\_bit\_depth.png | Bad bit depth: 7    |
| ihdr\_color\_type.png | Bad color type: 1   |
| ihdr\_compression\_method.png | Unrecognized compression method: 1 |
| ihdr\_filter\_method.png | Unrecognized filter method: 1 |
| ihdr\_image\_size.png | Bad image size: -32x-32 |
| ihdr\_interlace\_method.png | Unrecognized interlace method: 2 |
| itxt\_compression\_flag.png | Illegal iTXt compression flag: 2 |
| itxt\_compression\_method.png | Unrecognized iTXt compression method: 1 |
| itxt\_keyword\_length.png | Invalid keyword length: 0 |
| itxt\_keyword\_length\_2.png | Invalid keyword length: 80 |
| missing\_idat.png | Required data chunk(s) not found |
| missing\_ihdr.png | IHDR chunk must be first chunk |
| missing\_plte.png | Required PLTE chunk not found |
| missing\_plte\_2.png | Required PLTE chunk not found |
| multiple\_bkgd.png | Multiple bKGD chunks are not allowed |
| multiple\_chrm.png | Multiple cHRM chunks are not allowed |
| multiple\_gama.png | Multiple gAMA chunks are not allowed |
| multiple\_hist.png | Multiple hIST chunks are not allowed |
| multiple\_iccp.png | Multiple iCCP chunks are not allowed |
| multiple\_ihdr.png | Multiple IHDR chunks are not allowed |
| multiple\_offs.png | Multiple oFFs chunks are not allowed |
| multiple\_pcal.png | Multiple pCAL chunks are not allowed |
| multiple\_phys.png | Multiple pHYs chunks are not allowed |
| multiple\_plte.png | Multiple PLTE chunks are not allowed |
| multiple\_sbit.png | Multiple sBIT chunks are not allowed |
| multiple\_scal.png | Multiple sCAL chunks are not allowed |
| multiple\_srgb.png | Multiple sRGB chunks are not allowed |
| multiple\_ster.png | Multiple sTER chunks are not allowed |
| multiple\_time.png | Multiple tIME chunks are not allowed |
| multiple\_trns.png | Multiple tRNS chunks are not allowed |
| nonconsecutive\_idat.png | IDAT chunks must be consecutive |
| offs\_after\_idat.png | oFFs cannot appear after IDAT |
| pcal\_after\_idat.png | pCAL cannot appear after IDAT |
| phys\_after\_idat.png | pHYs cannot appear after IDAT |
| plte\_after\_idat.png | Required PLTE chunk not found |
| plte\_empty.png | PLTE chunk cannot be empty |
| plte\_in\_grayscale.png | PLTE chunk found in grayscale image |
| plte\_length\_mod\_three.png | PLTE chunk length indivisible by 3: 2 |
| plte\_too\_many\_entries.png | Too many palette entries: 17 |
| plte\_too\_many\_entries\_2.png | Too many palette entries: 257 |
| sbit\_after\_idat.png | sBIT cannot appear after IDAT |
| sbit\_after\_plte.png | sBIT cannot appear after PLTE |
| scal\_after\_idat.png | sCAL cannot appear after IDAT |
| splt\_after\_idat.png | sPLT cannot appear after IDAT |
| srgb\_after\_idat.png | sRGB cannot appear after IDAT |
| srgb\_after\_plte.png | sRGB cannot appear after PLTE |
| ster\_after\_idat.png | sTER cannot appear after IDAT |
| time\_value\_range.png | tIME month value 0 is out of bounds (1-12) |
| trns\_after\_idat.png | tRNS cannot appear after IDAT |
| trns\_bad\_color\_type.png | tRNS prohibited for color type 6 |
| trns\_too\_many\_entries.png | Too many transparency palette entries (200 > 173) |
| phys\_unit\_specifier.png | Illegal pHYs chunk unit specifier: 2 |
| offs\_unit\_specifier.png | Illegal oFFs chunk unit specifier: 2 |
| scal\_unit\_specifier.png | Illegal sCAL chunk unit specifier: 3 |
| scal\_floating\_point.png | For input string: "Q.527777777778" |
| scal\_negative.png | sCAL measurements must be >= 0 |
| scal\_zero.png | sCAL measurements must be >= 0 |
| sbit\_sample\_depth.png | Illegal sBIT sample depth |
| sbit\_sample\_depth\_2.png | Illegal sBIT sample depth |
| ster\_mode.png | Unknown sTER mode: 2 |
| splt\_sample\_depth.png | Sample depth must be 8 or 16 |
| splt\_length\_mod\_6.png | Incorrect sPLT data length for given sample depth |
| splt\_length\_mod\_10.png | Incorrect sPLT data length for given sample depth |
| splt\_duplicate\_name.png | Duplicate suggested palette name Lemonade |
| ztxt\_compression\_method.png | Unrecognized compression method: 3 |
| ztxt\_data\_format.png | unknown compression method |
| length\_ihdr.png | Bad IHDR chunk length: 14 (expected 13) |
| length\_iend.png | Bad IEND chunk length: 1 (expected 0) |
| length\_ster.png | Bad sTER chunk length: 2 (expected 1) |
| length\_srgb.png | Bad sRGB chunk length: 2 (expected 1) |
| length\_gama.png | Bad gAMA chunk length: 3 (expected 4) |
| length\_phys.png | Bad pHYs chunk length: 8 (expected 9) |
| length\_time.png | Bad tIME chunk length: 6 (expected 7) |
| length\_offs.png | Bad oFFs chunk length: 8 (expected 9) |
| length\_chrm.png | Bad cHRM chunk length: 31 (expected 32) |
| length\_gifg.png | Bad gIFg chunk length: 5 (expected 4) |
| length\_trns\_gray.png | Bad tRNS chunk length: 0 (expected 2) |
| length\_trns\_rgb.png | Bad tRNS chunk length: 0 (expected 6) |
| length\_trns\_palette.png | Too many transparency palette entries (174 > 173) |
| length\_hist.png | Bad hIST chunk length: 28 (expected 30) |
| length\_sbit.png | Bad sBIT chunk length: 4 (expected 3) |
| length\_sbit\_2.png | Bad sBIT chunk length: 3 (expected 1) |
| length\_bkgd\_gray.png | Bad bKGD chunk length: 6 (expected 2) |
| length\_bkgd\_rgb.png | Bad bKGD chunk length: 2 (expected 6) |
| length\_bkgd\_palette.png | Bad bKGD chunk length: 6 (expected 1) |
| truncate\_zlib.png | Unexpected end of ZLIB input stream |
| truncate\_zlib\_2.png | Unexpected end of ZLIB input stream |
| truncate\_idat\_0.png | Unexpected end of image data |
| truncate\_idat\_1.png | Unexpected end of image data |
| unknown\_filter\_type.png | Unrecognized filter type 5 |
| text\_trailing\_null.png | Text value contains null |
| private\_compression\_method.png | Unrecognized compression method: 128 |
| private\_filter\_method.png | Unrecognized filter method: 128 |
| private\_interlace\_method.png | Unrecognized interlace method: 128 |
| private\_filter\_type.png | Unrecognized filter type 128 |