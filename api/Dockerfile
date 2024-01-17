# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21-jammy AS build

ARG MONGODB_USERNAME
ARG MONGODB_PASSWORD
ARG MONGODB_HOST

ENV MONGODB_USERNAME ${MONGODB_USERNAME}
ENV MONGODB_PASSOWRD ${MONGODB_PASSWORD}
ENV MONGODB_HOST ${MONGODB_HOST}

WORKDIR /app
COPY . /app

RUN mvn clean install

# Stage 2: Deployment
FROM eclipse-temurin:21-jammy

WORKDIR /app
COPY --from=build /app/target/*-SNAPSHOT-jar-with-dependencies.jar /app/app.jar
EXPOSE 7000
ENTRYPOINT java -jar /app/app.jar