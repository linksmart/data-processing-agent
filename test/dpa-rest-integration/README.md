# DPA REST Integration Test

## The testing strategy

1. Starting broker
2. Starting Data Processing Agent
   1. Configure DPA for broker-host
   2. Start data-processing-agent
3. Start the integration test
   1. Check the HTTP server (wait until DPA is up)
   2. Register 2 statements
   3. Publish messages regarding to statement
   4. Cross check the pub & sub messages
   5. Delete statements
   6. Expect not to receive messages from CEP

Test messages are plain SenML objects like:

```
{"bn" : "dev1", "bt":100000000}
```

Registered statements are simple as selecting all fields of SenML messages:

```
{
    "name": "query1",
    "statement": "select bn, bt from begin=SenML(bn = 'dev1')"
}
```

It is possible to see published messages in the broker with a command line MQTT client like:

```
$ mosquitto_sub -t '#' -v
```

## Running the tests

`iot.data.processing.rest.agent` needs to be build before running the test: `mvn clean install`.

### Locally

Perquisites:

* DPA Rest Application
* MQTT Broker

You can configure MQTT broker and DPA information with following environment variables for `dpa-rest-integration`:

| Variable      | Default value         |
| ------------- |:---------------------:|
| DPA_ROOT_URL  | http://localhost:8319 |
| MQTT_HOST     | localhost             |
| MQTT_PORT     | 1883                  |

NodeJS packages needs to be installed via `npm install`
Run `npm test` or `npm start` (it runs Mocka programmatically, it can be used for debugging test as the main thread)

### In container

```
 docker-compose up
```



```
cp gpl-artifacts/distributions/rest/IoTDataProcessingAgent/target/iot.data.processing.rest.agent-1.4.0-SNAPSHOT.jar ~/projects/dpa-integration-test/dpa/
```



## TODO

* Use more lightweight image for `mqtt-broker` (like alpine)
* Move `dpa` build step and `docker-compose` into a proper CI pipeline definition with a CI system
