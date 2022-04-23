package huffman;

import java.io.*;

public class BitOutputStream {

    private FileOutputStream output;
    private int digits;     
    private int numDigits;  

    private static final int BYTE_SIZE = 8;  

public BitOutputStream(String file) {
        try {
            output = new FileOutputStream(file);
            
        } catch (IOException e) {
            throw new RuntimeException(e.toString());
        }
        digits = numDigits = 0;
    }

public void writeBit(int bit) {
        if (bit < 0 || bit > 1) {
            throw new IllegalArgumentException("Illegal bit: " + bit);
        }
        digits += bit << numDigits;
        numDigits++;
        if (numDigits == BYTE_SIZE) {
            flush();
        }
    }

    public void writeByte(StringBuilder b) {
        byte[] bb = b.toString().getBytes();
        for (int i = 0; i < bb.length; i++) {
            digits = bb[i];
            flush();

        }

    }
        public void writeByte(String b) {
        byte[] bb = b.toString().getBytes();
        for (int i = 0; i < bb.length; i++) {
            digits = bb[i];
            flush();

        }

    }
private void flush() {
        try {
            output.write(digits);
      
        } catch (IOException e) {
            throw new RuntimeException(e.toString());
        }
        digits = 0;
        numDigits = 0;
    }

public void close() {
        if (numDigits > 0) {
            flush();
        }
        try {
            output.close();
        } catch (IOException e) {
            throw new RuntimeException(e.toString());
        }
    }

protected void finalize() {
        close();
    }
}
