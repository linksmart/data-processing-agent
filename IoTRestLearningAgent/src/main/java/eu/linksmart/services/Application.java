package eu.linksmart.services;

import com.google.gson.Gson;
import eu.linksmart.services.event.ceml.CEMLRest;
import eu.linksmart.services.event.feeder.RestConnector;
import eu.linksmart.services.event.core.DataProcessingCore;
import eu.almanac.event.datafusion.utils.generic.Component;
import eu.almanac.event.datafusion.utils.generic.ComponentInfo;
import eu.linksmart.api.event.components.AnalyzerComponent;
import eu.linksmart.services.event.intern.DynamicConst;
import eu.linksmart.services.event.intern.Utils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

/**
 * Created by José Ángel Carvajal on 13.08.2015 a researcher of Fraunhofer FIT.
 */
@Configuration
@PropertySource("conf.cfg")
@Import(value = {RestConnector.class,  CEMLRest.class})
@EnableAutoConfiguration
@ConfigurationProperties
@SpringBootApplication
@RestController
@EnableSwagger2
public class Application {

    public static void main(String[] args) {

        String confFile = Const.DEFAULT_CONFIGURATION_FILE;

        if(args.length>0)
            confFile= args[0];
        System.setProperty("spring.config.name",confFile);
        Boolean hasStarted = DataProcessingCore.start(confFile);
        if(hasStarted) {
            SpringApplication springApp =  new SpringApplication(Application.class);
            try {
                springApp.setDefaultProperties(Utils.createPropertyFiles(confFile));
                
            } catch (IOException e) {
                e.printStackTrace();
            }
            springApp.run(args);


        }

    }
    @RequestMapping("/")
    public ResponseEntity<String> status() {

        Map<String, Object> map = new Hashtable<>();
        Map<String, ArrayList<ComponentInfo>> aux = new Hashtable<>();
        map.put("Distribution","DataFusionRestManager");
        AnalyzerComponent.loadedComponents.values().stream().filter(components -> !components.isEmpty()).forEach(components -> {
            ComponentInfo component = components.values().iterator().next();
            for (String implementationOf : component.getImplementationOf()) {
                if (!aux.containsKey(implementationOf))
                    aux.put(implementationOf, new ArrayList<>());
                aux.get(implementationOf).add(component);

            }
        });

        map.put("LoadedComponents",aux);
        try {
            return new ResponseEntity<>((new Gson()).toJson(map), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("IoT REST Learning Agent")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("IoT REST Learning Agent")
                .description("IoT Agent for Stream Learning and Processing")
                //.termsOfServiceUrl("http://www-03.ibm.com/software/sla/sladb.nsf/sla/bm?Open")
                .contact("José Ángel Carvajal Soto")
                .license("Apache License Version 2.0")
               // .licenseUrl("https://github.com/IBM-Bluemix/news-aggregator/blob/master/LICENSE")
                .version("2.0")
                .build();
    }
}
