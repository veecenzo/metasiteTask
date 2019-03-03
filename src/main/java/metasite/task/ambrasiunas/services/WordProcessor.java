package metasite.task.ambrasiunas.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class WordProcessor implements Runnable {

    public final Logger logger = LoggerFactory.getLogger(getClass());

    private final FileInputStream fileInputStream;
    private final String fileName;
    private final String fileCatalog;

    public WordProcessor(FileInputStream fileInputStream, String catalogName, String fileName) {
        this.fileCatalog = catalogName;
        this.fileName = fileName;
        this.fileInputStream = fileInputStream;
    }

    @Override
    public void run() {
        try (Scanner wordScanner = new Scanner(this.fileInputStream);) {
            wordScanner.useDelimiter(" ");
            String word;
            Map<String, Map> patternToWordMap = new ConcurrentHashMap<>();

            while (wordScanner.hasNext()) {
                word = trimWord(wordScanner.next());
                parseInput(patternToWordMap, word);
            }
            writeOutput(patternToWordMap);

        } catch (IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

    private synchronized void parseInput(Map<String, Map> mapInput, String word) {
        List<String> patternList = new ArrayList<>();

        patternList.add("^[a-g].*$");
        patternList.add("^[h-n].*$");
        patternList.add("^[o-u].*$");
        patternList.add("^[v-z].*$");

        Map<String, AtomicInteger> wordMap = new ConcurrentHashMap<String, AtomicInteger>() {
        };

        for (String pattern : patternList) {
            if (word.matches(pattern)) {
                if (mapInput.containsKey(pattern)) {
                    wordMap = mapInput.get(pattern);
                    mapInput.put(pattern, addWord(wordMap, word));
                } else {
                    mapInput.put(pattern, addWord(wordMap, word));
                }
            }
        }
    }


    private synchronized void writeOutput(Map<String, Map> mapOutput) throws IOException {
        FileOutputStream outputStream;

        for (Map.Entry<String, Map> entry : mapOutput.entrySet()) {
            String patternKey = entry.getKey();
            Map<String, AtomicInteger> wordMap = entry.getValue();

            StringBuilder stringBuilder = new StringBuilder();

            for (Map.Entry<String, AtomicInteger> mapEntry : wordMap.entrySet()) {
                stringBuilder.append(mapEntry.getKey()).append(" - ").append(mapEntry.getValue()).append("; \n");
            }
            String fileNameToWrite = String.format("%s/%s_%s.txt", fileCatalog, formatFileName(fileName), trimWord(patternKey));
            logger.info("Writing file:{}", fileNameToWrite);
            outputStream = new FileOutputStream(fileNameToWrite);
            outputStream.write(stringBuilder.toString().getBytes());
            outputStream.flush();
            outputStream.close();
        }
    }

    private synchronized Map<String, AtomicInteger> addWord(Map<String, AtomicInteger> map, String word) {
        if (map.containsKey(word)) {
            int incrementAndGet = map.get(word).incrementAndGet();
            map.put(word, new AtomicInteger(incrementAndGet));
        } else {
            map.put(word, new AtomicInteger(1));
        }
        return map;
    }

    private synchronized String trimWord(String word) {
        return word.toLowerCase().replaceAll("\\P{L}", "");
    }

    private synchronized String formatFileName(String name) {
        if(name.indexOf(".")>=0){
            return trimWord(name.substring(0, name.indexOf(".")));
        }else{
            return name;
        }
    }

}
