#!/bin/sh

#git clone https://${SERVER_USERNAME}:${SERVER_PASSWORD}@code.linksmart.eu/scm/${REPO} -b ${branch} code

#cd code

if [ -z "${URL}" ]; then
	URL="http://dpa:8319/v2/api-docs?group=LinkSmart%20(R)%20IoT%20Learning%20Agent"
fi
if [ -z "${FILE}" ]; then
	FILE=api-docs.json
fi
echo "" > ${FILE}

echo "calling endpoint ${URL} storeging in ${FILE} ...";
curl -f --stderr err "${URL}" | jq '.' > ${FILE};
cat err;

until [ -s ${FILE} ]; do
	sleep 1;
    echo "calling endpoint ${URL} storeging in ${FILE} ...";
	curl -f --stderr err "${URL}" | jq '.' > ${FILE};
	cat err;
done;

git add ${FILE}
git commit -am "[skip travis] AUTOMATIC COMMIT: updating Open API"
git push