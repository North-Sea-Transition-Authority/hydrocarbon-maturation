DO $$
DECLARE

    resource_header_rec RECORD;

BEGIN

    FOR resource_header_rec IN (
        SELECT m.padi_resource_id, m.well_registration_no, m.well_id, prg.remi_resource_id
        FROM remi.padi_header_well_mapping m
        JOIN remi.padi_resource_groupings prg ON prg.legacy_resource_id = m.padi_resource_id
        GROUP BY m.padi_resource_id, m.well_registration_no, m.well_id, prg.remi_resource_id
    )
    LOOP

        UPDATE remi.regulator_managed_information
        SET well_reference = resource_header_rec.well_registration_no,
            well_id = resource_header_rec.well_id
        WHERE resource_id = resource_header_rec.remi_resource_id;

    END LOOP;

END;
$$;