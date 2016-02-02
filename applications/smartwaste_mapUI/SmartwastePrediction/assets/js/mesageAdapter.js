TOPIC_INITIAL = "/almanac/issue/initial";
TOPIC_UPDATE = "/almanac/issue/update";


initclient();

State = {
    OPEN:0, SCHEDULED:1, DONE:2, CLOSED:3
}
Priority = {
    UNCLASSIFIED:0, MINOR:1, MOJOR:2, CRITICAL:3
}
function initclient(){
	initMqttClient();
}

function initMqttClient(){
	// Create a client instance
	client = new Paho.MQTT.Client("test.mosquitto.org",8080,  "SmartWaste");
    //client = new Paho.MQTT.Client("iot.eclipse.org",1883, "", "SmartWaste");
    //client = new Paho.MQTT.Client("almanac",9001,  "SmartWaste");
//tcp://m2m.eclipse.org:1883
//tcp://almanac.fit.fraunhofer.de:1883
	// set callback handlers
	client.onConnectionLost = onConnectionLost;
	client.onMessageArrived = onMessageArrived;

	// connect the client
    client.connect({onSuccess:onConnect});
}

// called when the client connects
function onConnect() {
  // Once a connection has been made, make a subscription and send a message.
  console.log("onConnect");
  client.subscribe(TOPIC_INITIAL);

  var issue1 = {
       id:"Jodfkrgldfgmldfbldblbldfknbldfbhn1",
       date:new Date(),
       creator:"Shreekantha",
       resource:"Bin@Augustinum",
       type:"bin",
       assignee:"XYZ",
       state:State.OPEN,
       FillLevel:10,
       etc:new Date(),
       priority:Priority.MINOR,
       geolocation:{lat:50.769040, lng: 7.198483}
   };


  var issue2 = {
       id:"fgjkfgndflgevnldfgvdfmng2",
       date:new Date(),
       creator:"Shreekantha",
       resource:"Bin@Augustinum",
       type:"bin",
       assignee:"XYZ",
       state:State.OPEN,
       FillLevel:5,
       etc:new Date(),
       priority:Priority.UNCLASSIFIED,
       geolocation:{lat:50.764534, lng:7.205693}
   };
     var issue3 = {
          id:"fgjkfgndflegvfnldfgvdfmng3",
          date:new Date(),
          creator:"Shreekantha",
          resource:"Bin@Augustinum",
          type:"bin",
          assignee:"XYZ",
          state:State.OPEN,
          FillLevel:99,
          etc:new Date(),
          priority:Priority.MAJOR,
          geolocation:{lat:50.760679, lng: 7.195222}
      };
           var issue4 = {
                id:"fgjkfgfndeflgvnldfgvdfmng4",
                date:new Date(),
                creator:"Shreekantha",
                resource:"Bin@Augustinum",
                type:"bin",
                assignee:"XYZ",
                FillLevel:80,
                state:State.OPEN,
                etc:new Date(),
                priority:Priority.CRITICAL,
                geolocation:{lat:50.765783, lng: 7.207753}
            };

            var issue5 = {
                id:"fgjkfgfndeflgvnldfgvfmng44",
                date:new Date(),
                creator:"Shreekantha",
                resource:"Bin@Augustinum",
                type:"bin",
                assignee:"XYZ",
                state:State.OPEN,
                FillLevel:80,
                etc:new Date(),
                priority:Priority.CRITICAL,
                geolocation:{lat:50.7673744, lng: 7.2110523}
            };

   var issues = [issue1,issue2,issue3,issue4,issue5]
   message = new Paho.MQTT.Message(JSON.stringify(issues));
   message.destinationName = TOPIC_INITIAL;
   client.send(message);
}

// called when the client loses its connection
function onConnectionLost(responseObject) {
  if (responseObject.errorCode !== 0) {
    console.log("onConnectionLost:"+responseObject.errorMessage);
  }
}

function onMarkerClick(issueid){
//TODO invoke webmethod
console.log("cliecked " +issueid);
}
// called when a message arrives
function onMessageArrived(message) {
    switch(message.destinationName) {
    case TOPIC_INITIAL:
        var issues = JSON.parse(message.payloadString);
        //	console.log("onMessageArrived:"+message.payloadString);
        initializeMarkers(issues,onMarkerClick);
        break;
    case TOPIC_UPDATE:
        var  issue = JSON.parse(message.payloadString);
         updateMarker(issue);
    }
}

