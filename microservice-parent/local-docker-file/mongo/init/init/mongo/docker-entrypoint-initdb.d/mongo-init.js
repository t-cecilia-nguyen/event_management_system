// create actual database

// user privileges

print('START');

db = db.getSiblingDB('event-service');

db.createUser(
    {
        user: 'admin',
        pwd: 'password',
        roles: [ {role: 'readWrite' , db : 'product-service'}],
    },
);

db.createCollection('user');

print('END');