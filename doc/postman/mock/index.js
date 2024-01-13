const PostmanLocalMockServer = require('@jordanwalsh23/postman-local-mock-server');
const fs = require('fs');

//Create the collection object.
let collection = JSON.parse(fs.readFileSync('../postman_collection.json', 'utf8'));

//Create a new server
let server = new PostmanLocalMockServer(3555, collection);

//Start the server
server.start();