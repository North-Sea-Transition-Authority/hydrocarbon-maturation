ALTER TABLE resources
ADD COLUMN archived BOOLEAN NOT NULL DEFAULT FALSE;

CREATE TABLE resources_aud (
    rev INTEGER NOT NULL,
    revtype INTEGER,
    id UUID,
    type TEXT NOT NULL,
    created_timestamp TIMESTAMP NOT NULL,
    archived BOOLEAN NOT NULL,
    PRIMARY KEY (rev, id),
    FOREIGN KEY (rev) REFERENCES audit_revisions
);