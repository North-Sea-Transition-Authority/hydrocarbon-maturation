ALTER TABLE projects
    RENAME COLUMN project_status TO project_phase;
ALTER TABLE projects
    ADD COLUMN project_status TEXT;

ALTER TABLE projects_aud
    RENAME COLUMN project_status TO project_phase;
ALTER TABLE projects_aud
    ADD COLUMN project_status TEXT;