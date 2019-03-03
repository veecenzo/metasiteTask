package tests.ambrasiunas.services;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import tests.ambrasiunas.ContextBase;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class FileProcessingServiceContextBase extends ContextBase {


    private final String inputCatalog = "inputTestCatalog";
    private final String outputCatalog = "outputTestCatalog";

    private final String testFile = "testFile.txt";

    @Before
    public void init() {
        if (!userCatalogService.createCatalog(inputCatalog)) {
            userCatalogService.cleanCatalog(inputCatalog);
        }
        if (!userCatalogService.createCatalog(outputCatalog)) {
            userCatalogService.cleanCatalog(outputCatalog);
        }
    }

    @Test
    public void testFileProcessingService() throws IOException {

        String testFileName = UUID.randomUUID().toString();
        String testFileContent = "Some content for the file that is being generated by this test1 and has alphabetic " +
                "words begining within a range that is required for this task. Words with z letter like zoo and v like voice or even" +
                "words like h like human and n like number and in some cases it has to be in between a and g with words like age and gun5!" +
                "also this skips special characters and such like th1s word with numbers51251 at the end.";

        int fileCount = 0;

        userCatalogService.saveFileToCatalog(testFileContent.getBytes(), inputCatalog, testFileName);
        File[] filesToProcess = userCatalogService.readCatalog(inputCatalog);

        for (File file : filesToProcess) {
            fileProcessingService.process(file, outputCatalog);
        }

        File[] filesOut = userCatalogService.readCatalog(outputCatalog);
        for (File file : filesOut) {
            fileCount++;
        }

        Assert.assertEquals("Invalid file count, should be 4 files generated!", fileCount, 4);

    }


}