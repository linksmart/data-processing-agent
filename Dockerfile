FROM maven:3-jdk-8-alpine
MAINTAINER Jose Angel Carvajal Soto <carvajal@fit.fhg.de>

WORKDIR /home

# installing git
#RUN apk add --no-cache wget

#ADD https://linksmart.eu/repo/service/local/artifact/maven/redirect?r=public&g=eu.linksmart.services.events.gpl.distributions&a=iot.learning.universal.agent&v=LATEST/ /home/
#RUN wget -o agent.jar https://linksmart.eu/repo/service/local/artifact/maven/redirect?r=public&g=eu.linksmart.services.events.gpl.distributions&a=iot.learning.universal.agent&v=LATEST

# installing git
RUN apk add --no-cache git


# cloning and building apache code
RUN git clone https://linksmart.eu/redmine/linksmart-opensource/linksmart-services/data-processing-agent.git
WORKDIR data-processing-agent
RUN mvn install

RUN git clone https://linksmart.eu/redmine/linksmart-opensource/linksmart-services/iot-data-processing-agent/gpl-artifacts.git
WORKDIR gpl-artifacts
RUN mvn install

# enabling environmental variables configuration
ENV env_var_enabled=true

# force the REST API port to the default one
ENV server_port=8319

# selecting the ESPER as CEP engine
ENV cep_init_engines=eu.linksmart.services.event.cep.engines.EsperEngine

# to start the LA (optional)
ENV agent_init_extensions=eu.linksmart.services.event.ceml.core.CEML,eu.linksmart.services.event.ceml.CEMLRest

# mounting configuration and extra dependencies volumes
VOLUME /config
VOLUME /dependencies

WORKDIR distributions/IoTAgent/target/

# starting the agent
ENTRYPOINT ["java", "-cp","./*:/dependencies/*", "org.springframework.boot.loader.PropertiesLauncher"]

EXPOSE 8319
# NOTES:
#	RUN:
#  		docker run [options] <<image-name>> [command]
#   OPTIONS:
# 		Define volume for configuration file:
#			-v <</path/on/host/machine/conf>>:/config
# 		Define volume for configuration file:
#			-v <</path/on/host/machine/dep>>:/dependencies
# 		Disable/enable REST API:
#			-e api_rest_enabled=<false/true>
#		Define default broker
#			-e connection_broker_mqtt_hostname=<hostname>
#		Expose REST:
#			-p "8319:8319"
#   COMMAND:
#       Custom configuration file (volume should be defined):
#           /config/config.cfg
#
