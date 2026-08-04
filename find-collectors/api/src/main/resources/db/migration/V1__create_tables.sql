CREATE TABLE IF NOT EXISTS states (
    id SERIAL PRIMARY KEY,
    uf VARCHAR(2) NOT NULL UNIQUE,
    name VARCHAR (30) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS cities(
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    state_id BIGINT NOT NULL REFERENCES states(id) ON UPDATE CASCADE ON DELETE NO ACTION,
    UNIQUE(name, state_id),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS persons(
    id SERIAL PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100),
    name VARCHAR(100),
    telephone VARCHAR(15),
    cep VARCHAR(10),
    address VARCHAR(100),
    district VARCHAR(100),
    number VARCHAR(10),
    person_type VARCHAR(20) NOT NULL,
    description TEXT,
    collect_point BOOLEAN DEFAULT FALSE,
    godfather_id BIGINT REFERENCES persons(id) ON UPDATE CASCADE ON DELETE NO ACTION,
    city_id BIGINT REFERENCES cities(id) ON UPDATE CASCADE ON DELETE NO ACTION,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS person_garbage_types (
    person_id BIGINT REFERENCES persons(id) ON UPDATE CASCADE ON DELETE NO ACTION,
    garbage_type VARCHAR(20) NOT NULL,
    PRIMARY KEY (person_id, garbage_type)
);

CREATE TABLE IF NOT EXISTS collects(
    id SERIAL PRIMARY KEY,
    date_and_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    accept BOOLEAN NOT NULL DEFAULT FALSE,
    collected BOOLEAN NOT NULL DEFAULT FALSE,
    recurrent BOOLEAN NOT NULL DEFAULT FALSE,
    cep VARCHAR(10),
    address VARCHAR(100) NOT NULL,
    district VARCHAR(100) NOT NULL,
    number VARCHAR(10) NOT NULL,
    city_id BIGINT NOT NULL REFERENCES cities(id) ON UPDATE CASCADE ON DELETE NO ACTION,
    collector_id BIGINT REFERENCES persons(id) ON UPDATE CASCADE ON DELETE NO ACTION,
    recycler_id BIGINT NOT NULL REFERENCES persons(id) ON UPDATE CASCADE ON DELETE NO ACTION,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
  );

CREATE TABLE IF NOT EXISTS collect_garbage_types (
    collect_id BIGINT REFERENCES collects(id) ON UPDATE CASCADE ON DELETE NO ACTION,
    garbage_type VARCHAR(20) NOT NULL,
    PRIMARY KEY (collect_id, garbage_type)
);