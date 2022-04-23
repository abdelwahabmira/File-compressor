package huffman;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class HuffmanCode {

    public static void main(String[] args) {
        while(true){
        HuffmanCode h = new HuffmanCode();
        
        
        System.out.println("Please enter file name");
        Scanner s = new Scanner(System.in);
        String fileName = s.nextLine();


        System.out.println("1.Compress input file\n2.Decompress input file");
        int choose = s.nextInt();

        if (choose == 1) {
            StringBuilder test = h.readFile(fileName);
            System.out.println("Compressing....");
            int[] charFreqs = new int[256];
            long start = System.currentTimeMillis();
            for (int i = 0; i < test.length(); i++) {
                charFreqs[test.charAt(i)]++;
            }
            HuffmanTree tree = buildTree(charFreqs);
            StringBuilder encode = encode(tree, test);

            h.compressFile(fileName, encode, test, tree);
            long end = System.currentTimeMillis();
            System.out.println("File is compressed in: " + (end - start) + " ms");
            File file1 = new File(fileName);
            File file2 = new File(fileName + "zip.txt");
            System.out.println(file2.length());
            float CompressionRatio= (float)((((float)file2.length() /(float)file1.length()))*100.0) ;
            System.out.println("Compression Ratio= "+" "+CompressionRatio +" %" );
      
            System.out.println();
        } else if (choose == 2) {
            System.out.println("Decompressing....");
            HashMap<String, Integer> hmap = new HashMap<String, Integer>();
            long start = System.currentTimeMillis();
            hmap = h.retrieveData(fileName);
            StringBuilder binary = h.readBinary(fileName);
            System.out.println("rana");
            String message = h.parseString(binary.toString(), hmap);
            //System.out.println("rana");
            h.writeDecompressedFile(message,fileName);
            long end = System.currentTimeMillis();
            System.out.println("File is decompressed in: " + (end - start) + " ms");
        }

    }
    }

    public static HuffmanTree buildTree(int[] charFreqs) {

        PriorityQueue<HuffmanTree> trees = new PriorityQueue<HuffmanTree>();

        for (int i = 0; i < charFreqs.length; i++) {
            if (charFreqs[i] > 0) {
                trees.offer(new HuffmanLeaf(charFreqs[i], (char) i));
            }
        }

        while (trees.size() > 1) {

            HuffmanTree a = trees.poll(); 
            HuffmanTree b = trees.poll(); 

            trees.offer(new HuffmanNode(a, b));
        }

        return trees.poll();
    }

    public static StringBuilder encode(HuffmanTree tree, StringBuilder encode) {
        if (tree != null)
        { StringBuilder encodeText = new StringBuilder("");
        for (int i = 0; i < encode.length(); i++) {
            encodeText.append(getCodes(tree, new StringBuffer(), encode.charAt(i)));
        }
        return encodeText;
    }   
        else
            return null;
}

    public static StringBuilder decode(HuffmanTree tree, StringBuilder encode) {
        
        if(tree!=null){
        StringBuilder decodeText = new StringBuilder("");
        HuffmanNode node = (HuffmanNode) tree;
        for (int i = 0; i < encode.length(); i++) {
            if (encode.charAt(i) == '0') {
                if (node.left instanceof HuffmanLeaf) {
                    decodeText.append(((HuffmanLeaf) node.left).value);
                    node = (HuffmanNode) tree;
                } else {
                    node = (HuffmanNode) node.left;
                }
            } else if (encode.charAt(i)== '1') {
                if (node.right instanceof HuffmanLeaf) {
                    decodeText.append(((HuffmanLeaf) node.right).value);
                    node = (HuffmanNode) tree;
                } else {
                    node = (HuffmanNode) node.right;
                }
            }
        }
        return decodeText;
    }
        return null;
    }

    public static void printCodes(HuffmanTree tree, StringBuffer prefix) {
        if(tree!=null){
        if (tree instanceof HuffmanLeaf) {
            HuffmanLeaf leaf = (HuffmanLeaf) tree;
            System.out.println(leaf.value + "\t" + leaf.frequency + "\t\t" + prefix);
        } else if (tree instanceof HuffmanNode) {
            HuffmanNode node = (HuffmanNode) tree;

            prefix.append('0');
            printCodes(node.left, prefix);
            prefix.deleteCharAt(prefix.length() - 1);

            prefix.append('1');
            printCodes(node.right, prefix);
            prefix.deleteCharAt(prefix.length() - 1);
        }
    }
    }
    
    public static String getCodes(HuffmanTree tree, StringBuffer prefix, char w) {
        if(tree!=null){
        if (tree instanceof HuffmanLeaf) {
            HuffmanLeaf leaf = (HuffmanLeaf) tree;

            if (leaf.value == w) {
                return prefix.toString();
            }

        } else if (tree instanceof HuffmanNode) {
            HuffmanNode node = (HuffmanNode) tree;

            prefix.append('0');
            String left = getCodes(node.left, prefix, w);
            prefix.deleteCharAt(prefix.length() - 1);

            prefix.append('1');
            String right = getCodes(node.right, prefix, w);
            prefix.deleteCharAt(prefix.length() - 1);

            if (left == null) {
                return right;
            } else {
                return left;
            }
        }
        }
        else{
        return null;
        }
        return null;
    }

    void compressFile(String filename, StringBuilder binary, StringBuilder input, HuffmanTree tree) {

        String fileName = filename + "zip.txt";
        BitOutputStream o = new BitOutputStream(fileName);
        char c;
        String code = "";
        String fileLine = "";

        String duplicatesRemoved = removeDuplicates(input.toString());

        for (int i = 0; i < duplicatesRemoved.length(); i++) {
            char ch = duplicatesRemoved.charAt(i);
            int asciiValue = (int) ch;
            char charIn= (char)asciiValue;
            code = (getCodes(tree, new StringBuffer(), ch));
            fileLine = asciiValue + " " + code + "\n";
            o.writeByte(fileLine);
        }

        fileLine = "s\n";
        o.writeByte(fileLine);

        int bit;

        for (int i = 0; i < binary.length(); i++) {
            c = binary.charAt(i);
            bit = Character.getNumericValue(c);
            o.writeBit(bit);
            //System.out.printf("%d", bit);	
        }

        o.finalize();
        System.out.println("File " + filename + " compression is done.");

    }

    void printHeader(String input, HuffmanTree tree) {

        char c;
        String code = "";
        System.out.println("Header table");

        String duplicatesRemoved = removeDuplicates(input);

        for (int i = 0; i < duplicatesRemoved.length(); i++) {

            char ch = duplicatesRemoved.charAt(i);
            int asciiValue = (int) ch;
            code = (getCodes(tree, new StringBuffer(), ch));
            System.out.println("Ascii value: " + asciiValue + "\t" + "Binary code: " + code);

        }
    }

    public String removeDuplicates(String str) {
        Set<Character> set = new HashSet<>();
        StringBuffer sf = new StringBuffer();

        for (int i = 0; i < str.length(); i++) {
            Character c = str.charAt(i);
            if (!set.contains(c)) {
                set.add(c);
                sf.append(c);
            }
        }

        return sf.toString();

    }

    public HashMap retrieveData(String fileName) {
        HashMap<String, Integer> hmap = new HashMap<String, Integer>();
        String line = null;
        StringBuilder test = new StringBuilder("");
        int i = 0;
        String code;
        int ascii;
        String[] splitted;
        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while ((line = bufferedReader.readLine()) != null) {
                
                test.append(line);
                if (test.indexOf("s") != -1) {
                    break;
                }
                splitted = test.toString().split(" ");
                ascii = Integer.parseInt(splitted[0]);
                code = splitted[1];
                hmap.put(code, ascii);
                splitted[0] = "";
                splitted[1] = "";
                test.replace(0,test.length(),"");
            }

            bufferedReader.close();
        } 
        catch (FileNotFoundException ex) {
            System.out.println("Unable to open file '" + fileName + "'");
        } catch (IOException ex) {
            System.out.println("Error reading file '" + fileName + "'");

        }

        return hmap;

    }

    public void printHashMap(HashMap hmap) {
        Set set = hmap.entrySet();
        Iterator iterator = set.iterator();

        while (iterator.hasNext()) {
            Map.Entry mentry = (Map.Entry) iterator.next();
            System.out.print("key is: " + mentry.getKey() + "value is: " + mentry.getValue());
            System.out.println();
        }
    }

    public StringBuilder readBinary(String fileName) {

        BitInputStream i = new BitInputStream(fileName);
        Scanner s = new Scanner(fileName);
        int result = 0;
        StringBuilder results = new StringBuilder("");
        char c;
        int y;

        while ((c = (char) i.readBit(0)) != 's') {

        }
        while ((y = i.readBit(1)) != -1) {
            results.append(Integer.toString(y));
        }

        i.finalize();

        return results;

    }

    public String parseString(String binary, HashMap hmap) {
        String temp = "";
        int count = 0;
        char c;
        int ascii;
        StringBuilder message = new StringBuilder("");

        for (int i = 0; i < binary.length(); i++) {
            c = binary.charAt(i);
            temp+=c;
            boolean flag = hmap.containsKey(temp);

            if (flag == true) {
                ascii = (int) hmap.get(temp);
                message.append(new Character((char) ascii).toString());
                temp ="";
            }
        }

        //System.out.println(message);
        return message.toString();

    }

    public StringBuilder readFile(String fileName) {

        String line = null;
        StringBuilder test =new StringBuilder(""); 
        int i = 0;
        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while ((line = bufferedReader.readLine()) != null) {
                test.append(line);
                test.append("\n");
            }

            bufferedReader.close();
        } //catch errors
        catch (FileNotFoundException ex) {
            System.out.println("Unable to open file '" + fileName + "'");
        } catch (IOException ex) {
            System.out.println("Error reading file '" + fileName + "'");

        }
        return test;

    }

    public  void writeDecompressedFile(String message, String fileName) {

        BitOutputStream o = new BitOutputStream(fileName + "1.txt");
        o.writeByte(message.toString());
        o.finalize();
        System.out.println("File " + fileName + " decompression is done.");

    }
}


