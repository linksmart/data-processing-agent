"""
 Created by Farshid Tavakolizadeh on 19.12.2016
 PyroAdapter exposes an external learning module via Pyro4
"""
import Pyro4
import sys, imp, os, logging
from optparse import OptionParser

logging.basicConfig(format='%(asctime)s %(levelname)s %(name)s: %(message)s', level=logging.DEBUG)
logger = logging.getLogger(__file__)

class PyroAdapter(object):
    def __init__(self):
        logger.info("Initializing Pyro object.")
        # TODO: move this to build(...) to support different backends on one pyro server
        try:
            module = imp.load_source(OPTIONS.bname, OPTIONS.bpath)
            self.backend = getattr(module, OPTIONS.bname)()
        except Exception as e:
            logger.error(e)

    @Pyro4.expose
    def build(self, classifier):
        return self.backend.build(classifier)

    @Pyro4.expose
    def learn(self, datapoint):
        self.backend.learn(datapoint)

    @Pyro4.expose
    def predict(self, datapoint):
        return self.backend.predict(datapoint)

    @Pyro4.expose
    def batchLearn(self, datapoints):
        self.backend.batchLearn(datapoints)

    @Pyro4.expose
    def batchPredict(self, datapoints):
        return self.backend.batchPredict(datapoints)

    @Pyro4.expose
    def destroy(self):
        self.backend.destroy()

    @Pyro4.expose
    def exportModel(self):
        return self.backend.exportModel()

    @Pyro4.expose
    def importModel(self, model):
        self.backend.importModel(model)


def startPyro(options):
    Pyro4.config.SERIALIZER = 'pickle'
    daemon = Pyro4.Daemon(host=options.host, port=options.port, nathost=options.nathost, natport=options.natport)
    uri = daemon.register(PyroAdapter)
    # e.g. uri: PYRO:obj_73fcc95930ed45caacba17be6bdbce74@localhost:43210
    print(uri)  # NOTE: This is read by the parent process.

    if options.nameserver:
        try:
            ns = Pyro4.locateNS()
            ns.register(options.rname, uri)
        except Exception as e:
            logger.error(e)
            raise SystemExit

    daemon.requestLoop()


def parseArgs():
    mandatoryArgs = ['bname', 'bpath']
    parser = OptionParser()
    parser.add_option("--bname", help="name of backend module")
    parser.add_option("--bpath", help="path to backend module (python script)")
    parser.add_option("--host", default="localhost", help="hostname to bind server on")
    parser.add_option("--port", type="int", default=0, help="port to bind server on (0=random)")
    parser.add_option("--nathost", help="the external host name to use in case of NAT")
    parser.add_option("--natport", type="int", help="the external port use in case of NAT")
    parser.add_option("--ns", dest="nameserver", action="store_true", default=False, help="register the server into pyro nameserver")
    parser.add_option("--rname", default="python-agent-0", help="name used for registration into pyro nameserver")
    options, args = parser.parse_args()
    # check mangatory args
    for opt in mandatoryArgs:
        if not getattr(options, opt):
            logger.error("Argument `{}` not given.".format(opt))
            parser.print_help()
            sys.exit(2)

    return options


def main(options):
    # Add directory of backend script to resolve local modules with relative paths
    backendDir = os.path.dirname(options.bpath)
    sys.path.append(backendDir)

    global OPTIONS
    OPTIONS = options

    startPyro(options)


if __name__ == "__main__":
    main(parseArgs())