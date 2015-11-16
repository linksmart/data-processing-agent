package eu.almanac;

import de.fraunhofer.fit.event.ceml.CEMLRest;
import eu.almanac.event.datafusion.core.DataFusionManagerCore;
import de.fraunhofer.fit.event.feeder.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by José Ángel Carvajal on 13.08.2015 a researcher of Fraunhofer FIT.
 */
@Configuration
@Import(value = { CEMLRest.class, RestStatementFeeder.class,RestEventFeeder.class })
@EnableAutoConfiguration
@SpringBootApplication
public class Application {
    public static void main(String[] args) {


        Boolean hasStarted = DataFusionManagerCore.start(args);

        if(hasStarted)
            SpringApplication.run(Application.class, args);

    }
}
