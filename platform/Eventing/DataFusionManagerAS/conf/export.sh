#!/bin/bash -e

sudo foreman export upstart /etc/init/ -f Procfile.deploy --app=DataFusionService --user=almanac