package huffman;


import java.io.*;

public class BitInputStream {

    private FileInputStream input;
    private int digits;     
    private int numDigits;  

    private static final int BYTE_SIZE = 8; 

public BitInputStream(String file) {
        try {
            input = new FileInputStream(file);
        } catch (IOException e) {
            throw new RuntimeException(e.toString());
        }
        nextByte();
    }


    public int readBit1() {
    
        if (digits == -1) {
            return -1;
        }
        int result = digits % 2;
        digits /= 2;
        numDigits++;
        if (numDigits == BYTE_SIZE) {
            nextByte();
        }
        return result;
    }
public int readBit(int isBit) {
        int result;
        if (isBit == 1) {
            if (digits == -1) {
                return -1;
            }
            result = digits % 2;
            digits /= 2;
            numDigits++;
            if (numDigits == BYTE_SIZE) {
                nextByte();
            }
        } else {
            result = digits;
            nextByte();
        }
        return result;
    }

public void nextByte() {
        try {
            digits = input.read();
        } catch (IOException e) {
            throw new RuntimeException(e.toString());
        }
        numDigits = 0;
    }

 public void close() {
        try {
            input.close();
        } catch (IOException e) {
            throw new RuntimeException(e.toString());
        }
    }

protected void finalize() {
        close();
    }
}
