#!/bin/sh
echo "window._env_ = { URL: '$APIURL' };" > /usr/share/nginx/html/config.js
exec "$@"

