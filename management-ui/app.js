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
    res.render('index', {composeProjectName: COMPOSE_PROJECT_NAME});
});

// Get the status of the infrastructure.
app.get('/api/status', async (req, res) => {
    docker.listContainers({all: true}, (err, containers) => {
        if (err) {
            console.error(err);
            return res.status(500).send('An error occurred while fetching the containers');
        }
        const status = containers.length === 0 ? 'Stopped' : 'Running';
        res.render('status', {infraStatus: status});
    });
});

// Get the list of services.
app.get('/api/services', async (req, res) => {
    try {
        const containers = await docker.listContainers({all: true});

        // Count the number of instances for each allowed service.
        const services = containers
            .reduce((acc, container) => {
                const serviceName = container.Labels['com.docker.compose.service'];
                if (serviceName && isValidServiceName(serviceName) && container.State === 'running') {
                    acc[serviceName] = (acc[serviceName] || 0) + 1;
                }
                return acc;
            }, {});

        // Add the services that are not running with 0 instances.
        COMPOSE_SERVICES.forEach(service => {
            if (!services[service]) {
                services[service] = 0;
            }
        });

        res.render('services', {services});
    } catch (error) {
        console.error(error);
        res.status(500).send('An error occurred while fetching the services');
    }
});

// Get the list of containers.
app.get('/api/containers', async (req, res) => {
    try {
        const containers = await docker.listContainers({all: true});
        res.render('containers', {containers});
    } catch (error) {
        console.error(error);
        res.status(500).send('An error occurred while fetching the containers');
    }
});

// Start a service.
app.post('/api/start', (req, res) => {
    const {serviceName} = req.body;
    if (!isValidServiceName(serviceName)) {
        console.error(`Invalid service name: ${serviceName}`);
        return res.status(400).send(`Invalid service name: ${serviceName}`);
    }

    // Execute docker compose start <serviceName>
    exec(`docker compose -p ${COMPOSE_PROJECT_NAME} start ${serviceName}`, (err) => {
        if (err) {
            console.error(err);
            return res.status(500).send('An error occurred while starting the service');
        }

        console.info(`Started service '${serviceName}'`);
        res.status(200).send(`Started service '${serviceName}'`);
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
    exec(`docker compose -p ${COMPOSE_PROJECT_NAME} stop ${serviceName}`, (err) => {
        if (err) {
            console.error(err);
            return res.status(500).send('An error occurred while stopping the service');
        }

        console.info(`Stopped service '${serviceName}'`);
        res.status(200).send(`Stopped service '${serviceName}'`);
    });
});

// Scale a service.
app.post('/api/scale', async (req, res) => {
    const {serviceName, serviceScale} = req.body;
    if (!isValidServiceName(serviceName)) {
        console.error(`Invalid service name: ${serviceName}`);
        return res.status(400).send(`Invalid service name: ${serviceName}`);
    } else if (serviceScale < 0 || serviceScale > COMPOSE_MAX_SCALE) {
        console.error(`Invalid scale number: ${serviceScale}`);
        return res.status(400).send(`Invalid scale number: ${serviceScale}`);
    }

    // Execute docker compose scale <serviceName>=<scaleNumber>
    exec(`docker compose -p ${COMPOSE_PROJECT_NAME} up --scale ${serviceName}=${serviceScale}`, (err) => {
        if (err) {
            console.error(err);
            return res.status(500).send('An error occurred while scaling the service.');
        }

        console.info(`Scaled service '${serviceName}' to ${serviceScale}.`);
        res.status(200).send(`Scaled service '${serviceName}' to ${serviceScale}.`);
    });
});

// Rebuild the infrastructure.
app.post('/api/rebuild', async (req, res) => {
    exec(`docker compose -p ${COMPOSE_PROJECT_NAME} up -d --build`, (err) => {
        if (err) {
            console.error(err);
            return res.status(500).send('An error occurred while rebuilding the infrastructure');
        }

        console.info(`Rebuilt infrastructure '${COMPOSE_PROJECT_NAME}'`);
        res.status(200).send(`Rebuilt infrastructure '${COMPOSE_PROJECT_NAME}'`);
    });
});

// Start the server.
app.listen(PORT, () => {
    console.log(`Docker management UI for infrastructure ${COMPOSE_PROJECT_NAME} running on port ${PORT}`);
});
