package tests.ambrasiunas.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import metasite.task.ambrasiunas.controllers.MainFileController;
import metasite.task.ambrasiunas.dto.GeneratedFileDto;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.hateoas.Link;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import tests.ambrasiunas.ContextBase;

import java.util.List;

import static org.springframework.hateoas.core.DummyInvocationUtils.methodOn;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TestMainController extends ContextBase {

    private static final String IMPORT_ENDPOINT = "/import";
    private static final String PROCESS_ENDPOINT = "/process";
    private static final String INPUT_ENDPOINT = "/input";
    private static final String OUTPUT_ENDPOINT = "/output";
    private static final String RETRIEVE_ENDPOINT = "/retrieve";
    private static final String fileName = "mainControllerTestFile.txt";

    @Before
    public void cleanUp() throws Exception {
        cleanInputCatalog();
        cleanOutputCatalog();
    }

    @Test
    public void testFileImport() throws Exception {
        importFile();
    }

    @Test
    public void testFileProcess() throws Exception {
        importFile();
        processCatalogFiles();
    }

    @Test
    public void testInvalidFileProcess() throws Exception {
        cleanInputCatalog();
        mockMvc
                .perform(post(PROCESS_ENDPOINT))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testRetrieveDto() throws Exception {
        importFile();
        processCatalogFiles();
        MvcResult mockMvcResult = mockMvc.perform(get(RETRIEVE_ENDPOINT))
                .andExpect(status().isOk()).andReturn();

        List<GeneratedFileDto> generatedFileDtos = objectMapper.readValue(mockMvcResult.getResponse().getContentAsString(), new TypeReference<List<GeneratedFileDto>>() {
        });

        Assert.assertEquals(generatedFileDtos.size(), 4);
        for (GeneratedFileDto generatedFileDto : generatedFileDtos) {
            Assert.assertEquals(generatedFileDto.getLinks().get(0).getRel(), "linkToFile");
            Link link = linkTo(methodOn(MainFileController.class)
                    .retrieveOutputFile("outputCatalogDir", generatedFileDto.getFileName())).withRel("linkToFile");
            Assert.assertEquals(generatedFileDto.getLinks().get(0).getHref(), link.getHref());
        }

    }

    private MvcResult importFile() throws Exception {
        MockMultipartFile importFile = new MockMultipartFile("file", fileName, "text/plain", testFileContent.getBytes());

        return mockMvc
                .perform(MockMvcRequestBuilders.multipart(IMPORT_ENDPOINT).file(importFile))
                .andExpect(status().isCreated())
                .andReturn();
    }

    private void processCatalogFiles() throws Exception {
        mockMvc
                .perform(post(PROCESS_ENDPOINT))
                .andExpect(status().isOk());
    }

    private void cleanInputCatalog() throws Exception {
        mockMvc
                .perform(delete(INPUT_ENDPOINT))
                .andExpect(status().isOk());
    }

    private void cleanOutputCatalog() throws Exception {
        mockMvc
                .perform(delete(OUTPUT_ENDPOINT))
                .andExpect(status().isOk());
    }
}
