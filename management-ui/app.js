const express = require('express');
const Docker = require('dockerode');
const path = require('path');
const bodyParser = require('body-parser');
const exec = require('child_process').exec;
const app = express();
const docker = new Docker();
const {env} = require('process');

const PORT = process.env.PORT || 3000;
const COMPOSE_PROJECT_NAME = env.COMPOSE_NAME || 'dai-lab-http';
const COMPOSE_SERVICES = process.env.COMPOSE_SERVICES.split(',');
const COMPOSE_MAX_SCALE = process.env.COMPOSE_MAX_SCALE || 10;

app.set('view engine', 'ejs');
app.set('views', path.join(__dirname, 'views'));
app.use(express.static(path.join(__dirname, 'public')));
app.use(bodyParser.json());

// Check if the service name is in the allowed list.
const isValidServiceName = service => {
    return COMPOSE_SERVICES.includes(service);
}

// Render the view with the list of containers and services.
app.get('/', async (req, res) => {
    // FIXME: reimplement this using docker socket
    try {
        const containers = await docker.listContainers({all: true});
        const services = containers
            .reduce((acc, container) => {
                const serviceName = container.Labels['com.docker.compose.service'];
                if (serviceName) {
                    acc[serviceName] = (acc[serviceName] || 0) + 1;
                }
                return acc;
            }, {});

        res.render('view', {composeProjectName: COMPOSE_PROJECT_NAME, containers, services});
        containers.forEach(container => console.log(container));
    } catch (error) {
        console.error(error);
        res.status(500).send('An error occurred while fetching the containers.');
    }
});

// Start a service.
app.post('/api/start', (req, res) => {
    const {serviceName} = req.body;
    if (!isValidServiceName(serviceName)) {
        console.error(`Invalid service name: ${serviceName}`);
        return res.status(400).send(`Invalid service name: ${serviceName}`);
    }

    exec(`docker compose -p ${COMPOSE_PROJECT_NAME} start ${serviceName}`, (err, stdout, stderr) => {
        if (err) {
            console.error(`Error: ${err}`);
            return res.status(500).send('An error occurred while starting the service.');
        }

        console.info(`Started service '${serviceName}'.`);
        res.status(200).send(`Started service '${serviceName}'.`);
    });
});

// Stop a service.
app.post('/api/stop', (req, res) => {
    const {serviceName} = req.body;
    if (!isValidServiceName(serviceName)) {
        console.error(`Invalid service name: ${serviceName}`);
        return res.status(400).send(`Invalid service name: ${serviceName}`);
    }

    // Execute docker compose stop <serviceName>
    exec(`docker compose -p ${COMPOSE_PROJECT_NAME} stop ${serviceName}`, (err, stdout, stderr) => {
        if (err) {
            console.error(`Error: ${err}`);
            return res.status(500).send('An error occurred while stopping the service.');
        }

        console.info(`Stopped service '${serviceName}'.`);
        res.status(200).send(`Stopped service '${serviceName}'.`);
    });
});

// Scale a service.
app.post('/api/scale', async (req, res) => {
    const {serviceName, scaleNumber} = req.body;
    if (!isValidServiceName(serviceName)) {
        console.error(`Invalid service name: ${serviceName}`);
        return res.status(400).send(`Invalid service name: ${serviceName}`);
    } else if (scaleNumber < 0 || scaleNumber > COMPOSE_MAX_SCALE) {
        console.error(`Invalid scale number: ${scaleNumber}`);
        return res.status(400).send(`Invalid scale number: ${scaleNumber}`);
    }

    // Execute docker compose scale <serviceName>=<scaleNumber>
    exec(`docker compose -p ${COMPOSE_PROJECT_NAME} up --scale ${serviceName}=${scaleNumber}`, (err, stdout, stderr) => {
        if (err) {
            console.error(`Error: ${err}`);
            return res.status(500).send('An error occurred while scaling the service.');
        }

        console.info(`Scaled service '${serviceName}' to ${scaleNumber}.`);
        res.status(200).send(`Scaled service '${serviceName}' to ${scaleNumber}.`);
    });
});

// Start the server.
app.listen(PORT, () => {
    console.log(`Docker management UI for infrastructure ${COMPOSE_PROJECT_NAME} running on port ${PORT}`);
});
