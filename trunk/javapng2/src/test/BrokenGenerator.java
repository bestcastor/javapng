import java.io.*;
import java.util.*;
import java.util.zip.*;

/*
need corrupted images:
x Private critical chunk encountered
x Corrupted chunk type
x IHDR chunk must be first chunk
x PLTE must precede hIST
x Required PLTE chunk not found
x   IDAT
x   tRNS
  Cannot appear after PLTE
x   cHRM
x   gAMA
    iCCP
x   sBIT
    sRGB
x Required data chunk(s) not found (IEND after PLTE w/o IDAT)
  Cannot appear after IDAT
x   PLTE
    cHRM
x   gAMA
    iCCP
    sBIT
    sRGB
    bKGD
    hIST
    tRNS
    pHYs
    sPLT
    oFFs
    pCAL
    sCAL
x   sTER
x IDAT chunks must be consecutive (IDAT, non-IDAT, IDAT)
  Multiple chunks are not allowed (all except sPLT, iTXt, tEXt, zTXt)
x   IHDR
x   PLTE
x   gAMA
    ...
  Unrecognized filter type (Defilterer)
x Bad chunk length (negative length)
x Bad CRC value for chunk
  Unrecognized compression method (iCCP, zTXt, iTXt)
  Invalid keyword length
  Bad bit depth
  Bad bit depth for color type
  Bad color type
  Unrecognized compression method (IHDR)
  Unrecognized filter method (IHDR)
  Unrecognized interlace method (IHDR)
  PLTE chunk length indivisible by 3
  Too many palette entries
  PLTE chunk found in grayscale image
  Too many transparency palette entries
x tRNS prohibited for color type
  Meaningless zero gAMA chunk value
  Illegal pHYs chunk unit specifier
  Illegal sBIT sample depth
  ...

need chunks: 
  oFFs, sCAL, sTER, iTXt, iCCP, sRGB
*/  
public class BrokenGenerator
{
    private static final int IHDR = 0x49484452;
    private static final int PLTE = 0x504c5445;
    private static final int IDAT = 0x49444154;
    private static final int IEND = 0x49454e44;
    private static final int bKGD = 0x624b4744;
    private static final int cHRM = 0x6348524d;
    private static final int gAMA = 0x67414d41;
    private static final int hIST = 0x68495354;
    private static final int iCCP = 0x69434350;
    private static final int iTXt = 0x69545874;
    private static final int pHYs = 0x70485973;
    private static final int sBIT = 0x73424954;
    private static final int sPLT = 0x73504c54;
    private static final int sRGB = 0x73524742;
    private static final int tEXt = 0x74455874;
    private static final int tIME = 0x74494d45;
    private static final int tRNS = 0x74524e53;
    private static final int zTXt = 0x7a545874;
    private static final int oFFs = 0x6f464673;
    private static final int pCAL = 0x7043414c;
    private static final int sCAL = 0x7343414c;
    private static final int gIFg = 0x67494667;
    private static final int gIFx = 0x67494678;
    private static final int sTER = 0x73544552;

    // private types
    private static final int heRB = 0x68655242;

    // bad types
    private static final int GaMA = 0x47614d41;
    private static final int gAM_ = 0x67614d5f;

    public static void main(String[] args)
    throws Exception
    {
        gen("suite/basn3p08.png", "broken/header_not_first.png", swap(IHDR, gAMA));
        gen("suite/basn3p08.png", "broken/gamma_after_palette.png", swap(PLTE, gAMA));
        gen("suite/basn3p08.png", "broken/palette_after_data.png", swap(PLTE, IDAT));
        gen("suite/basn3p08.png", "broken/missing_data.png", remove(find(IDAT)));
        gen("suite/basn3p08.png", "broken/missing_palette.png", remove(find(PLTE)));
        gen("suite/basn3p08.png", "broken/private_critical.png", setType(find(gAMA), GaMA));
        gen("suite/basn3p08.png", "broken/corrupt_type.png", setType(find(gAMA), gAM_));
        gen("suite/basn3p08.png", "broken/nonconsecutive_data.png",
            addAfter(find(IDAT), custom(heRB, new byte[0])),
            addAfter(find(heRB), find(IDAT)));
        gen("suite/basn3p08.png", "broken/corrupt_crc.png", setCRC(find(IHDR), 0x12345678));
        gen("suite/basn3p08.png", "broken/negative_length.png", setLength(find(gAMA), -20));
        gen("suite/basn3p08.png", "broken/multiple_gamma.png", duplicate(gAMA));
        gen("suite/basn3p08.png", "broken/multiple_palette.png", duplicate(PLTE));
        gen("suite/basn3p08.png", "broken/multiple_header.png", duplicate(IHDR));
        gen("suite/ch1n3p04.png", "broken/hist_before_palette.png", swap(PLTE, hIST));
        gen("suite/tbbn3p08.png", "broken/missing_palette_trans.png", remove(find(PLTE)));
        gen("suite/ccwn3p08.png", "broken/chrom_after_palette.png", swap(PLTE, cHRM));
        gen("suite/basn3p02.png", "broken/sigbits_after_palette.png", swap(PLTE, sBIT));
        gen("suite/basn0g01.png", "broken/gamma_after_data.png", swap(IDAT, gAMA));
        gen("suite/basn3p08.png", "broken/stereo_after_data.png",
            addAfter(find(IDAT), custom(sTER, new byte[]{ 0 })));
        Chunk trans = extract("suite/tbbn1g04.png", tRNS);
        gen("suite/basn4a08.png", "broken/trans_bad_color_type.png",
            addAfter(find(gAMA), custom(trans)));
    }

