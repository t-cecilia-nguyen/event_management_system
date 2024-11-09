print('----------START----------')

// get access to the booking-service database
db = db.getSiblingDB('booking-service')

db.createUser(
    {
        user: 'admin',
        pwd: 'password',
        roles: [{role: 'readWrite', db: 'booking-service'}]
    }
);

db.createCollection('user');

print('----------END----------')