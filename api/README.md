# Blog API

This API is a simple RESTful service for managing blog posts and related comments. It is built using
the [Javalin](https://javalin.io/) framework and
a [MongoDB](https://www.mongodb.com/) database. This documents focuses solely on the API itself. For a more general
overview of the project, please refer to the [main README](../README.md).

## Features

- Create, read, update and delete blog posts.
- Create, read, update, and delete comments on blog posts.
- Resources are identified by UUIDs.

## Endpoints

### Blogs

The `/api/blogs` route supports the CRUD operations for blogs. The following endpoints are available:

- `GET /api/blogs`: Retrieve all blogs.
- `GET /api/blogs/{id}`: Retrieve a blog by its ID.
- `POST /api/blogs`: Create a new blog. This endpoint requires a JSON body with the blog `title` and `content` fields
  set appropriately.
- `PATCH /api/blogs/{id}`: Update a blog by its ID. This endpoint requires a JSON body with the updated blog details.
- `DELETE /api/blogs/{id}`: Delete a blog by its ID. This will also erase all comments related to the blog.

### Comments

The `/api/blogs/{id}/comments` route supports the CRUD operations for comments on a blog. The following endpoints are
available:

- `GET /api/blogs/{id}/comments`: Retrieve all comments on a blog.
- `GET /api/blogs/{id}/comments/{id}`: Retrieve a comment on a blog by its ID.
- `POST /api/blogs/{id}/comments`: Create a new comment on a blog. This endpoint requires a JSON body with
  the `_blogId`, `author` and `content` fields set appropriately.
- `PATCH /api/blogs/{id}/comments/{id}`: Update a comment on a blog by its ID. This endpoint requires a JSON body
  with the updated comment details.
- `DELETE /api/blogs/{id}/comments/{id}`: Delete a comment on a blog by its ID.

## Usage

### Pre-requisites

- Java 21 and Maven 3.9.1 to build the project.
- A MongoDB database to store the application data. You can run `docker run mongo:latest` to get a fresh instance of
  MongoDB going. Refer to the [official guide](https://www.mongodb.com/compatibility/docker) for further instructions.
- Setting the environment variables in your IDE/terminal. If you want to use Docker, you should copy the `.env.example`
  file to `.env` and modify it.

### Setup

You can run the API either locally or using Docker.

#### Standalone

The API can be run as a standalone application. To do so, follow these steps:

1. Navigate to the `api` directory.
2. Run `mvn clean install` to build the project.
3. Run `java -jar target/httpapi-1.0-SNAPSHOT-jar-with-dependencies.jar` to start the server.
4. The API is now available at [http://localhost:7000](http://localhost:7000).

If you need to specify the environment variables on the command line, you can do so in the following way :

```shell
MONGO_INITDB_ROOT_USERNAME=root          \
MONGO_INITDB_ROOT_PASSWORD=password      \
MONGO_INITDB_DATABASE=dai                \
MONGO_INITDB_ROOT_HOST=mongodb:27017     \
DATABASE_URI=mongodb://mongodb:27017/dai \
java -jar target/httpapi-1.0-SNAPSHOT-jar-with-dependencies.jar
```

#### Docker

The API can also be run as a Docker container. To do so, follow these steps:

1. Navigate to the `api` directory.
2. Run `docker build . -t dai-lab-http/api` to build the Docker image.
3. Run `docker run -p 7000:7000 dai-lab-http/api` to start the container.
4. The API is now available at [http://localhost:7000](http://localhost:7000).

#### Docker compose

The project uses docker-compose to deploy the different components based on their respective Dockerfiles.
The stack is named `dai-lab-http`. The [docker-compose](../docker-compose.yaml) file is located at the root of the
repository.

To run the whole stack including the API, do the following:

1. Navigate to the project root directory.
2. Run `docker compose up -d --build` to build and run the stack.
3. The API is now available at [http://api.traefik.me](http://api.traefik.me).

## Examples

To create a blog, send a POST request to `/api/blogs` with the following JSON body:

```json
{
  "title": "My Blog",
  "content": "This is my blog."
}
```

To create a comment, send a POST request to `/api/blogs/{id}/comments` with `{id}` being the UUID of the targeted blog
with the following JSON body (don't forget to also specify the `blogId` in the request body):

```json
{
  "blogId": "123",
  "content": "This is a comment."
}
```

Further example requests can be found in the [demo](./demo) folder.

## Error Handling

The API uses HTTP status codes to indicate the success or failure of a request. In case of an error, a JSON response is
returned with a message detailing the error.

## Database

The application uses MongoDB as its primary database, which is containerized using Docker. The MongoDB instance is
configured through environment variables.
The data is persisted across restarts using a Docker volume. The database is seeded
with [dummy data](../db/blog_data.json) using another Docker container.