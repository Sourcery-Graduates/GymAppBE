--liquibase formatted sql

--changeset PiotrBaranowski:create_table_workout_data.routine_like
CREATE TABLE workout_data.routine_like (
    user_id UUID NOT NULL,
    routine_id UUID NOT NULL,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    modified_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by  UUID                     NOT NULL,
    modified_by UUID                     NOT NULL,
    PRIMARY KEY (user_id, routine_id)
);