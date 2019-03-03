package metasite.task.ambrasiunas.controllers;

import metasite.task.ambrasiunas.dto.GeneratedFileDto;
import metasite.task.ambrasiunas.services.FileProcessingService;
import metasite.task.ambrasiunas.services.UserCatalogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.core.DummyInvocationUtils.methodOn;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@RestController
public class MainFileController extends BaseController {

    public final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    UserCatalogService userCatalogService;

    @Autowired
    FileProcessingService fileProcessingService;

    @RequestMapping(method = RequestMethod.POST, path = "/import")
    public ResponseEntity<String> importFilesToCatalog(
            @RequestParam("file") MultipartFile file,
            @ModelAttribute("inputCatalogDir") String inputCatalog) throws IOException {

        logger.info("Importing file {}",file.getOriginalFilename());

        userCatalogService.createCatalog(inputCatalog);
        userCatalogService.saveFileToCatalog(file.getBytes(), inputCatalog, file.getOriginalFilename());

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/process")
    public ResponseEntity<String> processCatalogFiles(
            @ModelAttribute("inputCatalogDir") String inputCatalog,
            @ModelAttribute("outputCatalogDir") String outputCatalog) throws IOException {


        File[] files = userCatalogService.readCatalog(inputCatalog);
        if(files == null || files.length <= 0){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        userCatalogService.createCatalog(outputCatalog);

        for (File file : files) {
            fileProcessingService.process(file, outputCatalog);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/retrieve", method = RequestMethod.GET)
    public ResponseEntity<List<GeneratedFileDto>> retrieveFilesFromCatalog(@ModelAttribute("outputCatalogDir") String outputCatalog) {

        List<GeneratedFileDto> generatedFiles = new ArrayList<>();

        File[] files = userCatalogService.readCatalog(outputCatalog);
        if(files.length <= 0){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        for (File file : files) {
            generatedFiles.add(entityToDto(file.getName()));
        }

        return new ResponseEntity<>(generatedFiles, HttpStatus.OK);
    }

    @RequestMapping(value = "/retrieve/{fileName}", method = RequestMethod.GET)
    public ResponseEntity<FileSystemResource> retrieveOutputFile(@ModelAttribute("outputCatalogDir") String outputCatalog,
                                                                       @PathVariable("fileName") String fileName) {

        File processedFile = userCatalogService.getFileByName(outputCatalog, fileName);
        if(processedFile == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new FileSystemResource(processedFile), HttpStatus.OK);
    }

    @RequestMapping(value = "/input", method = RequestMethod.DELETE)
    public ResponseEntity<Void> cleanInputCatalog(@ModelAttribute("inputCatalogDir") String inputCatalog) {
        userCatalogService.cleanCatalog(inputCatalog);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/output", method = RequestMethod.DELETE)
    public ResponseEntity<Void> cleanOutputCatalog(@ModelAttribute("outputCatalogDir") String outputCatalog) {
        userCatalogService.cleanCatalog(outputCatalog);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    private GeneratedFileDto entityToDto(String fileName){

        GeneratedFileDto generatedFileDto = new GeneratedFileDto();
        generatedFileDto.setFileName(fileName);

        generatedFileDto.add(linkTo(methodOn(MainFileController.class)
                .retrieveOutputFile(outputCatalogDir, fileName)).withRel("linkToFile"));

        return generatedFileDto;
    }
}
