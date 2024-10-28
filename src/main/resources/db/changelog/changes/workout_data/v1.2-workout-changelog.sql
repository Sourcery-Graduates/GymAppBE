--liquibase formatted sql

--changeset PiotrBaranowski:create_table_workout_data.routine_like
CREATE TABLE workout_data.routine_like (
    userId UUID NOT NULL,
    routineId UUID NOT NULL REFERENCES workout_data.routine(id),
    PRIMARY KEY (userId, routineId)
)