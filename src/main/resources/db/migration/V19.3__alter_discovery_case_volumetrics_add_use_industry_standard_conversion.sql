ALTER TABLE combined_case_volumetric_totals
    ADD COLUMN use_industry_standard_conversion BOOLEAN;

ALTER TABLE gas_case_volumetric_totals
    ADD COLUMN use_industry_standard_conversion BOOLEAN;

ALTER TABLE oil_case_volumetric_totals
    ADD COLUMN use_industry_standard_conversion BOOLEAN;