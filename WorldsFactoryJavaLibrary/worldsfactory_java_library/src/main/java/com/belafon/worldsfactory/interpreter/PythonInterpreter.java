package com.belafon.worldsfactory.interpreter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class PythonInterpreter {
    private static final String END_OF_MESSAGE = "_eom_";
    private static final String EXCEPTION = "_exception_";
    private ProcessBuilder processBuilder;
    private Process process;
    private BufferedWriter bw;
    private BufferedReader br;

    public PythonInterpreter() {
        processBuilder = new ProcessBuilder("python3", "-i");

        try {
            process = processBuilder.start();
        } catch (IOException e) {
            throw new Error(e);
        }

        var os = process.getOutputStream();
        var osw = new OutputStreamWriter(os);
        bw = new BufferedWriter(osw);
        var isr = new InputStreamReader(process.getInputStream());
        br = new BufferedReader(isr);
    }

    public void sendCommand(String command) {
        try {
            bw.write(command);
            bw.newLine();
            bw.flush();
        } catch (IOException e) {
            if(!process.isAlive())
                return;
            
            throw new Error(e);
        }
    }

    public String sendCommandWithOutput(String command) {
        sendCommand(command + "\nprint('" + END_OF_MESSAGE + "')");
        return receiveData();
    }

    public List<String> sendCommandWithOutputSeparatedLines(String command) {
        sendCommand(command + "\nprint('" + END_OF_MESSAGE + "')");
        return receiveDataWithLinesInLIst();
    }

    /**
     * Delivers data from python interpreter as a string,
     * it handles exceptions
     * @return
     */
    public String receiveData() {
        StringBuilder output = new StringBuilder();
        String line;
        try {
            while ((line = br.readLine()) != null) {
                if (line.equals(END_OF_MESSAGE))
                    break;

                if (line.equals(EXCEPTION)) {
                    String exceptionMessage = receiveData();
                    throw new Error(exceptionMessage);
                }

                output.append(line);
                output.append(System.lineSeparator());

            }
        } catch (IOException e) {
            if(!process.isAlive())
                return output.toString();
            throw new Error(e);
        }

        if (output.length() > 0
                && output.charAt(output.length() - 1) == '\n')
            output.deleteCharAt(output.length() - 1);

        return output.toString();
    }

    /**
     * Delivers raw data from python interpreter,
     * it doesnt handle exceptions
     * @return list of lines
     */
    public List<String> receiveDataWithLinesInLIst() {
        List<String> output = new ArrayList<>();
        String line;
        try {
            while ((line = br.readLine()) != null) {
                if (line.equals(END_OF_MESSAGE))
                    break;

                output.add(line);
            }
        } catch (IOException e) {
            if(!process.isAlive())
                return output;
            
                throw new Error(e);
        }

        return output;
    }

    public void close() {
        try {
            bw.close();
            br.close();
            process.destroy();
        } catch (IOException e) {
            throw new Error(e);
        }
    }
}