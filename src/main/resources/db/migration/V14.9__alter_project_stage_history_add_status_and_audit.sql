ALTER TABLE project_stage_history
ADD COLUMN project_stage_status TEXT;

CREATE TABLE project_stage_history_aud(
    rev INTEGER NOT NULL,
    revtype INTEGER,
    id UUID,
    project_id UUID,
    stage TEXT,
    stage_date DATE,
    project_stage_status TEXT,
    PRIMARY KEY (rev, id),
    FOREIGN KEY (rev) REFERENCES audit_revisions
);