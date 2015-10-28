import paho.mqtt.client as mqtt
import time

empty = False
full = False 


# The callback for when the client receives a CONNACK response from the server.
def on_connect(client, userdata, flags, rc):
    print("Connected with result code "+str(rc))

    # Subscribing in on_connect() means that if we lose the connection and
    # reconnect then subscriptions will be renewed.
    # client.subscribe("/#")

# The callback for when a PUBLISH message is received from the server.
def on_message(client, userdata, msg):
	global empty
	global full
	if msg.topic == "/federation1/amiat/v2/cep/102988187844182390838723116562667376558665655792916865539798299523235573891825" and not empty:
		empty = True
		#client.unsubscribe("/+/+/v2/cep/102988187844182390838723116562667376558665655792916865539798299523235573891825")
		print("empty query is working")
	if msg.topic == "/federation1/amiat/v2/cep/42494233324326995802191807088947086659260162522691801934921022547972842911256" and not full:
		full = True
		#client.unsubscribe("/+/+/v2/cep/42494233324326995802191807088947086659260162522691801934921022547972842911256")
                print("full query is working")
	#print(msg.topic)
    



client = mqtt.Client()
client.on_connect = on_connect
client.on_message = on_message

client.connect("almanac", 1883, 60)
client.subscribe("/+/+/v2/cep/102988187844182390838723116562667376558665655792916865539798299523235573891825")
client.subscribe("/+/+/v2/cep/42494233324326995802191807088947086659260162522691801934921022547972842911256")

# Blocking call that processes network traffic, dispatches callbacks and
# handles reconnecting.
# Other loop*() functions are available that give a threaded interface and a
# manual interface.
# client.loop_forever()
client.loop_start()

while not empty or not full:
	if not full:
		client.publish("/federation1/amiat-pi/v2/observation/1/1", '{"Time":"2015-10-23T11:57:57.092Z","ResultValue":99.99,"ResultType":"Level","Sensor":{"id":"1"}}')
	if not empty:
		client.publish("/federation1/amiat-pi/v2/observation/2/2", '{"Time":"2015-10-23T11:57:57.092Z","ResultValue":20.20,"ResultType":"Level","Sensor":{"id":"2"}}')
	time.sleep(1)
