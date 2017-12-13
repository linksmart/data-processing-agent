FROM java:8-jre-alpine

LABEL project="Linksmart(R) IoT Agents"
LABEL "project.code"=LA
LABEL organization="Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e.V."
LABEL institute=FIT
LABEL department=UCC
LABEL group=UCUC
LABEL "org.code"="FhG.FIT.UCC.UCUC"
LABEL version="2.0"
LABEL distribution=standard
LABEL maintainer="Jose Angel Carvajal Soto <carvajal@fit.fhg.de>"


ADD target/*.jar ./

# starting the agent
ENTRYPOINT ["java", "-cp","./*", "org.springframework.boot.loader.PropertiesLauncher"]