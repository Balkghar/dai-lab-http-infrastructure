#!/bin/sh
echo "window._env_ = { URL: '$URL' };" > /usr/share/nginx/html/config.js
exec "$@"

