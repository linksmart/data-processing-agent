package eu.linksmart.services;

import com.google.gson.Gson;
import eu.almanac.event.datafusion.core.DataFusionManagerCore;
import de.fraunhofer.fit.event.feeder.*;
import eu.almanac.event.datafusion.utils.generic.Component;
import eu.almanac.event.datafusion.utils.generic.ComponentInfo;
import eu.linksmart.api.event.datafusion.AnalyzerComponent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by José Ángel Carvajal on 13.08.2015 a researcher of Fraunhofer FIT.
 */
@Configuration
@Import(value = {  RestStatementFeeder.class,RestEventFeeder.class })
@EnableAutoConfiguration
@SpringBootApplication
@RestController
public class Application {
    public static void main(String[] args) {


        Boolean hasStarted = DataFusionManagerCore.start(args);
        if(hasStarted)
            SpringApplication.run(Application.class, args);

    }
    @RequestMapping("/")
    public ResponseEntity<String> status() {

        Map<String, Object> map = new Hashtable<>();
        Map<String, ArrayList<ComponentInfo>> aux = new Hashtable<>();
        map.put("Distribution","DataFusionRestManager");
        for(Map<Component,ComponentInfo> components: AnalyzerComponent.loadedComponents.values()){
            if(!components.isEmpty()) {
                ComponentInfo component = components.values().iterator().next();
                for (String implementationOf : component.getImplementationOf()) {
                    if (!aux.containsKey(implementationOf))
                        aux.put(implementationOf, new ArrayList<ComponentInfo>());
                    ((ArrayList<ComponentInfo>) aux.get(implementationOf)).add((ComponentInfo) component);

                }
            }

        }

        map.put("LoadedComponents",aux);
        try {
            return new ResponseEntity<String>((new Gson()).toJson(map), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }
}
