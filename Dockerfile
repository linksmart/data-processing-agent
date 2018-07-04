FROM java:8-jre-alpine

LABEL project="Linksmart(R) IoT Agents"
LABEL "project.code"=LA
LABEL organization="Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e.V."
LABEL institute=FIT
LABEL department=UCC
LABEL group=UCUC
LABEL "org.code"="FhG.FIT.UCC.UCUC"
LABEL version="2.0"
LABEL distribution=standard
LABEL maintainer="Jose Angel Carvajal Soto <carvajal@fit.fhg.de>"

ARG engine
ARG extensions
ARG version

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
ADD https://nexus.linksmart.eu/repository/maven-releases/eu/linksmart/services/events/distributions/iot.learning.universal.agent/${version}/iot.learning.universal.agent-${version}.jar agent.jar


# starting the agent
ENTRYPOINT ["java", "-cp","./*:/dependencies/*", "org.springframework.boot.loader.PropertiesLauncher"]