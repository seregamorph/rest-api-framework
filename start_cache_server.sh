# After running first time, open http://localhost:9080/ in browser and create new folder named "build-cache"
docker run -d \
     -e WEBDAV_USERNAME=admin \
     -e WEBDAV_PASSWORD=admin \
     -p 9080:80 \
     -v $(pwd)/.mvn/remote-cache:/var/webdav/public \
     --name cache-server \
     xama/nginx-webdav

