## Changes from v2.0-rc8 → v2.0 (2008-05-26) ##

  * Remove undocumented low-pass filter code
  * Add missing `PngConfig` documentation
  * Remove use of `ProGuard`

## Changes from v2.0-rc6 → v2.0-rc8 (2008-04-10) ##

  * Fix bug skipping over extra IDAT data
  * Fix bug with tRNS chunk in paletted images
  * Fix CRC calculation bug
  * Allow multiple private chunks
  * Some low-level changes needed by APNG code

(skipped rc7)

## Changes from v2.0-rc5 → v2.0-rc6 (2006-12-01) ##

  * Fix bugs found by `FindBugs`
  * Change chunk read method to use `DataInput` instead of `PngInputStream`
  * Fix stereo chunk parsing
  * Fix compressed `iTXt` parsing
  * Remove `PngChunk`, fold constants into `PngConstants`
  * Add suite of broken images, for test coverage

## Changes from v2.0-rc4 → v2.0-rc5 (2006-06-12) ##

  * Added `PngConfig.READ_EXCEPT_METADATA`
  * Better progressive performance
  * Support image source region
  * Reduced number of classes to make jar smaller
  * Use `ProGuard` to make jar smaller
  * Eliminate all use of reflection
  * Combine ext package into main package
  * Support new `sTER` registered chunk
  * Performance improvements
  * Removed `get/setUserExponent` from configuration
  * Removed support for limiting pass decoding
  * Use Builder pattern for `PngConfig` to make configuration immutable

## Changes from v2.0-rc3 → v2.0-rc4 (2006-04-05) ##

  * Java IIO-compatible plugin (thanks to Dimitri Koussa)
  * Change `sBIT` to use `byte[]` instead of int[.md](.md)
  * Support decimation subsampling
  * Support limiting decoding of passes to a particular range

## Changes from v2.0-rc2 → v2.0-rc3 (2005-10-07) ##

  * Fix decoding when there is leftover `IDAT` data
  * Move some methods from `PngConfig` to `PngImage`
  * Add `handleProgress` methods to `PngImage`
  * Support abort from `handleFrame`, `handleProgress`

## Changes from v2.0-rc1 → v2.0-rc2 (2005-09-14) ##

  * Fixed a buffering bug in `PngInputStream`
  * Made `PngImage` implement `java.awt.Transparency`

## Changes from v1.3.0 → v2.0-rc1 (2005-08-05) ##

  * Complete rewrite, now requires Java 1.2 or higher
  * Uses the modern `BufferedImage`-based Java APIs
  * Decoded images use less memory
  * Smaller jar file (under 35K)
  * Access to raw image data
  * 16-bit images do not have to be reduced to 8-bit
  * Faster decoding
  * License changed from LGPL to GPL-with-library-exception (the same as the GNU Classpath project)