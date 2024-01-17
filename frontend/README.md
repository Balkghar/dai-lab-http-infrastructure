# Static website

This is a static website containing a few notes.

## Build

First you need to build the docker images with this command:

```bash
docker build -t dai/frontend .
```

Then you can run the images:

```bash
docker run -d -p 80:80 dai/frontend
```

The images is build in two stages. This method is called [multi-stage builds](https://docs.docker.com/build/building/multi-stage/).
The first stage uses [Node.js](https://nodejs.org/en) to build the CSS for the website using [TailwindCSS](https://tailwindcss.com/). Through PostCSS, Tailwind tailors the final stylesheet to include only what actually is used in the [index.css](./src/index.css) file. This build stage also copies all the files to the `static` folder.

The second step creates an Nginx container and statically serves the documents from the `static` folder.

## Configuration

The configuration of Nginx can be modified at [nginx.conf](./conf/nginx.conf).
Tailwind's configuration is at [tailwind.config.js](./src/tailwind.config.js), see at [TailwindCSS doc](https://v2.tailwindcss.com/docs).

## Code

There is a [index.html](./src/index.html), but it is only the base for the website. All the html balise are constructed from the [script.js](./src/script.js) file.
The script.js execute all the command to the API, from the fetch to the create or even the delete. It dynamically adapts the current content of the website when a request is sent to the API.
