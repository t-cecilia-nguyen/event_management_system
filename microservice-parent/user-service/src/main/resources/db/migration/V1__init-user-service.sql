CREATE TABLE t_users (
    id BIGSERIAL NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL,
    user_type VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);
