"use strict";
/*
	Basic functions for a simple Web server, serving static files, logging, and returning status codes
		by Alexandre Alapetite http://alexandre.alapetite.fr
*/

var os = require('os'),
	fs = require('fs'),
	path = require('path');

var basicHttp = {

	escapeHtml: function (text) {
		return text.replace(/&/g, "&amp;")
			.replace(/</g, "&lt;")
			.replace(/>/g, "&gt;")
			.replace(/"/g, "&quot;")
			.replace(/'/g, "&#039;");
	},

	serverSignature: 'Node.js ' + process.version + ' / ' + os.type() + ' ' + os.release() + ' ' + os.arch(),

	log: function (req, res) {
		console.log('HTTP:\t' + (new Date()).toISOString() + '\t' + req.connection.remoteAddress + '\t' + res.statusCode + '\t"' + req.method + ' ' + req.url + '"\t"' +
			(req.headers['user-agent'] || '') + '"\t"' + (req.headers['referer'] || '') + '"');
	},

	serveHome: function (req, res) {
		var now = new Date();
		res.writeHead(200, {
			'Content-Type': 'text/html; charset=UTF-8',
			'Date': now.toUTCString(),
			'Server': basicHttp.serverSignature,
		});
		res.end('<!DOCTYPE html>\n\
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-GB" lang="en-GB">\n\
<head>\n\
<meta charset="UTF-8" />\n\
<title>Node.js on Raspberry Pi</title>\n\
<meta name="robots" content="noindex" />\n\
<meta name="viewport" content="initial-scale=1.0,width=device-width" />\n\
</head>\n\
<body>\n\
<pre>\n\
Hello ' + req.connection.remoteAddress + '!\n\
This is ' + req.connection.localAddress + ' running <a href="http://nodejs.org/" rel="external">Node.js</a> :-)\n\
It is now ' + now.toISOString() + '.\n\
</pre>\n\
<ul>\n\
</ul>\n\
</body>\n\
</html>\n\
 ');
	},

	serve400: function (req, res) {
		res.writeHead(400, {
			'Content-Type': 'text/html; charset=UTF-8',
			'Date': (new Date()).toUTCString(),
			'Server': basicHttp.serverSignature,
		});
		res.end('<!DOCTYPE html>\n\
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-GB" lang="en-GB">\n\
<head>\n\
<meta charset="UTF-8" />\n\
<title>400 Bad request</title>\n\
</head>\n\
<body>\n\
<h1>Bad request</h1>\n\
<p>Your browser sent a request that this server could not understand.</p>\n\
</body>\n\
</html>\n\
 ');
	},

	serve404: function (req, res) {
	//When a static file is not found
		res.writeHead(404, {
			'Content-Type': 'text/html; charset=UTF-8',
			'Date': (new Date()).toUTCString(),
			'Server': basicHttp.serverSignature,
		});
		res.end('<!DOCTYPE html>\n\
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-GB" lang="en-GB">\n\
<head>\n\
<meta charset="UTF-8" />\n\
<title>404 Not Found</title>\n\
</head>\n\
<body>\n\
<h1>Not Found</h1>\n\
<p>The requested <abbr title="Uniform Resource Locator">URL</abbr> <kbd>' +
	basicHttp.escapeHtml(req.url) + '</kbd> was not found on this server.</p>\n\
</body>\n\
</html>\n\
 ');
	},

	serve405: function (req, res, allowedMethods) {
		res.writeHead(405, {
			'Content-Type': 'text/html; charset=UTF-8',
			'Date': (new Date()).toUTCString(),
			'Server': basicHttp.serverSignature,
			'Allow': allowedMethods,
		});
		res.end('<!DOCTYPE html>\n\
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-GB" lang="en-GB">\n\
<head>\n\
<meta charset="UTF-8" />\n\
<title>405 Method Not Allowed</title>\n\
</head>\n\
<body>\n\
<h1>Method Not Allowed</h1>\n\
<p>The requested method <kbd>' + basicHttp.escapeHtml(req.method) + '</kbd> is not allowed at this URL.</p>\n\
</body>\n\
</html>\n\
 ');
	},

	serve406: function (req, res) {
		res.writeHead(406, {
			'Content-Type': 'text/html; charset=UTF-8',
			'Date': (new Date()).toUTCString(),
			'Server': basicHttp.serverSignature,
		});
		res.end('<!DOCTYPE html>\n\
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-GB" lang="en-GB">\n\
<head>\n\
<meta charset="UTF-8" />\n\
<title>406 Not Acceptable</title>\n\
</head>\n\
<body>\n\
<h1>Not Acceptable</h1>\n\
<p>The content-type is not acceptable for this URL.</p>\n\
</body>\n\
</html>\n\
 ');
	},

	serve500: function (req, res, ex) {
		console.warn(ex);
		res.writeHead(500, {
			'Content-Type': 'text/html; charset=UTF-8',
			'Date': (new Date()).toUTCString(),
			'Server': basicHttp.serverSignature,
		});
		res.end('<!DOCTYPE html>\n\
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-GB" lang="en-GB">\n\
<head>\n\
<meta charset="UTF-8" />\n\
<title>500 Internal Server Error</title>\n\
</head>\n\
<body>\n\
<h1>Internal Server Error</h1>\n\
<p>The server encountered an internal error or misconfiguration and was unable to complete your request.</p>\n\
<pre>' + ex + '</pre>\n\
</body>\n\
</html>\n\
 ');
	},

	serve503: function (req, res) {
		res.writeHead(503, {
			'Content-Type': 'text/html; charset=UTF-8',
			'Date': (new Date()).toUTCString(),
			'Server': basicHttp.serverSignature,
		});
		res.end('<!DOCTYPE html>\n\
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-GB" lang="en-GB">\n\
<head>\n\
<meta charset="UTF-8" />\n\
<title>503 Service Unavailable</title>\n\
</head>\n\
<body>\n\
<h1>Service Unavailable</h1>\n\
<p>The service you are requesting is temporarily unavailable.</p>\n\
</body>\n\
</html>\n\
 ');
	},

	serveStaticFile: function (req, res) {
		if ((/^\/[a-z0-9_-]+\.[a-z]{2,4}$/i).test(req.url) && (!(/\.\./).test(req.url))) {
			var myPath = './static' + req.url;
			fs.stat(myPath, function (err, stats) {
				if ((!err) && stats.isFile()) {
					var ext = path.extname(myPath),
						mimes = { '.css': 'text/css', '.html': 'text/html', '.ico': 'image/x-icon', '.jpg': 'image/jpeg',
							'.js': 'application/javascript', '.json': 'application/json', '.png': 'image/png', '.txt': 'text/plain', '.xml': 'application/xml' },
						modifiedDate = new Date(stats.mtime).toUTCString();
					if (modifiedDate === req.headers['if-modified-since']) {
						res.writeHead(304, {
							'Content-Type': ext && mimes[ext] ? mimes[ext] : 'application/octet-stream',
							'Date': (new Date()).toUTCString()
						});
						res.end();
					} else {
						res.writeHead(200, {
							'Content-Type': ext && mimes[ext] ? mimes[ext] : 'application/octet-stream',
							'Content-Length': stats.size,
							'Cache-Control': 'public, max-age=86400',
							'Date': (new Date()).toUTCString(),
							'Last-Modified': modifiedDate,
							'Server': basicHttp.serverSignature
						});
						fs.createReadStream(myPath).pipe(res);
					}
				} else {
					basicHttp.serve404(req, res);
				}
			});
		} else {
			basicHttp.serve404(req, res);
		}
	},

	serveJson: function (req, res, json) {
		res.writeHead(200, {
			'Content-Type': 'application/json; charset=UTF-8',
			'Date': (new Date()).toUTCString(),
			'Server': basicHttp.serverSignature,
		});
		res.end(JSON.stringify(json));
	},

};

exports.basicHttp = basicHttp;
