FROM node:20 AS build
WORKDIR /src
COPY ./src/package.json .
RUN npm install
COPY ./src .
RUN npm run build

FROM nginx
ENV URL="https://api.traefik.me"
COPY --from=build /src/static /usr/share/nginx/html
COPY conf /etc/nginx
VOLUME /usr/share/nginx/html
VOLUME /etc/nginx
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh
ENTRYPOINT ["/entrypoint.sh"]
CMD ["nginx", "-g", "daemon off;"]