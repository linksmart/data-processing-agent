FROM java:openjdk-8-jre
MAINTAINER Can Güney Aksakalli <can.gueney.aksakalli@fit.fraunhofer.de>


WORKDIR /usr/src/app
ADD target/*-jar-with-dependencies.jar agent.jar

RUN ln -s /config/conf.cfg ./conf.cfg

VOLUME /config
ENTRYPOINT ["java", "-cp","./*", "eu.linksmart.services.Application"]
