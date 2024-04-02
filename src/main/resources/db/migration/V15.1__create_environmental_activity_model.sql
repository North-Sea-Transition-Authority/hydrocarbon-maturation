CREATE TABLE environmental_activity(
    id UUID PRIMARY KEY,
    project_id UUID NOT NULL,
        CONSTRAINT project_submission_estimates_fk_projects FOREIGN KEY (project_id) REFERENCES projects(id),
    submission_date DATE,
    consultation_start_date DATE,
    consultation_closing_date DATE
);
CREATE INDEX environmental_activity_idx1_project_id ON environmental_activity(project_id);

CREATE TABLE environmental_activity_aud(
    rev INTEGER NOT NULL,
    revtype INTEGER,
    id UUID,
    project_id UUID,
    submission_date DATE,
    consultation_start_date DATE,
    consultation_closing_date DATE,
    PRIMARY KEY (rev, id),
    FOREIGN KEY (rev) REFERENCES audit_revisions
);