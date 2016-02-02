FROM node:latest

VOLUME /logs
VOLUME /config
RUN mkdir -p /opt/prs/app
WORKDIR /opt/prs/app

COPY . /opt/prs/app
RUN npm build

ENV NODE_ENV=production \
    INSTANCE_NAME=WaterManagementInstance \
    VL_SCHEME=http \
    VL_HOST=localhost \
    VL_PORT=80

ENV VL_PUBLIC_URL="" \
    MQTT_BROKER_URL=mqtt://localhost/

ENV NETWORK_MANAGER_URL=http://localhost:8181 \
    RESOURCE_CATALOGUE_URN=urn:schemas-upnp-org:IoTdevice:OGCapplicationIoTresourcemanager:1 \
    RESOURCE_CATALOGUE_URL=http://localhost:44441/

ENV SCRAL_URL=http://localhost:8080/connectors.rest/ \
    SCRAL_UI_URL=http://localhost:8080/gui/ \
    STORAGE_MANAGER_UTL=http://cnet006.cloudapp.net/Dmf/SensorThings/ \
    DFM_URL=http://localhost:8319/ \
    SANTANDER_URL=http://data.smartsantander.eu/ISMB/

ENV LOG_LEVEL=debug

CMD [ "npm", "start" ]

EXPOSE 80
