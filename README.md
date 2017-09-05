IoT Agents
===================

Implementation of the [IoT Agents](https://docs.linksmart.eu/display/LA).

## Code structure

The code structure is the following:

* `/AgentAPI` - Definition of APIs in form of Java interfaces and simple `JavaBeans`.
* `/Applications` - Implementation of the stating point of the application. E.g. in plain Java the `main` function or in OSGi the `Activation` class.
	** `Applications/ServiceLouncher` - is the common application starter using a `main` function. 
* `/cemlComponents` - Implementation of the CEML framework. This is an extension of the IoT Data-Processing agent that transform it into the IoT Learning Agent.
	** `cemlComponents/ComplexEventMachineLearning` - main implementation classes of the CEML framework. 
	** `cemlComponents/CemlRestAPI` - implementation of the REST API of the CEML framework. 
	** `cemlComponents/Models` - inclusion of Learning Models for the IoT Learning Agent using the CEML framework. 
		*** `cemlComponents/Models/AutoregressiveNeuralNetworkModel` - Using the `DeepLearning4J` libraries this is an implementation of the Autoregressive Neural Network Algorithm.
		*** `cemlComponents/Models/LinearRegressionModel` - simple implementation of a model for testing proposes.
		*** `cemlComponents/Models/PythonModelsIntegrator` - connector using Pyro to allow the usage of Python in the IoT Learning Agent.
* `/cepComponents` - Here there are the implementations of the different integrations to Complex-Event Processing Engines and DataTypes integrations and other Tooling using by them (CEP and DataTypes).
	** `cepComponents/SiddhiWrapper` - Implementation of the integration of the Agent to the WSO2 CEP engine. 
	** `cepComponents/cepTooling` - Some utility function that can be used inside the CEP engines. 
	** `cepComponents/payload` - Implementation of several standards supported by the IoT Agents, such as OGC SensorThings. 
* `/DataProcessingCore` - Main implementation of the IoT Agent API and the main functionalities.
* `/distributions` - Packing of the code into executable artifacts.
	** `distributions/IoTAgent` - Basic Java JAR distribution of the IoT Agent including WSO2 CEP Engine and the CEML Framework. 
* `/test` - Related code to Continuous Integration and other testing related tasks.
* `/test/dpa-rest-integration` - Integration Test of the DPA.
* `/Utils` - Generic code used in several places.

## Compile from source

```
git clone https://code.linksmart.eu/scm/la/data-processing-agent.git code
cd code
mvn install 
```

## Dependencies
To install maven in Linux:

```
apt-get install maven
```

For use maven in docker see: [Maven Docker Image](https://hub.docker.com/_/maven/)

## Run DPA
```
java -jar distributions/IoTAgents/target/iot.learning.universal.agent-<current.version>.jar
```

## Run LA
```
export env_var_enabled=true
export agent_init_extensions=eu.linksmart.services.event.ceml.core.CEML
java -jar distributions/IoTAgents/target/iot.learning.universal.agent-<current.version>.jar
```
# Documentation 
For more please see: [IoT Agents](https://docs.linksmart.eu/display/LA).
