print('----------START----------')

// get access to the approval-service database
db = db.getSiblingDB('approval-service')

db.createUser(
    {
        user: 'admin',
        pwd: 'password',
        roles: [{role: 'readWrite', db: 'approval-service'}]
    }
);

db.createCollection('user');

print('----------END----------')