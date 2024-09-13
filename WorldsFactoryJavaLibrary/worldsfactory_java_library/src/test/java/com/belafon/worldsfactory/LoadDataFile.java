package com.belafon.worldsfactory;

import java.io.InputStream;
import java.util.Scanner;

import org.junit.Assert;

public class LoadDataFile {
    public String loadCode(String fileName) {
        InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream(fileName);
        StringBuilder exampleCode = new StringBuilder();
        if (inputStream != null) {
            Scanner scanner = new Scanner(inputStream);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                exampleCode.append(line).append("\n");
            }
            scanner.close();
        } else {
            Assert.fail("File not found");
        }
        return exampleCode.toString();
    }
}
