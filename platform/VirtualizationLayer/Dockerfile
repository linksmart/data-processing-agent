FROM node:4.1.2

VOLUME /logs
VOLUME /config
RUN mkdir -p /opt/prs/app
WORKDIR /opt/prs/app

COPY . /opt/prs/app
RUN npm build

ENV NODE_ENV=production

ENV INSTANCE_NAME=WaterManagementInstance

ENV VL_SCHEME=http
ENV VL_HOST=localhost
ENV VL_PORT=80

ENV LOG_LEVEL=debug

CMD [ "npm", "start" ]

EXPOSE 80
