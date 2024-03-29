/**
 * Docker management application
 * Features api endpoints to manage a docker-compose infrastructure and a web interface to interact with the api.
 *
 * Authors: Aubry Mangold, Hugo Germano
 */

const express = require('express');
const Docker = require('dockerode');
const path = require('path');
const {env} = require('process');
const bodyParser = require('body-parser');
const {exec, spawn} = require('child_process');
const docker = new Docker();

const app = express();
const expressWs = require('express-ws')(app);

const APP_PORT = 3000;
const COMPOSE_PROJECT_NAME = env.COMPOSE_NAME || 'dai-lab-http';
const COMPOSE_SERVICES = env.COMPOSE_SERVICES.split(',');
const COMPOSE_MAX_SCALE = env.COMPOSE_MAX_SCALE || 10;
const COMPOSE_DEFAULT_SCALE = env.COMPOSE_DEFAULT_SCALE || 1;

app.set('view engine', 'ejs');
app.set('views', path.join(__dirname, 'views'));
app.use(express.static(path.join(__dirname, 'public')));
app.use(bodyParser.json());

let servicesScale = {}
COMPOSE_SERVICES.forEach(service => {
  servicesScale[service] = COMPOSE_DEFAULT_SCALE;
});

// Check if the service name is in the allowed list.
const isValidServiceName = service => {
    return COMPOSE_SERVICES.includes(service);
}

// Logging middleware
app.use((req, res, next) => {
    const clientIp = req.headers['x-forwarded-for'] || req.connection.remoteAddress;
    console.log(`[${new Date().toISOString()}] ${req.method} ${req.url} from ${clientIp}`);
    next();
});

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
    docker.listContainers({all: true}, (err, containers) => {
        if (err) {
            console.error(error);
            return res.status(500).send('An error occurred while fetching the containers');
        }
        let filteredContainers = containers.filter(c => isValidServiceName(c.Labels['com.docker.compose.service']));
        // Strip the container name from the prefix slash
        filteredContainers.forEach(c => c.Name = c.Names[0].substring(1));
        res.render('containers', {containers: filteredContainers});
    });
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

    servicesScale[serviceName] = serviceScale;

    // Create a string that contains --scale <serviceName>=<scaleNumber> for each entry of servicesScale. Scaling
    // a single service with --scale would result in losing the scale of the remaining services.
    const scaleParams = Object.entries(servicesScale).map(([key, value]) => `--scale ${key}=${value}`).join(' ');

    const command = `chroot /host /bin/bash -c "docker compose -f $(docker compose ls | sed -n '2p' | awk '{print $3}') up -d ${scaleParams}"`
    exec(command, (err) => {
        if (err) {
            console.error(err);
            return res.status(500).send('An error occurred while scaling the service.');
        }

        res.status(200).send(`Scaled service '${serviceName}' to ${serviceScale}.`);
    });
});

// Restart the infrastructure.
app.post('/api/restart', async (req, res) => {
    exec(`docker compose -p ${COMPOSE_PROJECT_NAME} up -d`, (err) => {
        if (err) {
            console.error(err);
            return res.status(500).send('An error occurred while restarting the infrastructure');
        }

        res.status(200).send(`Restarted infrastructure '${COMPOSE_PROJECT_NAME}'`);
    });
});

// Rebuild the infrastructure.
app.post('/api/rebuild', async (req, res) => {
    exec(`chroot /host /bin/bash -c "docker compose -f $(docker compose ls | sed -n '2p' | awk '{print $3}') up -d --build"`, (err) => {
        if (err) {
            console.error(err);
            return res.status(500).send('An error occurred while rebuilding the infrastructure');
        }

        res.status(200).send(`Rebuilt infrastructure '${COMPOSE_PROJECT_NAME}'`);
    });
});

// Logs websocket.
let logProcess;
app.ws('/api/logs', ws => {
    ws.on('message', message => {
        console.log(`[${new Date().toISOString()}] WebSocket '${message}' from ${ws._socket.remoteAddress}`);
        const [type, name] = message.split(' ');
        logProcess && logProcess.kill(); // Kill the previous exec process if any.
        if (type === 'hello') {
            return;
        } else if (type === 'infra') {
            logProcess = streamInfraLogs(name, ws);
        } else if (type === 'container') {
            logProcess = streamContainerLogs(name, ws);
        }
    });

    ws.on('error', error => {
        console.error(`[${new Date().toISOString()}] WebSocket error: ${error}`);
    });

    setInterval(() => {
        if (buffer.length > 0) {
            ws.send(buffer.join('\n'));
            buffer = [];
        }
    }, 250)
});

// Infrastructure logs streaming function.
const streamInfraLogs = (infra, ws) => {
    const process = spawn('docker', ['compose', '-p', COMPOSE_PROJECT_NAME, 'logs', '-f']);

    process.stdout.on('data', data => bufferedSend(ws, data.toString()));
    process.stderr.on('data', data => bufferedSend(ws, data.toString()));
    return process;
}

// Container logs streaming function.
const streamContainerLogs = (container, ws) => {
    const process = spawn('docker', ['logs', '-f', container]);

    process.stdout.on('data', data => bufferedSend(ws, data.toString()));
    process.stderr.on('data', data => bufferedSend(ws, data.toString()));
    return process;
}

// Buffered sending through the WebSocket.
let buffer = [];
const bufferedSend = (ws, message) => {
    buffer.push(message);
}

// Start the server.
app.listen(APP_PORT, () => {
    console.log(`Docker management UI for infrastructure ${COMPOSE_PROJECT_NAME} running on port ${APP_PORT}`);
});
