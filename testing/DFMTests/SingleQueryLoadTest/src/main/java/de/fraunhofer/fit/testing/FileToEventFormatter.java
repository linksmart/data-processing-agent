package de.fraunhofer.fit.testing;

import javafx.scene.input.DataFormat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * Created by José Ángel Carvajal on 22.12.2015 a researcher of Fraunhofer FIT.
 */
public class FileToEventFormatter {

    private BufferedReader bufferedReader;

    public FileToEventFormatter(String filePath) throws FileNotFoundException {

        bufferedReader = new BufferedReader(new FileReader(filePath));
    }

    public EventType
}
