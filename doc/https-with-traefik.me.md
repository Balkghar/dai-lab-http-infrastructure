# HTTPS with traefik.me

[tls.yaml](../tls.yml)

```yaml
tls:
  stores:
    default:
      defaultCertificate:
        certFile: /etc/ssl/traefik/cert.pem
        keyFile: /etc/ssl/traefik/privkey.pem
  certificates:
    - certFile: /etc/ssl/traefik/cert.pem
      keyFile: /etc/ssl/traefik/privkey.pem
```

[traefik.yaml](../traefik.yml)

```yaml
logLevel: INFO

api:
  insecure: true
  dashboard: true

entryPoints:
  http:
    address: ":80"
  https:
    address: ":443"

providers:
  file:
    filename: /etc/traefik/tls.yml
  docker:
    endpoint: unix:///var/run/docker.sock
    watch: true
    exposedByDefault: true
    defaultRule: "HostRegexp(`{{ index .Labels \"com.docker.compose.service\"}}.traefik.me`,`{{ index .Labels \"com.docker.compose.service\"}}-{dashed-ip:.*}.traefik.me`)"
```

service to download certs
[docker-compose.yaml](../docker-compose.yaml)

```yaml
  reverse-proxy-https-helper:
    image: alpine
    command: sh -c "cd /etc/ssl/traefik && wget traefik.me/cert.pem -O cert.pem && wget traefik.me/privkey.pem -O privkey.pem"
    volumes:
      - certs:/etc/ssl/traefik
```

link to access certificat
[docker-compose.yaml](../docker-compose.yaml)

```yaml
volumes:
      ...
      - ./traefik.yml:/etc/traefik/traefik.yml
      - ./tls.yml:/etc/traefik/tls.yml
      - certs:/etc/ssl/traefik
```

labels to redirect http to https
[docker-compose.yaml](../docker-compose.yaml)

```yaml
    labels:
      ...
      - "traefik.http.middlewares.https_redirect.redirectscheme.scheme=https"
      - "traefik.http.middlewares.https_redirect.redirectscheme.permanent=true"
      - "traefik.http.routers.http_catchall.rule=HostRegexp(`{any:.+}`)"
      - "traefik.http.routers.http_catchall.entrypoints=http"
      - "traefik.http.routers.http_catchall.middlewares=https_redirect"
```

volume to store certificats
[docker-compose.yaml](../docker-compose.yaml)

```yaml
volumes:
  certs:
```
