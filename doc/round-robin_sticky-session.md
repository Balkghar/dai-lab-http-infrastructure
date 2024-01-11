# Round Robin and sticky session

Round robin is a load-balencing strategry that distribute the charge of a service equally between all their container.
Sticky session allow that when a client start to communicate with a certain server (in our case a container), the client keep communicating with until the end of the communication.

## Round Robin with Traefik

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
