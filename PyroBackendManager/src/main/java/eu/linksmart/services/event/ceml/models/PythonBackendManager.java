package eu.linksmart.services.event.ceml.models;

import eu.linksmart.api.event.exceptions.InternalException;
import eu.linksmart.services.event.core.DataProcessingCore;
import eu.linksmart.services.event.intern.Const;
import eu.linksmart.services.event.intern.SharedSettings;
import eu.linksmart.services.utils.EditableService;
import eu.linksmart.services.utils.configuration.Configurator;
import eu.linksmart.services.utils.function.Utils;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.client.ApiClient;
import io.swagger.client.api.ScApi;
import io.swagger.client.model.ServiceDocs;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import io.swagger.client.model.Service;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;


/**
 * Created by José Ángel Carvajal on 24.05.2018 a researcher of Fraunhofer FIT.
 */
@Configuration
@EnableAutoConfiguration
@ConfigurationProperties
@SpringBootApplication
@RestController
@EnableSwagger2
public class PythonBackendManager {
    protected static int requestCount=0;
    private transient final static Logger loggerService = LogManager.getLogger(PythonBackendManager.class);
    private transient final static Configurator conf = Configurator.getDefaultConfig();

    protected final static String pyroAdapterFilename = "pyroAdapter.py";
    public static void main(String[] args) {


        PythonBackendManager.init();
        while (true){

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }



    }
    public static void init() {
        Configurator.getDefaultConfig().enableEnvironmentalVariables();
        SpringApplication springApp;

        springApp = new SpringApplication(PythonBackendManager.class);

        springApp.addInitializers();
        springApp.run();

        String scURL= conf.getString(eu.linksmart.services.utils.constants.Const.LINKSMART_SERVICE_CATALOG_ENDPOINT);
        if(scURL!=null) {
            ApiClient apiClient = new ApiClient();
            apiClient.setBasePath(scURL);
            ScApi client = new ScApi(apiClient);
            Service service = registrationInfo();
            client.idPut(service.getId(),service);
        }

    }
    private static Service registrationInfo(){
        EditableService service = new EditableService();
        service.setName("_linksmart-agent-python-manager.tcp_");
        service.setMeta(new HashMap<>());
        Map<String,String> map = new HashMap<>();
        String host="localhost";
        try {
            host=InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        map.put("HTTP", "http://"+host+"8666/");
        service.setApis(map);
        ServiceDocs doc = new ServiceDocs(),doc2 = new ServiceDocs();
        doc.setDescription("Open API V2");
        doc.setUrl("http://"+host+"8666"+"/v2/api-docs?group=LinkSmart(R)%20Agent%20Python%20Backend%20Manager");
        doc.setType("application/json");
        doc.setApis(Collections.singletonList("HTTP"));

        doc2.setDescription("Open API V2");
        doc2.setUrl("http://"+host+"8666"+"/swagger-ui.html");
        doc2.setType("html/text");
        doc2.setApis(Collections.singletonList("HTTP"));

        service.setDocs(Arrays.asList(doc,doc2));
        return service;
    }
    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer() {
        return (container -> {
            container.setPort(8666);
        });
    }
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK")
    })
    @RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> status() {

        try {
            return new ResponseEntity<>((SharedSettings.getSerializer()).toString(new Hashtable<>()), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = PyroBackedResponse.class),
            @ApiResponse(code = 500, message = "General Error:", response = PyroBackedResponse.class)
    })
    @RequestMapping(value = "/pyro/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PyroBackedResponse> run(
            @RequestBody PyroBackendRequest request
    ) {

       PyroBackedResponse response = new PyroBackedResponse();
        try {
            requestCount++;
            response.uri=constructProxy(copyPyScripts(pyroAdapterFilename), request.name, request.path, request.registerName,request.nameServer,request.host,request.port);
            if(response.uri==null){
                response.statusMessage="Unexpected Error in python see logs";
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception e){
            response.uri=null;
            response.statusMessage=e.getMessage();
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
    private File copyPyScripts(String pyScriptFilename) throws IOException {
        File file = new File(System.getProperty("java.io.tmpdir")+File.separator+pyScriptFilename);
        file.deleteOnExit();

        FileUtils.copyURLToFile(PythonPyroBase.class.getClassLoader().getResource(pyScriptFilename), file);

        return file;
    }
    private String constructProxy(File file , String moduleName, String path, String registerName, boolean nameServer, String host, int port) throws IOException, InternalException {

        loggerService.info("Saved script to {}", file.getAbsolutePath());
        String pythonPath = conf.getString(Const.PYTHON_PATH);
        pythonPath = pythonPath==null ? "" : pythonPath;
        // Path to script is passed as parameter
        String[] cmd = {pythonPath + "python", "-u", file.getAbsolutePath(),
                "--bname=" + moduleName,
                "--bpath=" + path,
                "--rname=" + registerName,
                "--host=" + host,
                "--port=" + port,
                nameServer?"--ns":""
        };

        return Utils.runGetLastOutput(cmd, moduleName,loggerService);
    }
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("LinkSmart(R) Agent Python Backend Manager")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("LinkSmart(R) Agent Python Backend Manager")
                .description("The service allows the agent to start Python backend models")
                .version("1.0.0")
                .license("Apache License version 2.0")
                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0")
                .build();
    }


}
