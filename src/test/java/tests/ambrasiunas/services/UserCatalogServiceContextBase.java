package tests.ambrasiunas.services;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import tests.ambrasiunas.ContextBase;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class UserCatalogServiceContextBase extends ContextBase {

    private final String testCatalog = "testCatalog";
    private final String testFile = "testFile.txt";

    @Before
    public void init() {
        userCatalogService.cleanCatalog(testCatalog);
        userCatalogService.removeCatalog(testCatalog);
    }

    @Test
    public void testUserCatalogService() throws IOException {

        String testFileName = UUID.randomUUID().toString();

        Assert.assertTrue("Catalog already created", userCatalogService.createCatalog(testCatalog));
        Assert.assertFalse("Catalog can not be dublicated !", userCatalogService.createCatalog(testCatalog));

        userCatalogService.saveFileToCatalog(testFileContent.getBytes(), testCatalog, testFileName);

        File[] files = userCatalogService.readCatalog(testCatalog);
        Assert.assertNotNull("File not found!",files[0]);
        Assert.assertEquals("Invalid file found", files[0].getName(), testFileName);

    }

}
