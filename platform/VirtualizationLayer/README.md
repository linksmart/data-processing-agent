# Installation of the Virtualization Layer
Requires Node.js >= 10.40. For MQTT, works with protocol 3.1.1+ (e.g. Mosquitto version 1.3+).


1. See http://nodejs.org.
	* On Ubuntu:
	```sh
	apt-get install nodejs npm
	```

	* On Raspbian (Raspberry Pi):
		* Either from a binary package from: http://nodejs.org/dist/
			http://nodejs.org/dist/v0.10.27/node-v0.10.27-linux-arm-pi.tar.gz
			How-to: http://alexandre.alapetite.fr/doc-alex/raspberrypi-nodejs-arduino/#nodejs
			
		* Or compile from source from: http://nodejs.org/dist/latest/
			How-to: http://elinux.org/Node.js_on_RPi

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
