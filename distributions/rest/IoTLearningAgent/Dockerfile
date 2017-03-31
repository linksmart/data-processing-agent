FROM java:openjdk-8-jre
MAINTAINER Can Güney Aksakalli <can.gueney.aksakalli@fit.fraunhofer.de>


WORKDIR /usr/src/app
ADD target/*.jar agent.jar

RUN ln -s /config/conf.cfg ./conf.cfg

VOLUME /config
ENTRYPOINT ["java", "-cp","./dependencies/*:./*", "org.springframework.boot.loader.PropertiesLauncher"]

#  docker run -v <<//D/workspaces/IoTAgents/gpl-artifacts/distributions/rest/IoTLearningAgent/docker-conf/>>:/config --add-host=broker:<<ip>> la-rest