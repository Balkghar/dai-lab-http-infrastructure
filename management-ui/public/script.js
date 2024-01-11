// Script for the management UI
// Authors: Aubry Mangold, Hugo Germano

// API URL
const apiUrl = "/api";

// Send a command to the API.
const apiCommand = (command, data) => {
    fetch(`${apiUrl}/${command}`, {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(data)
    }).then(
        response => {
            console.log(response);
            if (response.status === 200) {
                notify(`Command '${command}' executed successfully.`, 'success');
                updateContainers();
                updateServices();
            }
            else {
                notify(`Command '${command}' failed.`, 'error');
            }
        }
    ).catch(error => {
        console.log(error);
        notify(`Command '${command}' failed.`, 'error');
    });
}

// Notify the user.
const notify = (message, status) => {
    const toast = document.createElement('div');
    toast.classList.add('notification');
    toast.textContent = message;

    const icon = document.createElement('span');
    icon.classList.add('inline-block', 'mr-2');
    icon.innerHTML = status === 'success' ? successIcon : errorIcon;
    toast.appendChild(icon);

    document.body.appendChild(toast);

    setTimeout(() => {
        document.body.removeChild(toast);
    }, 5000);
}

// Update the status.
const updateStatus = () => {
    fetch(`${apiUrl}/status`, {}).then(response => {
        // Replace the section.status > div with the new one
        response.text().then(data => {
            document.querySelector('section.status > p').outerHTML = data;
        }).catch(error => console.error(error));
    });
}

// Update the services list.
const updateServices = () => {
    fetch(`${apiUrl}/services`, {}).then(response => {
        // Replace the section.status > div with the new one
        response.text().then(data => {
            document.querySelector('section.services > table').outerHTML = data;
        }).catch(error => console.error(error));
    });
}

// FIXME: refactorable into a single function
// Update the containers list.
const updateContainers = () => {
    fetch(`${apiUrl}/containers`, {}).then(response => {
        // Replace the section.status > div with the new one
        response.text().then(data => {
            document.querySelector('section.containers > ul').outerHTML = data;
        }).catch(error => console.error(error));
    });
}

// Start service event listener.
const startHandler = event => {
        var serviceName = event.target.dataset.service;
        console.info("Start service: " + serviceName);
        apiCommand('start', {serviceName});
}

// Stop service event listener.
const stopHandler = event => {
    var serviceName = event.target.dataset.service;
    console.info("Stop service: " + serviceName);
    apiCommand('stop', {serviceName});
}

// Add service event listener.
const addHandler = event => {
    var serviceName = event.target.dataset.service;
    var serviceScale = event.target.dataset.scale++;
    console.info(`Add service: ${serviceName}.`);
    apiCommand('scale', {serviceName, serviceScale});
}

// Remove service event listener.
const removeHandler = event => {
    var serviceName = event.target.dataset.service;
    var serviceScale = event.target.dataset.scale--;
    console.info(`Remove service: ${serviceName}.`);
    apiCommand('scale', {serviceName, serviceScale});
}

// Icons
const successIcon = `<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-4 h-4">
  <path stroke-linecap="round" stroke-linejoin="round" d="M9 12.75 11.25 15 15 9.75M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0Z" />
</svg>
`

const errorIcon = `
<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-4 h-4">
  <path stroke-linecap="round" stroke-linejoin="round" d="M12 9v3.75m9-.75a9 9 0 1 1-18 0 9 9 0 0 1 18 0Zm-9 3.75h.008v.008H12v-.008Z" />
</svg>`

// DOM loaded handler.
document.addEventListener('DOMContentLoaded', () => {
    notify('Ready', 'success');

    updateStatus();
    updateServices();
    updateContainers();

    setTimeout(updateStatus, 1000);
});