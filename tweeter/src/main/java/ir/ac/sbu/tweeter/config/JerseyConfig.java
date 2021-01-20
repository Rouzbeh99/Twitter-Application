package ir.ac.sbu.tweeter.config;

import lombok.extern.slf4j.Slf4j;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

import javax.ws.rs.ApplicationPath;

@Slf4j
@Component
@ApplicationPath("tweeter")
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        log.info("Configuring services");
        packages("ir.ac.sbu.tweeter.service");
    }
}
