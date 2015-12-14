package eu.almanac;

import com.google.gson.Gson;
import de.fraunhofer.fit.event.ceml.CEMLRest;
import eu.almanac.event.datafusion.core.DataFusionManagerCore;
import de.fraunhofer.fit.event.feeder.*;
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

/**
 * Created by José Ángel Carvajal on 13.08.2015 a researcher of Fraunhofer FIT.
 */
@Configuration
@Import(value = { CEMLRest.class, RestStatementFeeder.class,RestEventFeeder.class })
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

        return new ResponseEntity<String>((new Gson()).toJson(AnalyzerComponent.loadedComponents), HttpStatus.OK);
    }
}
