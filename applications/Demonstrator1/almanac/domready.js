var DOM = new function() {
	var IS_READY = false;
	var CALLBACKS = [];
	var SELF = this;

	SELF.ready = function(callback) {
		//check to see if we're already finished
		if (IS_READY === true && typeof callback === 'function') {
			callback();
			return;
		}

		//else, add this callback to the queue
		CALLBACKS.push(callback);
	};
	var addEvent = function(event, obj, func) {
		if (window.addEventListener) {
			obj.addEventListener(event, func, false);
		} else if (document.attachEvent) {
			obj.attachEvent('on' + event, func);
		}
	};
	var doScrollCheck = function() {
		//check to see if the callbacks have been fired already
		if (IS_READY === true) {
			return;
		}

		//now try the scrolling check
		try {
			document.documentElement.doScroll('left');
		} catch (error) {
			setTimeout(doScrollCheck, 1);
			return;
		}

		//there were no errors with the scroll check and the callbacks have not yet fired, so fire them now
		fireCallbacks();
	};
	var fireCallbacks = function() {
		//check to make sure these fallbacks have not been fired already
		if (IS_READY === true) {
			return;
		}

		//loop through the callbacks and fire each one
		var callback = false;
		for (var i = 0, len = CALLBACKS.length; i < len; i++) {
			callback = CALLBACKS[i];
			if (typeof callback === 'function') {
				callback();
			}
		}

		//now set a flag to indicate that callbacks have already been fired
		IS_READY = true;
	};
	var listenForDocumentReady = function() {
		//check the document readystate
		if (document.readyState === 'complete') {
			return fireCallbacks();
		}

		//begin binding events based on the current browser
		if (document.addEventListener) {
			addEvent('DOMContentLoaded', document, fireCallbacks);
			addEvent('load', window, fireCallbacks);
		} else if (document.attachEvent) {
			addEvent('load', window, fireCallbacks);
			addEvent('readystatechange', document, fireCallbacks);

			//check for the scroll stuff
			if (document.documentElement.doScroll && window.frameset === null) {
				doScrollCheck();
			}
		}
	};

	//since we have the function declared, start listening
	listenForDocumentReady();
};