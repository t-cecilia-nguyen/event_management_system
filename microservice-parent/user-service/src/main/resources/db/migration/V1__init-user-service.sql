CREATE TYPE role_enum AS ENUM ('STUDENT', 'ADMIN', 'STAFF', 'FACULTY');
CREATE TYPE usertype_enum AS ENUM ('STUDENT', 'STAFF', 'FACULTY', 'VISITOR');

CREATE TABLE t_users (
    id BIGSERIAL NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    role role_enum NOT NULL,
    userType usertype_enum NOT NULL,
    PRIMARY KEY (id)
);
