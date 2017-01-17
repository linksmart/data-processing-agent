"""
 Created by Farshid Tavakolizadeh on 19.12.2016
 PyroAdapter exposes an external learning module via Pyro4
"""
import Pyro4
import sys, imp, os, getopt


class PyroAdapter(object):
    def __init__(self):
        self.backend = backendModule

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


def startPyro(args):
    Pyro4.config.SERIALIZER = 'pickle'
    daemon = Pyro4.Daemon()
    uri = daemon.register(PyroAdapter)
    if (args["nameserver"]):
        try:
            ns = Pyro4.locateNS()
            ns.register("learning-agent", uri)
        except Exception as e:
            print("Exception: {}".format(e))
            raise SystemExit

    # e.g. uri: PYRO:obj_73fcc95930ed45caacba17be6bdbce74@localhost
    print(uri)  # NOTE: This is read by the parent process.
    daemon.requestLoop()


def parseArgs(argv):
    mandatoryArgs = ['bname', 'bpath']
    parsed = {'bname': None, 'bpath': None, 'nameserver': False}

    def help(exitcode):
        print 'Usage: pyroAdapter.py --bname=<backend-name> --bpath=<backend-path> --ns'
        sys.exit(exitcode)

    try:
        opts, args = getopt.getopt(argv, "h", ["bname=", "bpath=", "ns"])
    except getopt.GetoptError:
        help(2)
    for opt, arg in opts:
        if opt == '-h':
            help(0)
        elif opt == "--bname":
            parsed["bname"] = arg
        elif opt == "--bpath":
            parsed["bpath"] = arg
        elif opt == '--ns':
            parsed["nameserver"] = True

    # check mangatory args
    for k in parsed:
        if k in mandatoryArgs and parsed[k] is None:
            help(2)

    return parsed


def main(args):
    # Add directory of backend script to resolve local modules with relative paths
    backendDir = os.path.dirname(args["bpath"])
    sys.path.append(backendDir)

    module = imp.load_source(args["bname"], args["bpath"])
    global backendModule
    backendModule = getattr(module, args["bname"])()

    startPyro(args)


if __name__ == "__main__":
    main(parseArgs(sys.argv[1:]))
