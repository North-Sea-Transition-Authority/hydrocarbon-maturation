DO $$
DECLARE

    permeability_rec RECORD;
    has_permeability BOOLEAN;

BEGIN

    FOR permeability_rec IN (
        SELECT sd.id sd_id, r.type, sd.submission_type, prdd.*
        FROM remi.submission_details sd
        JOIN remi.submissions s ON s.id = sd.submission_id
        JOIN remi.resources r ON r.id = s.resource_id
        JOIN remi.padi_res_data_disc prdd ON prdd.resdat_id = sd.legacy_res_data_id
        WHERE r.type = 'DISCOVERY' OR sd.submission_type = 'POST_DRILL_DRY_HOLE'
    )
    LOOP

        has_permeability := FALSE;
        SELECT COALESCE(
            permeability_rec.permeability_min::TEXT,
            permeability_rec.permeability_average::TEXT,
            permeability_rec.permeability_max::TEXT,
            permeability_rec.permeability_average_type,
            permeability_rec.permeability_source,
            permeability_rec.permeability_comment
        ) IS NOT NULL
        INTO has_permeability;

        -- dry hole has specific question on if permeability details exist
        IF has_permeability OR permeability_rec.submission_type = 'POST_DRILL_DRY_HOLE' THEN

            INSERT INTO remi.permeability_details(
                id,
                submission_detail_id,
                has_permeability,
                permeability_min,
                permeability_max,
                permeability_average,
                average_method_used,
                source,
                source_other,
                permeability_comments,
                kv_kh_average
            ) VALUES (
                gen_random_uuid(),
                permeability_rec.sd_id,
                has_permeability,
                permeability_rec.permeability_min,
                permeability_rec.permeability_max,
                permeability_rec.permeability_average,
                'ARITHMETIC', -- PaDI only has arithmetic
                CASE
                    WHEN permeability_rec.permeability_source = 'Conventional core' THEN 'CONVENTIONAL'
                    WHEN permeability_rec.permeability_source = 'Sidewall core' THEN 'SIDEWALL'
                    WHEN permeability_rec.permeability_source = 'Formation Pressure Tester' THEN 'FORMATION_PRESSURE_TESTER'
                    WHEN permeability_rec.permeability_source = 'Log transform' THEN 'LOG_TRANSFORM'
                    WHEN permeability_rec.permeability_source IS NOT NULL THEN 'OTHER'
                END,
                NULL, -- other types don't appear to have comments. Placing any comments in the permeability_comments field
                permeability_rec.permeability_comment,
                NULL -- does this exist in PaDI?
            );

        END IF;

    END LOOP;

END;

$$;