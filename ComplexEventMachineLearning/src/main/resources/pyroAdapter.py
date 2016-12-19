"""
 Created by Farshid Tavakolizadeh on 19.12.2016
 PyroAdapter exposes an external learning module via Pyro4
"""
import Pyro4
import sys
import imp
import os

backendScript = ""

class PyroAdapter(object):

    @Pyro4.expose
    def build(self, classifier):
        print("backendScript: %s" % backendScript)
        # Add directory of backend script to resolve local modules with relative paths
        backendDir = os.path.dirname(backendScript)
        sys.path.append(backendDir)

        backend = imp.load_source("Agent", backendScript)
        self.agent = backend.Agent()
        self.agent.build(classifier)

    @Pyro4.oneway
    def learn(self, datapoint):
        self.agent.learn(datapoint)

    @Pyro4.expose
    def predict(self, datapoint):
        return self.agent.predict(datapoint)


if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Missing arguments.")
        sys.exit(2)

    # Get the path to backend script
    backendScript = sys.argv[1]
    # Start Pyro
    Pyro4.config.SERIALIZER = 'pickle'
    daemon = Pyro4.Daemon()
    uri = daemon.register(PyroAdapter)
    print(uri)
    daemon.requestLoop()
