from flask import Flask
from flask import request
from flask import abort
from flask import Response
import sys, time,shlex,os,uuid,json,jsonify,Pyro4
from subprocess import PIPE, Popen
from threading  import Thread
from werkzeug.exceptions import default_exceptions
from werkzeug.exceptions import HTTPException

app = Flask(__name__)
app.debug = True
try:
	from Queue import Queue, Empty
except ImportError:
	from queue import Queue, Empty  # python 3.x
	
__all__ = ['make_json_app']
ON_POSIX = 'posix' in sys.builtin_module_names
threads=dict()
ADAPTER_PATH="pyroAdapter.py"
THREAD='thread'
PYRO='pyro'
PROXY='proxy'
PYTHON_PATH="python"
def enqueue_output(out, queue):
	for line in iter(out.readline, b''):
		queue.put(line)
	out.close()
def run(id,arguments):
	p = Popen(shlex.split(PYTHON_PATH+" -u "+ADAPTER_PATH+" --bname="+arguments['name']+" --bpath="+arguments['path']+(" --host="+arguments['host'] if 'host' in arguments else "")+(" --port="+arguments['port'] if 'port' in arguments else "")+(" --nathost="+arguments['natHost'] if 'natHost' in arguments else "")+(" --natport="+arguments['natPort'] if 'natPort' in arguments else "")+ (" --ns" if 'ns' in arguments else "") +(" --rname="+arguments['registerName'] if 'registerName' in arguments else "")), stdout=PIPE, bufsize=1, close_fds=ON_POSIX)
	q = Queue()
	t = Thread(target=enqueue_output, args=(p.stdout, q))
	t.daemon = True # thread dies with the program
	t.start()
	# ... do other things here
	i=0
	ret =""
	while( i<30):
	# read line without blocking
		try:  line = q.get_nowait() # or q.get(timeout=.1)
		except Empty:
			#print('no output yet')
			time.sleep(1.0)
			i+=1
		else: # got line
			
			if not ("PYRO" in str(line)):
				ret += str(line); 
				i+=1
			else:
				ret=(str(line).replace('\\r','').replace('\\n','').replace("b'",'').replace("'",''))
				break
	proxy=Pyro4.Proxy(ret)
	if ret == "":
		abort(500,"unknown error")
	elif not ("PYRO" in ret):
		abort(500,ret )
	if not proxy.hi():
		abort(500, "Unknown error. Unable to spawn remote object!")
	threads[id] ={THREAD:t,PYRO:ret,PROXY:proxy}
	return ret

@app.route('/pyro/<string:id>', methods=['PUT'])
@app.route('/pyro/', methods=['POST'], defaults={'id': None})
def create(id):
	if id == None:
		id = str(uuid.uuid4())
		while id in threads:
			id = str(uuid.uuid4())
			
	if (not 'name' in request.json ) or (not 'path' in request.json ):
		abort(400, "JSON property name and path are mandatory!")
	if not os.path.isfile(ADAPTER_PATH): 
		abort(500, "The configured path of the adapter is wrong contact your admin!")
	if not os.path.isfile(request.json['path']):
		abort(400, "The given path to the module is wrong!")
	if id in threads:
		abort(400, "Id already exists and cannot be overwrite!")
		
	return Response(json.dumps({'uri':run(id,request.json), 'statusMessage':"OK"}),mimetype='application/json')
		
@app.route('/pyro/<string:id>', methods=['GET'])
def get(id):
	if (not id in threads):
		abort(404, "The id doesn't exist!")
	return Response(json.dumps({'uri':threads[id][PYRO], 'statusMessage':"OK"}),mimetype='application/json')
	
@app.route('/pyro/<string:id>', methods=['DELETE'])
def remove(id):
	if (not id in threads):
		abort(404, "The id doesn't exist!")
	running=False
	try:
		running=threads[id][PROXY].hi()
	except:
		del(threads[id])
		if threads[id][THREAD].is_alive():
			abort(500, "UNSTABLE STATUS: The pyro object is not reachable but the the container thread still running and unable to be stopped! The server will remove this entry but doesn't not ensure that the thread stops!")
		else:
			abort(207, "The pyro object is already not reachable! Both the thread and the pyro proxy are already destroy!")
	
	
	try:
		threads[id][PROXY].shutdown()
	except:
		i= 'ignored'
	time.sleep(2.0)
	try:
		running=hreads[id][PROXY].hi()
	except:
		if running:
			running=False
		
	if not running and not threads[id][THREAD].is_alive():
		del(threads[id])
		return Response(json.dumps({'statusMessage':"OK"}),mimetype='application/json')
	elif not running:
		del(threads[id])
		abort(500, "Pyro object is stopped! The container thread not! this may died either later or when the sever stops")
	else:		
		abort(500, "I just can't!")
@app.route('/pyro/', methods=['GET'])
def getAll():
	ret = dict()
	ar=[]
	for id in threads:
		ret[id] = {'uri':threads[id][PYRO], 'statusMessage':"OK"}
		
	return Response(json.dumps(ret),mimetype='application/json')
@app.errorhandler(Exception)
def handle_error(e):
	code = 500
	if isinstance(e, HTTPException):
		code = e.code
	print(e)
	return  Response(json.dumps({'statusMessage':str(e)}),mimetype='application/json'),code

if 'PYTHON_PATH' in os.environ:
	PYTHON_PATH = os.environ['PYTHON_PATH']
	
if 'ADAPTER_PATH' in os.environ:
	ADAPTER_PATH = os.environ['ADAPTER_PATH']
if __name__ == '__main__':
    app.run(debug=True)
    app.run(debug=True)