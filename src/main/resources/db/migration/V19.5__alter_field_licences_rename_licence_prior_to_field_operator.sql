ALTER TABLE field_licences
    RENAME COLUMN licence_id TO licence_prior_to_field_operator_id;

ALTER TABLE field_licences
    RENAME COLUMN licence_reference TO licence_prior_to_field_operator_reference;