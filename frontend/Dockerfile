FROM node:20 AS build
WORKDIR /src
COPY ./src/package.json .
RUN npm install
COPY ./src .
RUN npm run build

FROM nginx
COPY --from=build /src/static /usr/share/nginx/html
COPY conf /etc/nginx
VOLUME /usr/share/nginx/html
VOLUME /etc/nginx