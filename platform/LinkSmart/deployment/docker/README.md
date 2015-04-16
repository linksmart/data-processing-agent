# Environment
  
  Scripts tested with Docker 1.3.2.
  Docker 1.0.1 wont work.

# Building
```
the base lsgc docker image:
docker build --rm -t lsgc/base lsgc-base


the latest lsgc distribution docker image:

For the LSGC-distribution enter the lsgc-distribution folder and run the build.sh script. It will grab the newest 
distribution binary from the artifact server, then generate a Dockerfile and create a docker image.

the zmq supernode docker image:
docker run  -d -p 1099:1099 -p 8082:8082 -p 8181:8181 --name="lsgc-distribution" lsgc/distribution

```

# Running
```
docker run -d -p 7000:7000 -p 7001:7001 --name="lsgc-zmq-supernode" lsgc/zmq-supernode

or

docker run  -d -p 1099:1099 -p 8082:8082 -p 8181:8181 --name="lsgc-distribution" lsgc/distribution
```
