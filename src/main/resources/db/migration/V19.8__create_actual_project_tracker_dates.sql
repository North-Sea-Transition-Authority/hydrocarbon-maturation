CREATE TABLE actual_project_dates (
    id UUID PRIMARY KEY,
    project_id UUID NOT NULL,
    actual_project_date_type TEXT,
    date DATE,
    actual_project_date_status TEXT,
    CONSTRAINT actual_project_dates_fk_projects FOREIGN KEY (project_id) REFERENCES projects(id)
);

CREATE INDEX actual_project_dates_idx1_project_id ON actual_project_dates(project_id);

CREATE TABLE actual_project_dates_aud(
    rev INTEGER NOT NULL,
    revtype INTEGER,
    id UUID,
    project_id UUID,
    actual_project_date_type TEXT,
    date DATE,
    actual_project_date_status TEXT,
    PRIMARY KEY (rev, id),
    FOREIGN KEY (rev) REFERENCES audit_revisions
);