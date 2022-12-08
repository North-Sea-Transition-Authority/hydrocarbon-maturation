ALTER TABLE general_details
    ADD resource_on_a_licence BOOLEAN;

CREATE TABLE general_details_licences (
    id UUID PRIMARY KEY,
    general_details_id UUID NOT NULL,
    licence_reference TEXT,
    licence_id INTEGER,
    CONSTRAINT general_details_licences_fk FOREIGN KEY (general_details_id) REFERENCES general_details (id)
);

CREATE INDEX general_details_licences_idx1 ON general_details_licences(general_details_id);