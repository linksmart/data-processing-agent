package eu.almanac;

import eu.almanac.event.datafusion.core.DataFusionManagerCore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by José Ángel Carvajal on 13.08.2015 a researcher of Fraunhofer FIT.
 */
@Configuration
@ComponentScan("eu.almanac.event.datafusion.feeder")
@EnableAutoConfiguration
@SpringBootApplication
public class Application {
    public static void main(String[] args) {


        Boolean hasStarted = DataFusionManagerCore.start(args);

        if(hasStarted)
            SpringApplication.run(Application.class, args);

    }
}
