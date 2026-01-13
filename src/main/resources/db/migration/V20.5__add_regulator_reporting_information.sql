CREATE TABLE remi.resource_groups (
    id UUID PRIMARY KEY NOT NULL
);

ALTER TABLE remi.resource_group_links
ADD CONSTRAINT resource_group_links_fk2 FOREIGN KEY (resource_group_id) REFERENCES resource_groups(id);

CREATE TABLE remi.resource_groups_aud (
    rev INTEGER NOT NULL,
    revtype INTEGER NOT NULL,
    id UUID NOT NULL,
    PRIMARY KEY (rev, id),
    FOREIGN KEY (rev) REFERENCES audit_revisions
);

CREATE TABLE remi.regulator_reporting_information (
    id UUID PRIMARY KEY NOT NULL,
    resource_group_id UUID NOT NULL,
    CONSTRAINT regulator_reporting_information_group_fk FOREIGN KEY (resource_group_id) REFERENCES resource_groups(id),
    resource_name TEXT,
    opportunity_type TEXT,
    other_type_of_opportunity TEXT,
    hydrocarbon_case TEXT,
    well_id INTEGER,
    well_reference TEXT
);

CREATE TABLE remi.regulator_reporting_information_aud (
    rev INTEGER NOT NULL,
    revtype INTEGER NOT NULL,
    id UUID,
    resource_group_id UUID,
    resource_name TEXT,
    opportunity_type TEXT,
    other_type_of_opportunity TEXT,
    hydrocarbon_case TEXT,
    well_id INTEGER,
    well_reference TEXT,
    PRIMARY KEY (rev, id),
    FOREIGN KEY (rev) REFERENCES audit_revisions
);