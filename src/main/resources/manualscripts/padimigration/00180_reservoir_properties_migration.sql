DO $$

DECLARE

    reservoir_rec RECORD;

BEGIN

    FOR reservoir_rec IN (
        SELECT sd.id sd_id, prdd.facies, prdd.connectivity
        FROM remi.submission_details sd
        JOIN remi.submissions s ON s.id = sd.submission_id
        JOIN remi.resources r ON r.id = s.resource_id
        JOIN remi.padi_res_data_disc prdd ON prdd.resdat_id = sd.legacy_res_data_id
        WHERE r.type = 'DISCOVERY' OR sd.submission_type = 'POST_DRILL_DRY_HOLE'
    )
    LOOP

        IF COALESCE(
            reservoir_rec.facies,
            reservoir_rec.connectivity
        ) IS NOT NULL THEN

            INSERT INTO remi.reservoir_properties (
                id,
                submission_detail_id,
                reservoir_facies,
                reservoir_connectivity
            ) VALUES (
                gen_random_uuid(),
                reservoir_rec.sd_id,
                reservoir_rec.facies,
                reservoir_rec.connectivity
            );

        END IF;

    END LOOP;

END;

$$;