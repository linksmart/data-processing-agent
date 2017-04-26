FROM maven:3-jdk-8
MAINTAINER Jose Angel Carvajal Soto <carvajal@fit.fhg.de>


COPY . /usr/src/app/
WORKDIR /usr/src/app

RUN git clone https://linksmart.eu/redmine/linksmart-opensource/linksmart-services/data-processing-agent.git
WORKDIR data-processing-agent
RUN mvn install

RUN git clone https://linksmart.eu/redmine/linksmart-opensource/linksmart-services/data-processing-agent.git
WORKDIR gpl-artifacts
RUN mvn install

WORKDIR distributions/IoTAgent/target/

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
