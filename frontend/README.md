# Static website

This is a static website containing a few notes.

## Setup

First you need to build the docker images with this command:

```console
docker build -t web-image .
```

Then you can run the images:

```console
docker run -d -p 80:80 web-image
```

If you want to change the images' name, modifiy the argument "web-image" on the building command.
If you want to change the content of the website, feel free to modifiy the content of the [src](./src/) folder.
The configuration of NGINX can be modified at [nginx.conf](./conf/nginx.conf).
