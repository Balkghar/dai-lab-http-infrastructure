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

### Setup

You can run the API either locally or in a docker container. Note that the server requires the
[environment variables](../.env.example) to be set in order to connect to the database.

1. Clone the repository.
2. Navigate to the project directory.
3. Run `mvn clean install` to build the project.
4. Run `java -jar target/httpapi-1.0-SNAPSHOT-jar-with-dependencies.jar` to start the server.

If you need to specify the environment variables on the command line, you can do so in the following way :

```console
MONGO_INITDB_ROOT_USERNAME=root          \
MONGO_INITDB_ROOT_PASSWORD=password      \
MONGO_INITDB_DATABASE=dai                \
MONGO_INITDB_ROOT_HOST=mongodb:27017     \
DATABASE_URI=mongodb://mongodb:27017/dai \
java -jar target/httpapi-1.0-SNAPSHOT-jar-with-dependencies.jar
```

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