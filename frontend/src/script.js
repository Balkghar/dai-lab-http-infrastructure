const notes = [
   {
      "id": 1,
      "title": "Brute Force Breakfast",
      "content": "Tried brute-forcing my cereal box. Now it's encrypted with milk.",
      "createdAt": "2023-01-01T10:00:00Z",
      "updatedAt": "2023-01-01T10:15:00Z"
   },
   {
      "id": 2,
      "title": "Ping the Pizza",
      "content": "Sent a ping to my pizza. No response. Guess it's offline.",
      "createdAt": "2023-01-02T09:00:00Z",
      "updatedAt": "2023-01-02T09:30:00Z"
   },
   {
      "id": 3,
      "title": "Firewall Fiasco",
      "content": "My campfire won't start. Must be a strong firewall.",
      "createdAt": "2023-01-03T08:00:00Z",
      "updatedAt": "2023-01-03T08:20:00Z"
   },
   {
      "id": 4,
      "title": "Root Access Denied",
      "content": "Tried to get root access to my garden. Permission denied.",
      "createdAt": "2023-01-04T07:00:00Z",
      "updatedAt": "2023-01-04T07:10:00Z"
   },
   {
      "id": 5,
      "title": "Proxy Pillow",
      "content": "I used a proxy to sleep. Dream server couldn't locate me.",
      "createdAt": "2023-01-05T11:00:00Z",
      "updatedAt": "2023-01-05T11:05:00Z"
   },
   {
      "id": 6,
      "title": "DDoS Dinner",
      "content": "My dinner got DDoSed. Too many requests at the dinner table.",
      "createdAt": "2023-01-06T12:00:00Z",
      "updatedAt": "2023-01-06T12:30:00Z"
   },
   {
      "id": 7,
      "title": "Hack the Snack",
      "content": "Tried hacking into a snack bar. Got a mouthful of data.",
      "createdAt": "2023-01-07T13:00:00Z",
      "updatedAt": "2023-01-07T13:15:00Z"
   },
   {
      "id": 8,
      "title": "Phishing Phobia",
      "content": "Went phishing in a lake. Caught nothing but spam.",
      "createdAt": "2023-01-08T14:00:00Z",
      "updatedAt": "2023-01-08T14:20:00Z"
   },
   {
      "id": 9,
      "title": "Keylogger Conundrum",
      "content": "Installed a keylogger on my piano. Now it's recording Bach.",
      "createdAt": "2023-01-09T15:00:00Z",
      "updatedAt": "2023-01-09T15:30:00Z"
   },
   {
      "id": 10,
      "title": "SQL Injection Salad",
      "content": "Added SQL injection to my salad. It returned unexpected carrots.",
      "createdAt": "2023-01-10T16:00:00Z",
      "updatedAt": "2023-01-10T16:10:00Z"
   }
];

function loadData(){
   notes.forEach(createDivElement);
}

function createDivElement(note) {

   var h1 = document.createElement("h1");
   var p =  document.createElement("p");
   var newContenth1 = document.createTextNode(note.title);
   var newContentp = document.createTextNode(note.content);
   p.appendChild(newContentp);
   h1.appendChild(newContenth1);
   document.querySelector("#notes").append(h1)
   document.querySelector("#notes").append(p);
}