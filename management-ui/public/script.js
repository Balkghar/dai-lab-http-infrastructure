// Script for the management UI
// Authors: Aubry Mangold, Hugo Germano

const apiURl = "/api";

// Start service event listener
document.querySelectorAll('.start-service').forEach(function (element) {
    element.addEventListener('click', function (event) {
        var serviceName = event.target.dataset.service;
        console.info("Start service: " + serviceName);
        fetch(apiURl + '/start', {
            method: 'POST', headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({serviceName}),
        }).then(response => console.log(response))
            .then(data => console.log(data))
            .catch(error => console.error(error))
    });
});

// Stop service event listener
document.querySelectorAll('.stop-service').forEach(function (element) {
    element.addEventListener('click', function (event) {
        var serviceName = event.target.dataset.service;
        console.info("Stop service: " + serviceName);
        fetch(apiURl + '/stop', {
            method: 'POST', headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({serviceName}),
        }).then(response => console.log(response))
            .then(data => console.log(data))
            .catch(error => console.error(error))
    });
});

// Add service event listener
document.querySelectorAll('.add-service').forEach(function (element) {
    element.addEventListener('click', function (event) {
        var serviceName = event.target.dataset.service;
        console.info("Add service: " + serviceName);
    });
});

// Remove service event listener
document.querySelectorAll('.remove-service').forEach(function (element) {
    element.addEventListener('click', function (event) {
        var serviceName = event.target.dataset.service;
        console.info("Remove service: " + serviceName);
    });
});