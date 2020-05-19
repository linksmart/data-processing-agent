IoT Learning Agent
======================
[![GitHub tag (latest release)](https://img.shields.io/github/tag/linksmart/data-processing-agent.svg?label=release)](https://github.com/linksmart/linksmart-java-utils/tags)
[![Build Status](https://img.shields.io/travis/com/linksmart/data-processing-agent/master?label=master)](https://travis-ci.com/linksmart/linksmart-java-utils)
[![Build Status](https://img.shields.io/travis/com/linksmart/data-processing-agent/release?label=release)](https://travis-ci.com/linksmart/linksmart-java-utils)
[![Docker Pulls](https://img.shields.io/docker/pulls/linksmart/la?label=docker%20linksmart%2Fla)](https://hub.docker.com/r/linksmart/la/tags)
[![Docker Pulls](https://img.shields.io/docker/pulls/linksmart/dpa?label=docker%20linksmart%2Fdpa)](https://hub.docker.com/r/linksmart/dpa/tags)

The IoT Learning Agent (**LA**) is bing developed for all kind of store-less data processing, from simple data annotation or aggregation to complex data machine learning techniques. The LA fulfill the task of a LinkSmart® Processor and a bit of a Message Handler (see LinkSmart® Specification), and they are ideal for intelligent on-demand data management or analysis in IoT environments, from edge computing to cloud computing. The LA can be used as edge standalone service or as a computational node in the cloud.

The LA is a service that offer Complex-Event Processing as service and Real-time Machine Learning as a service. The agent provides three APIs, the Stream Mining API (Statement API), the Learning API (CEML API) and the IO API. The Statement and CEML APIs are CRUD (Create, Read, Update, Delete) and JSON based, while the IO are write-only (for Input) or read-only (for Output). The APIs are implemented as HTTPs RESTful and MQTT. There is a lightweight version of the LA without the learning API, named **Data-Processing Agent** (**DPA**) 

For more documentation please see [IoT Learning Agent Wiki](https://github.com/linksmart/data-processing-agent/wiki).

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

## Acknowledgements 

This work was applied and supported by the European Commission through:
 * The [ALMANAC FP7 project](https://www.fit.fraunhofer.de/en/fb/ucc/projects/almanac.html) under grant no. 609081.
 * The [IMPReSS H2020 project](https://www.fit.fraunhofer.de/en/fb/ucc/projects/impress.html) under grant no. 614100.
 * The [EXCELL H2020 project](https://www.fit.fraunhofer.de/en/fb/ucc/projects/excell.html) under grant no. 691829.
 * The [COMPOSITION H2020 project](https://www.fit.fraunhofer.de/en/fb/ucc/projects/composition.html) under grant no. 723145.

## Publications
 * [An online machine learning framework for early detection of product failures in an Industry 4.0 context](https://www.tandfonline.com/doi/abs/10.1080/0951192X.2019.1571238)
 * [Optimization Framework for Short-Term Control of Energy Storage Systems](https://ieeexplore.ieee.org/abstract/document/8571722) 
 * [Enabling Smart Cities through IoT: The ALMANAC Way](https://www.taylorfrancis.com/books/9781315156026/chapters/10.1201/9781315156026-8) 
 * [Industry 4.0: Mining Physical Defects in Production of Surface-Mount Devices](http://eprints.sztaki.hu/9288/1/Tavakolizadeh_146_3306416_ny.pdf) 
 * [CEML: Mixing and moving complex event processing and machine learning to the edge of the network for IoT applications](https://dl.acm.org/citation.cfm?id=2991575) 
 * [ALMANAC: Internet of Things for Smart Cities](https://ieeexplore.ieee.org/abstract/document/7300833) 
 
