package A_RunApplication;


import java.io.File;
import java.io.IOException;

import B_Classes.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;



public class Driver_FX extends Application {


    static int[] rep = new int[256];
    static String fileName;
    static String fileEnd;
    static String filePath;

    static String fileName2;
    static String fileEnd2;
    static String filePath2;

    static File in;
    static TextField pathField;
    static Button compress;


    static TextField pathField2;
    static Button decompress;
    static Stage stage;
    static BorderPane root;

    //static Button head;
   // static Button statistics;
    static WriteData write1;

    static VBox rightBox;


    @Override
    public void start(Stage primaryStage) {
        try {
            stage = primaryStage;
            root = new BorderPane();
            root.setStyle("-fx-background-color: #48D1CC;");
            Scene scene = new Scene(root,800,600);

            VBox topBox = new VBox(20);
            topBox.setId("TOP");
            topBox.setAlignment(Pos.CENTER);

            pathField = new TextField();
            pathField.setEditable(false);
            pathField.setMinWidth(300);
            pathField.setMinHeight(30);
            HBox box = new HBox(30);
            topBox.setAlignment(Pos.CENTER);
            topBox.setPadding(new Insets(20,0,0,50));
            Button browse = new Button("Select file");
            browse.setMinWidth(100);
            compress = new Button("Compress");
            box.getChildren().addAll(pathField,browse,compress);
            topBox.getChildren().add(box);

            
        

            VBox bottomBox = new VBox(20);
            bottomBox.setId("BOTTOM");
            pathField2 = new TextField();
            pathField2.setEditable(false);
            pathField2.setMinWidth(300);
            pathField2.setMinHeight(30);
            HBox box2 = new HBox(30);
            bottomBox.setAlignment(Pos.CENTER);
            bottomBox.setPadding(new Insets(20,0,0,50));
            Button browse2 = new Button("select file");
            browse2.setMinWidth(100);
            decompress = new Button("Decompress");
            box2.getChildren().addAll(pathField2,browse2,decompress);
            bottomBox.getChildren().add(box2);

            
           

            
            
            
            ///////////////////////////////
            //action

            browse.setOnAction(e -> readOriginFileName());
            compress.setOnAction(e -> startCompress());

            browse2.setOnAction(e -> readCompressedFile());
            decompress.setOnAction(e -> startDecompress());


            

            VBox leftBox = new VBox(30);
            leftBox.setId("LEFT");
            leftBox.setMinWidth(150);
            leftBox.setAlignment(Pos.CENTER);
            
            root.setLeft(leftBox);



            rightBox = new VBox();


            root.setTop(topBox);
            root.setBottom(bottomBox);

            primaryStage.setScene(scene);
            primaryStage.setTitle("Huffman Code");
            primaryStage.show();


        } catch(Exception e) {
            e.printStackTrace();
        }
    }


    static void readOriginFileName() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        in = fileChooser.showOpenDialog(null);
        fileName = in.getName();
        int ind = in.getPath().lastIndexOf(".");
        filePath = in.getPath().substring(0,ind) + ".huff";
        int index = fileName.lastIndexOf(".");
        fileEnd = fileName.substring(index+1);
        pathField.setText(in.getPath());
        System.out.println(fileName);
        System.out.println(in.getPath());
        System.out.println(fileEnd);
    }



    static void  startCompress()  {
        try {
           
            ReadFile read = new ReadFile();
            read.readFile(rep,in);
            HuffmanCode encode = new HuffmanCode();
            encode.generateCodes(rep);
            System.out.println(10);
            System.out.println(encode.codes);
            System.out.println(11);
            write1 = new WriteData();
            write1.compress(encode.codes, rep,in,filePath,fileEnd);
        } catch (Exception e) {
            System.out.println("error  " + e.getMessage());
        }
    }

    static void readCompressedFile() {
        FileChooser fileChooser = new  FileChooser();
        fileChooser.setTitle("Open Resource File");
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("Huffman Code Compressd(*.huff)", "*.huff");
        fileChooser.getExtensionFilters().add(extFilter);
        in = fileChooser.showOpenDialog(null);
        fileName = in.getName();
        int ind = fileName.lastIndexOf(".");
        fileName = fileName.substring(0, ind);
        int index = in.getPath().lastIndexOf(".");
        filePath = in.getPath().substring(0,index) + "-.";
        pathField2.setText(in.getPath());


    }

    static void  startDecompress()  {
        try {
            WriteData write = new WriteData();
            write.decompress(in,filePath,fileName);

        } catch (Exception e) {
            System.out.println("error  " + e.getMessage());
        }
    }

    public static void main(String[] args) throws IOException {


        launch(args);
    }
}

