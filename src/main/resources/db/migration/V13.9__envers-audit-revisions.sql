CREATE TABLE audit_revisions (
  rev SERIAL PRIMARY KEY,
  updated_at TIMESTAMPTZ,
  updated_by_user_id VARCHAR(255),
  updated_by_proxy_user_id VARCHAR(255)
);