"""
 Created by Farshid Tavakolizadeh on 19.12.2016
 PyroAdapter exposes an external learning module via Pyro4
"""
import Pyro4
import sys
import imp
import os

class PyroAdapter(object):
    @Pyro4.expose
    def init(self, backend):
        print("backend: %s" % backend)
        # Add directory of backend script to resolve local modules with relative paths
        backendDir = os.path.dirname(backend["script"])
        sys.path.append(backendDir)

        module = imp.load_source(backend["name"], backend["script"])
        self.backend = getattr(module, backend["name"])()

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

    # def export(self):
    #
    # def import(self, model):


# Start Pyro
Pyro4.config.SERIALIZER = 'pickle'
daemon = Pyro4.Daemon()
uri = daemon.register(PyroAdapter)
print(uri)
daemon.requestLoop()
