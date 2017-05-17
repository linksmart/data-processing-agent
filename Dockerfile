FROM maven:3-jdk-8-alpine
MAINTAINER Jose Angel Carvajal Soto <carvajal@fit.fhg.de>

# installing git
RUN apk add --no-cache wget

RUN wget -o agent.jar https://linksmart.eu/repo/service/local/artifact/maven/redirect?r=public&g=eu.linksmart.services.events.gpl.distributions&a=iot.learning.universal.agent&v=LATEST

# enabling environmental variables configuration
ENV env_var_enabled=true

# force the REST API port to the default one
ENV server_port=8319

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
