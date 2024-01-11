# Round Robin and sticky session

There is nothing to change at the previous configuration because round robin is the default strategy of the load balancer of Traefik. See at [Traefik documentation](https://doc.traefik.io/traefik/routing/services/#load-balancing).

To configure the sticky session for a Traefik service, we only need to add two lines at the [docker-compose.yaml](../docker-compose.yaml) at the labels of the service API.

```yaml
   labels:
      ...
      - "traefik.http.services.api.loadBalancer.sticky.cookie=true"
      - "traefik.http.services.api.loadBalancer.sticky.cookie.name=api"
```

The first line indicate that we enable the sticky session and the second specify the name of the cookie.

See at [Traefik documentation](https://doc.traefik.io/traefik/routing/services/#sticky-sessions) for documentation.

The configuration can be verified via the logs of the Traefik container:

```bash
$ docker logs dai-lab-http-reverse-proxy-1
...
172.27.0.1 - - [11/Jan/2024:14:23:34 +0000] "GET / HTTP/1.1" 200 308 "-" "-" 1 "static@docker" "http://172.27.0.10:80" 0ms
172.27.0.1 - - [11/Jan/2024:14:23:34 +0000] "GET /script.js HTTP/1.1" 304 0 "-" "-" 2 "static@docker" "http://172.27.0.12:80" 0ms
172.27.0.1 - - [11/Jan/2024:14:23:34 +0000] "GET /script.js HTTP/1.1" 200 2717 "-" "-" 3 "static@docker" "http://172.27.0.8:80" 0ms
172.27.0.1 - - [11/Jan/2024:14:23:34 +0000] "GET /favicon.ico HTTP/1.1" 404 153 "-" "-" 4 "static@docker" "http://172.27.0.5:80" 1ms
...
# and for the API
172.27.0.1 - - [11/Jan/2024:14:24:12 +0000] "GET /api/blogs/ HTTP/1.1" 200 939 "-" "-" 5 "api@docker" "http://172.27.0.11:7000" 220ms
172.27.0.1 - - [11/Jan/2024:14:24:14 +0000] "GET /api/blogs/ HTTP/1.1" 200 939 "-" "-" 6 "api@docker" "http://172.27.0.11:7000" 6ms
```

> (⚠️) the logs of Traefik needs to be activated

The first lines use a different container for each request (for the static website) and the second lines indicate that all the communication are done with the same container.
