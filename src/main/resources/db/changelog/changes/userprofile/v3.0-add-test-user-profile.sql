--liquibase formatted sql

--changeset przemyslawren:add-test-user
INSERT INTO user_profiles.profiles(id, created_at, modified_at, created_by, modified_by, user_id,
                                   username, first_name, last_name, bio, avatar_url, location, settings)
VALUES ('a17cc8da-3b61-4946-bea8-88babffdff57',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP,
        '8e274e39-8ad3-4ccf-a697-48a749b26d33',
        '8e274e39-8ad3-4ccf-a697-48a749b26d33',
        '8e274e39-8ad3-4ccf-a697-48a749b26d33',
        'test',
        'John',
        'Doe',
        'Gym App Enthusiast',
        NULL,
        'Planet Earth',
        NULL
       );