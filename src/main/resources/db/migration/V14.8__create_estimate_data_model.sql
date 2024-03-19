CREATE TABLE project_submission_estimates(
    id UUID PRIMARY KEY,
    project_id UUID NOT NULL,
        CONSTRAINT project_submission_estimates_fk_projects FOREIGN KEY (project_id) REFERENCES projects(id),
    submission_estimate_type TEXT,
    earliest_date DATE,
    most_likely_date DATE,
    latest_date DATE,
    date_when_estimated DATE,
    submission_estimate_status TEXT
);
CREATE INDEX project_submission_estimates_idx1_project_id ON project_submission_estimates(project_id);

CREATE TABLE project_submission_estimates_aud(
     rev INTEGER NOT NULL,
     revtype INTEGER,
     id UUID,
     project_id UUID,
     submission_estimate_type TEXT,
     earliest_date DATE,
     most_likely_date DATE,
     latest_date DATE,
     date_when_estimated DATE,
     submission_estimate_status TEXT,
     PRIMARY KEY (rev, id),
     FOREIGN KEY (rev) REFERENCES audit_revisions
);