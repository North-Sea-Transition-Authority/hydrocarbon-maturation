CREATE TABLE field_licences (
    id UUID PRIMARY KEY NOT NULL,
    field_identifier INTEGER NOT NULL,
        CONSTRAINT field_wells_fk FOREIGN KEY (field_identifier) REFERENCES fields(field_identifier),
    licence_reference TEXT,
    licence_id INTEGER
);

ALTER TABLE fields
ADD COLUMN is_on_licence BOOLEAN;

CREATE INDEX field_licences_idx1 on field_licences (field_identifier);