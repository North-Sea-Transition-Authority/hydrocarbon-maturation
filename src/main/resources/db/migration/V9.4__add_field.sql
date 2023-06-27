CREATE TABLE fields (
  field_identifier INTEGER PRIMARY KEY,
  resource_id UUID NOT NULL,
  field_status TEXT,
  legal_field_name TEXT,
  corrected_legal_field_name TEXT,
  common_name_for_field TEXT,
  responsible_organisation_id INTEGER,
  onshore_offshore TEXT,
  geographical_area TEXT,
  responsible_team_type TEXT,
  field_number TEXT,
  infrastructure_host BOOLEAN,
  determination_status TEXT,
  parent_determined_field_id INTEGER,
  CONSTRAINT fields_fk FOREIGN KEY (resource_id) REFERENCES resources (id)
);

CREATE INDEX fields_idx1 ON fields (resource_id);

-- Last field identifier on live is 7752
CREATE SEQUENCE field_identifier_seq START WITH 8000;