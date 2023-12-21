FROM mongo:latest

ARG MONGODB_URI
ARG MONGODB_USERNAME
ARG MONGODB_PASSWORD
ARG MONGODB_HOST

ENV MONGODB_URI ${MONGODB_URI}
ENV MONGODB_USERNAME ${MONGODB_USERNAME}
ENV MONGODB_PASSOWRD ${MONGODB_PASSWORD}
ENV MONGODB_HOST ${MONGODB_HOST}

COPY blog_data.json /blog_data.json
COPY comment_data.json /comment_data.json

CMD mongoimport --authenticationDatabase=admin --username ${MONGODB_USERNAME} --password ${MONGODB_PASSOWRD} --uri ${MONGODB_URI} --collection blogs --drop --jsonArray --file /blog_data.json
CMD mongoimport --authenticationDatabase=admin --username ${MONGODB_USERNAME} --password ${MONGODB_PASSOWRD} --uri ${MONGODB_URI} --collection comments --drop --jsonArray --file /comment_data.json
