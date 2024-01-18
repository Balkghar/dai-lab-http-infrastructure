# HTTPS with traefik.me

To configure the https, the [docker-compose.yaml](../docker-compose.yaml) file needs to be adjusted.

First, some labels are added to the reverse-proxy service, the first block redirect all http to https, the second
enables https for the dashboard and the third allows the dashboard to reach the api to collects data.

The labels used in the application are the following:

```yaml
    labels:
      - "traefik.http.middlewares.https_redirect.redirectscheme.scheme=https"
      - "traefik.http.middlewares.https_redirect.redirectscheme.permanent=true"
      - "traefik.http.routers.http_catchall.rule=HostRegexp(`{any:.+}`)"
      - "traefik.http.routers.http_catchall.entrypoints=http"
      - "traefik.http.routers.http_catchall.middlewares=https_redirect"

      - "traefik.http.routers.dashboard.rule=Host(`traefik.traefik.me`)"
      - "traefik.http.routers.dashboard.service=dashboard@internal"
      - "traefik.http.routers.dashboard.tls=true"

      - "traefik.http.routers.api.rule=Host(`traefik.traefik.me`) && PathPrefix(`/api`)"
      - "traefik.http.routers.api.service=api@internal"
      - "traefik.http.routers.api.tls=true"
```

Traefik has to have access to the certificate to use them. There is also two configuration file to configurate the https
on Traefik. The first one is the configuration file for Traefik, the second one is the configuration file for the TLS.
The third volume is where the certificates are stored.

```yaml
volumes:
  ...
  - ./traefik.yml:/etc/traefik/traefik.yml
  - ./tls.yml:/etc/traefik/tls.yml
  - certs:/etc/ssl/traefik
```

This service download the certificates and put them in the certs volume.

```yaml
  reverse-proxy-https-helper:
    image: alpine
    command: sh -c "cd /etc/ssl/traefik && wget traefik.me/cert.pem -O cert.pem && wget traefik.me/privkey.pem -O privkey.pem"
    volumes:
      - certs:/etc/ssl/traefik
```

The volume to store the certificats.

```yaml
volumes:
  certs:
```

The [tls.yml](../reverse-proxy/tls.yml) that configure the path to the certificate for Traefik.

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

The [traefik.yaml](../reverse-proxy/traefik.yml) that enables Traefik to use HTTPS.

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
