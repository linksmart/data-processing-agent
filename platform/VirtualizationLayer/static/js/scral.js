// the configuration parameters
mqtt_broker = "130.192.86.65";
mqtt_port = 8000;
mqtt_websocket_endpoint = "/mosquitto";
mqtt_user = "landing.trn.federation1";
scral_url = "./scral";
dfm_url = "http://trn.federation1.almanac-project.eu:8000/dfm";
network_manager_url="/tunnel/";
storage_manager_url="./sm/help";
resource_catalog_url="./ResourceCatalogue/";


// the watchdog timer
var watchdog;
var vlcConnectionTimer;

// the active services tracking
var activeServicesCount = 0;
var allServicesCount = 6;

// the mqtt message counter
var mqttMsgCount = 0;
var mqttCounterTimer; 
var mqttConnectiontimer;
var client;

$(document).ready(function() {
	handleServiceCountUIUpdate()
	getDevicesCount();
	getStatementCount();
	getNetworkManagerStatus();
	getStorageManagerStatus(); 
	getResourceCatalogStatus();
	websocketSetUp();
	mqttConnectAndCheck();
	
	setInterval(function() {
		getDevicesCount();
		getStatementCount();
		getNetworkManagerStatus();
		getStorageManagerStatus();
		getResourceCatalogStatus();
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
	var webSocket = new WebSocket('ws://' + location.host + '/ws/custom-events');

	webSocket.onerror = function (event) {
			console.log('WebSocket error: ' + JSON.stringify(event));
			//schedule retry in 30s
			vlcConnectionTimer = setTimeout(websocketSetUp,30000);
		};

	webSocket.onopen = function () {
			console.log('WebSocket connected');
			if(vlcConnectionTimer)
				clear(vlcConnectionTimer);
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
			if(jsonData)
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
	var uuid = new UUIDGenerator();
	client = new Paho.MQTT.Client(mqtt_broker, mqtt_port, mqtt_websocket_endpoint, mqtt_user+uuid.generateUUID());

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
  
  //reset the re-connection timer
  if(mqttConnectiontimer)
	  clear(mqttConnectiontimer);
  
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
  
  // re - Connect
  // connect the client
  mqttConnectiontimer = setInterval(function() {
	  
	  console.log("Attempting re-connection after 5s...");
	  
	  console.log("client currently:"+client.isConnected());
	  if (client.isConnected()== false) {
		  client.connect({onSuccess : onConnect , mqttVersion : 3});
	  }
  }, 5000);
 
}

function updateMessagePerSecondCount()
{
  //get the current value
  var nMsg = mqttMsgCount;
  mqttMsgCount = 0;
  
  //update the count
  $("#mqttMsgPerSec").text(nMsg);
}

//----------- End MQTT broker ----------
//----------- Network Manager ----------
function networkManagerOnline() {
	if ($("#networkManagerStatus").text() === "Offline") {
		incActiveServices();
	}
	$("#networkManagerStatus").text("Online");
	//set the class
	$("#networkManagerStatus").removeClass("label-danger");
	$("#networkManagerStatus").addClass("label-success");
}

function networkManagerOffline() {
	if ($("#networkManagerStatus").text() === "Online") {
		decActiveServices();
	}
	$("#networkManagerStatus").text("Offline");
	//set the class
	$("#networkManagerStatus").addClass("label-danger");
	$("#networkManagerStatus").removeClass("label-success");
}

function getNetworkManagerStatus() {
	$.ajax({
		url : network_manager_url,
		type : "GET",
		crossDomain : true,
		success : function(data) {
			console.log(data);
			networkManagerOnline();
		},
		error : function(xhr) {
			if (xhr.status == 400) {
				networkManagerOnline();	//A request without parameter currently returns a 400
			} else {
				networkManagerOffline();
			}
		}
	});
}
//--------- END Network Manager ----------

//----------- Storage Manager ----------
function getStorageManagerStatus() {
	$.ajax({
		url : storage_manager_url,
		type : "GET",
		crossDomain : true,
		success : function(data) {
			console.log(data)
			if($("#storageManagerStatus").text()=="Offline")
				incActiveServices();
			$("#storageManagerStatus").text("Online");
			//set the class
			$("#storageManagerStatus").removeClass("label-danger");
			$("#storageManagerStatus").addClass("label-success");
			
		},
		error : function() {
			if($("#storageManagerStatus").text()=="Online")
				decActiveServices();
			$("#storageManagerStatus").text("Offline");
			//set the class
			$("#storageManagerStatus").addClass("label-danger");
			$("#storageManagerStatus").removeClass("label-success");
			
		}
	});
}
//--------- END Storage Manager  ----------

//----------- Resource Catalog ----------
function getResourceCatalogStatus() {
	$.ajax({
		url : resource_catalog_url,
		type : "GET",
		crossDomain : true,
		success : function(data) {
			console.log(data)
			if($("#resourceCatalogStatus").text()=="Offline")
				incActiveServices();
			$("#resourceCatalogStatus").text("Online");
			//set the class
			$("#resourceCatalogStatus").removeClass("label-danger");
			$("#resourceCatalogStatus").addClass("label-success");
			
		},
		error : function() {
			if($("#resourceCatalogStatus").text()=="Online")
				decActiveServices();
			$("#resourceCatalogStatus").text("Offline");
			//set the class
			$("#resourceCatalogStatus").addClass("label-danger");
			$("#resourceCatalogStatus").removeClass("label-success");
			
		}
	});
}
//--------- END Resource Catalog  ----------