package SDMEngine;

import javafx.concurrent.Task;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;


public class CalculateOpenFileTask extends Task<Boolean> {


    private String fileName = "";
    private final int SLEEP_TIME = 1;

    public CalculateOpenFileTask(String fileName){
        this.fileName = fileName;
    }

    public CalculateOpenFileTask(){

    }

    @Override
    protected Boolean call()  {
        final long[] totalLetters = {0};
        final int[] currentLettersNumber = {0};

        try {

            updateMessage("Fetching file...");
            BufferedReader reader = Files.newBufferedReader(
                    Paths.get(fileName),
                    StandardCharsets.UTF_8);

            reader
                    .lines()
                    .forEach(aLine -> {
                        String[] words = aLine.split(" ");
                        Arrays.stream(words)
                                .forEach(word ->{
                                    totalLetters[0] += word.length();
                                });
                    });


            reader = Files.newBufferedReader(
                    Paths.get(fileName),
                    StandardCharsets.UTF_8);

            updateMessage("Start downloading...");

            reader
                    .lines()
                .forEach(aLine -> {
                String[] words = aLine.split(" ");
                Arrays.stream(words)
                        .forEach(word ->{
                            for(int i =0 ; i< word.length();i++){

                                currentLettersNumber[0]++;
                                updateProgress(currentLettersNumber[0], totalLetters[0]);
                               if(currentLettersNumber[0]>totalLetters[0]/4){
                                    updateMessage("Still have some time...");
                                }
                                if(currentLettersNumber[0]>totalLetters[0]/2){
                                    updateMessage("Pass more than a half!");
                                }
                                if(currentLettersNumber[0]>(totalLetters[0]-(totalLetters[0]/4))){
                                    updateMessage("Almost there!");
                                }
                                try {
                                    if (currentLettersNumber[0] % 2 == 0) {
                                        Thread.sleep(SLEEP_TIME);
                                    }
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    });

            updateMessage("Done!");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Boolean.TRUE;
    }

}

