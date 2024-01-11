// Script for the management UI
// Authors: Aubry Mangold, Hugo Germano

// API URL
const apiUrl = "/api";
const RESTART_PROMPT = "Are you sure you want to restart the infrastructure?\r\n\r\nThis may interrupt running services. The infrastructure may take a while to restart.";

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
            } else {
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
    toast.classList.add(status === 'success' ? 'bg-green-600' : 'bg-red-600');
    toast.textContent = message;

    const icon = document.createElement('span');
    icon.classList.add('inline-block', 'mr-2', 'align-middle');
    icon.innerHTML = status === 'success' ? successIcon : errorIcon;
    toast.prepend(icon);

    document.body.appendChild(toast);

    setTimeout(() => document.body.removeChild(toast), 5000);
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

// Update the containers list.
const updateContainers = () => {
    fetch(`${apiUrl}/containers`, {}).then(response => {
        // Replace the section.status > div with the new one
        response.text().then(data => {
            document.querySelector('section.containers > ul').outerHTML = data;
        }).catch(error => console.error(error));
    });
}

// Restart infrastructure event handler.
const restartHandler = event => {
    const confirmed = confirm(RESTART_PROMPT);
    if (!confirmed) {
        return;
    }
    notify('Infrastructure restarting...');
    console.info("Restart infrastructure.");
    fetch(`${apiUrl}/restart`, {method: 'POST'}).then(response => {
        setTimeout(() => document.location.reload(), 3000);
    }).catch(error => {
        notify('Failed to restart infrastructure', 'error');
        console.error(error)
    });
}

// Rebuild infrastructure event handler.
const rebuildHandler = event => {
    const confirmed = confirm(RESTART_PROMPT);
    if (!confirmed) {
        return;
    }
    console.info("Rebuild infrastructure.");
    notify('Infrastructure rebuilding...');
    fetch(`${apiUrl}/rebuild`, {method: 'POST'}).then(response => {
        setTimeout(() => document.location.reload(), 10000);
    }).catch(error => {
        notify('Failed to rebuild infrastructure', 'error');
        console.error(error)
    });
}

// Start service event handler.
const startHandler = event => {
    const serviceName = event.target.dataset.service;
    console.info("Start service: " + serviceName);
    apiCommand('start', {serviceName});
}

// Stop service event handler.
const stopHandler = event => {
    const serviceName = event.target.dataset.service;
    console.info("Stop service: " + serviceName);
    apiCommand('stop', {serviceName});
}

// Add service event handler.
const addHandler = event => {
    const serviceName = event.target.dataset.service;
    const serviceScale = event.target.dataset.scale++;
    console.info(`Add service: ${serviceName}.`);
    apiCommand('scale', {serviceName, serviceScale});
}

// Remove service event handler.
const removeHandler = event => {
    const serviceName = event.target.dataset.service;
    const serviceScale = event.target.dataset.scale--;
    console.info(`Remove service: ${serviceName}.`);
    apiCommand('scale', {serviceName, serviceScale});
}

// Icons
const successIcon = `<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
  <path stroke-linecap="round" stroke-linejoin="round" d="M9 12.75 11.25 15 15 9.75M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0Z" />
</svg>
`

const errorIcon = `
<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
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