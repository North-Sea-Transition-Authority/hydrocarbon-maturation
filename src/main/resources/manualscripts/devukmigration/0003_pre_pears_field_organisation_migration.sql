DO $$
DECLARE

    field_operator_rec RECORD;

BEGIN

    FOR field_operator_rec IN (
        SELECT o.*
        FROM remi.devuk_pre_pears_field_organisations o
    )
    LOOP

        UPDATE remi.fields f
        SET
            responsible_party_prior_to_field_operator_id = field_operator_rec.organ_id,
            organisation_name = field_operator_rec.name,
            organisation_registered_number = field_operator_rec.registered_number
        WHERE f.field_identifier = field_operator_rec.field_identifier;

    END LOOP;

END;
$$;