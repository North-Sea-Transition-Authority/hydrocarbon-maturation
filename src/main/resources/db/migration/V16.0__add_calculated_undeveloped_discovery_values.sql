ALTER TABLE undeveloped_discovery_information
    ADD COLUMN oil_equivalent_recoverable_one_c DECIMAL,
    ADD COLUMN oil_equivalent_recoverable_two_c DECIMAL,
    ADD COLUMN oil_equivalent_recoverable_three_c DECIMAL,
    ADD COLUMN calculated_complexity DECIMAL,
    ADD COLUMN percentage_gas DECIMAL;

ALTER TABLE undeveloped_discovery_information_aud
    ADD COLUMN oil_equivalent_recoverable_one_c DECIMAL,
    ADD COLUMN oil_equivalent_recoverable_two_c DECIMAL,
    ADD COLUMN oil_equivalent_recoverable_three_c DECIMAL,
    ADD COLUMN calculated_complexity DECIMAL,
    ADD COLUMN percentage_gas DECIMAL;