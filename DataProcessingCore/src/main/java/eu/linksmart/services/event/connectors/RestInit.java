package eu.linksmart.services.event.connectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.almanac.event.datafusion.utils.generic.ComponentInfo;
import eu.linksmart.api.event.components.AnalyzerComponent;
import eu.linksmart.api.event.components.IncomingConnector;
import eu.linksmart.services.event.intern.Const;
import eu.linksmart.services.event.intern.SharedSettings;
import eu.linksmart.services.event.intern.AgentUtils;
import eu.linksmart.services.utils.configuration.Configurator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
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
@PropertySource("__def__agent__conf__.cfg")
@EnableAutoConfiguration
@ConfigurationProperties
@SpringBootApplication
@RestController
@EnableSwagger2
public class RestInit implements IncomingConnector {
    static Properties info = null;
    protected transient static Logger loggerService = LogManager.getLogger(RestInit.class);
    private static Configurator conf = Configurator.getDefaultConfig();
    private static boolean la = false;
    private static boolean gpl = false;

    public static void init(Configurator conf) {
        RestInit.conf = conf;


        try {
            info = AgentUtils.createPropertyFiles("service.info");
        } catch (IOException e) {
            LogManager.getLogger(RestInit.class).error(e.getMessage(), e);
        }
        if (conf.containsKeyAnywhere(Const.ADDITIONAL_CLASS_TO_BOOTSTRAPPING))
            Arrays.asList(conf.getStringArray(Const.ADDITIONAL_CLASS_TO_BOOTSTRAPPING)).forEach(s -> {
                        if (s.toLowerCase().contains("ceml"))
                            la = true;
                    }
            );
        if (conf.containsKeyAnywhere(Const.ADDITIONAL_CLASS_TO_BOOTSTRAPPING))
            Arrays.asList(conf.getStringArray(Const.ADDITIONAL_CLASS_TO_BOOTSTRAPPING)).forEach(s -> {
                        if (s.toLowerCase().contains("esper"))
                            gpl = true;
                    }
            );

        Class[] clas = null;
        if (conf.containsKeyAnywhere(Const.REST_API_EXTENSION)) {
            String[] modules = conf.getStringArray(Const.REST_API_EXTENSION);
            clas = new Class[modules.length + 1];

            for (int i = 0; i < modules.length; i++) {
                try {
                    if (!"".equals(modules[i])) {
                        Class c = Class.forName(modules[i]);
                        loggerService.info("Extension: " + c.getSimpleName() + " loaded");
                        clas[i] = c;
                        i++;
                    }
                } catch (ClassNotFoundException e) {
                    loggerService.error(e.getMessage(), e);
                }
            }


        }
        SpringApplication springApp;
        if (clas != null) {
            clas[clas.length - 1] = RestInit.class;
            springApp = new SpringApplication(clas);
        } else
            springApp = new SpringApplication(RestInit.class);

        springApp.setDefaultProperties(toProperties(conf));

        springApp.addInitializers();
        springApp.run();


    }
    static {
        if (conf.containsKeyAnywhere(Const.ENABLE_REST_API)&&  conf.getBoolean(Const.ENABLE_REST_API))
            RestInit.init(conf);
    }
    @RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> status() {

        Map<String, Object> map = new Hashtable<>();
        Map<String, ArrayList<ComponentInfo>> aux = new Hashtable<>();
        map.put("Distribution", info.getProperty((la)?"linksmart.service.info.distribution.name.la":"linksmart.service.info.distribution.name.dpa"));
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
            return new ResponseEntity<>((SharedSettings.getSerializer()).toString(map), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName(info.getProperty((la)?"linksmart.service.info.distribution.name.la":"linksmart.service.info.distribution.name.dpa"))
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(info.getProperty((la)?"linksmart.service.info.distribution.name.la":"linksmart.service.info.distribution.name.dpa"))
                .description(info.getProperty("linksmart.service.info.distribution.description"))
                .version(AgentUtils.getVersion())
                        //.termsOfServiceUrl("http://www-03.ibm.com/software/sla/sladb.nsf/sla/bm?Open")
                .contact(new Contact(info.getProperty((la)?"linksmart.service.info.distribution.name.la":"linksmart.service.info.distribution.name.dpa"),info.getProperty("linksmart.service.info.distribution.contact.url"),info.getProperty("linksmart.service.info.distribution.contact.email")))
                .license(info.getProperty((!gpl)?"linksmart.service.info.distribution.license":"linksmart.service.info.distribution.license.gpl"))
                .licenseUrl(info.getProperty((!gpl)?"linksmart.service.info.distribution.url":"linksmart.service.info.distribution.url.gpl"))
                .build();
    }
    static private Properties toProperties(Configurator configurator){
        Properties properties = new Properties();
        if(configurator.containsKeyAnywhere(Const.SPRING_MANAGED_FEATURES)) {
            String [] keys = configurator.getStringArray(Const.SPRING_MANAGED_FEATURES);
            for (String key :keys) {
                if (configurator.containsKeyAnywhere(key))
                    properties.put(key.replace("_","."), configurator.getString(key));
            }
        }
        return properties;
    }
    @Bean
    public ObjectMapper objectMapper() {
        return (ObjectMapper) SharedSettings.getSerializerDeserializer().getParser();
    }

    @Override
    public boolean isUp() {
        return true;
    }
}
