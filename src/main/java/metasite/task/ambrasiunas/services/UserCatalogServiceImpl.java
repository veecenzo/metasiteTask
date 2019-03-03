package metasite.task.ambrasiunas.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class UserCatalogServiceImpl implements UserCatalogService {

    public final Logger logger = LoggerFactory.getLogger(getClass());


    public UserCatalogServiceImpl() {
    }

    @Override
    public boolean createCatalog(String catalogName) {
        File file = new File(catalogName);

        if (!file.exists()) {
            return file.mkdirs();
        }

        return false;
    }

    @Override
    public File[] readCatalog(String catalogName) {
        File fileDir = new File(catalogName);
        return fileDir.listFiles();
    }

    @Override
    public File getFileByName(String catalogName, String fileName){
        File fileDir = new File(catalogName);
        for(File file: fileDir.listFiles()){
            if(file.getName().equals(fileName)){
                return file;
            }
        }
        return null;
    }

    @Override
    public void saveFileToCatalog(byte[] fileData, String catalogName, String fileName) throws IOException {

        File file = new File(String.format("%s/%s", catalogName, fileName));
        if (!file.exists()) {
            if (file.createNewFile()) {
                logger.info("File:{}/{} created !", catalogName, fileName);
            } else {
                logger.error("Failed to create file:{}/{}!", catalogName, fileName);
            }
        }
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(fileData);
        fileOutputStream.close();

    }

    @Override
    public void cleanCatalog(String catalogName) {
        File fileDir = new File(catalogName);

        File[] files = fileDir.listFiles();
        if (files == null || files.length <= 0) {
            logger.info("No files to delete from :{}!", catalogName);
            return;
        }

        for (File file : files) {
            if (file.delete()) {
                logger.info("File:{}/{} deleted !", catalogName, file.getName());
            } else {
                logger.error("Failed to delete file:{}/{}!", catalogName, file.getName());
            }
        }

    }

    @Override
    public boolean removeCatalog(String catalogName) {
        File fileDir = new File(catalogName);

        if (fileDir.exists()) {
            return fileDir.delete();
        }

        return false;
    }

}
