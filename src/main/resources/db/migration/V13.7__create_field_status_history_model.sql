CREATE TABLE field_status_history (
  id UUID PRIMARY KEY NOT NULL,
  field_identifier INTEGER NOT NULL,
  field_status TEXT NOT NULL,
  status_date TIMESTAMP,

  CONSTRAINT field_status_fk_field_id FOREIGN KEY (field_identifier) REFERENCES fields(field_identifier)
);
CREATE INDEX idx_field_status_history_on_status_date ON field_status_history(status_date);

INSERT INTO field_status_history (id, field_identifier, field_status, status_date)
SELECT uuid_generate_v1(), f.field_identifier, f.field_status, now()
FROM remi.fields f;

ALTER TABLE fields DROP COLUMN field_status;