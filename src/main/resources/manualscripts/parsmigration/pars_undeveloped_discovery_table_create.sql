CREATE TABLE remi.pars_undeveloped_discoveries (
    resource_id                             UUID,
    resource_name                           TEXT,
    centre_of_interest                      TEXT,
    trap_description                        TEXT,
    organisation_name                       NUMERIC,
    created_date                            DATE,
    date_volumetric_information_relevant_to DATE,
    oil_in_place                            DECIMAL,
    oil_recoverable_one_c                   DECIMAL,
    oil_recoverable_two_c                   DECIMAL,
    oil_recoverable_three_c                 DECIMAL,
    gas_in_place                            DECIMAL,
    gas_recoverable_one_c                   DECIMAL,
    gas_recoverable_two_c                   DECIMAL,
    gas_recoverable_three_c                 DECIMAL
);