ALTER TABLE gas_case_outputs
    ADD COLUMN use_industry_standard_conversion BOOLEAN;

ALTER TABLE mixed_case_outputs
    ADD COLUMN use_industry_standard_conversion BOOLEAN;

ALTER TABLE oil_case_outputs
    ADD COLUMN use_industry_standard_conversion BOOLEAN;
