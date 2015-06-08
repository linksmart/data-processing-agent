#!/bin/sh
MAVEN_METADATA=maven-metadata.xml
ARTIFACT_NAME="eu.linksmart.gc.distribution.main.deployable"
REPO_URL="https://linksmart.eu/repo/content/repositories/public/eu/linksmart/gc/distribution/eu.linksmart.gc.distribution.main.deployable/0.0.1-SNAPSHOT/"
echo "maven metadata file : $MAVEN_METADATA"
echo "repo url : $REPO_URL"
# retrieve maven metadata to get latest distribution artifact
wget $REPO_URL$MAVEN_METADATA
# extract latest version over xpath
export LSGC_BUILD=$(xmllint --xpath "string(//metadata/versioning/snapshotVersions/snapshotVersion[2]/value)" $MAVEN_METADATA)
echo "current LSGC build: $LSGC_BUILD"
# grab latest binary distribution from artifact server
wget $REPO_URL$ARTIFACT_NAME-$LSGC_BUILD-bin.tar.gz
export LSGC_DIST_FILE=$ARTIFACT_NAME-$LSGC_BUILD-bin.tar.gz
# construct a docker file from template
envsubst '$LSGC_DIST_FILE' < Dockerfile.template > Dockerfile
# create docker image
sudo docker build -rm -t lsgc/distribution .
# clean up temporary files
rm Dockerfile
rm $MAVEN_METADATA
rm $LSGC_DIST_FILE
