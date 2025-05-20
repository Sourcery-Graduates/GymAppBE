--liquibase formatted sql

--changeset przemyslawren:add-test-user
INSERT INTO user_auth.users(id, username, password, email, created_at, modified_at, is_enabled, provider, provider_id)
VALUES ('8e274e39-8ad3-4ccf-a697-48a749b26d33',
        'test',
        '$2a$12$LLwtoKemMerhBho7Jjn2Penq3JdfPQsXEVq/vvf5G1rLsYE8xix9.',
        'sourcery.graduates@gmail.com',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP,
        true,
        NULL,
        NULL
        );