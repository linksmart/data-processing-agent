var NavBarClass = function(){};

NavBarClass.prototype.onMqttConnecting = function()
{
	$("#mqttStatusIcon").removeClass();
	$("#mqttStatusIcon").addClass("icon-loading");
	
	$("#mqttStatusText").text("Connecting to the MQTT broker ");
} 

NavBarClass.prototype.onMqttConnect = function()
{
	$("#mqttStatusIcon").removeClass();
	$("#mqttStatusIcon").addClass("icon-thumbs-up");
	
	$("#mqttStatusText").text("Connected to the MQTT broker ");
}

NavBarClass.prototype.onMqttSubscribed = function()
{
	$("#mqttStatusIcon").removeClass();
	$("#mqttStatusIcon").addClass("icon-checkmark");
	
	$("#mqttStatusText").text("Properly subscribed to MQTT topics");
}

NavBarClass.prototype.onMqttConnectionLost = function()
{
	$("#mqttStatusIcon").removeClass();
	$("#mqttStatusIcon").addClass("icon-cancel-2");
	
	$("#mqttStatusText").text("Connection failed ");
}