CREATE TABLE undeveloped_discoveries (
    id                       UUID PRIMARY KEY NOT NULL,
    is_undeveloped_discovery BOOLEAN,
    resource_id              UUID,
    CONSTRAINT undeveloped_discoveries_fk_resources FOREIGN KEY (resource_id) REFERENCES resources(id)
);
CREATE INDEX undeveloped_discoveries_idx_resource_id ON undeveloped_discoveries(resource_id);

CREATE TABLE undeveloped_discoveries_aud (
    rev                      INTEGER NOT NULL,
    revtype                  INTEGER,
    id                       UUID,
    is_undeveloped_discovery BOOLEAN,
    resource_id              UUID,
    PRIMARY KEY (rev, id),
    FOREIGN KEY (rev) REFERENCES audit_revisions
);