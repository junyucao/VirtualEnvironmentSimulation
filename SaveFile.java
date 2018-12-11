//package com.jetbrain;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SaveFile {
    public static void SaveGame (byte[] save) {
        FileOutputStream fos = null;
        File file;
        try {
            //Specify the file path here
            file = new File("/Users/david/Documents/Test/GameSave.bin");
            fos = new FileOutputStream(file);

        /*If the file is not found
     * at the specified location it would create
     * a new file*/
            if (!file.exists()) {
                file.createNewFile();
            }

            fos.write(save);
            fos.flush(); //forces the file to be written out
            System.out.println("Game saved Successfully");
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
        finally {
            try {
                if (fos != null)
                {
                    fos.close();
                }
            }
            catch (IOException ioe) {
                System.out.println("Error in closing the Stream");
            }
        }
    }
}


