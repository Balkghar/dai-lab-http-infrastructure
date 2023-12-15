# dai-lab-http-infrastructure

## Static Website

See at [frondend](./frontend/README.md) for the documentation of the static website.

## Docker compose

To use the docker-compose file, please run this command (-d is for detached):

```console
docker compose up -d
```

To stop it, use this command:

```console
docker compose down
```

Content of the docker-compose:

```yaml
services:
  web:
    image: web-image # name of the images
    build: ./frontend # indicate the folder where the Dockerfile is
    ports:
      - "8080:80" # the port to expose
```

