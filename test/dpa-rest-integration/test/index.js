var supertest = require('supertest'),
    expect = require("chai").expect,
    mqtt = require('mqtt');

var agent = supertest
    .agent(process.env.DPA_ROOT_URL || 'http://localhost:8319');

var mqttBroker = {
    host: process.env.MQTT_HOST || 'localhost',
    port: process.env.MQTT_PORT || 1883
};

describe('The REST Service', function () {

    it('should start the http server', function (done) {
        this.timeout(16000);
        this.retries(15);

        setTimeout(function () {
            agent.get('/')
                .expect(200, done);
        }, 1000);
    });
});

var flag=false;
describe('CEPEngine', function () {
    


    var statement1 = {}, statement2 = {};


    it('should be loaded', function (done) {
        agent.get('/')
            .expect(200)
            .set('Accept', 'application/json')
            .expect('Content-Type', /json/)
            .send()
            .end((err, res) => {
                expect(res).to.have.deep.property('body.LoadedComponents.CEPEngine');
                done();
            });
    });


    it('creates Statement 1', statementInserter(1, statement1));

    it('reads statement 1', function (done) {
        agent.get('/statement/' + statement1.id)
            .expect(200)
            .set('Accept', 'application/json')
            .expect('Content-Type', /json/)
            .end((err, res) => {
                if (err) return done(err);
                expect(res).to.have.deep.property('body.responses[0].messageType', 'SUCCESS');
                expect(res).to.have.deep.property('body.responses[0].topic', statement1.topic);
                done();
            });
    });

    it('creates Statement 2', statementInserter(2, statement2));


    it('should publish respected topics for Statement 1 & 2', function (done) {


        var numberOfMessages = 100;
        this.timeout(30000);

        var messagesReceived = {};
        messagesReceived[statement1.id] = [];
        messagesReceived[statement2.id] = [];


        var messagesPublished = {};
        messagesPublished[statement1.id] = [];
        messagesPublished[statement2.id] = [];

        var client = mqtt.connect(mqttBroker);

        var counter = 0;
        client.on('message', function (topic, message) {
            ds = JSON.parse(message).result;
            messagesReceived[topic.split('/')[topic.split('/').length-2]].push(ds);
            counter++;
            if (counter === numberOfMessages) {
                client.end();
                expect(messagesPublished).to.deep.equal(messagesReceived);
                done();
            }
        });


        client.on('connect', function () {
            //console.log("subscribing to:", statement1.topic,statement2.topic );
            client.subscribe([
               // '/outgoing/' + statement1.id + '/' + statement1.agentID + statement1.id,
               // '/outgoing/' + statement2.id + '/' + statement2.agentID + statement2.id
			    statement1.topic,
                statement2.topic
                //"LS/LA/+/OGC/1.0/Datastreams/#"
                
				]);

            publisher();
        });


        function publisher() {
            var counter = 0;
            var i = setInterval(function () {
                var t = +new Date,
                    ds;

                if ((Math.floor(Math.random() * 2) === 0)) {
                    ds = {
                        bn: 'dev1',
                        bt: t
                    };
                    messagesPublished[statement1.id].push(ds);
                    client.publish('/testing/generator/dev1', JSON.stringify(ds));
                } else {
                    ds = {
                        bn: 'dev2',
                        bt: t
                    };
                    messagesPublished[statement2.id].push(ds);
                    client.publish('/testing/generator/dev2', JSON.stringify(ds));
                }

                counter++;
                if (counter === numberOfMessages) {
                    clearInterval(i);
                }
            }, 100);
        }

    });

    it('deletes Statement 1', function (done) {
        agent.delete('/statement/' + statement1.id)
            .expect(200)
            .set('Accept', 'application/json')
            .expect('Content-Type', /json/)
            .end((err, res) => {
                if (err) return done(err);
                expect(res).to.have.deep.property('body.responses[0].messageType', 'SUCCESS');
                done();
            });
    });


    it('should return 404 for Statement 1 after deleting', function (done) {
        agent.get('/statement/' + statement1.id)
            .expect(404, done);
    });


    it('should not publish topic for Statement 1 anymore', function (done) {

        this.timeout(3000);
        var client = mqtt.connect(mqttBroker);

        client.on('message', function (topic, message) {
            done('Still publishing messages from CEP Engine for a deleted statement');
        });

        client.on('connect', function () {
            client.subscribe(
                // '/outgoing/' + statement1.id + '/' + statement1.agentID + statement1.id
				statement1.topic
				);
            // there is a bug for statement1.topic!!! Do not rely on that!

            // publish a message for statement 1
            var ds = {
                bn: 'dev1',
                bt: +new Date
            }
            client.publish('/testing/generator/dev1', JSON.stringify(ds))
        })


        setTimeout(function () {
            done();
        }, 2000);
    });

});


function statementInserter(no, statement) {
    return function (done) {
        agent.post('/statement/')
            .expect(201)
            .set('Accept', 'application/json')
            .expect('Content-Type', /json/)
            .send({
                "name": "statement" + no,
                "statement": "select bn, bt from begin=SenML(bn = 'dev" + no + "')"
            })
            .end((err, res) => {
                if (err) return done(err);
                expect(res).to.have.deep.property('body.responses[0].messageType', 'SUCCESS');
                statement['id'] = Object.keys(res.body.resources)[0];
                statement['topic'] = res.body.responses[0].topic;
                statement['agentID'] = res.body.responses[0].agentID;
               // console.log(statement['id']);
               // console.log(statement['topic']);
                done();
            });
    }
}