ALTER TABLE regulator_managed_information
    ADD COLUMN linked_to_shelved_cop_field BOOLEAN;

ALTER TABLE regulator_managed_information_aud
    ADD COLUMN linked_to_shelved_cop_field BOOLEAN;