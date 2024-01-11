const express = require('express');
const Docker = require('dockerode');
const path = require('path');
const bodyParser = require('body-parser');
const exec = require('child_process').exec;
const app = express();
const docker = new Docker();

const composeProjectName = 'dai-lab-http'; // FIXME: hard-coded


app.set('view engine', 'ejs');
app.set('views', path.join(__dirname, 'views'));
app.use(express.static(path.join(__dirname, 'public')));
app.use(bodyParser.json());

// Render the view with the list of containers and services.
app.get('/', async (req, res) => {
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

        res.render('view', {composeProjectName, containers, services});
        containers.forEach(container => console.log(container));
    } catch (error) {
        console.error(error);
        res.status(500).send('An error occurred while fetching the containers.');
    }
});

app.post('/api/start', (req, res) => {
    const {serviceName} = req.body;

    // Execute docker compose start <serviceName>
    exec(`docker compose -p ${composeProjectName} start ${serviceName}`, (err, stdout, stderr) => {
        if (err) {
            console.error(`Error: ${err}`);
            console.error(`Stderr: ${stderr}`);
            return res.status(500).send('An error occurred while starting the service.');
        }

        console.info(`Started service ${serviceName}.`);
        console.log(`Stdout: ${stdout}`);
        res.status(200).send(`Started service ${serviceName}.`);
    });
});

app.post('/api/stop', (req, res) => {
    const {serviceName} = req.body;

    // Execute docker compose stop <serviceName>
    exec(`docker compose -p ${composeProjectName} stop ${serviceName}`, (err, stdout, stderr) => {
        if (err) {
            console.error(`Error: ${err}`);
            console.error(`Stderr: ${stderr}`);
            return res.status(500).send('An error occurred while stopping the service.');
        }

        console.info(`Stopped service ${serviceName}.`);
        console.log(`Stdout: ${stdout}`);
        res.status(200).send(`Stopped service ${serviceName}.`);
    });
});

app.post('/api/scale', async (req, res) => {
    const {serviceName, scaleNumber} = req.body;

    res.status(200).send(`Scaled service ${serviceName} to ${scaleNumber} replicas.`);
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
    console.log(`Docker management UI running on port ${PORT}`);
});
