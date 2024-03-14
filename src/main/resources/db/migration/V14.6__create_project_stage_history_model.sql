CREATE TABLE project_stage_history(
    id UUID PRIMARY KEY NOT NULL,
    project_id UUID NOT NULL,
        CONSTRAINT project_stage_history_fk_project FOREIGN KEY (project_id) REFERENCES projects(id),
    stage TEXT,
    stage_date DATE
);
CREATE INDEX project_stage_history_idx_projects ON project_stage_history(project_id);