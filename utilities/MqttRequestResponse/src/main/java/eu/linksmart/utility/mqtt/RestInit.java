package eu.linksmart.utility.mqtt;

import eu.almanac.event.datafusion.utils.generic.ComponentInfo;
import eu.linksmart.services.utils.configuration.Configurator;
import eu.linksmart.services.utils.function.Utils;
import eu.linksmart.services.utils.serialization.DefaultDeserializer;
import eu.linksmart.services.utils.serialization.DefaultSerializer;
import eu.linksmart.services.utils.serialization.Deserializer;
import eu.linksmart.services.utils.serialization.Serializer;
import org.slf4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.io.IOException;
import java.util.*;

/**
 * Created by José Ángel Carvajal on 10.04.2017 a researcher of Fraunhofer FIT.
 */
@Configuration
@PropertySource("__def__conf__.cfg")
@EnableAutoConfiguration
@ConfigurationProperties
@SpringBootApplication
@RestController
@EnableSwagger2
public class RestInit {
    static Properties info = null;
    protected transient static Logger loggerService = Utils.initLoggingConf(RestInit.class);
    private static Configurator conf = Configurator.getDefaultConfig();
    private static boolean la = false;
    static final String SPRING_MANAGED_FEATURES = "spring_managed_configuration_features";
    private static boolean gpl = false;

    private Serializer serializer = new DefaultSerializer();
    private Deserializer deserializer = new DefaultDeserializer();

    public static void init() {

        try {
            Class.forName(Rest2Mqtt.class.getCanonicalName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            info = Utils.createPropertyFiles("service.info");
        } catch (IOException e) {
            Utils.initLoggingConf(RestInit.class).error(e.getMessage(), e);
        }



        SpringApplication springApp;

        springApp = new SpringApplication(RestInit.class);

        springApp.setDefaultProperties(toProperties(conf));

        springApp.addInitializers();
        springApp.run();


    }
    @RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> status() {

        Map<String, Object> map = new Hashtable<>();
        Map<String, ArrayList<ComponentInfo>> aux = new Hashtable<>();
        map.put("Distribution", info.getProperty("linksmart.service.info.distribution.name"));


        map.put("LoadedComponents",aux);
        try {
            return new ResponseEntity<>(serializer.toString(map), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName(info.getProperty("linksmart.service.info.distribution.name"))
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(info.getProperty("linksmart.service.info.distribution.name"))
                .description(info.getProperty("linksmart.service.info.distribution.description"))
                .version(Utils.getVersion())
                        //.termsOfServiceUrl("http://www-03.ibm.com/software/sla/sladb.nsf/sla/bm?Open")
                .contact(new Contact(info.getProperty("linksmart.service.info.distribution.name"),info.getProperty("linksmart.service.info.distribution.contact.url"),info.getProperty("linksmart.service.info.distribution.contact.email")))
                .license("linksmart.service.info.distribution.license")
                .licenseUrl("linksmart.service.info.distribution.url")
                .build();
    }
    static private Properties toProperties(Configurator configurator){
        Properties properties = new Properties();
        if(configurator.containsKeyAnywhere(SPRING_MANAGED_FEATURES)) {
            String [] keys = configurator.getStringArray(SPRING_MANAGED_FEATURES);
            for (String key :keys) {
                if (configurator.containsKeyAnywhere(key))
                    properties.put(key, configurator.getString(key));
            }
        }
        return properties;
    }

}
