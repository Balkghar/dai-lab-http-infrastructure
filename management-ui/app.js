const express = require('express');
const Docker = require('dockerode');
const path = require('path');
const bodyParser = require('body-parser');

const app = express();
const docker = new Docker(); // connects to your Docker daemon

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

app.post('/start', async (req, res) => {
    const {serviceName} = req.body;

    try {
        let service = await docker.getService(serviceName);
        await service.update({version: null, updateConfig: {}}); // FIXME
        res.status(200).send(`Started service ${serviceName}.`);
    } catch (error) {
        console.error(error);
        res.status(500).send('An error occurred while starting the service.');
    }
});

app.post('/scale', async (req, res) => {
    const {serviceName, scaleNumber} = req.body;

    try {
        let service = await docker.getService(serviceName);
        let serviceInfo = await service.inspect();

        let updatedSpec = {...serviceInfo.Spec, Mode: {Replicated: {Replicas: scaleNumber}}};
        await service.update({version: serviceInfo.Version.Index}, updatedSpec);

        res.status(200).send(`Scaled service ${serviceName} to ${scaleNumber} replicas.`);
    } catch (error) {
        console.error(error);
        res.status(500).send('An error occurred while scaling the service.');
    }
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
    console.log(`Docker management UI running on port ${PORT}`);
});
