DO $$
DECLARE

    single_reservoir_rec RECORD;
    oil_totals_exist INTEGER;
    gas_totals_exist INTEGER;
    combined_totals_exist INTEGER;

BEGIN

    FOR single_reservoir_rec IN (
        WITH res_count AS (
            SELECT sd.id sd_id, COUNT(rsd.id) rsd_count
            FROM remi.resources r
            JOIN remi.submissions s ON s.resource_id = r.id
            JOIN remi.submission_details sd ON sd.submission_id = s.id
            JOIN remi.reservoir_submission_details rsd ON rsd.submission_detail_id = sd.id
            AND r.type = 'DISCOVERY'
            GROUP BY sd.id
        )
        SELECT
            rc.sd_id,
            rsd.id rsd_id,
            oco.use_industry_standard_conversion oil_use_industry_standard,
            gco.has_associated_condensate_recoverable_resources gas_associated_condensate,
            gco.use_industry_standard_conversion gas_use_industry_standard,
            mco.has_associated_condensate_recoverable_resources combined_associated_condensate,
            mco.use_industry_standard_conversion combined_use_industry_standard,
            pd.*
        FROM res_count rc
        JOIN remi.reservoir_submission_details rsd ON rsd.submission_detail_id = rc.sd_id
        JOIN remi.probability_distributions pd ON pd.reservoir_submission_detail_id = rsd.id
        LEFT JOIN remi.oil_case_outputs oco ON oco.reservoir_submission_detail_id = rsd.id
        LEFT JOIN remi.gas_case_outputs gco ON gco.reservoir_submission_detail_id = rsd.id
        LEFT JOIN remi.mixed_case_outputs mco ON mco.reservoir_submission_detail_id = rsd.id
        WHERE rc.rsd_count = 1
    )
    LOOP
        
        -- check if totals exist for each type so only one record is created per submission detail
        SELECT 1
        INTO oil_totals_exist
        WHERE EXISTS (
            SELECT 1
            FROM remi.oil_case_volumetric_totals o 
            WHERE o.submission_detail_id = single_reservoir_rec.sd_id
        );

        SELECT 1
        INTO gas_totals_exist
        WHERE EXISTS (
            SELECT 1
            FROM remi.gas_case_volumetric_totals o
            WHERE o.submission_detail_id = single_reservoir_rec.sd_id
        );

        SELECT 1
        INTO combined_totals_exist
        WHERE EXISTS (
            SELECT 1
            FROM remi.combined_case_volumetric_totals o
            WHERE o.submission_detail_id = single_reservoir_rec.sd_id
        );

        IF single_reservoir_rec.oil_use_industry_standard IS NOT NULL AND oil_totals_exist IS NULL THEN

            INSERT INTO remi.oil_case_volumetric_totals (
                id,
                submission_detail_id,
                use_industry_standard_conversion
            ) VALUES (
                 gen_random_uuid(),
                 single_reservoir_rec.sd_id,
                 single_reservoir_rec.oil_use_industry_standard
             );

        END IF;

        IF (single_reservoir_rec.gas_associated_condensate IS NOT NULL OR single_reservoir_rec.gas_use_industry_standard IS NOT NULL) AND gas_totals_exist IS NULL THEN

            INSERT INTO remi.gas_case_volumetric_totals (
                id,
                submission_detail_id,
                has_associated_condensate_recoverable_resources,
                use_industry_standard_conversion
            ) VALUES (
                gen_random_uuid(),
                single_reservoir_rec.sd_id,
                single_reservoir_rec.gas_associated_condensate,
                single_reservoir_rec.gas_use_industry_standard
            );

        END IF;

        IF (single_reservoir_rec.combined_associated_condensate IS NOT NULL OR single_reservoir_rec.combined_use_industry_standard IS NOT NULL) AND combined_totals_exist IS NULL THEN

            INSERT INTO remi.combined_case_volumetric_totals (
                id,
                submission_detail_id,
                has_associated_condensate_recoverable_resources,
                use_industry_standard_conversion
            ) VALUES (
                gen_random_uuid(),
                single_reservoir_rec.sd_id,
                single_reservoir_rec.combined_associated_condensate,
                single_reservoir_rec.combined_use_industry_standard
            );

        END IF;

        IF single_reservoir_rec.instance = 'OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY' THEN

            INSERT INTO remi.probability_distributions (
                id,
                submission_detail_id,
                distribution_type,
                instance,
                min_value,
                p99,
                p90,
                mode_value,
                p50,
                mean_value,
                p10,
                p1,
                max_value,
                constant_value,
                most_likely_value,
                comments,
                three_c,
                two_c,
                one_c,
                other_distribution_type,
                reservoir_submission_detail_id
            ) VALUES (
                 gen_random_uuid(),
                 single_reservoir_rec.sd_id,
                 single_reservoir_rec.distribution_type,
                 'TOTAL_OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY',
                 single_reservoir_rec.min_value,
                 single_reservoir_rec.p99,
                 single_reservoir_rec.p90,
                 single_reservoir_rec.mode_value,
                 single_reservoir_rec.p50,
                 single_reservoir_rec.mean_value,
                 single_reservoir_rec.p10,
                 single_reservoir_rec.p1,
                 single_reservoir_rec.max_value,
                 single_reservoir_rec.constant_value,
                 single_reservoir_rec.most_likely_value,
                 single_reservoir_rec.comments,
                 single_reservoir_rec.three_c,
                 single_reservoir_rec.two_c,
                 single_reservoir_rec.one_c,
                 single_reservoir_rec.other_distribution_type,
                 NULL
             );

        END IF;

        IF single_reservoir_rec.instance = 'OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY' THEN

            INSERT INTO remi.probability_distributions (
                id,
                submission_detail_id,
                distribution_type,
                instance,
                min_value,
                p99,
                p90,
                mode_value,
                p50,
                mean_value,
                p10,
                p1,
                max_value,
                constant_value,
                most_likely_value,
                comments,
                three_c,
                two_c,
                one_c,
                other_distribution_type,
                reservoir_submission_detail_id
            ) VALUES (
                 gen_random_uuid(),
                 single_reservoir_rec.sd_id,
                 single_reservoir_rec.distribution_type,
                 'TOTAL_OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY',
                 single_reservoir_rec.min_value,
                 single_reservoir_rec.p99,
                 single_reservoir_rec.p90,
                 single_reservoir_rec.mode_value,
                 single_reservoir_rec.p50,
                 single_reservoir_rec.mean_value,
                 single_reservoir_rec.p10,
                 single_reservoir_rec.p1,
                 single_reservoir_rec.max_value,
                 single_reservoir_rec.constant_value,
                 single_reservoir_rec.most_likely_value,
                 single_reservoir_rec.comments,
                 single_reservoir_rec.three_c,
                 single_reservoir_rec.two_c,
                 single_reservoir_rec.one_c,
                 single_reservoir_rec.other_distribution_type,
                 NULL
             );

        END IF;

        IF single_reservoir_rec.instance = 'OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY' THEN

            INSERT INTO remi.probability_distributions (
                id,
                submission_detail_id,
                distribution_type,
                instance,
                min_value,
                p99,
                p90,
                mode_value,
                p50,
                mean_value,
                p10,
                p1,
                max_value,
                constant_value,
                most_likely_value,
                comments,
                three_c,
                two_c,
                one_c,
                other_distribution_type,
                reservoir_submission_detail_id
            ) VALUES (
                 gen_random_uuid(),
                 single_reservoir_rec.sd_id,
                 single_reservoir_rec.distribution_type,
                 'TOTAL_OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY',
                 single_reservoir_rec.min_value,
                 single_reservoir_rec.p99,
                 single_reservoir_rec.p90,
                 single_reservoir_rec.mode_value,
                 single_reservoir_rec.p50,
                 single_reservoir_rec.mean_value,
                 single_reservoir_rec.p10,
                 single_reservoir_rec.p1,
                 single_reservoir_rec.max_value,
                 single_reservoir_rec.constant_value,
                 single_reservoir_rec.most_likely_value,
                 single_reservoir_rec.comments,
                 single_reservoir_rec.three_c,
                 single_reservoir_rec.two_c,
                 single_reservoir_rec.one_c,
                 single_reservoir_rec.other_distribution_type,
                 NULL
             );

        END IF;

        IF single_reservoir_rec.instance = 'GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY' THEN

            INSERT INTO remi.probability_distributions (
                id,
                submission_detail_id,
                distribution_type,
                instance,
                min_value,
                p99,
                p90,
                mode_value,
                p50,
                mean_value,
                p10,
                p1,
                max_value,
                constant_value,
                most_likely_value,
                comments,
                three_c,
                two_c,
                one_c,
                other_distribution_type,
                reservoir_submission_detail_id
            ) VALUES (
                 gen_random_uuid(),
                 single_reservoir_rec.sd_id,
                 single_reservoir_rec.distribution_type,
                 'TOTAL_GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY',
                 single_reservoir_rec.min_value,
                 single_reservoir_rec.p99,
                 single_reservoir_rec.p90,
                 single_reservoir_rec.mode_value,
                 single_reservoir_rec.p50,
                 single_reservoir_rec.mean_value,
                 single_reservoir_rec.p10,
                 single_reservoir_rec.p1,
                 single_reservoir_rec.max_value,
                 single_reservoir_rec.constant_value,
                 single_reservoir_rec.most_likely_value,
                 single_reservoir_rec.comments,
                 single_reservoir_rec.three_c,
                 single_reservoir_rec.two_c,
                 single_reservoir_rec.one_c,
                 single_reservoir_rec.other_distribution_type,
                 NULL
             );

        END IF;

        IF single_reservoir_rec.instance = 'GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY' THEN

            INSERT INTO remi.probability_distributions (
                id,
                submission_detail_id,
                distribution_type,
                instance,
                min_value,
                p99,
                p90,
                mode_value,
                p50,
                mean_value,
                p10,
                p1,
                max_value,
                constant_value,
                most_likely_value,
                comments,
                three_c,
                two_c,
                one_c,
                other_distribution_type,
                reservoir_submission_detail_id
            ) VALUES (
                 gen_random_uuid(),
                 single_reservoir_rec.sd_id,
                 single_reservoir_rec.distribution_type,
                 'TOTAL_GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY',
                 single_reservoir_rec.min_value,
                 single_reservoir_rec.p99,
                 single_reservoir_rec.p90,
                 single_reservoir_rec.mode_value,
                 single_reservoir_rec.p50,
                 single_reservoir_rec.mean_value,
                 single_reservoir_rec.p10,
                 single_reservoir_rec.p1,
                 single_reservoir_rec.max_value,
                 single_reservoir_rec.constant_value,
                 single_reservoir_rec.most_likely_value,
                 single_reservoir_rec.comments,
                 single_reservoir_rec.three_c,
                 single_reservoir_rec.two_c,
                 single_reservoir_rec.one_c,
                 single_reservoir_rec.other_distribution_type,
                 NULL
             );

        END IF;

        IF single_reservoir_rec.instance = 'OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY' THEN

            INSERT INTO remi.probability_distributions (
                id,
                submission_detail_id,
                distribution_type,
                instance,
                min_value,
                p99,
                p90,
                mode_value,
                p50,
                mean_value,
                p10,
                p1,
                max_value,
                constant_value,
                most_likely_value,
                comments,
                three_c,
                two_c,
                one_c,
                other_distribution_type,
                reservoir_submission_detail_id
            ) VALUES (
                 gen_random_uuid(),
                 single_reservoir_rec.sd_id,
                 single_reservoir_rec.distribution_type,
                 'TOTAL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY',
                 single_reservoir_rec.min_value,
                 single_reservoir_rec.p99,
                 single_reservoir_rec.p90,
                 single_reservoir_rec.mode_value,
                 single_reservoir_rec.p50,
                 single_reservoir_rec.mean_value,
                 single_reservoir_rec.p10,
                 single_reservoir_rec.p1,
                 single_reservoir_rec.max_value,
                 single_reservoir_rec.constant_value,
                 single_reservoir_rec.most_likely_value,
                 single_reservoir_rec.comments,
                 single_reservoir_rec.three_c,
                 single_reservoir_rec.two_c,
                 single_reservoir_rec.one_c,
                 single_reservoir_rec.other_distribution_type,
                 NULL
             );
            
        END IF;

        IF single_reservoir_rec.instance = 'OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_INDUSTRY_STANDARD_DISCOVERY' THEN

            INSERT INTO remi.probability_distributions (
                id,
                submission_detail_id,
                distribution_type,
                instance,
                min_value,
                p99,
                p90,
                mode_value,
                p50,
                mean_value,
                p10,
                p1,
                max_value,
                constant_value,
                most_likely_value,
                comments,
                three_c,
                two_c,
                one_c,
                other_distribution_type,
                reservoir_submission_detail_id
            ) VALUES (
                 gen_random_uuid(),
                 single_reservoir_rec.sd_id,
                 single_reservoir_rec.distribution_type,
                 'TOTAL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_INDUSTRY_STANDARD_DISCOVERY',
                 single_reservoir_rec.min_value,
                 single_reservoir_rec.p99,
                 single_reservoir_rec.p90,
                 single_reservoir_rec.mode_value,
                 single_reservoir_rec.p50,
                 single_reservoir_rec.mean_value,
                 single_reservoir_rec.p10,
                 single_reservoir_rec.p1,
                 single_reservoir_rec.max_value,
                 single_reservoir_rec.constant_value,
                 single_reservoir_rec.most_likely_value,
                 single_reservoir_rec.comments,
                 single_reservoir_rec.three_c,
                 single_reservoir_rec.two_c,
                 single_reservoir_rec.one_c,
                 single_reservoir_rec.other_distribution_type,
                 NULL
             );

        END IF;

        IF single_reservoir_rec.instance = 'OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY' THEN

            INSERT INTO remi.probability_distributions (
                id,
                submission_detail_id,
                distribution_type,
                instance,
                min_value,
                p99,
                p90,
                mode_value,
                p50,
                mean_value,
                p10,
                p1,
                max_value,
                constant_value,
                most_likely_value,
                comments,
                three_c,
                two_c,
                one_c,
                other_distribution_type,
                reservoir_submission_detail_id
            ) VALUES (
                 gen_random_uuid(),
                 single_reservoir_rec.sd_id,
                 single_reservoir_rec.distribution_type,
                 'TOTAL_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY',
                 single_reservoir_rec.min_value,
                 single_reservoir_rec.p99,
                 single_reservoir_rec.p90,
                 single_reservoir_rec.mode_value,
                 single_reservoir_rec.p50,
                 single_reservoir_rec.mean_value,
                 single_reservoir_rec.p10,
                 single_reservoir_rec.p1,
                 single_reservoir_rec.max_value,
                 single_reservoir_rec.constant_value,
                 single_reservoir_rec.most_likely_value,
                 single_reservoir_rec.comments,
                 single_reservoir_rec.three_c,
                 single_reservoir_rec.two_c,
                 single_reservoir_rec.one_c,
                 single_reservoir_rec.other_distribution_type,
                 NULL
             );

        END IF;

    END LOOP;

END;
$$;