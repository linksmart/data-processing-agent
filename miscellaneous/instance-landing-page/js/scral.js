// the configuration parameters
mqtt_broker = "130.192.86.65";
mqtt_port = 8000;
mqtt_websocket_endpoint = "/mosquitto";
mqtt_user = "landing.trn.federation1";
scral_url = "http://scral.trn.federation1.almanac-project.eu:8000";
vlc_ws_url = "ws://130.192.86.65:8088";
dfm_url = "http://trn.federation1.almanac-project.eu:8000/dfm";



// the watchdog timer
var watchdog;

// the active services tracking
var activeServicesCount = 0;
var allServicesCount = 6;

// the mqtt message counter
var mqttMsgCount = 0;
var mqttCounterTimer; 


$(document).ready(function() {
	handleServiceCountUIUpdate()
	getDevicesCount();
	getStatementCount()
	/*setInterval(function() {
		getDevicesCount();
	}, 5000);*/
	websocketSetUp();
	mqttConnectAndCheck();
	
	//---- set timers ------
	setInterval(function() {
		getStatementCount();
	}, 5000);
});

// ------- Active services ---------------
function incActiveServices()
{
    activeServicesCount++;
    handleServiceCountUIUpdate()
}
function decActiveServices()
{
    activeServicesCount--;
    handleServiceCountUIUpdate()
}

function handleServiceCountUIUpdate()
{
  $("#activeServicesCount").text(activeServicesCount);
  if(activeServicesCount > allServicesCount*2/3)
  {
    $("#activeServicesCount").addClass("label-success");
    $("#activeServicesCount").removeClass("label-warning");
    $("#activeServicesCount").removeClass("label-danger");
  }
  else if(activeServicesCount > allServicesCount*1/3)
  {
    $("#activeServicesCount").removeClass("label-success");
    $("#activeServicesCount").addClass("label-warning");
    $("#activeServicesCount").removeClass("label-danger");
  }
  else
  {
    $("#activeServicesCount").removeClass("label-success");
    $("#activeServicesCount").removeClass("label-warning");
    $("#activeServicesCount").addClass("label-danger");
  }
    
}
// ---------------------------------------

// ------- SCRAL -------------------------
function getDevicesCount() {
	$.ajax({
		url : scral_url+"/countDevice",
		type : "GET",
		crossDomain : true,
		success : function(data) {
			deviceCount = data;
			fillCountDevice(deviceCount);
			if($("#scralStatus").text()=="Offline")
			  incActiveServices();
			$("#scralStatus").text("Online");
			
			//set the class
			$("#scralStatus").removeClass("label-danger");
			$("#scralStatus").addClass("label-success");
		},
		error : function() {
			deviceCount = 0;
			if($("#scralStatus").text()=="Online")
			  decActiveServices();
			fillCountDevice(deviceCount);
			$("#scralStatus").text("Offline");
			
			//set the class
			$("#scralStatus").addClass("label-danger");
			$("#scralStatus").removeClass("label-success");
			
		}
	});
}

function fillCountDevice(count) {
	$("#deviceCountCatalogue").text(count);
	$("#deviceCountScral").text(count);
}

// ------- END SCRAL ---------------------

// ------- VLC ---------------------------

//web socket connection
function websocketSetUp()
{
  try {
	var webSocket = new WebSocket(vlc_ws_url+ '/ws/custom-events');

	webSocket.onerror = function (event) {
			console.log('WebSocket error: ' + JSON.stringify(event));
		};

	webSocket.onopen = function () {
			console.log('WebSocket connected');
			webSocket.send("{\"topic\":\"/broadcast\"}");
		};

	webSocket.onclose = function () {
			console.log('WebSocket closed');
		};

	webSocket.onmessage = function (event) {
			console.log('WebSocket message: ' + event.data);
			var jsonData = JSON.parse(event.data)
			if(jsonData.payload && jsonData.payload.type == "ALIVE")
			  handleAliveMessage();
		};
  } catch (ex) {
	console.log('Exception: ' + ex.message);
  }	
}

//handle the Virtualization Layer Alive message
function handleAliveMessage()
{
  //reset the watchdog timer
  if(watchdog)
    clearTimeout(watchdog)
    
  if($("#vlcStatus").text()=="Offline")
			  incActiveServices();
    
  //set the alive status
  $("#vlcStatus").text("Online");
  
  //set the class
  $("#vlcStatus").removeClass("label-danger");
  $("#vlcStatus").addClass("label-success");
  
  //start the watchdog
  // the ALIVE interval is 60000ms to be safe we permit one missing alive message
  watchdog = setTimeout(handleVLCOffline, 120000);
}

function handleVLCOffline()
{
  //set the alive status
  $("#vlcStatus").text("Offline");
  
  //set the class
  $("#vlcStatus").addClass("label-danger");
  $("#vlcStatus").removeClass("label-success");
  
  decActiveServices();
}

//--------- END VLC -----------------------

//--------- DFM ---------------------------
function getStatementCount() {
	$.ajax({
		url : dfm_url+"/statement/",
		type : "GET",
		crossDomain : true,
		success : function(data) {
			var jsonData = JSON.parse(data);
			$("#dfmQueryCount").text(""+Object.keys(jsonData.EsperEngine).length);
			if($("#dfmStatus").text()=="Offline")
			  incActiveServices();
			$("#dfmStatus").text("Online");
			//set the class
			$("#dfmStatus").removeClass("label-danger");
			$("#dfmStatus").addClass("label-success");
		},
		error : function() {
			deviceCount = 0;
			$("#dfmQueryCount").text("0");
			if($("#dfmStatus").text()=="Online")
			  decActiveServices();
			$("#dfmStatus").text("Offline");
			//set the class
			$("#dfmStatus").addClass("label-danger");
			$("#dfmStatus").removeClass("label-success");
			
		}
	});
}
//--------- END DFM -----------------------

//--------- MQTT Broker -------------------
function mqttConnectAndCheck()
{
	client = new Paho.MQTT.Client(mqtt_broker, mqtt_port, mqtt_websocket_endpoint, mqtt_user);

	// set callback handlers
	client.onConnectionLost = onConnectionLost;
	client.onMessageArrived = onMessageArrived;
	client.onSuccess = onConnect;


	// connect the client
	client.connect({onSuccess : onConnect , mqttVersion : 3});
}

function onConnect(data)
{
  //handle connection
  console.log("MQTT Connected");
  
  //update the service count
  incActiveServices();
  
  //updated the status label
  $("#brokerStatus").text("Online");
  
  //set the class
  $("#brokerStatus").removeClass("label-danger");
  $("#brokerStatus").addClass("label-success");
  
  //subscribe to everything
  client.subscribe("/#", {
		qos : 0
	});
  
  mqttCounterTimer = setInterval(function() {
	  updateMessagePerSecondCount();
	}, 1000)
}
function onMessageArrived(data)
{
  console.log("MQTT Data Arrived "+data);
  
  mqttMsgCount++;
  
}
function onConnectionLost(data)
{
  console.log("MQTT Connection Lost");
  
  //update the service count
  decActiveServices();
  
  //updated the status label
  $("#brokerStatus").text("Offline");
  
  //set the class
  $("#brokerStatus").addClass("label-danger");
  $("#brokerStatus").removeClass("label-success");
  
  //clear the sampling timer
  clearInterval(mqttCounterTimer);
 
}

function updateMessagePerSecondCount()
{
  //get the current value
  var nMsg = mqttMsgCount;
  mqttMsgCount = 0;
  
  //update the count
  $("#mqttMsgPerSec").text(nMsg);
}