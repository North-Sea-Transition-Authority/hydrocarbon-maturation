DO $$
DECLARE

    oil_and_gas_output_rec RECORD;
    mixed_output_rec RECORD;
    custom_dist_type TEXT := 'CUSTOM';

BEGIN
    CREATE OR REPLACE FUNCTION calculate_summed_equiv(oil_val NUMERIC, cond_val NUMERIC, gas_val NUMERIC, ass_gas_val NUMERIC)
    RETURNS NUMERIC AS $calculate_summed_equiv$
    BEGIN
        oil_val := COALESCE(oil_val, 0);
        cond_val := COALESCE(cond_val, 0);
        gas_val := COALESCE(gas_val, 0);
        ass_gas_val := COALESCE(ass_gas_val, 0);
        RETURN oil_val + cond_val + ((gas_val + ass_gas_val)/5.8);
    END;
    $calculate_summed_equiv$ LANGUAGE plpgsql;


    -- Get oil and gas outputs and apply to either oil or gas hydrocarbon case
    -- as we can't rely on the PaDI hydrocarbon case to be accurate for the information supplied
    FOR oil_and_gas_output_rec IN (
        SELECT prdvo.*, sd.id sd_id, r.type resource_type, rsd.id rsd_id
        FROM remi.padi_res_data_vol_outputs prdvo
        JOIN remi.submission_details sd ON sd.legacy_res_data_id = prdvo.resdat_id
        JOIN remi.reservoir_submission_details rsd ON rsd.submission_detail_id = sd.id
        JOIN remi.submissions s ON s.id = sd.submission_id
        JOIN remi.resources r ON r.id = s.resource_id
        JOIN remi.padi_res_data_header prdh ON prdh.resdat_id = sd.legacy_res_data_id
        WHERE sd.submission_type != 'POST_DRILL_DRY_HOLE'
        AND LOWER(prdh.hc_case) IN ('oil','gas','condensate')
        AND r.type = 'PROSPECT'
    )
    LOOP

        -- oil case outputs
        INSERT INTO remi.oil_case_outputs (
            id,
            reservoir_submission_detail_id,
            has_associated_gas_recoverable_resources,
            use_industry_standard_conversion
        ) VALUES (
            gen_random_uuid(),
            oil_and_gas_output_rec.rsd_id,
            FALSE, -- updated later if required
            FALSE -- updated later if required
        );

        -- oil in place
        IF COALESCE(
            oil_and_gas_output_rec.oil_inplace_p99,
            oil_and_gas_output_rec.oil_inplace_p90,
            oil_and_gas_output_rec.oil_inplace_mode,
            oil_and_gas_output_rec.oil_inplace_p50,
            oil_and_gas_output_rec.oil_inplace_mean,
            oil_and_gas_output_rec.oil_inplace_p10,
            oil_and_gas_output_rec.oil_inplace_p01
        ) IS NOT NULL THEN

            INSERT INTO remi.probability_distributions (
                id,
                submission_detail_id,
                distribution_type,
                instance,
                p99,
                p90,
                mode_value,
                p50,
                mean_value,
                p10,
                p1,
                comments,
                reservoir_submission_detail_id
            ) VALUES (
                gen_random_uuid(),
                oil_and_gas_output_rec.sd_id,
                custom_dist_type,
                'OIL_OUTPUTS_OIL_IN_PLACE_PROSPECT',
                abs(oil_and_gas_output_rec.oil_inplace_p99),
                abs(oil_and_gas_output_rec.oil_inplace_p90),
                abs(oil_and_gas_output_rec.oil_inplace_mode),
                abs(oil_and_gas_output_rec.oil_inplace_p50),
                abs(oil_and_gas_output_rec.oil_inplace_mean),
                abs(oil_and_gas_output_rec.oil_inplace_p10),
                abs(oil_and_gas_output_rec.oil_inplace_p01),
                oil_and_gas_output_rec.oil_inplace_comment,
                oil_and_gas_output_rec.rsd_id
            );

        END IF;

        -- oil recoverable resources
        IF COALESCE(
            oil_and_gas_output_rec.oil_recoverable_p99,
            oil_and_gas_output_rec.oil_recoverable_p90,
            oil_and_gas_output_rec.oil_recoverable_mode,
            oil_and_gas_output_rec.oil_recoverable_p50,
            oil_and_gas_output_rec.oil_recoverable_mean,
            oil_and_gas_output_rec.oil_recoverable_p10,
            oil_and_gas_output_rec.oil_recoverable_p01
        ) IS NOT NULL THEN

            INSERT INTO remi.probability_distributions (
                id,
                submission_detail_id,
                distribution_type,
                instance,
                p99,
                p90,
                mode_value,
                p50,
                mean_value,
                p10,
                p1,
                comments,
                reservoir_submission_detail_id
            ) VALUES (
                gen_random_uuid(),
                oil_and_gas_output_rec.sd_id,
                custom_dist_type,
                'OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_PROSPECT',
                abs(oil_and_gas_output_rec.oil_recoverable_p99),
                abs(oil_and_gas_output_rec.oil_recoverable_p90),
                abs(oil_and_gas_output_rec.oil_recoverable_mode),
                abs(oil_and_gas_output_rec.oil_recoverable_p50),
                abs(oil_and_gas_output_rec.oil_recoverable_mean),
                abs(oil_and_gas_output_rec.oil_recoverable_p10),
                abs(oil_and_gas_output_rec.oil_recoverable_p01),
                oil_and_gas_output_rec.oil_recoverable_comment,
                oil_and_gas_output_rec.rsd_id
            );

        END IF;

        -- associated gas recoverable resources
        IF COALESCE(
            oil_and_gas_output_rec.assgas_recoverable_p99,
            oil_and_gas_output_rec.assgas_recoverable_p90,
            oil_and_gas_output_rec.assgas_recoverable_mode,
            oil_and_gas_output_rec.assgas_recoverable_p50,
            oil_and_gas_output_rec.assgas_recoverable_mean,
            oil_and_gas_output_rec.assgas_recoverable_p10,
            oil_and_gas_output_rec.assgas_recoverable_p01
        ) IS NOT NULL THEN

            UPDATE remi.oil_case_outputs
            SET has_associated_gas_recoverable_resources = TRUE
            WHERE reservoir_submission_detail_id = oil_and_gas_output_rec.rsd_id;

            INSERT INTO remi.probability_distributions (
                id,
                submission_detail_id,
                distribution_type,
                instance,
                p99,
                p90,
                mode_value,
                p50,
                mean_value,
                p10,
                p1,
                comments,
                reservoir_submission_detail_id
            ) VALUES (
                gen_random_uuid(),
                oil_and_gas_output_rec.sd_id,
                custom_dist_type,
                'OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT',
                abs(oil_and_gas_output_rec.assgas_recoverable_p99),
                abs(oil_and_gas_output_rec.assgas_recoverable_p90),
                abs(oil_and_gas_output_rec.assgas_recoverable_mode),
                abs(oil_and_gas_output_rec.assgas_recoverable_p50),
                abs(oil_and_gas_output_rec.assgas_recoverable_mean),
                abs(oil_and_gas_output_rec.assgas_recoverable_p10),
                abs(oil_and_gas_output_rec.assgas_recoverable_p01),
                oil_and_gas_output_rec.assgas_recoverable_comment,
                oil_and_gas_output_rec.rsd_id
            );

        END IF;

        -- oil equivalent recoverable resources
        IF COALESCE(
            oil_and_gas_output_rec.oilequiv_recoverable_p99,
            oil_and_gas_output_rec.oilequiv_recoverable_p90,
            oil_and_gas_output_rec.oilequiv_recoverable_mode,
            oil_and_gas_output_rec.oilequiv_recoverable_p50,
            oil_and_gas_output_rec.oilequiv_recoverable_mean,
            oil_and_gas_output_rec.oilequiv_recoverable_p10,
            oil_and_gas_output_rec.oilequiv_recoverable_p01
        ) IS NOT NULL THEN

            INSERT INTO remi.probability_distributions (
                id,
                submission_detail_id,
                distribution_type,
                instance,
                p99,
                p90,
                mode_value,
                p50,
                mean_value,
                p10,
                p1,
                comments,
                reservoir_submission_detail_id
            ) VALUES (
                gen_random_uuid(),
                oil_and_gas_output_rec.sd_id,
                custom_dist_type,
                'OIL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT',
                abs(oil_and_gas_output_rec.oilequiv_recoverable_p99),
                abs(oil_and_gas_output_rec.oilequiv_recoverable_p90),
                abs(oil_and_gas_output_rec.oilequiv_recoverable_mode),
                abs(oil_and_gas_output_rec.oilequiv_recoverable_p50),
                abs(oil_and_gas_output_rec.oilequiv_recoverable_mean),
                abs(oil_and_gas_output_rec.oilequiv_recoverable_p10),
                abs(oil_and_gas_output_rec.oilequiv_recoverable_p01),
                oil_and_gas_output_rec.oilequiv_recoverable_comment,
                oil_and_gas_output_rec.rsd_id
            );

        ELSE

            UPDATE remi.oil_case_outputs
            SET use_industry_standard_conversion = TRUE
            WHERE reservoir_submission_detail_id = oil_and_gas_output_rec.rsd_id;

            INSERT INTO remi.probability_distributions (
                id,
                submission_detail_id,
                distribution_type,
                instance,
                p99,
                p90,
                mode_value,
                p50,
                mean_value,
                p10,
                p1,
                comments,
                reservoir_submission_detail_id
            ) VALUES (
                 gen_random_uuid(),
                 oil_and_gas_output_rec.sd_id,
                 custom_dist_type,
                 'OIL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_INDUSTRY_STANDARD_PROSPECT',
                 calculate_summed_equiv(abs(oil_and_gas_output_rec.oil_recoverable_p99), 0, 0, abs(oil_and_gas_output_rec.assgas_recoverable_p99)),
                 calculate_summed_equiv(abs(oil_and_gas_output_rec.oil_recoverable_p90), 0, 0, abs(oil_and_gas_output_rec.assgas_recoverable_p90)),
                 calculate_summed_equiv(abs(oil_and_gas_output_rec.oil_recoverable_mode), 0, 0, abs(oil_and_gas_output_rec.assgas_recoverable_mode)),
                 calculate_summed_equiv(abs(oil_and_gas_output_rec.oil_recoverable_p50), 0, 0, abs(oil_and_gas_output_rec.assgas_recoverable_p50)),
                 calculate_summed_equiv(abs(oil_and_gas_output_rec.oil_recoverable_mean), 0, 0, abs(oil_and_gas_output_rec.assgas_recoverable_mean)),
                 calculate_summed_equiv(abs(oil_and_gas_output_rec.oil_recoverable_p10), 0, 0, abs(oil_and_gas_output_rec.assgas_recoverable_p10)),
                 calculate_summed_equiv(abs(oil_and_gas_output_rec.oil_recoverable_p01), 0, 0, abs(oil_and_gas_output_rec.assgas_recoverable_p01)),
                 NULL,
                 oil_and_gas_output_rec.rsd_id
             );

        END IF;

        -- gas case outputs
        INSERT INTO remi.gas_case_outputs (
            id,
            reservoir_submission_detail_id,
            has_associated_condensate_recoverable_resources,
            use_industry_standard_conversion
        ) VALUES (
            gen_random_uuid(),
            oil_and_gas_output_rec.rsd_id,
            FALSE, -- updated later if required
            FALSE -- updated later if required
        );

        -- gas in place
        IF COALESCE(
            oil_and_gas_output_rec.gas_inplace_p99,
            oil_and_gas_output_rec.gas_inplace_p90,
            oil_and_gas_output_rec.gas_inplace_mode,
            oil_and_gas_output_rec.gas_inplace_p50,
            oil_and_gas_output_rec.gas_inplace_mean,
            oil_and_gas_output_rec.gas_inplace_p10,
            oil_and_gas_output_rec.gas_inplace_p01
        ) IS NOT NULL THEN

            INSERT INTO remi.probability_distributions (
                id,
                submission_detail_id,
                distribution_type,
                instance,
                p99,
                p90,
                mode_value,
                p50,
                mean_value,
                p10,
                p1,
                comments,
                reservoir_submission_detail_id
            ) VALUES (
                gen_random_uuid(),
                oil_and_gas_output_rec.sd_id,
                custom_dist_type,
                'GAS_OUTPUTS_GAS_IN_PLACE_PROSPECT',
                abs(oil_and_gas_output_rec.gas_inplace_p99),
                abs(oil_and_gas_output_rec.gas_inplace_p90),
                abs(oil_and_gas_output_rec.gas_inplace_mode),
                abs(oil_and_gas_output_rec.gas_inplace_p50),
                abs(oil_and_gas_output_rec.gas_inplace_mean),
                abs(oil_and_gas_output_rec.gas_inplace_p10),
                abs(oil_and_gas_output_rec.gas_inplace_p01),
                oil_and_gas_output_rec.gas_inplace_comment,
                oil_and_gas_output_rec.rsd_id
            );

        END IF;

        -- gas recoverable resources
        IF COALESCE(
            oil_and_gas_output_rec.gas_recoverable_p99,
            oil_and_gas_output_rec.gas_recoverable_p90,
            oil_and_gas_output_rec.gas_recoverable_mode,
            oil_and_gas_output_rec.gas_recoverable_p50,
            oil_and_gas_output_rec.gas_recoverable_mean,
            oil_and_gas_output_rec.gas_recoverable_p10,
            oil_and_gas_output_rec.gas_recoverable_p01
        ) IS NOT NULL THEN

            INSERT INTO remi.probability_distributions (
                id,
                submission_detail_id,
                distribution_type,
                instance,
                p99,
                p90,
                mode_value,
                p50,
                mean_value,
                p10,
                p1,
                comments,
                reservoir_submission_detail_id
            ) VALUES (
                gen_random_uuid(),
                oil_and_gas_output_rec.sd_id,
                custom_dist_type,
                'GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_PROSPECT',
                abs(oil_and_gas_output_rec.gas_recoverable_p99),
                abs(oil_and_gas_output_rec.gas_recoverable_p90),
                abs(oil_and_gas_output_rec.gas_recoverable_mode),
                abs(oil_and_gas_output_rec.gas_recoverable_p50),
                abs(oil_and_gas_output_rec.gas_recoverable_mean),
                abs(oil_and_gas_output_rec.gas_recoverable_p10),
                abs(oil_and_gas_output_rec.gas_recoverable_p01),
                oil_and_gas_output_rec.gas_recoverable_comment,
                oil_and_gas_output_rec.rsd_id
            );

        END IF;

        -- associated condensate recoverable resources
        IF COALESCE(
            oil_and_gas_output_rec.asscon_recoverable_p99,
            oil_and_gas_output_rec.asscon_recoverable_p90,
            oil_and_gas_output_rec.asscon_recoverable_mode,
            oil_and_gas_output_rec.asscon_recoverable_p50,
            oil_and_gas_output_rec.asscon_recoverable_mean,
            oil_and_gas_output_rec.asscon_recoverable_p10,
            oil_and_gas_output_rec.asscon_recoverable_p01
        ) IS NOT NULL THEN

            UPDATE remi.gas_case_outputs
            SET has_associated_condensate_recoverable_resources = TRUE
            WHERE reservoir_submission_detail_id = oil_and_gas_output_rec.rsd_id;

            INSERT INTO remi.probability_distributions (
                id,
                submission_detail_id,
                distribution_type,
                instance,
                p99,
                p90,
                mode_value,
                p50,
                mean_value,
                p10,
                p1,
                comments,
                reservoir_submission_detail_id
            ) VALUES (
                gen_random_uuid(),
                oil_and_gas_output_rec.sd_id,
                custom_dist_type,
                'GAS_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT',
                abs(oil_and_gas_output_rec.asscon_recoverable_p99),
                abs(oil_and_gas_output_rec.asscon_recoverable_p90),
                abs(oil_and_gas_output_rec.asscon_recoverable_mode),
                abs(oil_and_gas_output_rec.asscon_recoverable_p50),
                abs(oil_and_gas_output_rec.asscon_recoverable_mean),
                abs(oil_and_gas_output_rec.asscon_recoverable_p10),
                abs(oil_and_gas_output_rec.asscon_recoverable_p01),
                oil_and_gas_output_rec.asscon_recoverable_comment,
                oil_and_gas_output_rec.rsd_id
            );

        END IF;

        -- oil equivalent recoverable resources
        IF COALESCE(
            oil_and_gas_output_rec.oilequiv_recoverable_p99,
            oil_and_gas_output_rec.oilequiv_recoverable_p90,
            oil_and_gas_output_rec.oilequiv_recoverable_mode,
            oil_and_gas_output_rec.oilequiv_recoverable_p50,
            oil_and_gas_output_rec.oilequiv_recoverable_mean,
            oil_and_gas_output_rec.oilequiv_recoverable_p10,
            oil_and_gas_output_rec.oilequiv_recoverable_p01
        ) IS NOT NULL THEN

            INSERT INTO remi.probability_distributions (
                id,
                submission_detail_id,
                distribution_type,
                instance,
                p99,
                p90,
                mode_value,
                p50,
                mean_value,
                p10,
                p1,
                comments,
                reservoir_submission_detail_id
            ) VALUES (
                gen_random_uuid(),
                oil_and_gas_output_rec.sd_id,
                custom_dist_type,
                'GAS_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT',
                abs(oil_and_gas_output_rec.oilequiv_recoverable_p99),
                abs(oil_and_gas_output_rec.oilequiv_recoverable_p90),
                abs(oil_and_gas_output_rec.oilequiv_recoverable_mode),
                abs(oil_and_gas_output_rec.oilequiv_recoverable_p50),
                abs(oil_and_gas_output_rec.oilequiv_recoverable_mean),
                abs(oil_and_gas_output_rec.oilequiv_recoverable_p10),
                abs(oil_and_gas_output_rec.oilequiv_recoverable_p01),
                oil_and_gas_output_rec.oilequiv_recoverable_comment,
                oil_and_gas_output_rec.rsd_id
            );

        ELSE

            UPDATE remi.gas_case_outputs
            SET use_industry_standard_conversion = TRUE
            WHERE reservoir_submission_detail_id = oil_and_gas_output_rec.rsd_id;

            INSERT INTO remi.probability_distributions (
                id,
                submission_detail_id,
                distribution_type,
                instance,
                p99,
                p90,
                mode_value,
                p50,
                mean_value,
                p10,
                p1,
                comments,
                reservoir_submission_detail_id
            ) VALUES (
                 gen_random_uuid(),
                 oil_and_gas_output_rec.sd_id,
                 custom_dist_type,
                 'GAS_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_INDUSTRY_STANDARD_PROSPECT',
                 calculate_summed_equiv(0, abs(oil_and_gas_output_rec.asscon_recoverable_p99), abs(oil_and_gas_output_rec.gas_recoverable_p99), 0),
                 calculate_summed_equiv(0, abs(oil_and_gas_output_rec.asscon_recoverable_p90), abs(oil_and_gas_output_rec.gas_recoverable_p90), 0),
                 calculate_summed_equiv(0, abs(oil_and_gas_output_rec.asscon_recoverable_mode), abs(oil_and_gas_output_rec.gas_recoverable_mode), 0),
                 calculate_summed_equiv(0, abs(oil_and_gas_output_rec.asscon_recoverable_p50), abs(oil_and_gas_output_rec.gas_recoverable_p50), 0),
                 calculate_summed_equiv(0, abs(oil_and_gas_output_rec.asscon_recoverable_mean), abs(oil_and_gas_output_rec.gas_recoverable_mean), 0),
                 calculate_summed_equiv(0, abs(oil_and_gas_output_rec.asscon_recoverable_p10), abs(oil_and_gas_output_rec.gas_recoverable_p10), 0),
                 calculate_summed_equiv(0, abs(oil_and_gas_output_rec.asscon_recoverable_p01), abs(oil_and_gas_output_rec.gas_recoverable_p01), 0),
                 NULL,
                 oil_and_gas_output_rec.rsd_id
             );

        END IF;

    END LOOP;

    -- If mixed hydrocarbon case we can simply populate the mixed case output types
    FOR mixed_output_rec IN (
        SELECT prdvo.*, sd.id sd_id, r.type resource_type, rsd.id rsd_id
        FROM remi.padi_res_data_vol_outputs prdvo
        JOIN remi.submission_details sd ON sd.legacy_res_data_id = prdvo.resdat_id
        JOIN remi.reservoir_submission_details rsd ON rsd.submission_detail_id = sd.id
        JOIN remi.submissions s ON s.id = sd.submission_id
        JOIN remi.resources r ON r.id = s.resource_id
        JOIN remi.padi_res_data_header prdh ON prdh.resdat_id = sd.legacy_res_data_id
        WHERE sd.submission_type != 'POST_DRILL_DRY_HOLE'
        AND LOWER(prdh.hc_case) = 'mixed oil and gas'
        AND r.type = 'PROSPECT'
    )
    LOOP

        -- mixed case outputs
        INSERT INTO remi.mixed_case_outputs (
            id,
            reservoir_submission_detail_id,
            has_associated_gas_recoverable_resources,
            use_industry_standard_conversion
        ) VALUES (
             gen_random_uuid(),
             mixed_output_rec.rsd_id,
             FALSE, -- updated later if required
             FALSE -- updated later if required
         );

        -- oil in place
        IF COALESCE(
            mixed_output_rec.oil_inplace_p99,
            mixed_output_rec.oil_inplace_p90,
            mixed_output_rec.oil_inplace_mode,
            mixed_output_rec.oil_inplace_p50,
            mixed_output_rec.oil_inplace_mean,
            mixed_output_rec.oil_inplace_p10,
            mixed_output_rec.oil_inplace_p01
        ) IS NOT NULL THEN

            INSERT INTO remi.probability_distributions (
                id,
                submission_detail_id,
                distribution_type,
                instance,
                p99,
                p90,
                mode_value,
                p50,
                mean_value,
                p10,
                p1,
                comments,
                reservoir_submission_detail_id
            ) VALUES (
                gen_random_uuid(),
                mixed_output_rec.sd_id,
                custom_dist_type,
                'MIXED_CASE_OIL_IN_PLACE_PROSPECT',
                abs(mixed_output_rec.oil_inplace_p99),
                abs(mixed_output_rec.oil_inplace_p90),
                abs(mixed_output_rec.oil_inplace_mode),
                abs(mixed_output_rec.oil_inplace_p50),
                abs(mixed_output_rec.oil_inplace_mean),
                abs(mixed_output_rec.oil_inplace_p10),
                abs(mixed_output_rec.oil_inplace_p01),
                mixed_output_rec.oil_inplace_comment,
                mixed_output_rec.rsd_id
            );

        END IF;

        -- oil recoverable resources
        IF COALESCE(
            mixed_output_rec.oil_recoverable_p99,
            mixed_output_rec.oil_recoverable_p90,
            mixed_output_rec.oil_recoverable_mode,
            mixed_output_rec.oil_recoverable_p50,
            mixed_output_rec.oil_recoverable_mean,
            mixed_output_rec.oil_recoverable_p10,
            mixed_output_rec.oil_recoverable_p01
        ) IS NOT NULL THEN

            INSERT INTO remi.probability_distributions (
                id,
                submission_detail_id,
                distribution_type,
                instance,
                p99,
                p90,
                mode_value,
                p50,
                mean_value,
                p10,
                p1,
                comments,
                reservoir_submission_detail_id
            ) VALUES (
                gen_random_uuid(),
                mixed_output_rec.sd_id,
                custom_dist_type,
                'MIXED_CASE_OIL_RECOVERABLE_RESOURCES_PROSPECT',
                abs(mixed_output_rec.oil_recoverable_p99),
                abs(mixed_output_rec.oil_recoverable_p90),
                abs(mixed_output_rec.oil_recoverable_mode),
                abs(mixed_output_rec.oil_recoverable_p50),
                abs(mixed_output_rec.oil_recoverable_mean),
                abs(mixed_output_rec.oil_recoverable_p10),
                abs(mixed_output_rec.oil_recoverable_p01),
                mixed_output_rec.oil_recoverable_comment,
                mixed_output_rec.rsd_id
            );

        END IF;

        -- associated gas recoverable resources
        IF COALESCE(
            mixed_output_rec.assgas_recoverable_p99,
            mixed_output_rec.assgas_recoverable_p90,
            mixed_output_rec.assgas_recoverable_mode,
            mixed_output_rec.assgas_recoverable_p50,
            mixed_output_rec.assgas_recoverable_mean,
            mixed_output_rec.assgas_recoverable_p10,
            mixed_output_rec.assgas_recoverable_p01
        ) IS NOT NULL THEN

            UPDATE remi.mixed_case_outputs
            SET has_associated_gas_recoverable_resources = TRUE
            WHERE reservoir_submission_detail_id = mixed_output_rec.rsd_id;

            INSERT INTO remi.probability_distributions (
                id,
                submission_detail_id,
                distribution_type,
                instance,
                p99,
                p90,
                mode_value,
                p50,
                mean_value,
                p10,
                p1,
                comments,
                reservoir_submission_detail_id
            ) VALUES (
                gen_random_uuid(),
                mixed_output_rec.sd_id,
                custom_dist_type,
                'MIXED_CASE_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT',
                abs(mixed_output_rec.assgas_recoverable_p99),
                abs(mixed_output_rec.assgas_recoverable_p90),
                abs(mixed_output_rec.assgas_recoverable_mode),
                abs(mixed_output_rec.assgas_recoverable_p50),
                abs(mixed_output_rec.assgas_recoverable_mean),
                abs(mixed_output_rec.assgas_recoverable_p10),
                abs(mixed_output_rec.assgas_recoverable_p01),
                mixed_output_rec.assgas_recoverable_comment,
                mixed_output_rec.rsd_id
            );

        END IF;

        -- gas in place
        IF COALESCE(
            mixed_output_rec.gas_inplace_p99,
            mixed_output_rec.gas_inplace_p90,
            mixed_output_rec.gas_inplace_mode,
            mixed_output_rec.gas_inplace_p50,
            mixed_output_rec.gas_inplace_mean,
            mixed_output_rec.gas_inplace_p10,
            mixed_output_rec.gas_inplace_p01
        ) IS NOT NULL THEN

            INSERT INTO remi.probability_distributions (
                id,
                submission_detail_id,
                distribution_type,
                instance,
                p99,
                p90,
                mode_value,
                p50,
                mean_value,
                p10,
                p1,
                comments,
                reservoir_submission_detail_id
            ) VALUES (
                gen_random_uuid(),
                mixed_output_rec.sd_id,
                custom_dist_type,
                'MIXED_CASE_GAS_IN_PLACE_PROSPECT',
                abs(mixed_output_rec.gas_inplace_p99),
                abs(mixed_output_rec.gas_inplace_p90),
                abs(mixed_output_rec.gas_inplace_mode),
                abs(mixed_output_rec.gas_inplace_p50),
                abs(mixed_output_rec.gas_inplace_mean),
                abs(mixed_output_rec.gas_inplace_p10),
                abs(mixed_output_rec.gas_inplace_p01),
                mixed_output_rec.gas_inplace_comment,
                mixed_output_rec.rsd_id
            );

        END IF;

        -- gas recoverable resources
        IF COALESCE(
            mixed_output_rec.gas_recoverable_p99,
            mixed_output_rec.gas_recoverable_p90,
            mixed_output_rec.gas_recoverable_mode,
            mixed_output_rec.gas_recoverable_p50,
            mixed_output_rec.gas_recoverable_mean,
            mixed_output_rec.gas_recoverable_p10,
            mixed_output_rec.gas_recoverable_p01
        ) IS NOT NULL THEN

            INSERT INTO remi.probability_distributions (
                id,
                submission_detail_id,
                distribution_type,
                instance,
                p99,
                p90,
                mode_value,
                p50,
                mean_value,
                p10,
                p1,
                comments,
                reservoir_submission_detail_id
            ) VALUES (
                gen_random_uuid(),
                mixed_output_rec.sd_id,
                custom_dist_type,
                'MIXED_CASE_GAS_RECOVERABLE_RESOURCES_PROSPECT',
                abs(mixed_output_rec.gas_recoverable_p99),
                abs(mixed_output_rec.gas_recoverable_p90),
                abs(mixed_output_rec.gas_recoverable_mode),
                abs(mixed_output_rec.gas_recoverable_p50),
                abs(mixed_output_rec.gas_recoverable_mean),
                abs(mixed_output_rec.gas_recoverable_p10),
                abs(mixed_output_rec.gas_recoverable_p01),
                mixed_output_rec.gas_recoverable_comment,
                mixed_output_rec.rsd_id
            );

        END IF;

        -- associated condensate recoverable resources
        IF COALESCE(
            mixed_output_rec.asscon_recoverable_p99,
            mixed_output_rec.asscon_recoverable_p90,
            mixed_output_rec.asscon_recoverable_mode,
            mixed_output_rec.asscon_recoverable_p50,
            mixed_output_rec.asscon_recoverable_mean,
            mixed_output_rec.asscon_recoverable_p10,
            mixed_output_rec.asscon_recoverable_p01
        ) IS NOT NULL THEN

            INSERT INTO remi.probability_distributions (
                id,
                submission_detail_id,
                distribution_type,
                instance,
                p99,
                p90,
                mode_value,
                p50,
                mean_value,
                p10,
                p1,
                comments,
                reservoir_submission_detail_id
            ) VALUES (
                gen_random_uuid(),
                mixed_output_rec.sd_id,
                custom_dist_type,
                'MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT',
                abs(mixed_output_rec.asscon_recoverable_p99),
                abs(mixed_output_rec.asscon_recoverable_p90),
                abs(mixed_output_rec.asscon_recoverable_mode),
                abs(mixed_output_rec.asscon_recoverable_p50),
                abs(mixed_output_rec.asscon_recoverable_mean),
                abs(mixed_output_rec.asscon_recoverable_p10),
                abs(mixed_output_rec.asscon_recoverable_p01),
                mixed_output_rec.asscon_recoverable_comment,
                mixed_output_rec.rsd_id
            );

        END IF;

        -- oil equivalent recoverable resources
        IF COALESCE(
            mixed_output_rec.oilequiv_recoverable_p99,
            mixed_output_rec.oilequiv_recoverable_p90,
            mixed_output_rec.oilequiv_recoverable_mode,
            mixed_output_rec.oilequiv_recoverable_p50,
            mixed_output_rec.oilequiv_recoverable_mean,
            mixed_output_rec.oilequiv_recoverable_p10,
            mixed_output_rec.oilequiv_recoverable_p01
        ) IS NOT NULL THEN

            INSERT INTO remi.probability_distributions (
                id,
                submission_detail_id,
                distribution_type,
                instance,
                p99,
                p90,
                mode_value,
                p50,
                mean_value,
                p10,
                p1,
                comments,
                reservoir_submission_detail_id
            ) VALUES (
                gen_random_uuid(),
                mixed_output_rec.sd_id,
                custom_dist_type,
                'MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT',
                abs(mixed_output_rec.oilequiv_recoverable_p99),
                abs(mixed_output_rec.oilequiv_recoverable_p90),
                abs(mixed_output_rec.oilequiv_recoverable_mode),
                abs(mixed_output_rec.oilequiv_recoverable_p50),
                abs(mixed_output_rec.oilequiv_recoverable_mean),
                abs(mixed_output_rec.oilequiv_recoverable_p10),
                abs(mixed_output_rec.oilequiv_recoverable_p01),
                mixed_output_rec.oilequiv_recoverable_comment,
                mixed_output_rec.rsd_id
            );

        ELSE

            UPDATE remi.mixed_case_outputs
            SET use_industry_standard_conversion = TRUE
            WHERE reservoir_submission_detail_id = mixed_output_rec.rsd_id;

            INSERT INTO remi.probability_distributions (
                id,
                submission_detail_id,
                distribution_type,
                instance,
                p99,
                p90,
                mode_value,
                p50,
                mean_value,
                p10,
                p1,
                comments,
                reservoir_submission_detail_id
            ) VALUES (
                 gen_random_uuid(),
                 mixed_output_rec.sd_id,
                 custom_dist_type,
                 'MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_INDUSTRY_STANDARD_PROSPECT',
                 calculate_summed_equiv(abs(mixed_output_rec.oil_recoverable_p99), abs(mixed_output_rec.asscon_recoverable_p99), abs(mixed_output_rec.gas_recoverable_p99), abs(mixed_output_rec.assgas_recoverable_p99)),
                 calculate_summed_equiv(abs(mixed_output_rec.oil_recoverable_p90), abs(mixed_output_rec.asscon_recoverable_p90), abs(mixed_output_rec.gas_recoverable_p90), abs(mixed_output_rec.assgas_recoverable_p90)),
                 calculate_summed_equiv(abs(mixed_output_rec.oil_recoverable_mode), abs(mixed_output_rec.asscon_recoverable_mode), abs(mixed_output_rec.gas_recoverable_mode), abs(mixed_output_rec.assgas_recoverable_mode)),
                 calculate_summed_equiv(abs(mixed_output_rec.oil_recoverable_p50), abs(mixed_output_rec.asscon_recoverable_p50), abs(mixed_output_rec.gas_recoverable_p50), abs(mixed_output_rec.assgas_recoverable_p50)),
                 calculate_summed_equiv(abs(mixed_output_rec.oil_recoverable_mean), abs(mixed_output_rec.asscon_recoverable_mean), abs(mixed_output_rec.gas_recoverable_mean), abs(mixed_output_rec.assgas_recoverable_mean)),
                 calculate_summed_equiv(abs(mixed_output_rec.oil_recoverable_p10), abs(mixed_output_rec.asscon_recoverable_p10), abs(mixed_output_rec.gas_recoverable_p10), abs(mixed_output_rec.assgas_recoverable_p10)),
                 calculate_summed_equiv(abs(mixed_output_rec.oil_recoverable_p01), abs(mixed_output_rec.asscon_recoverable_p01), abs(mixed_output_rec.gas_recoverable_p01), abs(mixed_output_rec.assgas_recoverable_p01)),
                 NULL,
                 mixed_output_rec.rsd_id
             );

        END IF;

    END LOOP;

END;

$$;