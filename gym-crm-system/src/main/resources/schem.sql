CREATE SCHEMA IF NOT EXISTS gym_crm;

CREATE TABLE IF NOT EXISTS gym_crm.training_type (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS gym_crm.app_user (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS gym_crm.trainee (
    id BIGSERIAL PRIMARY KEY,
    date_of_birth DATE,
    address VARCHAR(255),
    user_id BIGINT NOT NULL UNIQUE REFERENCES gym_crm.app_user(id)
);

CREATE TABLE IF NOT EXISTS gym_crm.trainer (
    id BIGSERIAL PRIMARY KEY,
    specialization BIGINT NOT NULL REFERENCES gym_crm.training_type(id),
    user_id BIGINT NOT NULL UNIQUE
        REFERENCES gym_crm.app_user(id)
);

CREATE TABLE IF NOT EXISTS gym_crm.training (
    id BIGSERIAL PRIMARY KEY,
    trainee_id BIGINT NOT NULL REFERENCES gym_crm.trainee(id),
    trainer_id BIGINT NOT NULL REFERENCES gym_crm.trainer(id),
    training_name VARCHAR(255) NOT NULL,
    training_type_id BIGINT NOT NULL REFERENCES gym_crm.training_type(id),
    training_date DATE NOT NULL,
    training_duration INTEGER NOT NULL CHECK (training_duration > 0)
);