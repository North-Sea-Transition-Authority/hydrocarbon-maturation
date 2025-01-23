DO $$

DECLARE

    discovery_rec RECORD;

BEGIN

    FOR discovery_rec IN (
        SELECT rsd.id rsd_id, LOWER(prdh.hc_case) hydrocarbon_case
        FROM remi.reservoir_submission_details rsd
        JOIN remi.submission_details sd ON rsd.submission_detail_id = sd.id
        JOIN remi.submissions s ON s.id = sd.submission_id
        JOIN remi.resources r ON s.resource_id = r.id
        JOIN remi.padi_res_data_header prdh ON prdh.resdat_id = sd.legacy_res_data_id
        WHERE r.type = 'DISCOVERY'
    )
    LOOP

        IF discovery_rec.hydrocarbon_case IN ('gas','condensate') THEN

            INSERT INTO remi.reservoir_hydrocarbons (
                id,
                reservoir_submission_detail_id,
                hydrocarbons_discovered
            ) VALUES (
                 gen_random_uuid(),
                 discovery_rec.rsd_id,
                 'GAS_ONLY'
             );

        ELSIF discovery_rec.hydrocarbon_case = 'oil' THEN

            INSERT INTO remi.reservoir_hydrocarbons (
                id,
                reservoir_submission_detail_id,
                hydrocarbons_discovered
            ) VALUES (
                 gen_random_uuid(),
                 discovery_rec.rsd_id,
                 'OIL_ONLY'
             );

        ELSIF discovery_rec.hydrocarbon_case = 'mixed oil and gas' THEN

            INSERT INTO remi.reservoir_hydrocarbons (
                id,
                reservoir_submission_detail_id,
                hydrocarbons_discovered
            ) VALUES (
             gen_random_uuid(),
             discovery_rec.rsd_id,
             'MIXED_OIL_AND_GAS'
            );

        END IF;

    END LOOP;

END;

$$;