package SDMEngine;

import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class CalculateSmartOrderTask extends Task<Boolean> {
    private String fileName = "";
    private final int SLEEP_TIME = 1;

    public CalculateSmartOrderTask(String fileName){
        this.fileName = fileName;
    }

    public CalculateSmartOrderTask(){

    }

    @Override
    protected Boolean call()  {
        final long[] totalLetters = {1000};
        final int[] currentLettersNumber = {0};

        updateMessage("Calculating Smart Order...");

        for( ; currentLettersNumber[0] < totalLetters[0] ; currentLettersNumber[0]++){
            updateProgress(currentLettersNumber[0], totalLetters[0]);
            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        updateMessage("Done!");


        return Boolean.TRUE;
    }

}
