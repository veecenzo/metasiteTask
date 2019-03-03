package metasite.task.ambrasiunas.controllers;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class BaseController {

    public final static String inputCatalogDir = "inputFileDir";
    public final static String outputCatalogDir = "outputFileDir";

    @ModelAttribute("inputCatalogDir")
    public String getInputDir(HttpServletRequest httpServletRequest) {
        return getInputDirCatalog(getRemoteAdrdrCatalog(httpServletRequest));
    }

    @ModelAttribute("outputCatalogDir")
    public String getOutputDir(HttpServletRequest httpServletRequest) {
      return getOutputDirCatalog(getRemoteAdrdrCatalog(httpServletRequest));
    }

    private String getRemoteAdrdrCatalog(HttpServletRequest httpServletRequest){
        String remoteAdress = httpServletRequest.getRemoteAddr();
        if (remoteAdress == null) {
            return httpServletRequest.getHeader("X-Forwarded-For");
        } else {
            if(remoteAdress.startsWith("0:0") || remoteAdress.startsWith("127.0")){
                remoteAdress = "localhost";
            }
            return remoteAdress;
        }
    }

    private String getInputDirCatalog(String adress){
        return String.format("%s/%s", inputCatalogDir, adress);
    }

    private String getOutputDirCatalog(String adress){
        return String.format("%s/%s", outputCatalogDir, adress);
    }
}
