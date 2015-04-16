FROM lsgc/distribution:latest
MAINTAINER Karl Catewicz "catewicz@fit.fraunhofer.de"
# Grabs the weatherservice servlet and puts it into deploy folder of karaf 
ADD https://linksmart.eu/repo/content/repositories/public/eu/linksmart/gc/examples/eu.linksmart.gc.examples.weather.service/0.0.1-SNAPSHOT/eu.linksmart.gc.examples.weather.service-0.0.1-20150319.103958-17.jar ${LSGC_HOME}/deploy/
ADD jetty.xml ${LSGC_HOME}/etc/
EXPOSE 8882
# info (1) jetty port is modified from 8082 to 8882
# info (2) the servlet version is hardcoded. Do not use the image if you attend to test the servlet itself

