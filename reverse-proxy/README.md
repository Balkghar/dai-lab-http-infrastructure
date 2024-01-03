## Reverse proxy

A reverse proxy is configured to route traffic to multiple services located at different URLs. This allows the project to use a single entry point, the reverse proxy, to manage requests targeting different services running on different hosts or containers. This setup is beneficial for security by providing isolation, SSL capabilities and hiding backend servers from outside networks.

### Traefik

Traefik intercepts and routes every incoming request to the corresponding backend service. In the case of this project, that means the staic website and the API.

The configuration is done in the [docker-compose](./docker-compose.yaml) file. The only pre-requisit is that the containers that will be proxied to must listen on a port. In the case of this project, the static website uses port 80 and for the API uses port 7000. Although Traefik discovers these ports automatically, specific routing must be explicitly configured in the `docker-compose-yaml` file.

Traefik is configured in the following way:

```yaml
reverse-proxy:
    # The official v2 Traefik docker image
    image: traefik:v2.10
    # Enables the web UI and tells Traefik to listen to docker
    command: --api.insecure=true --providers.docker --log.level=DEBUG --accesslog=true
    ports:
      # The HTTP port
      - "80:80"
      # The Web UI (enabled by --api.insecure=true)
      - "8080:8080"
    volumes:
      # So that Traefik can listen to the Docker events
      - /var/run/docker.sock:/var/run/docker.sock
```

The labels that must be set to provide additional configuration for Traefik are as follows:

```yaml
labels:
      # Explicitly tell Traefik to expose this container at the specified path
      - "traefik.enable=true"
      - "traefik.http.routers.api.rule=Host(`localhost`) && PathPrefix(`/api`)"
      - "traefik.http.services.api.loadbalancer.server.port=7000"
```

After configuration, Traefik's dashbord is available at [the default 8080 port](http://localhost:8080).
