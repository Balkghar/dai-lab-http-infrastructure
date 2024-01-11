// Script for the management UI
// Authors: Aubry Mangold, Hugo Germano

// API URL
const apiUrl = "/api";

// Send a command to the API.
const apiCommand = (command, data) => {
    fetch(`${apiUrl}/${command}`, {
        method: 'POST', headers: {'Content-Type': 'application/json'}, body: JSON.stringify(data)
    }).then(response => console.log(response))
        .catch(error => console.error(error))
}

// Start service event listener.
document.querySelectorAll('.start-service').forEach(function (element) {
    element.addEventListener('click', function (event) {
        var serviceName = event.target.dataset.service;
        console.info("Start service: " + serviceName);
        apiCommand('start', {serviceName});
    });
});

// Stop service event listener.
document.querySelectorAll('.stop-service').forEach(function (element) {
    element.addEventListener('click', function (event) {
        var serviceName = event.target.dataset.service;
        console.info("Stop service: " + serviceName);
        apiCommand('stop', {serviceName});
    });
});

// Add service event listener.
document.querySelectorAll('.add-service').forEach(function (element) {
    element.addEventListener('click', function (event) {
        var serviceName = event.target.dataset.service;
        var serviceScale = event.target.dataset.scale++;
        console.info(`Add service: ${serviceName}.`);
        apiCommand('scale', {serviceName, serviceScale});
    });
});

// Remove service event listener.
document.querySelectorAll('.remove-service').forEach(function (element) {
    element.addEventListener('click', function (event) {
        var serviceName = event.target.dataset.service;
        var serviceScale = event.target.dataset.scale--;
        console.info(`Remove service: ${serviceName}.`);
        apiCommand('scale', {serviceName, serviceScale});
    });
});

