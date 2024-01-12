FROM node:20-alpine

ARG MGMT_PORT
ARG MGMT_URL
ARG COMPOSE_NAME
ARG COMPOSE_SERVICES
ARG COMPOSE_MAX_SCALE

ENV COMPOSE_NAME ${COMPOSE_NAME}
ENV COMPOSE_SERVICES ${COMPOSE_SERVICES}
ENV COMPOSE_MAX_SCALE ${COMPOSE_MAX_SCALE}
ENV MGMT_PORT ${MGMT_PORT}
ENV MGMT_URL ${MGMT_URL}

WORKDIR /usr/src/app
COPY . .
RUN npm install && npm run build
COPY --from=library/docker:latest /usr/local/bin/docker /usr/bin/docker
COPY --from=docker/compose:latest /usr/local/bin/docker-compose /usr/bin/docker-compose
EXPOSE 3000
CMD [ "npm", "start" ]