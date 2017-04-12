FROM java:openjdk-8-jre
MAINTAINER Jose Angel Carvajal Soto <carvajal@fit.fhg.de>


WORKDIR /usr/src/app
ADD distributions/IoTAgent/target/*.jar agent.jar

ENV env_var_enabled=true
ENV agent_init_extensions=eu.linksmart.services.event.ceml.core.CEML
ENV cep_init_engines=eu.linksmart.services.event.cep.engines.EsperEngine

RUN ln -s /config/conf.cfg ./conf.cfg

VOLUME /config
ENTRYPOINT ["java", "-cp","./*", "org.springframework.boot.loader.PropertiesLauncher"]
EXPOSE 8319
#  docker run -v <<//D/workspaces/IoTAgents/gpl-artifacts/distributions/rest/IoTDataProcessingAgent/docker-conf/>>:/config --add-host=broker:<<ip>> <<la-rest>>