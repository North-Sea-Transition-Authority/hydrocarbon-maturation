CREATE TABLE remi.pars_undeveloped_discoveries (
    pars_resource_name                      TEXT,
    remi_resource_name                      TEXT,
    year_volumetric_information_relevant_to TEXT,
    operator                                TEXT,
    oil_recoverable_one_c                   DECIMAL,
    oil_recoverable_two_c                   DECIMAL,
    oil_recoverable_three_c                 DECIMAL,
    gas_recoverable_one_c                   DECIMAL,
    gas_recoverable_two_c                   DECIMAL,
    gas_recoverable_three_c                 DECIMAL,
    oil_in_place                            DECIMAL,
    gas_in_place                            DECIMAL,
    remi_resource_id                        UUID
);

CREATE TABLE remi.pars_migration_log (
    pars_resource_name  TEXT,
    remi_resource_name  TEXT,
    remi_resource_id    UUID,
    migration_timestamp timestamptz,
    status              TEXT,
    failure_reason      TEXT
);