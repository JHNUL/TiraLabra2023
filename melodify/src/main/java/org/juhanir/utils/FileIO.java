package org.juhanir.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class FileIO {

    public InputStream readFile(String filePath) throws FileNotFoundException {
        return new FileInputStream(new File(filePath));
    }

}
