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

A [example](.env.example) `.env` file is provided with default values for the project.

## Docker compose

The project uses docker-compose to deploy the different components based on their respective Dockerfiles. 
The [docker-compose](./docker-compose.yaml) file is located at the root of the project.

The following services are defined:

- `web`: the static website. Runs on port 80.
- `mongo`: the MongoDB database.
- `mongo-seed`: a service that seeds the database with some initial data.
- `api`: the RESTful API. Runs on port 7000.

To run the project while rebuilding the images, use this command:

```console
docker compose up -d --build
```

To stop the stack, use the following command:

```console
docker compose down
```