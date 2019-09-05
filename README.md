IoT Learning Agent
======================
[![GitHub tag (latest release)](https://img.shields.io/github/tag/linksmartdata-processing-agent.svg?label=release)](https://github.com/linksmart/linksmart-java-utils/tags)
[![Build Status](https://img.shields.io/travis/com/linksmart/data-processing-agent/master?label=master)](https://travis-ci.com/linksmart/linksmart-java-utils)
[![Build Status](https://img.shields.io/travis/com/linksmart/data-processing-agent/release?label=release)](https://travis-ci.com/linksmart/linksmart-java-utils)
[![Docker Pulls](https://img.shields.io/docker/pulls/linksmart/la?label=docker%20linksmart%2Fla)](https://hub.docker.com/r/linksmart/la/tags)
[![Docker Pulls](https://img.shields.io/docker/pulls/linksmart/dpa?label=docker%20linksmart%2Fdpa)](https://hub.docker.com/r/linksmart/dpa/tags)

The IoT agents where developed for all kind of store-less data processing, from simple data annotation or aggregation to complex data machine learning techniques. The agents fulfill the task of a LinkSmart® Processor and a bit of a Message Handler (see LinkSmart® Specification), and they are ideal for intelligent on-demand data management or analysis in IoT environments, from edge computing to cloud computing. The Agents can be used as edge standalone service or as a computational node in the cloud.

The IoT agents are services that offer Complex-Event Processing as service and Real-time Machine Learning as a service. The agent provides three APIs, the Stream Mining API (Statement API), the Learning API (CEML API) and the IO API. The Statement and CEML APIs are CRUD (Create, Read, Update, Delete) and JSON based, while the IO are write-only (for Input) or read-only (for Output). The APIs are implemented as HTTPs RESTful and MQTT. 

For more documentation please see [IoT Data-Processing Agent and Learning Agent](https://docs.linksmart.eu/display/LA).

# Usage

```bash
  docker run linksmart/la:latest  
```
```bash
  curl -O eu/linksmart/services/events/gpl/distributions/iot.learning.universal.gpl.agent/1.8.2/iot.learning.universal.gpl.agent-<current.version>.jar
  export env_var_enabled=true
  export cep_init_engines=eu.linksmart.services.event.cep.engines.EsperEngine
  export agent_init_extensions=eu.linksmart.services.event.ceml.core.CEML
  java -jar distributions/IoTAgents/target/iot.learning.universal.agent-<current.version>.jar
  
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
  export env_var_enabled=true
  export cep_init_engines=eu.linksmart.services.event.cep.engines.EsperEngine
  java -jar distributions/IoTAgent/target/iot.learning.universal.agent-<current.version>.jar
```

## Run LA
```bash
  export env_var_enabled=true
  export cep_init_engines=eu.linksmart.services.event.cep.engines.EsperEngine
  export agent_init_extensions=eu.linksmart.services.event.ceml.core.CEML
  java -jar distributions/IoTAgents/target/iot.learning.universal.agent-<current.version>.jar
```
# Documentation 
For more please see: [IoT Data-Processing Agent and Learning Agent](https://docs.linksmart.eu/display/LA).

## Code structure

The code structure is the following:

* `/AgentAPI` - Definition of APIs in form of Java interfaces and simple `JavaBeans`.
* `/Applications` - Implementation of the stating point of the application. E.g. in plain Java the `main` function or in OSGi the `Activation` class.
* `Applications/ServiceLouncher` - is the common application starter using a `main` function. 
* `/cemlComponents` - Implementation of the CEML framework. This is an extension of the IoT Data-Processing agent that transform it into the IoT Learning Agent.
* `cemlComponents/ComplexEventMachineLearning` - main implementation classes of the CEML framework. 
* `cemlComponents/CemlRestAPI` - implementation of the REST API of the CEML framework. 
* `cemlComponents/Models` - inclusion of Learning Models for the IoT Learning Agent using the CEML framework. 
* `cemlComponents/Models/AutoregressiveNeuralNetworkModel` - Using the `DeepLearning4J` libraries this is an implementation of the Autoregressive Neural Network Algorithm.
* `cemlComponents/Models/LinearRegressionModel` - simple implementation of a model for testing proposes.
* `cemlComponents/Models/PythonModelsIntegrator` - connector using Pyro to allow the usage of Python in the IoT Learning Agent.
* `/cepComponents` - Here there are the implementations of the different integrations to Complex-Event Processing Engines and DataTypes integrations and other Tooling using by them (CEP and DataTypes).
* `cepComponents/SiddhiWrapper` - Implementation of the integration of the Agent to the WSO2 CEP engine. 
* `cepComponents/cepTooling` - Some utility function that can be used inside the CEP engines. 
* `cepComponents/payload` - Implementation of several standards supported by the IoT Agents, such as OGC SensorThings. 
* `/DataProcessingCore` - Main implementation of the IoT Agent API and the main functionalities.
* `/distributions` - Packing of the code into executable artifacts.
* `distributions/IoTAgent` - Basic Java JAR distribution of the IoT Agent including WSO2 CEP Engine and the CEML Framework. 
* `/test` - Related code to Continuous Integration and other testing related tasks.
* `/test/dpa-rest-integration` - Integration Test of the DPA.
* `/Utils` - Generic code used in several places.


## Contribute

Feel free to create an issue or pull request in GitHub in case you want to contribute to the software.
