FROM java:8-jre-alpine
MAINTAINER Jose Angel Carvajal Soto <carvajal@fit.fhg.de>

ARG engine
ARG extensions

# enabling environmental variables configuration
ENV env_var_enabled=true

# force the REST API port to the default one
ENV server_port=8319

# selecting the ESPER as CEP engine
ENV cep_init_engines=${engine}

# to start the LA (optional)
ENV agent_init_extensions=${extensions}


# mounting configuration and extra dependencies volumes
VOLUME /config
VOLUME /dependencies


# mounting configuration and extra dependencies volumes
ADD distributions/IoTAgent/target/*.jar .
ONBUILD ADD distributions/IoTAgent/target/*.jar .


# starting the agent
ENTRYPOINT ["java", "-cp","./*:/dependencies/*", "org.springframework.boot.loader.PropertiesLauncher"]
