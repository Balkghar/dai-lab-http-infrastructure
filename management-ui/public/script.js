// Script for the management UI
// Authors: Aubry Mangold, Hugo Germano

// Start service event listener
document.querySelectorAll('.start-service').forEach(function(element) {
    element.addEventListener('click', function(event) {
        var serviceName = event.target.dataset.service;
        console.log("Start service: " + serviceName);
    });
});

// Stop service event listener
document.querySelectorAll('.stop-service').forEach(function(element) {
    element.addEventListener('click', function(event) {
        var serviceName = event.target.dataset.service;
        console.log("Stop service: " + serviceName);
    });
});

// Add service event listener
document.querySelectorAll('.add-service').forEach(function(element) {
    element.addEventListener('click', function(event) {
        var serviceName = event.target.dataset.service;
        console.log("Add service: " + serviceName);
    });
});

// Remove service event listener
document.querySelectorAll('.remove-service').forEach(function(element) {
    element.addEventListener('click', function(event) {
        var serviceName = event.target.dataset.service;
        console.log("Remove service: " + serviceName);
    });
});