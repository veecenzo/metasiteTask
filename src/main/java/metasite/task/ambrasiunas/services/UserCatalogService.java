package metasite.task.ambrasiunas.services;

import java.io.File;
import java.io.IOException;

public interface UserCatalogService {

     boolean createCatalog(String catalogName);

     File[] readCatalog(String catalogName);

     File getFileByName(String catalogName, String fileName);

     void saveFileToCatalog(byte[] fileData, String catalogName, String fileName) throws IOException;;

     void cleanCatalog(String catalogName);

     boolean removeCatalog(String catalogName);
}
