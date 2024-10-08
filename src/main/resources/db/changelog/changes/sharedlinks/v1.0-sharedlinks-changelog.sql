--liquibase formatted sql

--changeset przemyslawren:create-links-table
CREATE TABLE IF NOT EXISTS shared_links.links (
    id              UUID PRIMARY KEY,
    user_id         UUID NOT NULL,
    routine_id      UUID NOT NULL,
    link            VARCHAR(256) NOT NULL,
    expires_at      TIMESTAMP WITH TIME ZONE NOT NULL,
    is_active       BOOLEAN NOT NULL,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    modified_at     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by      UUID NOT NULL,
    modified_by     UUID NOT NULL
);

--changeset przemyslawren:create-link-access-logs-table
CREATE TABLE IF NOT EXISTS shared_links.link_access_logs (
    id                  UUID PRIMARY KEY,
    link_id             UUID NOT NULL REFERENCES shared_links.links(id),
    accessed_at         TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    accessed_by_user_id UUID NOT NULL,
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    modified_at         TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by          UUID NOT NULL,
    modified_by         UUID NOT NULL
)