--liquibase formatted sql

--changeset HubertSciechowski:add-column-for-routine-exercise-weight-and-rest-time-units
ALTER TABLE workout_data.routine_exercise
    ADD COLUMN weight_unit VARCHAR(16) NOT NULL DEFAULT 'kg',
    ADD COLUMN rest_time_unit VARCHAR(16) NOT NULL DEFAULT 'seconds';

--changeset HubertSciechowski:add-column-for-workout-exercise-weight-and-rest-time-units
ALTER TABLE workout_data.workout_exercise
    ADD COLUMN weight_unit VARCHAR(16) NOT NULL DEFAULT 'kg',
    ADD COLUMN rest_time_unit VARCHAR(16) NOT NULL DEFAULT 'seconds';
