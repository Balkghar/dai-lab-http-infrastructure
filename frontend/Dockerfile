FROM node:20 AS build
WORKDIR /src
COPY ./src .
RUN npm install && npm run build

FROM nginx
COPY --from=build /src/static /usr/share/nginx/html
COPY conf /etc/nginx
VOLUME /usr/share/nginx/html
VOLUME /etc/nginx