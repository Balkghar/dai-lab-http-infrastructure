const express = require('express');
const Docker = require('dockerode');
const path = require('path');
const {env} = require('process');
const bodyParser = require('body-parser');
const exec = require('child_process').exec;
const docker = new Docker();

const app = express();
const expressWs = require('express-ws')(app);

const PORT = process.env.PORT || 3000;
const COMPOSE_PROJECT_NAME = env.COMPOSE_NAME || 'dai-lab-http';
const COMPOSE_SERVICES = process.env.COMPOSE_SERVICES.split(',');
const COMPOSE_MAX_SCALE = process.env.COMPOSE_MAX_SCALE || 10;
const WS_BUFFER_SIZE = 1024 * 1024; // 1MB
const WS_FLUSH_INTERVAL = 1000; // 1 second

app.set('view engine', 'ejs');
app.set('views', path.join(__dirname, 'views'));
app.use(express.static(path.join(__dirname, 'public')));
app.use(bodyParser.json());

// Check if the service name is in the allowed list.
const isValidServiceName = service => {
    return COMPOSE_SERVICES.includes(service);
}

// Logging middleware
app.use((req, res, next) => {
    const now = new Date().toISOString();
    const clientIp = req.headers['x-forwarded-for'] || req.connection.remoteAddress;
    console.log(`[${now}] ${req.method} ${req.url} from ${clientIp}`);
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
    try {
        const containers = await docker.listContainers({all: true});
        // Strip the container name from the prefix slash
        containers.forEach(c => c.Name = c.Names[0].substring(1));
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
    exec(`docker compose -p ${COMPOSE_PROJECT_NAME} up -d --scale ${serviceName}=${serviceScale}`, (err) => {
        if (err) {
            console.error(err);
            return res.status(500).send('An error occurred while scaling the service.');
        }

        console.info(`Scaled service '${serviceName}' to ${serviceScale}.`);
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

        console.info(`Restarted infrastructure '${COMPOSE_PROJECT_NAME}'`);
        res.status(200).send(`Restarted infrastructure '${COMPOSE_PROJECT_NAME}'`);
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

// Logs websocket.
let logProcess;
app.ws('/api/logs', ws => {
    ws.on('message', message => {
        console.log(`WebSocket message: ${message}`);
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
        console.error('WebSocket error:', error);
    });

    ws.on('close', () => {
        console.log('WebSocket connection closed');
    });
});

// Infrastructure logs streaming function. Buffers the logs and sends them to the client.
const streamInfraLogs = (infra, ws) => {
    let buffer = '';
    const logs = exec(`docker compose -p ${COMPOSE_PROJECT_NAME} logs -f`);
    logs.stdout.on('data', data => {
        buffer += data;
        if (Buffer.byteLength(buffer, 'utf8') >= WS_BUFFER_SIZE) {
            ws.send(buffer);
            buffer = '';
        }
    });

    setInterval(() => {
        if (buffer.length > 0) {
            ws.send(buffer);
            buffer = '';
        }
    }, WS_FLUSH_INTERVAL);

    return logs;
}

// Containers logs streaming function. Buffers the logs and sends them to the client.
const streamContainerLogs = (container, ws) => {
   let buffer = '';
    const logs = exec(`docker logs -f ${container}`);
    logs.stdout.on('data', data => {
        buffer += data;
        if (Buffer.byteLength(buffer, 'utf8') >= WS_BUFFER_SIZE) {
            ws.send(buffer);
            buffer = '';
        }
    });

    setInterval(() => {
        if (buffer.length > 0) {
            ws.send(buffer);
            buffer = '';
        }
    }, WS_FLUSH_INTERVAL);

    return logs;
}

// Start the server.
app.listen(PORT, () => {
    console.log(`Docker management UI for infrastructure ${COMPOSE_PROJECT_NAME} running on port ${PORT}`);
});
