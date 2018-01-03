var supertest = require('supertest'),
    expect = require('chai').expect,
    mqtt = require('mqtt');

var agent = supertest
    .agent(process.env.DPA_ROOT_URL || 'http://localhost:8319');

var mqttBroker = {
    host: process.env.MQTT_HOST || 'localhost',
    port: process.env.MQTT_PORT || 1883
};

var cemlQuery = {
    "Descriptors":
        {
            "TargetSize": 1,
            "InputSize": 1,
            "Type": "NUMBER"
        },
    "Model": {
        "Name": "LinearRegressionModel",
        "Targets": [
            {
                "Name": "RMSE",
                "Threshold": 5.0,
                "Method": "less"
            }
        ]
    },
    "LearningStreams": [
        {
            "statement": "select result as x, result as y from Observation"
        }
    ],
    "DeploymentStreams": [
        {
            "statement": "select <id>.predict(result) from Observation"
        }
    ],
    "Settings":
        {
            "ReportingEnabled": true
        }
};

describe('Learning API (CEML)', function () {

    it('should start the http server', function (done) {
        this.timeout(16000);
        this.retries(15);

        setTimeout(function () {
            agent.get('/ceml')
                .expect(200, done);
        }, 1000);
    });

    var ceml = {};

    it('should create a new ceml', function (done) {
        agent.put('/ceml/cemltest')
            .expect(201)
            .set('Accept', 'application/json')
            .expect('Content-Type', /json/)
            .send(cemlQuery)
            .end((err, res) => {
                if (err) return done(err);
                expect(res).to.have.deep.property('body.responses[0].messageType', 'SUCCESS');
                ceml['name'] = Object.keys(res.body.resources)[0];
                ceml['body'] = res.body.resources[ceml['name']];
                done();
            });
    });

    it('should learn incoming stream', function (done) {

        var numberOfMessages = 50;
        this.timeout(30000);

        var client = mqtt.connect(mqttBroker);
        var counter = 0;
        var end = false;
        client.on('message', function (topic, message) {
            if(!end) {
                var value = JSON.parse(message).ResultValue;

                expect(value.originalInput)
                    .to.closeTo(value.prediction[0], .1);
                counter++;
                // it starts to learn after ~3 messages
                if (counter > 3) {
                    end = true;
                    done();
                }
            }
        });

        client.on('connect', function () {
            // console.log("subscribing to:", statement1.topic,statement2.topic );
            client.subscribe([ceml['body'].deploymentStream[0].output[0]]);

            publisher();
        });

        function publisher() {
            for (var i = 1; i < numberOfMessages; i++) {
                // currently, there is a problem with casting int result to float,
                // so we are using float
                client.publish('LS/1/1/OGC/1.0/Datastreams/1', JSON.stringify({ id: i, result: i + 1e-5 }));
            }
        }
    });

    it('should delete the ceml', function (done) {
        agent.delete('/ceml/' + ceml['name'])
            .expect(200)
            .set('Accept', 'application/json')
            .expect('Content-Type', /json/)
            .end((err, res) => {
                if (err) return done(err);
                expect(res).to.have.deep.property('body.responses[0].messageType', 'SUCCESS');
                done();
            });
    });

    it('should return 404 for ceml after deleting', function (done) {
        agent.get('/ceml/' + ceml['name'])
            // .expect(404, done)
            .end((err, res) => {
                expect(res).to.have.deep.property('body.responses[0].status', 404);
                done();
            });
    });


});