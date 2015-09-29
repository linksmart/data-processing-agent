# Installation of the Virtualization Layer
Requires Node.js >= 0.10.40, and recommends the 0.10.x.
For MQTT, recommend protocol 3.1.1+ (e.g. Mosquitto version 1.3+).


1. See http://nodejs.org.
	* On Ubuntu or Debian jessie:
	```sh
	apt-get install nodejs npm
	```

	* Otherwise, use https://github.com/nodesource/distributions

2. Create and move to the directory where the VirtualizationLayer should be located, e.g.

```sh
mkdir -p /opt/virtualization-layer/
cd /opt/virtualization-layer/
```

3. Deploy the VirtualizationLayer source-code

4. Use `npm` to fetch the librairies and dependencies automatically:

For a production deployment:

```sh
npm install --production
```

Or if you want to perform continuous integration and other tests, leave out the `production` flag:

```sh
npm install
```

> The main dependencies include `ws`, `mqtt`, `node-ssdp`, and `request`.


# Setup of the Virtualization Layer

Look at the `config.js` without changing it, and edit a `config.local.js` file according to your network (and example is provided in `config.local.example.js`).



# Testing the Virtualization Layer:
(Only available when the installation was done without the `production` flag)

```sh
npm test
```


# Running the Virtualization Layer

## Either manually:

```sh
cd /opt/virtualization-layer/
nodejs index.js
```

## Or by using "pm2" https://github.com/Unitech/pm2
TODO: Finish documentation

```sh
sudo npm install -g pm2
pm2 start /usr/bin/nodejs /opt/virtualization-layer/index.js -- -v
pm2 save
pm2 startup
```

## Or from a cron at restart:
Edit the file `/etc/cron.d/almanac` and add:

```sh
@reboot root cd /opt/virtualization-layer/ && /usr/bin/nodejs /opt/virtualization-layer/index.js >> /var/log/virtualization-layer/virtualization-layer.log 2>&1 &
```

## Or from a Linux Upstart service

Add a new file `/etc/init/virtualization-layer.conf`

```sh
description	"ALMANAC VirtualizationLayer"

start on net-device-up
stop on shutdown

respawn
respawn limit 30 60

script
	cd /opt/virtualization-layer
	exec sudo -u almanac /usr/bin/nodejs /opt/virtualization-layer/index.js >> /var/log/virtualization-layer/virtualization-layer.log 2>&1
end script
```

And then use it a a service, such as:

```sh
service virtualization-layer restart
```


# Virtualization Layer API

## Pages

* Technical information about the instance: /virtualizationLayerInfo
* WebSocket HTML+JavaScript chat demo: /socket.html
* WebSocket JavaScript chat demo: /console.html

## HTTP Proxying + Format conversion

* Proxy to Network Manager tunnelling: /tunnel/0.0.0.8917820598345047854 (change the virtual address)
* Proxy to Resource Catalogue: /ResourceCatalogue/ogc/Things?filter= using Resource Catalogue API
* Proxy to Storage Manager: /sm/ using Storage Manager API
	* E.g. /sm/DataStreams%28ab1db42dea1bcdcb03f61b2a47ada8a77955715abe9bbed6611d82ba5ffa3570%29/Observations/$current
	* Format conversion to ATOM (RSS): /sm-rss/ using Storage Manager API
	* Format conversion to CSV: /sm-csv/ using Storage Manager API
	* Format conversion to TSV: /sm-tsv/ using Storage Manager API
* Proxy to SCRAL: /scral/devices using SCRAL API
* Proxy to SmartSantander: /santander/GetNodes using SmartSantander API

## WebSocket
Good tools to test include [wscat](https://github.com/websockets/wscat) (command line) and [Dark WebSocket Terminal](https://chrome.google.com/webstore/detail/dark-websocket-terminal/dmogdjmcpfaibncngoolgljgocdabhke) (Google Chrome extension).
(Replace localhost by the public URL of the instance)

* Pan-federation chat: ws://localhost/ws/chat
* Custom live events: ws://localhost/ws/custom-events﻿
	* One can subscribe to internal MQTT topics by sending JSON messages like the following (they must start by a slash /):
		* {"topic":"/federation1/test1"}
		* {"topic":"/federation1/test2"}
	* Or one can subscribe to multiple MQTT topics using a regular expression (the pattern is automatically anchored at beginning ^ and end $):
		* {"topic":"/federation1/test[3-7]", "matching":"regex"}
		* {"topic":"/federation1/test8/.*", "matching":"regex"}
	* Each time, one then receives a confirmation with the list of topics subscribed to:
		* {"subscriptions":{"/federation1/test1":true,"/federation1/test2":true},"subscriptionsRegex":{"/federation1/test[1-7]":{},"/federation1/test1/.*":{}}}
	* And one then receives push messages for all matching events, e.g.
		* {"topic":"/federation1/test1","payload":{"Hello":"World"}}
		* {"topic":"/federation1/test8/abcd","payload":{"Hello":"World"}}
