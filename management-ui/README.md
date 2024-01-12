# Docker Management UI

> ️⚠️ This application is only a proof-of-concept. Do NOT use it in production.

## Getting started

1. Clone this repository.
2. Copy and edit the .env file.
3. Navigate to the `management-ui` directory.
4. Run `npm run build && npm run start`.
5. The application is now available!

## Tech stack

This application was mainly built using the Express.js and Dockerode packages.

- [Express](https://expressjs.com/) handles the HTTP and WebSocket aspects of the application.
- [Dockerode](https://github.com/apocas/dockerode) is used to communicate with the Docker host in order to start and
  stop containers.
- [EJS](https://ejs.co/) is used for templating.
- [TailwindCSS](https://tailwindcss.com/) is used for styling.

The frontend is a single HTML page augmented by some JavaScript. It makes use of the `fetch` and `WebSocket` APIs.

Dockerode doesn't support docker compose and recommends to use docker swarm instead to handle production
infrastructures. It has been decided to **run docker compose commands** via the node `exec` function instead. This
exposes the application to serious security risks.

## Docker

The application is ready to go as a Docker container. Run the following commands to build and run the container:

```bash
docker build . -t dai-lab-http/management-ui
docker run -v /var/run/docker.sock:/var/run/docker.sock dai-lab-http/management-ui
```

> 💡 The host's Docker socket must be mounted as a volume for the UI to be able to control the infrastructure.

A docker compose infrastructure must be present on the machine and the environment variables must be correctly set for the container to work as a stand-alone.

## Docker compose

The application is built to work with a docker compose infrastructure. The minimum configuration is the following:

```yaml
management-ui:
  env_file:
    - .env
  build:
    context: ./management-ui
    dockerfile: Dockerfile
    args:
      - COMPOSE_PROJECT_NAME=$COMPOSE_PROJECT_NAME
      - COMPOSE_SERVICES=$COMPOSE_SERVICES
      - COMPOSE_MAX_SCALE=$COMPOSE_MAX_SCALE
      - PORT=$MGMT_PORT
      - URL=$MGMT_URL
  volumes:
    - /var/run/docker.sock:/var/run/docker.sock
```