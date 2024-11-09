// create actual database

// user privileges

print('START MONGO-INIT.JS SCRIPT ONCE: event-service');

db = db.getSiblingDB('event-service');

db.createUser(
    {
        user: 'admin',
        pwd: 'password',
        roles: [ {role: 'readWrite' , db : 'event-service'}],
    },
);

db.createCollection('user');

print('END MONGO-INIT.JS SCRIPT: event-service');