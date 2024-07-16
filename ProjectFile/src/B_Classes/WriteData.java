package B_Classes;

import java.io.BufferedInputStream; 
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Stack;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class WriteData {
    BufferedOutputStream out;
    BufferedInputStream in;

    VBox rightBox;

    TableView<Code> statTable;
    TextArea area;



    public  void compress(Hashtable<Character,Code> codes,int[] rep,File inFile,String filePath,String end) throws IOException {


        area = new TextArea();
        statTable = new TableView<>();
        TableColumn<Code, String> number = new TableColumn<>("   #   ");
        number.setSortable(false);
        number.setCellValueFactory(column-> new ReadOnlyObjectWrapper(statTable.getItems().indexOf(column.getValue())+1));
        TableColumn<Code,String> chars = new TableColumn<>("Character");
        TableColumn<Code,Integer> repetion = new TableColumn<>("Number Of repetion");
        TableColumn<Code,Integer> length = new TableColumn<>("Length");
        TableColumn<Code,String> huffCode = new TableColumn<>("Huff Code");



        statTable.getColumns().addAll(number,chars,repetion,length,huffCode);

        chars.setCellValueFactory(new PropertyValueFactory<>("ch"));
        repetion.setCellValueFactory(new PropertyValueFactory<>("rep"));
        length.setCellValueFactory(new PropertyValueFactory<>("length"));
        huffCode.setCellValueFactory(new PropertyValueFactory<>("code"));


        ObservableList<Code> list = getStatList(codes,rep);
        statTable.setItems(list);



        File outFile = new File(filePath);

        in = new BufferedInputStream( new FileInputStream(inFile)); // open file that i want to compress

        out = new BufferedOutputStream(new FileOutputStream(outFile)); // open file that  i want  to  write  my compressed data to

        MyWriter writeTiFile = new MyWriter(out); // create opject of MyWriter class that write data on the file as its type

        long numOfChars = 0;
        for (int i = 0 ; i < rep.length ; i++) // this loop for calculating number of character in the origin file
            numOfChars += rep[i];


        Label t1 = new Label("Size Before Compress  :  " + numOfChars + " Byte");
        System.out.println(1);
        System.out.println(numOfChars);
        writeTiFile.writeString(end); // write origin file's extension to the header of the compressed file
        writeTiFile.writeByte((byte) '|'); // write (new line) character after extension that help me to read it when i decompress the file
        writeTiFile.writeLong(numOfChars); // write number of character to the header of the compressed file



        int[] head = headCode(codes, rep); // generate the header code

        area.appendText(end + "\n" + numOfChars + "\n" + head.length + "\n");






        System.out.println(2);
        System.out.println("head : " + head.length);
        writeTiFile.writeByte((byte)(head.length - 1)); // write number of huffman codes



        // be carefull this method write the integer reversed
        for (int i = 0 ; i < head.length ; i++)
            writeTiFile.writeInt(head[i]); // write header

        for (int i = 0 ; i < head.length ; i++) {
            area.appendText(head[i] + "\n");
        }

        long numOfCharsAfterCompress = 0;

        long size = (long) (inFile.length() / 4);
        long rem = (long) (inFile.length() % 4);
        System.out.println(3);
        System.out.println("sizeee ===  " + size + "  remmmm === " + rem);




        int buff = 0; // this buffer count to 8 then reset
        byte[] dat = new byte[4]; // this array hold 4 bytes that encrypted to write it on file
        int datCount = 0; // counter for dat array
        for (long i  = 0 ; i < size ; i++) {
            byte[] arr = new byte[4];
            in.read(arr); // read 4 bytes each time to minimize consumption of cpu

            for (int j = 0 ; j < 4 ; j++) {
                String code = codes.get((char)(arr[j] & 0xFF)).code; // get the huffman code for the character

                numOfCharsAfterCompress += code.length();

                for (int l = 0 ; l < code.length() ; l++) {

                    /*
                     *
                     * in this loop i get every huffman code for every character in the origin file and stick the codes to each other
                     * in the dat array that consists of 4 bytes each bytes from 8 bits
                     * so every time i stick a byte i check of the array index of the array is full with bytes or not and check if the array is full
                     * with 4 bytes or not
                     *
                     *
                     *
                     */

                    buff++;
                    dat[datCount] <<= 1;
                    if (code.charAt(l) == '1') {
                        dat[datCount] |= 1;
                    }
                    else  {
                        dat[datCount] |= 0;
                    }

                    if (buff == 8) {
                        datCount++;
                        buff = 0;
                    }

                    if (datCount == 4) {
                        datCount = 0;
                        writeTiFile.write4Bytes(dat);
                        dat = new byte[4];
                    }

                }
            }

        }

        for (long i  = 0 ; i < rem ; i++) {

            /*
             *
             *
             * this loop is like the previous one but the previous one work for 4 bytes each time
             * but what if there is 3 bytes at the end ?
             * in this loop we will read the remaining bytes from the file and stick its code to the other bits
             */

            byte[] arr = new byte[1];
            in.read(arr);
            String code = codes.get((char)(arr[0] & 0xFF)).code;

            numOfCharsAfterCompress += code.length();

            for (int l = 0 ; l < code.length() ; l++) {
                buff++;
                dat[datCount] <<= 1;
                if (code.charAt(l) == '1') {
                    dat[datCount] |= 1;
                }
                else  {
                    dat[datCount] |= 0;
                }

                if (buff == 8) {
                    datCount++;
                    buff = 0;
                }

                if (datCount == 4) {
                    datCount = 0;
                    writeTiFile.write4Bytes(dat);
                    dat = new byte[4];
                }

            }

        }



        /*
         *
         * i the above loops i used " write4Bytes " method to write bytes to the file
         * but sometime there will be at the few bytes less than 4 that we didn't write because they are not equal 4
         * so we move them to a stack and write them bit by bit using " writeBit " method
         *
         */
        Stack<Integer> st = new Stack<>();

        while (buff != 0 && datCount >= 0) {

            for (int i = 0 ; i < buff ; i++) {
                if ((dat[datCount] & 1) == 1) {
                    st.push(1);
                } else  {
                    st.push(0);
                }
                dat[datCount] >>>= 1;
            }

            buff = 8;
            datCount--;
        }

        while (!st.isEmpty()) {
            if (st.pop() == 1) {
                writeTiFile.writeBit(true);
            }
            else  {
                writeTiFile.writeBit(false);
            }

        }

        while (writeTiFile.n != 0)
            writeTiFile.writeBit(false);



        in.close();



        Label t2 = new Label("Header Size : " + ((head.length * 4) + end.length() + 2 + 8) + " Byte");


        long sizeAfterCompress = (long) Math.ceil(numOfCharsAfterCompress / 8);
        Label t3 = new Label("Compressed Data Size : " + sizeAfterCompress + " Byte");

        Label t4 = new Label("Size After Compress : " +( sizeAfterCompress + head.length * 4 + 2 + 8 + end.length()) + " Byte");

        sizeAfterCompress += (head.length * 4) + 2 + 8 + end.length();

        int ratio = (int) ((double) sizeAfterCompress/numOfChars * 100);


        Label t5 = new Label("Ratio Of Compressing : " + ratio + "%");
        t1.setMinWidth(270);
        t2.setMinWidth(270);
        t3.setMinWidth(270);
        t4.setMinWidth(270);
        t5.setMinWidth(270);
        rightBox = new VBox(30);
        rightBox.setMinWidth(200);
        rightBox.setAlignment(Pos.CENTER_LEFT);
        rightBox.getChildren().addAll(t1,t2,t3,t4,t5);

        int cc = 1;

        out.close();

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText("State");
        alert.setContentText("Your file has been compressed");

        alert.showAndWait();


    }

    // this method is for gui
    public ObservableList<Code> getStatList(Hashtable<Character,Code> codes,int[] rep) {
        System.out.println(4);
        System.out.println("rep2 length = " );
        ObservableList<Code> list = FXCollections.observableArrayList();

        for (int i = 0 ; i < rep.length ; i++) {

            if (rep[i] > 0) {
                list.add(codes.get((char)i));
            }
        }
        return list;
    }




    public void decompress(File inFile,String filePath,String name) throws IOException {

        System.out.println("ttttt");
        in = new BufferedInputStream( new FileInputStream(inFile));



        // initialize the variable that will hold the file extension from huff file
        String fileExtension = "";



        // creat an array that will read the extension characters byte by byte
        byte[] b = new byte[1];


        // start reading extension
        while (true) {
            in.read(b);
            if ((int)b[0] == (int)'|')
                break;
            fileExtension += (char)(b[0] & 0xFF);
        }

        filePath += fileExtension;
        System.out.println(filePath);

        // create fileOutputStream with file path to start writing data
        FileOutputStream ff = new FileOutputStream(new File(filePath));
        out = new BufferedOutputStream(ff);
        MyWriter writeToFile = new MyWriter(out);


        // read number of characters in the origin file to find a point to stop reading data when reach it
        b = new byte[8];

        in.read(b);
        long numOfChars = 0;

        // read 8 bytes = 1 long which by shifting the bytes to the right 8 bits each time to get the long value
        for (int i = 7 ; i >= 0 ; i--) {
            numOfChars <<= 8;
            numOfChars |= (b[i] & 0xFF);
        }

        System.out.println(numOfChars);

        // read number of codes 0 - 255
        b = new byte[1];

        in.read(b);

        int numberOfCodes = (int)(b[0] & 0xFF) + 1;

        System.out.println("6666");
        System.out.println(numberOfCodes);
        Hashtable<Character,Code> codes = new Hashtable<>();

        // create hash that will hold each huff code as a key and the value for each code will be the origin character
        Hashtable<String,Character> revCodes = new Hashtable<>();

        // this method will get the huffman's code for each character from the head of the file
        codes =  getHuffmanCodes(numberOfCodes,revCodes);
        //System.out.println(codes);
        System.out.println(revCodes);

        StringBuilder codee = new StringBuilder();



        System.out.println("test");

        int start = 0;
        int n = 0;
        byte[] dat = new byte[4];
        int datCount = 0;
        long size = numOfChars / 4;
        int rem = (int) (numOfChars % 4);



        /*
         *
         * in this method we read 4 bytes each time and analysis them
         * for each byte i get its all bits bye comparing the fist bit from the righ using & op and stick it to (codee) which is StringBuilder
         * then  i start stick every 0 or 1 from the first of the codee to a string and each time i check if it exist in the hash O(1)
         * if it exist then get it's value (binary character value) and put it in an array(dat) then initialize that string and continue on this way
         * if (dat) array holds 4 byte we will print it to the file using spacific method
         *
         */

        for (int i = 0 ; i < size ; i++) {
            b = new byte[4];
            in.read(b);

            for (int k = 0 ; k < 4 ; k++) {
                start = codee.length();

                for (int j = 0 ; j < 8 ; j++) {
                    if ((b[k] & 1) == 1)
                        codee.insert(start, "1");
                    else
                        codee.insert(start, "0");
                    b[k] >>>= 1;
                }

                String str = "";

                int index = 0;

                // the (index) value will check if we finish the codee without finding any matching with the hash keys then the loop will stop and read
                // another byte and redoing the steps again
                while (index < codee.length() && (n < numOfChars)) {
                    str += codee.charAt(index++);
                    if (revCodes.containsKey(str)) {
                        n++;
                        codee.delete(0, index);
                        int c = revCodes.get(str);
                        dat[datCount++] = (byte)c;

                        if (datCount == 4) {
                            writeToFile.write4Bytes(dat);
                            dat = new byte[4];
                            datCount = 0;
                        }
                        str = "";
                        index = 0;
                    }
                }

            }
        }

        for (int i = 0 ; i < rem ; i++) {
            b = new byte[1];
            in.read(b);


            start = codee.length();

            for (int j = 0 ; j < 8 ; j++) {
                if ((b[0] & 1) == 1)
                    codee.insert(start, "1");
                else
                    codee.insert(start, "0");
                b[0] >>= 1;
            }

            String str = "";

            int index = 0;

            while (index < codee.length() && (n < numOfChars)) {

                str += codee.charAt(index++);
                if (revCodes.containsKey(str)) {
                    n++;
                    codee.delete(0, index);
                    int c = revCodes.get(str);
                    dat[datCount++] = (byte)c;
                    //	writeToFile.writeByte((byte)c);
                    if (datCount == 4) {
                        writeToFile.write4Bytes(dat);
                        dat = new byte[4];
                        datCount = 0;
                    }
                    str = "";
                    index = 0;
                }
            }

        }



        if (datCount != 0) {
            for (int i = 0 ; i < datCount ; i++)
                writeToFile.writeByte(dat[i]);
        }


        System.out.println(numOfChars);
        System.out.println(n);

        out.close();
        in.close();

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText("State");
        alert.setContentText("Your file has been decompressed");

        alert.showAndWait();


    }

    public Hashtable<Character,Code>  getHuffmanCodes(int numberOfCodes,Hashtable<String,Character> revCodes) throws IOException  {

        /*
         *
         * this method work as this ->
         * when we compress the file we store some information in the head as following ->
         * we store them in an (int) = 4 bytes but reversed
         * the first byte from the right hold the binary value of the character
         * the second byte from the right hold the value of the length of the huffman's  code
         * the last 2 bytes hold the code it self
         *  so lets follow the code
         *
         *
         */
        byte[] b;
        Hashtable<Character,Code> codes = new Hashtable<>();
        for (int i = 0 ; i < numberOfCodes ; i++) {
            System.out.println(5);

            System.out.println("i = " + i);
            // i create a stringBuilder that will hold the code for each character
            StringBuilder c = new StringBuilder();
            // read the integer (4 bytes)
            b = new byte[4];
            in.read(b);
            // will hold the 4 bytes
            int code = 0;

            /* beacuse we wrote the integer reversed in the head
             * so the first 2 bytes from the left will hold the huffman's code
             * we get the second one then stick it to the (code) value we shift it to the left
             * then we get the first one and stick it to the (code) without shifting it
             * so actually we swipe the first 2 bytes because they are reversed
             */
            code |= (b[1] & 0xFF);
            code <<= 8;
            code |= (b[0] & 0xFF);

            // this loop go throw the (code) n bits where n is the length of the huff code that stored in the 2nd index of the array
            for (int j = 0 ; j < (b[2] & 0xFF)  ; j++) {
                /*
                 * each loop we compare the first bit from the right using & operation ans if it 1 so insert 1 to he first of the (c) which hold the huff it self
                 * and if it zero we will insert zero as a string for both zero or 1
                 */
                if ((code & 1) == 1) {
                    c.insert(0, "1");
                } else
                    c.insert(0, "0");
                code >>>= 1;
            }

            // codes.put((char)(b[3] & 0xFF), new Code((char)(b[3] & 0xFF),c.toString(),rep2[b[0] & 0xFF]));
            System.out.println(6);
            System.out.println("c = " + c);

            // we store the huff code as a key in the hash and its value will be the character that in the index 3
            revCodes.put(c.toString(), (char)(b[3] & 0xFF));

        }
        return codes;
    }

    public int[] headCode(Hashtable<Character,Code> codes,int[] rep) {

        /*
         *
         * in this method i generate a good representation for the huffman codes and their characters by put the character and its length and its
         * huffman code in 1 integer (4 bytes)
         * the first byte from left will contain the character value 0 - 256 (8 bits)
         * the second byte from left will contains the length of the huff code for that character
         * the othe 2 bytes wilontain the huff code so that i have 2 bytes (16 bit) which mean it can contains 16 digit for huffman code
         * which is large length and it's impossible that the length of the huffman code rach this number of digit
         * we have 256 so what ever number of repetion for any character so that it's huffman code will not be more that 16
         *
         *
         *
         *
         */

        int count = 0;
        int counter = 0;
        for (int i = 0 ; i < rep.length ; i++)
            if (rep[i] > 0)
                count++;
        int[] headArr = new int[count]; // create integer array that will hold the integer value for the every character information

        for (int i = 0 ; i < rep.length ; i++) {


            if (rep[i] > 0) {
                System.out.println(7);
                System.out.println(i);
                int num = 0;

                //num |= codes.get((char)i).code.length();

                String c = codes.get((char)i).code; // get huffman code for the character from hash table which is O(1)
                System.out.println(8);
                System.out.println(c + "  " + c.length());
                for (int j = 0 ; j < c.length() ; j++) {
                    num <<= 1; // shift the integer by 1 bit to the left
                    if (c.charAt(j) == '1') {
                        num |= 1; // make or operation for each digit of the huffman code with num that will contains the information
                    } else {
                        num |= 0;
                    }

                }
                int ch = i;
                ch <<= 24; // shift character value 24 bit to the left so that it will be in the first byte to the left of the integer
                int l = c.length();
                l <<= 16; // shift the length of huffman code 16 bit to the left so that it will be in the second byte to the left of the integer
                num |= ch; // make or operation that will put the value of character to the fisrt byte of the integer
                num |= l; // make of operation that will put the calue of huffman code to the second byte of the integer

                /*
                 *  integer representation >>  00000000000000000000000000000000
                 *  my representation >>	      ccccccccllllllllhhhhhhhhhhhhhhhh
                 *
                 */
                System.out.println(9);
                System.out.println(num);
                headArr[counter++] = num;

            }
        }
        return headArr;

    }



}