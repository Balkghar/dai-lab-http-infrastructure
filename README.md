# dai-lab-http-infrastructure

## Static Website

See at [frontend](./frontend/README.md) for the documentation of the static website.

## Blogs API

The API is a simple RESTful service that allows to create, read, update and delete blog posts and associated comments.
It is written in Java using the [Javalin](https://javalin.io/) framework and uses a [MongoDB](https://www.mongodb.com/)
database.

The API supports the following operations:

- `GET /api/blogs`: retrieve all blogs.
- `GET /api/blogs/{id}`: retrieve a blog by its ID.
- `POST /api/blogs`: create a new blog.
- `PATCH /api/blogs/{id}`: update a blog by its ID.
- `DELETE /api/blogs/{id}`: delete a blog by its ID.
- `GET /api/blogs/{id}/comments`: retrieve all comments on a blog.
- `GET /api/blogs/{id}/comments/{id}`: retrieve a comment on a blog by its ID.
- `POST /api/blogs/{id}/comments`: create a new comment on a blog.
- `PATCH /api/blogs/{id}/comments/{id}`: update a comment on a blog by its ID.
- `DELETE /api/blogs/{id}/comments/{id}`: delete a comment on a blog by its ID.

The API depends on a MongoDB database to store the application data. Some environment variables are also required to be
set in the `.env` file in order to connect to the database.

See the [API documentation](./api/README.md) for a detailed description of the API.

## Environment variables

The following environment variables are required to run the API and the database:

- `MONGO_INITDB_ROOT_USERNAME`: the username of the MongoDB root user.
- `MONGO_INITDB_ROOT_PASSWORD`: the password of the MongoDB root user.
- `MONGO_INITDB_DATABASE`: the name of the database to use.
- `MONGO_INITDB_ROOT_HOST`: the host of the MongoDB database.
- `DATABASE_URI`: the URI of the MongoDB database.
- `COMPOSE_NAME`: the name of the docker compose infrastructure.
- `COMPOSE_SERVICES`: the services that may be managed by the UI.
- `COMPOSE_MAX_SCALE`: the maximum scale of the services of the infrastructure.

A [example](.env.example) `.env` file is provided with default values for the project.

## Docker compose

The project uses docker-compose to deploy the different components based on their respective Dockerfiles.
The stack is named `dai-lab-http`. The [docker-compose](./docker-compose.yaml) file is located at the root of the
project.

The following services are defined:

- `web`: the static website. Runs on port 80. Accessible at [localhost](localhost)
- `mongo`: the MongoDB database. Runs on port 27017.
- `mongo-seed`: a service that seeds the database with some initial data found in the [db](./db) directory.
- `api`: the RESTful API. Runs on port 7000. Accessible at [localhost/api](localhost/api)
- `traefik`: the reverse proxy. Runs on port 8080. Accessible at [localhost:8080](localhost:8080).

To run the project while rebuilding the images, use this command:

```shell
docker compose up -d --build
```

To stop the stack, use the following command:

```shell
docker compose down
```

## Traefik

Traefik is used as a reverse proxy for the project. Further documentation about reverse proxies can be found in
the [related README file](./reverse-proxy/README.md).

The Traefik configuration is the following:

- The static website is attainable at the [localhost](http://localhost) URL.
- The API server is attainable at the [localhost/api](http://localhost/api) URL.

Furthermore, the Traefik dashboard is available at [localhost:8080](http://localhost:8080).

## Scalability and load-balancing

The application is set up to use 5 instances of each service using the `replicas` attribute in
the [docker-compose](./docker-compose.yaml) configuration.

Further information about scalability and load balancing may be found in the [documentation](./reverse-proxy/README.md).

## Round-robin and sticky sessions

Round robin is a load-balencing strategy that distributes the charge of a service equally between all available
instances. Sticky sessions allow a client to always communicate with the same container. Both are implemented using Traefik in the docker-compose file.

See at [round robin and sticky session](./doc/round-robin_sticky-session.md) how to configure it.

## Securing Traefik with HTTPS

[Traefik.me](https://traefik.me) allows to have a valid certificate on a LAN network without the need of generating one.
The dns server of traefik.me resolve all request to *.traefik.me to 127.0.0.1. If you want another to resolve to another
IP adress, you need to add it to the url like this 10.0.0.1.traefik.me.

See [https-with-traefik.me](./doc/https-with-traefik.me.md) for detailed deployment instructions.

## Management UI

The project comes with a homemade management UI. The UI allows to restart and rebuild the infrastructure, start, stop
and scale containers. It also displays container logs using a WebSocket.

The UI is set to be accessible on the `mgmt` subdomain by default. The containers that need to be managed must be
allow-listed in the .env file.

> ⚠️ This project may contain vulnerabilities. Use at your own risk.

More information about the use and configuration of management UI can be found in its
own [README](./management-ui/README.md).