package com.belafon.worldsfactory;

import org.junit.Test;

import com.belafon.worldsfactory.interpreter.PythonInterpreter;

public class PythonInterpreterTest {
    @Test
    public void writeAndRead() {
        PythonInterpreter interpreter = new PythonInterpreter();
        try {
            var hello = interpreter.sendCommandWithOutput("print('Hello World!')");
            assert hello.equals("Hello World!");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                interpreter.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
