# started from subscribed example on the paho library

import sys
import paho.mqtt.client as mqtt
from IoTEntityEvent import *
from IoTObservationEvent import *


MQTT_BROKER= "130.192.86.227"
MQTT_BROKER_PORT = 1883
MQTT_PROTOCOL_VERSION=mqtt.MQTTv31
MQTT_QOS=0

entities = {};
observations = {}

MQTT_TOPICS = [
	("/almanac/metadata/iotentity/#", MQTT_QOS),
	("/almanac/observation/iotentity/#", MQTT_QOS)
	]

gbl_i=0;

def on_connect(mqttc, obj, flags, rc):
    print("rc: "+str(rc))



def on_message(mqttc, obj, msg):
	global gbl_i;
	ev="";
	if "observation" in msg.topic:
		ev = IoTObservationEvent(msg.payload);
		print(str(ev));
		observations[ev.About] = ev;
	elif "almanac/metadata/iotentity" in msg.topic:	
		ev = IoTEntityEvent(msg.payload);
		#print(str(ev));
		entities[ev.About] = ev;
		print(ev.About);
		print(str(ev));
	else:
		print("other");
	#print(msg.topic+"\n\n"+str(msg.payload)+"\n")
	print "###"+ str(gbl_i) + ":" + str(len(entities)) + " unique entities and " + str(len(observations)) + " observations subjects so far"
	gbl_i=gbl_i+1;

def on_publish(mqttc, obj, mid):
    print("mid: "+str(mid))

def on_subscribe(mqttc, obj, mid, granted_qos):
    print("Subscribed: "+str(mid)+" "+str(granted_qos))

def on_log(mqttc, obj, level, string):
    print(string)

def main():
	mqttc = mqtt.Client(protocol=MQTT_PROTOCOL_VERSION)
	mqttc.on_message = on_message
	mqttc.on_connect = on_connect
	mqttc.on_publish = on_publish
	mqttc.on_subscribe = on_subscribe
	# Uncomment to enable debug messages
	#mqttc.on_log = on_log

	mqttc.connect(MQTT_BROKER, MQTT_BROKER_PORT, 60)
	mqttc.subscribe(MQTT_TOPICS)
	mqttc.loop_forever()

if __name__ == '__main__':
    try:
        args = sys.argv[1:]
        main()
    except KeyboardInterrupt:
        pass

print('Exiting.');
