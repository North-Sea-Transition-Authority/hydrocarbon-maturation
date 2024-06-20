CREATE TABLE reservoirs_aud (
    rev INTEGER NOT NULL,
    revtype INTEGER,
    id UUID,
    name TEXT,
    PRIMARY KEY (rev, id),
    FOREIGN KEY (rev) REFERENCES audit_revisions
);

CREATE TABLE fields_aud (
    rev INTEGER NOT NULL,
    revtype INTEGER,
    field_identifier INTEGER,
    resource_id UUID,
    field_status TEXT,
    legal_field_name TEXT,
    corrected_legal_field_name TEXT,
    common_name_for_field TEXT,
    responsible_organisation_id INTEGER,
    organisation_name TEXT,
    organisation_registered_number TEXT,
    onshore_offshore TEXT,
    field_number TEXT,
    infrastructure_host BOOLEAN,
    determination_status TEXT,
    parent_determined_field_id INTEGER,
    is_on_licence BOOLEAN,
    PRIMARY KEY (rev, field_identifier),
    FOREIGN KEY (rev) REFERENCES audit_revisions
);

CREATE TABLE regulator_managed_information_aud (
    rev INTEGER NOT NULL,
    revtype INTEGER,
    id UUID,
    resource_id UUID,
    responsible_team TEXT,
    geographical_area TEXT,
    PRIMARY KEY (rev, id),
    FOREIGN KEY (rev) REFERENCES audit_revisions
);

CREATE TABLE resource_group_links_aud (
    rev INTEGER NOT NULL,
    revtype INTEGER,
    id UUID,
    resource_group_id UUID,
    resource_id UUID,
    preferred_view BOOLEAN,
    legacy_resource_id TEXT,
    PRIMARY KEY (rev, id),
    FOREIGN KEY (rev) REFERENCES audit_revisions
);