FROM node:20-alpine

ARG COMPOSE_NAME
ARG COMPOSE_SERVICES
ARG COMPOSE_MAX_SCALE

ENV COMPOSE_NAME ${COMPOSE_NAME}
ENV COMPOSE_SERVICES ${COMPOSE_SERVICES}
ENV COMPOSE_MAX_SCALE ${COMPOSE_MAX_SCALE}

WORKDIR /usr/src/app
COPY . .
RUN npm install && npm run build
COPY --from=library/docker:latest /usr/local/bin/docker /usr/bin/docker
COPY --from=docker/compose:latest /usr/local/bin/docker-compose /usr/bin/docker-compose
EXPOSE 3000
CMD [ "npm", "start" ]