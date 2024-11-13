-- changeset TymofiiSkrypko:create-routine_likes-table
CREATE TABLE workout_data.routine_like
(
    id          UUID                     NOT NULL,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    routine_id  UUID                     NOT NULL,
    user_id     UUID                     NOT NULL,
    CONSTRAINT pk_routine_like PRIMARY KEY (id),
    CONSTRAINT fk_routine_like_on_routine FOREIGN KEY (routine_id) REFERENCES workout_data.routine (id),
    CONSTRAINT uq_user_routine UNIQUE (routine_id, user_id) -- Ensures a user can only like a routine once
);