    private static Chunk extract(String src, int type)
    throws IOException
    {
        return find(type).query(readChunks(new File(src)));
    }

    private static void gen(String src, String dst, Processor... processors)
    throws IOException
    {
        List<Chunk> chunks = readChunks(new File(src));
        for (Processor p : processors)
            p.process(chunks);
        writeChunks(new File(dst), chunks);
    }

    private static Processor swap(int t1, int t2)
    {
        return swap(find(t1), find(t2));
    }

    private static Processor duplicate(int type)
    {
        return addAfter(find(type), find(type));
    }

    private static Processor addAfter(final Query after, final Query chunk)
    {
        return new Processor(){
            public void process(List<Chunk> chunks) {
                chunks.add(chunks.indexOf(after.query(chunks)) + 1, chunk.query(chunks));
            }
        };
    }

    private static Query custom(int type, byte[] data)
    throws IOException
    {
        return custom(new Chunk(type, data));
    }

    private static Query custom(final Chunk chunk)
    throws IOException
    {
        return new Query(){
            public Chunk query(List<Chunk> chunks) {
                return chunk;
            }
        };
    }

    private static Processor setLength(final Query q, final int length)
    {
        return new Processor(){
            public void process(List<Chunk> chunks) {
                q.query(chunks).length = length;
            }
        };
    }

    private static Processor setCRC(final Query q, final int crc)
    {
        return new Processor(){
            public void process(List<Chunk> chunks) {
                q.query(chunks).crc = crc;
            }
        };
    }

    private static Processor setType(final Query q, final int type)
    {
        return new Processor(){
            public void process(List<Chunk> chunks) throws IOException {
                Chunk chunk = q.query(chunks);
                chunk.type = type;
                chunk.crc = crc(type, chunk.data);
            }
        };
    }

    private static Processor remove(final Query q)
    {
        return new Processor(){
            public void process(List<Chunk> chunks) {
                chunks.remove(q.query(chunks));
            }
        };
    }

    private static Processor swap(final Query q1, final Query q2)
    {
        return new Processor(){
            public void process(List<Chunk> chunks) {
                Collections.swap(chunks,
                                 chunks.indexOf(q1.query(chunks)),
                                 chunks.indexOf(q2.query(chunks)));
            }
        };
    }

    private static Query find(int type)
    {
        return find(type, 0);
    }

    private static Query find(final int type, final int index)
    {
        return new Query(){
            public Chunk query(List<Chunk> chunks) {
                int count = 0;
                for (Chunk chunk : chunks) {
                    if (chunk.type == type && index == count++)
                        return chunk;
                }
                return null;
            }
        };
    }

    private interface Query
    {
        Chunk query(List<Chunk> chunks);
    }

    private interface Processor
    {
        void process(List<Chunk> chunks) throws IOException;
    }

    private static class Chunk
    {
        public int type;
        public byte[] data;
        public int crc;
        public int length;
        
        public Chunk(int type, byte[] data)
        throws IOException
        {
            this(type, data, crc(type, data));
        }
        
        public Chunk(int type, byte[] data, int crc)
        {
            this.type = type;
            this.data = data;
            this.crc = crc;
            this.length = data.length;
        }
    }

    private static int crc(int type, byte[] bytes)
    throws IOException
    {
        CheckedOutputStream checked = new CheckedOutputStream(new NullOutputStream(), new CRC32());
        DataOutputStream data = new DataOutputStream(checked);
        data.writeInt(type);
        data.write(bytes);
        data.flush();
        return (int)checked.getChecksum().getValue();
    }
    
    private static List<Chunk> readChunks(File src)
    throws IOException
    {
        List<Chunk> chunks = new ArrayList<Chunk>();
        FileInputStream in = new FileInputStream(src);
        try {
            DataInputStream data = new DataInputStream(new BufferedInputStream(in));
            data.readLong(); // signature
            int type;
            do {
                byte[] bytes = new byte[data.readInt()];
                type = data.readInt();
                data.readFully(bytes);
                int crc = data.readInt();
                chunks.add(new Chunk(type, bytes, crc));
            } while (type != IEND);
            return chunks;
        } finally {
            in.close();
        }
    }

    private static void writeChunks(File dst, List<Chunk> chunks)
    throws IOException
    {
        FileOutputStream out = new FileOutputStream(dst);
        try {
            DataOutputStream data = new DataOutputStream(out);
            data.writeLong(0x89504E470D0A1A0AL);
            for (Chunk chunk : chunks) {
                data.writeInt(chunk.length);
                data.writeInt(chunk.type);
                data.write(chunk.data);
                data.writeInt(chunk.crc);
            }
            data.flush();
        } finally {
            out.close();
        }
    }

    private static class NullOutputStream extends OutputStream
    {
        public void write(int b) { }
        public void write(byte[] b) { }
        public void write(byte[] b, int off, int len) { }
    }
}
