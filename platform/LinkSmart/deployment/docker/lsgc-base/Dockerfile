FROM ubuntu:14.04
MAINTAINER Alexandr Krylovskiy "alexandr.krylovskiy@fit.fraunhofer.de"
ENV REFRESHED_AT 2014-12-17

# update ubuntu image
RUN apt-get update
RUN apt-get install -y wget unzip openjdk-7-jdk git maven openssl nmap mc nano curl

# install the fraunhofer certificate
RUN wget http://cdp1.pca.dfn.de/fraunhofer-ca/pub/cacert/cacert.pem -O /usr/local/share/ca-certificates/fhg.crt
RUN update-ca-certificates

# configure local m2
RUN mkdir -p /root/.m2/
COPY m2-settings.xml /root/.m2/settings.xml

# set env
ENV JAVA_HOME /usr/lib/jvm/java-7-openjdk-amd64
ENV LSGC_HOME /opt/lsgc
