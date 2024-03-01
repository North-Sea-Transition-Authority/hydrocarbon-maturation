CREATE TABLE projects (
  id UUID PRIMARY KEY NOT NULL,
  field_identifier INTEGER NOT NULL,
      CONSTRAINT projects_fk_fields FOREIGN KEY (field_identifier) REFERENCES fields(field_identifier),
  project_name TEXT,
  project_type TEXT,
  project_status TEXT,
  is_part_of_pipeline BOOLEAN,
  submission_detail_id UUID,
    CONSTRAINT projects_fk_submission_details FOREIGN KEY (submission_detail_id) REFERENCES submission_details(id),
  created_timestamp TIMESTAMP
);
CREATE INDEX projects_idx_field_identifier ON projects(field_identifier);

CREATE TABLE projects_aud (
    rev INTEGER NOT NULL,
    revtype INTEGER,
    id                   UUID,
    field_identifier     INTEGER,
    project_name         TEXT,
    project_type         TEXT,
    project_status       TEXT,
    is_part_of_pipeline  BOOLEAN,
    submission_detail_id UUID,
    created_timestamp    TIMESTAMP,
    PRIMARY KEY (rev, id),
    FOREIGN KEY (rev) REFERENCES audit_revisions
);