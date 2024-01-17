# Scalability and load balacing

## Scalability

Scalability allows to add or remove resources dynamically to meet a varying demand and allow for reliability and performance. This application uses a horizontal type of scaling where the workload is distributed between multiple instances of the same service. This achieved using docker and Traefik together.

The docker compose configuration accepts the `replicas` attribute which specifies the initial amount of clones of a service that should be created. New instances of each service may then be created by using the command `docker compose up -d --scale <instance_name>=<count>`. Note that the docker services can not use the `container_name` attributes if they want to have replicas.

Excrept from the [docker-compose](../docker-compose.yaml) configuration:

```yaml
api:
  labels:
    - "traefik.enable=true"
    - "traefik.http.routers.api.rule=Host(`localhost`) && PathPrefix(`/api`)"
    - "traefik.http.services.api.loadbalancer.server.port=7000"
  deploy:
    replicas: 5
```

Additionnaly, you can adapt the number of replicas from the CLI, like that:
```bash
 docker compose up --scale web=2 -d
```
The field `web` is the service to scale.

Traefik will then automatically detect the multiple instances of a same service.

## Load balancing

By default the load balancer is enabled for each service of Traefik.

> The Services are responsible for configuring how to reach the actual services that will eventually handle the incoming requests.
> -- <cite>[Traefik documentation](https://doc.traefik.io/traefik/routing/services/)</cite>

The load balancer will distribute evenly the load of the work between each replicas of the container.

Round robin is default strategy of the load balancer, currently it is the only one supported by Traefik.

The Traefik logs should display something similar to this for the web:

```log
172.21.0.1 - - [04/Jan/2024:14:34:19 +0000] "GET / HTTP/1.1" 304 0 "-" "-" 145 "static@docker" "http://172.21.0.11:80" 0ms
172.21.0.1 - - [04/Jan/2024:14:34:19 +0000] "GET / HTTP/1.1" 304 0 "-" "-" 146 "static@docker" "http://172.21.0.8:80" 0ms
172.21.0.1 - - [04/Jan/2024:14:34:20 +0000] "GET / HTTP/1.1" 304 0 "-" "-" 147 "static@docker" "http://172.21.0.5:80" 0ms
```

and this for the api:

```log
172.21.0.1 - - [04/Jan/2024:14:35:09 +0000] "GET /api/blogs/ HTTP/1.1" 200 939 "-" "-" 148 "api@docker" "http://172.21.0.14:7000" 241ms
172.21.0.1 - - [04/Jan/2024:14:35:12 +0000] "GET /api/blogs/ HTTP/1.1" 200 939 "-" "-" 149 "api@docker" "http://172.21.0.6:7000" 213ms
172.21.0.1 - - [04/Jan/2024:14:35:12 +0000] "GET /api/blogs/ HTTP/1.1" 200 939 "-" "-" 150 "api@docker" "http://172.21.0.9:7000" 204ms
172.21.0.1 - - [04/Jan/2024:14:35:13 +0000] "GET /api/blogs/ HTTP/1.1" 200 939 "-" "-" 151 "api@docker" "http://172.21.0.13:7000" 217ms
```

The load balancer is working because we can see that each time a resources is requested, the IP were it forwads changed. This is the round robin strategy where it simply forward the request to a different replicas.
