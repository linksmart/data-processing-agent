FROM java:openjdk-8-jre
MAINTAINER Jose Angel Carvajal Soto <carvajal@fit.fhg.de>


WORKDIR /usr/src/app
ADD distributions/IoTAgent/target/*.jar agent.jar

ENV env_var_enabled=true
ENV cep_init_engines=eu.linksmart.services.event.cep.engines.EsperEngine


VOLUME /config
VOLUME /dependencies
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
