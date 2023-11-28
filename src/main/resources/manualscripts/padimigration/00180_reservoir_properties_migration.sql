DO $$

DECLARE

    discovery_reservoir_rec RECORD;
    dry_well_reservoir_rec RECORD;

BEGIN

    FOR discovery_reservoir_rec IN (
        SELECT rsd.id rsd_id, prdd.facies, prdd.connectivity
        FROM remi.padi_res_data_disc prdd
        JOIN remi.padi_res_data_header prdh ON prdh.resdat_id = prdd.resdat_id
        JOIN remi.padi_reservoirs pr ON pr.legacy_res_data_id = prdh.resdat_id
        JOIN remi.reservoir_submission_details rsd ON rsd.reservoir = pr.remi_reservoir_id
        WHERE prdh.opportunity_type = 'Discovery'
    )
    LOOP

        IF COALESCE(
            discovery_reservoir_rec.facies,
            discovery_reservoir_rec.connectivity
        ) IS NOT NULL THEN

            INSERT INTO remi.reservoir_properties (
                id,
                reservoir_submission_detail_id,
                reservoir_facies,
                reservoir_connectivity
            ) VALUES (
                gen_random_uuid(),
                discovery_reservoir_rec.rsd_id,
                discovery_reservoir_rec.facies,
                discovery_reservoir_rec.connectivity
            );

        END IF;

    END LOOP;

    FOR dry_well_reservoir_rec IN (
        SELECT rsd.id rsd_id, prdd.facies, prdd.connectivity
        FROM remi.padi_res_data_disc prdd
        JOIN remi.padi_res_data_header prdh ON prdh.resdat_id = prdd.resdat_id
        JOIN remi.submission_details sd ON sd.legacy_res_data_id = prdh.resdat_id
        JOIN remi.reservoir_submission_details rsd ON rsd.submission_detail_id = sd.id
        WHERE sd.submission_type = 'POST_DRILL_DRY_HOLE'
    )
    LOOP

        IF COALESCE(
            dry_well_reservoir_rec.facies,
            dry_well_reservoir_rec.connectivity
        ) IS NOT NULL THEN

            INSERT INTO remi.reservoir_properties (
                id,
                reservoir_submission_detail_id,
                reservoir_facies,
                reservoir_connectivity
            ) VALUES (
                gen_random_uuid(),
                dry_well_reservoir_rec.rsd_id,
                dry_well_reservoir_rec.facies,
                dry_well_reservoir_rec.connectivity
            );

        END IF;

    END LOOP;

END;

$$;