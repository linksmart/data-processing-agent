IoT Learning Agent
======================
[![GitHub tag (latest release)](https://img.shields.io/github/tag/linksmart/data-processing-agent.svg?label=release)](https://github.com/linksmart/linksmart-java-utils/tags)
[![Build Status](https://img.shields.io/travis/com/linksmart/data-processing-agent/master?label=master)](https://travis-ci.com/linksmart/linksmart-java-utils)
[![Build Status](https://img.shields.io/travis/com/linksmart/data-processing-agent/release?label=release)](https://travis-ci.com/linksmart/linksmart-java-utils)
[![Docker Pulls](https://img.shields.io/docker/pulls/linksmart/la?label=docker%20linksmart%2Fla)](https://hub.docker.com/r/linksmart/la/tags)
[![Docker Pulls](https://img.shields.io/docker/pulls/linksmart/dpa?label=docker%20linksmart%2Fdpa)](https://hub.docker.com/r/linksmart/dpa/tags)

The IoT Learning Agent (**LA**) is bing developed for all kind of store-less data processing, from simple data annotation or aggregation to complex data machine learning techniques. The LA fulfill the task of a LinkSmart® Processor and a bit of a Message Handler (see LinkSmart® Specification), and they are ideal for intelligent on-demand data management or analysis in IoT environments, from edge computing to cloud computing. The LA can be used as edge standalone service or as a computational node in the cloud.

The LA is a service that offer Complex-Event Processing as service and Real-time Machine Learning as a service. The agent provides three APIs, the Stream Mining API (Statement API), the Learning API (CEML API) and the IO API. The Statement and CEML APIs are CRUD (Create, Read, Update, Delete) and JSON based, while the IO are write-only (for Input) or read-only (for Output). The APIs are implemented as HTTPs RESTful and MQTT. There is a lightweight version of the LA without the learning API, named **Data-Processing Agent** (**DPA**) 

For more documentation please see [IoT Learning Agent](https://docs.linksmart.eu/display/LA).

# Deployment

Using Docker

```bash
  docker run -p "8319:8319" linksmart/la:latest  
```
Without docker
 
```bash
  curl -O eu/linksmart/services/events/gpl/distributions/iot.learning.universal.gpl.agent/1.8.2/iot.learning.universal.gpl.agent-<current.version>.jar
   env_var_enabled=true cep_init_engines=eu.linksmart.services.event.cep.engines.EsperEngine env_var_enabled=true cep_init_engines=eu.linksmart.services.event.cep.engines.EsperEngine java -cp ./* "org.springframework.boot.loader.PropertiesLauncher"  
```
# Compile from source

```bash
  git clone https://github.com/linksmart/data-processing-agent.git code
  cd code
  mvn install 
```

## Dependencies
To install maven in Linux:

```bash
  apt-get install maven
```

For use maven in docker see: [Maven Docker Image](https://hub.docker.com/_/maven/)

## Run DPA
```bash
   cd gpl-artifacts/distribution/IoTAgent/target
  env_var_enabled=true cep_init_engines=eu.linksmart.services.event.cep.engines.EsperEngine env_var_enabled=true cep_init_engines=eu.linksmart.services.event.cep.engines.EsperEngine java -cp ./* "org.springframework.boot.loader.PropertiesLauncher"
```

## Run LA
```bash
  
   cd gpl-artifacts/distribution/IoTAgent/target
  env_var_enabled=true cep_init_engines=eu.linksmart.services.event.cep.engines.EsperEngine env_var_enabled=true cep_init_engines=eu.linksmart.services.event.cep.engines.EsperEngine java -cp ./* "org.springframework.boot.loader.PropertiesLauncher"
```

# Documentation 
For more please see: [IoT Learning Agent](https://docs.linksmart.eu/display/LA).

## Contribute

Feel free to create an issue or pull request in GitHub in case you want to contribute to the software.
