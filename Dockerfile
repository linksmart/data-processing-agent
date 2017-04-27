FROM maven:3-jdk-8-alpine
MAINTAINER Jose Angel Carvajal Soto <carvajal@fit.fhg.de>

# installing git
RUN apk add --no-cache git

# cloning and building apache code
RUN git clone https://linksmart.eu/redmine/linksmart-opensource/linksmart-services/data-processing-agent.git
WORKDIR data-processing-agent
RUN mvn install

# cloning and building LGPL code
RUN git clone https://linksmart.eu/redmine/linksmart-opensource/linksmart-services/iot-data-processing-agent/gpl-artifacts.git
WORKDIR gpl-artifacts
RUN mvn install

# moving to the jar location
WORKDIR distributions/IoTAgent/target/

# enabling environmental variables configuration
ENV env_var_enabled=true

# force the REST API port to the default one
ENV server_port = 8319

# selecting the ESPER as CEP engine
ENV cep_init_engines=eu.linksmart.services.event.cep.engines.EsperEngine

# mounting configuration and extra dependencies volumes
VOLUME /config
VOLUME /dependencies

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
