ALTER TABLE fields
    RENAME COLUMN is_on_licence TO is_on_licence_prior_to_field_operator;

ALTER TABLE fields_aud
    RENAME COLUMN is_on_licence TO is_on_licence_prior_to_field_operator;