ALTER TABLE fields
    ADD COLUMN field_located_in_ukcs BOOLEAN,
    ADD COLUMN field_located_in_england BOOLEAN;

ALTER TABLE fields_aud
    ADD COLUMN field_located_in_ukcs BOOLEAN,
    ADD COLUMN field_located_in_england BOOLEAN;