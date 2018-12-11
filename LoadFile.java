//package com.jetbrain;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.*;
public class LoadFile {

    public static String LoadGame() {

        // The name of the file to open.
        String fileName = "/Users/david/Documents/Test/GameSave.bin";

        // This will reference one line at a time
        String line = "";
        String Load = "";

        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader =
                    new FileReader(fileName);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader =
                    new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
                //System.out.println(line);
                Load = Load + line;
            }
            // Always close files.
            bufferedReader.close();
            System.out.println("Game file loaded successfully");
            return Load;

        }
        catch(FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" +
                            fileName + "'");
        }
        catch(IOException ex) {
            System.out.println(
                    "Error reading file '"
                            + fileName + "'");
        }
        return null;
    }
}


