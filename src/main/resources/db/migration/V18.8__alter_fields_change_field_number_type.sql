ALTER TABLE fields
    ALTER COLUMN field_number TYPE INTEGER USING field_number::integer;

ALTER TABLE fields_aud
    ALTER COLUMN field_number TYPE INTEGER USING field_number::integer;