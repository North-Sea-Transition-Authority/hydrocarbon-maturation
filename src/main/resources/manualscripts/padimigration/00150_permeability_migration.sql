DO $$
DECLARE

    discovery_permeability_rec RECORD;
    dry_well_permeability_rec RECORD;
    has_permeability_flag BOOLEAN;

BEGIN

    FOR discovery_permeability_rec IN (
        SELECT rsd.id rsd_id, prdd.*
        FROM remi.padi_res_data_disc prdd
        JOIN remi.padi_res_data_header prdh ON prdh.resdat_id = prdd.resdat_id
        JOIN remi.padi_reservoirs pr ON pr.legacy_res_data_id = prdh.resdat_id
        JOIN remi.reservoir_submission_details rsd ON rsd.reservoir = pr.remi_reservoir_id
        WHERE prdh.opportunity_type = 'Discovery'
    )
    LOOP

        has_permeability_flag := FALSE;
        SELECT COALESCE(
            discovery_permeability_rec.permeability_min::TEXT,
            discovery_permeability_rec.permeability_average::TEXT,
            discovery_permeability_rec.permeability_max::TEXT,
            discovery_permeability_rec.permeability_average_type,
            discovery_permeability_rec.permeability_source,
            discovery_permeability_rec.permeability_comment
        ) IS NOT NULL
        INTO has_permeability_flag;

        -- dry hole has specific question on if permeability details exist
        IF has_permeability_flag THEN

            INSERT INTO remi.permeability_details(
                id,
                reservoir_submission_detail_id,
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
                discovery_permeability_rec.rsd_id,
                has_permeability_flag,
                discovery_permeability_rec.permeability_min,
                discovery_permeability_rec.permeability_max,
                discovery_permeability_rec.permeability_average,
                'ARITHMETIC', -- PaDI only has arithmetic
                CASE
                    WHEN discovery_permeability_rec.permeability_source = 'Conventional core' THEN 'CONVENTIONAL'
                    WHEN discovery_permeability_rec.permeability_source = 'Sidewall core' THEN 'SIDEWALL'
                    WHEN discovery_permeability_rec.permeability_source = 'Formation Pressure Tester' THEN 'FORMATION_PRESSURE_TESTER'
                    WHEN discovery_permeability_rec.permeability_source = 'Log transform' THEN 'LOG_TRANSFORM'
                    WHEN discovery_permeability_rec.permeability_source IS NOT NULL THEN 'OTHER'
                END,
                NULL, -- other types don't appear to have comments. Placing any comments in the permeability_comments field
                discovery_permeability_rec.permeability_comment,
                NULL -- does this exist in PaDI?
            );

        END IF;

    END LOOP;

    FOR dry_well_permeability_rec IN (
        SELECT rsd.id rsd_id, prdd.*
        FROM remi.padi_res_data_disc prdd
        JOIN remi.padi_res_data_header prdh ON prdh.resdat_id = prdd.resdat_id
        JOIN remi.submission_details sd ON sd.legacy_res_data_id = prdh.resdat_id
        JOIN remi.reservoir_submission_details rsd ON rsd.submission_detail_id = sd.id
        WHERE sd.submission_type = 'POST_DRILL_DRY_HOLE'
    )
    LOOP

        has_permeability_flag := FALSE;
        SELECT COALESCE(
           dry_well_permeability_rec.permeability_min::TEXT,
           dry_well_permeability_rec.permeability_average::TEXT,
           dry_well_permeability_rec.permeability_max::TEXT,
           dry_well_permeability_rec.permeability_average_type,
           dry_well_permeability_rec.permeability_source,
           dry_well_permeability_rec.permeability_comment
        ) IS NOT NULL
        INTO has_permeability_flag;

        -- dry hole has specific question on if permeability details exist
        IF has_permeability_flag THEN

            INSERT INTO remi.permeability_details(
                id,
                reservoir_submission_detail_id,
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
                dry_well_permeability_rec.rsd_id,
                has_permeability_flag,
                dry_well_permeability_rec.permeability_min,
                dry_well_permeability_rec.permeability_max,
                dry_well_permeability_rec.permeability_average,
                'ARITHMETIC', -- PaDI only has arithmetic
                CASE
                    WHEN dry_well_permeability_rec.permeability_source = 'Conventional core' THEN 'CONVENTIONAL'
                    WHEN dry_well_permeability_rec.permeability_source = 'Sidewall core' THEN 'SIDEWALL'
                    WHEN dry_well_permeability_rec.permeability_source = 'Formation Pressure Tester' THEN 'FORMATION_PRESSURE_TESTER'
                    WHEN dry_well_permeability_rec.permeability_source = 'Log transform' THEN 'LOG_TRANSFORM'
                    WHEN dry_well_permeability_rec.permeability_source IS NOT NULL THEN 'OTHER'
                END,
                NULL, -- other types don't appear to have comments. Placing any comments in the permeability_comments field
                dry_well_permeability_rec.permeability_comment,
                NULL -- does this exist in PaDI?
            );

        END IF;

    END LOOP;

END;

$$;