# Installation of the Virtualization Layer

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
mkdir -p /home/almanac/platform/nodejs/VirtualizationLayer/
cd /home/almanac/platform/nodejs/VirtualizationLayer/
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

> The main dependencies include `socket.io`, `mqtt`, `node-ssdp`, and `request`.


# Setup of the Virtualization Layer

Edit the config.js file according to your network.

TODO: More information to follow


# Testing the Virtualization Layer:
(Only available when the installation was done without the `production` flag)

```sh
npm test
```


# Running the Virtualization Layer

## Either manually:

```sh
cd /home/almanac/platform/nodejs/VirtualizationLayer
nodejs index.js
```

## Or from a cron at restart:
Edit the file `/etc/cron.d/almanac` and add:

```sh
@reboot root cd /home/almanac/platform/nodejs/VirtualizationLayer && nodejs /home/almanac/platform/nodejs/VirtualizationLayer/index.js >> /home/almanac/platform/log/VirtualizationLayer.log 2>&1 &
```

## TODO: Document using "forever" https://github.com/nodejitsu/forever
