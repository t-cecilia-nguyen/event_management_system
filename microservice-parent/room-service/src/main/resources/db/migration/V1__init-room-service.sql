CREATE TABLE t_rooms (
    id BIGSERIAL NOT NULL,
    room_name VARCHAR(255) NOT NULL,
    capacity INT NOT NULL,
    features TEXT,
    availability BOOLEAN NOT NULL,
    PRIMARY KEY (id)
);
