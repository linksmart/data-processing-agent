FROM lsgc/base:latest
MAINTAINER Alexandr Krylovskiy "alexandr.krylovskiy@fit.fraunhofer.de"
RUN mkdir -p ${LSGC_HOME}/zmq-supernode
ADD ZMQ-Supernode-Distribution.zip ${LSGC_HOME}/zmq-supernode/
RUN cd ${LSGC_HOME}/zmq-supernode/ && unzip ZMQ-Supernode-Distribution.zip && chmod a+x ./start.sh
WORKDIR ${LSGC_HOME}/zmq-supernode/
ENTRYPOINT ["./start.sh", "0.0.0.0", 7000, 7001]
EXPOSE 7000
EXPOSE 7001
