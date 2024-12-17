INSERT INTO remi.devuk_historic_equity_partners_and_operators (
    field_identifier,
    start_date,
    end_date,
    organ_id,
    organisation_name,
    operator_flag
)
SELECT
    m.field_identifier,
    TO_DATE(m.start_date,'DD/MM/YYYY'),
    TO_DATE(m.end_date,'DD/MM/YYYY'),
    m.organ_id,
    m.organisation_name,
    m.operator_flag
FROM remi.devuk_historic_equity_partners_migration m