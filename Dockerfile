FROM java:8-jdk

RUN mkdir -p /usr/src/myapp /config
WORKDIR /usr/src/myapp

RUN wget -q -O "LeanringAgent.jar" "https://linksmart.eu/repo/service/local/artifact/maven/redirect?r=releases&g=eu.linksmart.services.events.gpl.distributions.rest&a=iot.learning.rest.agent&v=LATEST"

WORKDIR /config


RUN wget -q -O "conf.cfg" "https://linksmart.eu/repo/service/local/artifact/maven/redirect?r=releases&g=eu.linksmart.services.events.gpl.distributions.rest&a=iot.learning.rest.agent&v=LATEST&c=configuration&e=cfg"

WORKDIR /usr/src/myapp

RUN ln -s /config/conf.cfg ./conf.cfg

VOLUME /config
EXPOSE 8319

CMD ["java", "-jar", "LeanringAgent.jar"]

