package com.java.source;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class ConverterFactory {

    /**
     * Get Input and Output filenames from command line interface
     *
     * @param args
     */
    public static void main(String[] args) {
        try {
            String input = args[0];
            String output = args[1];
            XMLJSONConverterI obj = new ConverterImpl();
            String status = obj.convertJSONtoXML(input, output);
            System.out.println("JSON2XMLGeneration " + status);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Please pass input and output filenames");
        } catch (Exception e) {
            System.out.println("Exception while Passing Input/Output filenames");
        }

    }

}
