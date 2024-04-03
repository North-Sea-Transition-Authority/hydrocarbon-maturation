CREATE TABLE project_capex (
  id UUID PRIMARY KEY,
  project_id UUID NOT NULL,
    CONSTRAINT project_capex_fk_projects FOREIGN KEY (project_id) REFERENCES projects(id),
  date_provided DATE,
  total_capex NUMERIC,
  source_of_information TEXT,
  project_capex_status TEXT
);
CREATE INDEX project_capex_idx1_project_id ON project_capex(project_id);

CREATE TABLE project_capex_aud(
  rev INTEGER NOT NULL,
  revtype INTEGER,
  id UUID,
  project_id UUID,
  date_provided DATE,
  total_capex NUMERIC,
  source_of_information TEXT,
  project_capex_status TEXT,
  PRIMARY KEY (rev, id),
  FOREIGN KEY (rev) REFERENCES audit_revisions
);