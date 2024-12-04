CREATE TABLE remi.devuk_field (
  field_identifier NUMERIC,
  name TEXT,
  status NUMERIC,
  sub_type TEXT,
  infrastructure_host_flag TEXT,
  on_offshore TEXT,
  median_line_flag TEXT,
  parent_field_identifier NUMERIC,
  geographic_area_mnem TEXT,
  field_prt_number NUMERIC,
  consulting_authorities TEXT
);

CREATE TABLE remi.devuk_pre_pears_field_organisations (
    field_identifier  INTEGER,
    organ_id          INTEGER,
    name              TEXT,
    registered_number TEXT
);