FROM java:openjdk-8-jre
MAINTAINER Jose Angel Carvajal Soto <carvajal@fit.fhg.de>


WORKDIR /usr/src/app
ADD distributions/IoTAgent/target/*.jar agent.jar

ENV env_var_enabled=true
ENV agent_init_extensions=eu.linksmart.services.event.ceml.core.CEML

RUN ln -s /config/conf.cfg ./conf.cfg

VOLUME /config
ENTRYPOINT ["java", "-cp","./*", "org.springframework.boot.loader.PropertiesLauncher"]
EXPOSE 8319
# NOTES:
#	RUN:
#  		docker run <<image-name>>
# OPTIONS:
# 		Define configuration file:
#			-v <<absolute/path/folder/where/config/file/is>>:/config
# 		Disable/enable REST API:
#			-e api_rest_enabled=<false/true>
#		Define default broker
#			-e connection_broker_mqtt_hostname=<hostname>
#		Expose REST:
#			-p "8319:8319"
#