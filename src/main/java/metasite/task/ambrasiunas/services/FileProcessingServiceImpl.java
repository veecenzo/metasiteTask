package metasite.task.ambrasiunas.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class FileProcessingServiceImpl implements FileProcessingService  {

    public final Logger logger = LoggerFactory.getLogger(getClass());

    public FileProcessingServiceImpl() {
    }
    @Override
    public void process(File file, String outputDir) {

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        WordProcessor wordProcessor;
        try {
            logger.info("Porcessing file:{}", file.getName());
            wordProcessor = new WordProcessor(new FileInputStream(file), outputDir, file.getName());
            executorService.execute(wordProcessor);
        } catch (FileNotFoundException e) {
            logger.error("File not found: {}", e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException ex) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }

    }

}
