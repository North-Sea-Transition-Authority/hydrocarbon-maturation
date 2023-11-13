DO $$
DECLARE

    oil_and_gas_output_rec RECORD;
    mixed_output_rec RECORD;
    custom_dist_type TEXT := 'CUSTOM';

BEGIN

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
    )
    LOOP

        -- oil case outputs
        INSERT INTO remi.oil_case_outputs (
            id,
            reservoir_submission_detail_id,
            has_associated_gas_recoverable_resources
        ) VALUES (
            gen_random_uuid(),
            oil_and_gas_output_rec.rsd_id,
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
                comments
            ) VALUES (
                gen_random_uuid(),
                oil_and_gas_output_rec.sd_id,
                custom_dist_type,
                CASE
                    WHEN oil_and_gas_output_rec.resource_type = 'PROSPECT' THEN 'OIL_OUTPUTS_OIL_IN_PLACE_PROSPECT'
                    ELSE 'OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY'
                END,
                abs(oil_and_gas_output_rec.oil_inplace_p99),
                abs(oil_and_gas_output_rec.oil_inplace_p90),
                abs(oil_and_gas_output_rec.oil_inplace_mode),
                abs(oil_and_gas_output_rec.oil_inplace_p50),
                abs(oil_and_gas_output_rec.oil_inplace_mean),
                abs(oil_and_gas_output_rec.oil_inplace_p10),
                abs(oil_and_gas_output_rec.oil_inplace_p01),
                oil_and_gas_output_rec.oil_inplace_comment
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
                comments
            ) VALUES (
                gen_random_uuid(),
                oil_and_gas_output_rec.sd_id,
                custom_dist_type,
                CASE
                    WHEN oil_and_gas_output_rec.resource_type = 'PROSPECT' THEN 'OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_PROSPECT'
                    ELSE 'OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY'
                END,
                abs(oil_and_gas_output_rec.oil_recoverable_p99),
                abs(oil_and_gas_output_rec.oil_recoverable_p90),
                abs(oil_and_gas_output_rec.oil_recoverable_mode),
                abs(oil_and_gas_output_rec.oil_recoverable_p50),
                abs(oil_and_gas_output_rec.oil_recoverable_mean),
                abs(oil_and_gas_output_rec.oil_recoverable_p10),
                abs(oil_and_gas_output_rec.oil_recoverable_p01),
                oil_and_gas_output_rec.oil_recoverable_comment
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
                comments
            ) VALUES (
                gen_random_uuid(),
                oil_and_gas_output_rec.sd_id,
                custom_dist_type,
                CASE
                    WHEN oil_and_gas_output_rec.resource_type = 'PROSPECT' THEN 'OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT'
                    ELSE 'OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY'
                END,
                abs(oil_and_gas_output_rec.assgas_recoverable_p99),
                abs(oil_and_gas_output_rec.assgas_recoverable_p90),
                abs(oil_and_gas_output_rec.assgas_recoverable_mode),
                abs(oil_and_gas_output_rec.assgas_recoverable_p50),
                abs(oil_and_gas_output_rec.assgas_recoverable_mean),
                abs(oil_and_gas_output_rec.assgas_recoverable_p10),
                abs(oil_and_gas_output_rec.assgas_recoverable_p01),
                oil_and_gas_output_rec.assgas_recoverable_comment
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
                comments
            ) VALUES (
                gen_random_uuid(),
                oil_and_gas_output_rec.sd_id,
                custom_dist_type,
                CASE
                    WHEN oil_and_gas_output_rec.resource_type = 'PROSPECT' THEN 'OIL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT'
                    ELSE 'OIL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY'
                END,
                abs(oil_and_gas_output_rec.oilequiv_recoverable_p99),
                abs(oil_and_gas_output_rec.oilequiv_recoverable_p90),
                abs(oil_and_gas_output_rec.oilequiv_recoverable_mode),
                abs(oil_and_gas_output_rec.oilequiv_recoverable_p50),
                abs(oil_and_gas_output_rec.oilequiv_recoverable_mean),
                abs(oil_and_gas_output_rec.oilequiv_recoverable_p10),
                abs(oil_and_gas_output_rec.oilequiv_recoverable_p01),
                oil_and_gas_output_rec.oilequiv_recoverable_comment
            );

        END IF;

        -- gas case outputs
        INSERT INTO remi.gas_case_outputs (
            id,
            reservoir_submission_detail_id,
            has_associated_condensate_recoverable_resources
        ) VALUES (
            gen_random_uuid(),
            oil_and_gas_output_rec.rsd_id,
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
                comments
            ) VALUES (
                gen_random_uuid(),
                oil_and_gas_output_rec.sd_id,
                custom_dist_type,
                CASE
                    WHEN oil_and_gas_output_rec.resource_type = 'PROSPECT' THEN 'GAS_OUTPUTS_GAS_IN_PLACE_PROSPECT'
                    ELSE 'GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY'
                END,
                abs(oil_and_gas_output_rec.gas_inplace_p99),
                abs(oil_and_gas_output_rec.gas_inplace_p90),
                abs(oil_and_gas_output_rec.gas_inplace_mode),
                abs(oil_and_gas_output_rec.gas_inplace_p50),
                abs(oil_and_gas_output_rec.gas_inplace_mean),
                abs(oil_and_gas_output_rec.gas_inplace_p10),
                abs(oil_and_gas_output_rec.gas_inplace_p01),
                oil_and_gas_output_rec.gas_inplace_comment
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
                comments
            ) VALUES (
                gen_random_uuid(),
                oil_and_gas_output_rec.sd_id,
                custom_dist_type,
                CASE
                    WHEN oil_and_gas_output_rec.resource_type = 'PROSPECT' THEN 'GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_PROSPECT'
                    ELSE 'GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY'
                END,
                abs(oil_and_gas_output_rec.gas_recoverable_p99),
                abs(oil_and_gas_output_rec.gas_recoverable_p90),
                abs(oil_and_gas_output_rec.gas_recoverable_mode),
                abs(oil_and_gas_output_rec.gas_recoverable_p50),
                abs(oil_and_gas_output_rec.gas_recoverable_mean),
                abs(oil_and_gas_output_rec.gas_recoverable_p10),
                abs(oil_and_gas_output_rec.gas_recoverable_p01),
                oil_and_gas_output_rec.gas_recoverable_comment
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
                comments
            ) VALUES (
                gen_random_uuid(),
                oil_and_gas_output_rec.sd_id,
                custom_dist_type,
                CASE
                    WHEN oil_and_gas_output_rec.resource_type = 'PROSPECT' THEN 'GAS_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT'
                    ELSE 'GAS_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY'
                END,
                abs(oil_and_gas_output_rec.asscon_recoverable_p99),
                abs(oil_and_gas_output_rec.asscon_recoverable_p90),
                abs(oil_and_gas_output_rec.asscon_recoverable_mode),
                abs(oil_and_gas_output_rec.asscon_recoverable_p50),
                abs(oil_and_gas_output_rec.asscon_recoverable_mean),
                abs(oil_and_gas_output_rec.asscon_recoverable_p10),
                abs(oil_and_gas_output_rec.asscon_recoverable_p01),
                oil_and_gas_output_rec.asscon_recoverable_comment
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
                comments
            ) VALUES (
                gen_random_uuid(),
                oil_and_gas_output_rec.sd_id,
                custom_dist_type,
                CASE
                    WHEN oil_and_gas_output_rec.resource_type = 'PROSPECT' THEN 'GAS_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT'
                    ELSE 'GAS_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY'
                END,
                abs(oil_and_gas_output_rec.oilequiv_recoverable_p99),
                abs(oil_and_gas_output_rec.oilequiv_recoverable_p90),
                abs(oil_and_gas_output_rec.oilequiv_recoverable_mode),
                abs(oil_and_gas_output_rec.oilequiv_recoverable_p50),
                abs(oil_and_gas_output_rec.oilequiv_recoverable_mean),
                abs(oil_and_gas_output_rec.oilequiv_recoverable_p10),
                abs(oil_and_gas_output_rec.oilequiv_recoverable_p01),
                oil_and_gas_output_rec.oilequiv_recoverable_comment
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
    )
    LOOP

        -- mixed case outputs
        INSERT INTO remi.mixed_case_outputs (
            id,
            reservoir_submission_detail_id,
            has_associated_gas_recoverable_resources
        ) VALUES (
             gen_random_uuid(),
             mixed_output_rec.sd_id,
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
                comments
            ) VALUES (
                gen_random_uuid(),
                mixed_output_rec.sd_id,
                custom_dist_type,
                CASE
                    WHEN mixed_output_rec.resource_type = 'PROSPECT' THEN 'MIXED_CASE_OIL_IN_PLACE_PROSPECT'
                    ELSE 'MIXED_CASE_OIL_IN_PLACE_DISCOVERY'
                END,
                abs(mixed_output_rec.oil_inplace_p99),
                abs(mixed_output_rec.oil_inplace_p90),
                abs(mixed_output_rec.oil_inplace_mode),
                abs(mixed_output_rec.oil_inplace_p50),
                abs(mixed_output_rec.oil_inplace_mean),
                abs(mixed_output_rec.oil_inplace_p10),
                abs(mixed_output_rec.oil_inplace_p01),
                mixed_output_rec.oil_inplace_comment
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
                comments
            ) VALUES (
                gen_random_uuid(),
                mixed_output_rec.sd_id,
                custom_dist_type,
                CASE
                    WHEN mixed_output_rec.resource_type = 'PROSPECT' THEN 'MIXED_CASE_OIL_RECOVERABLE_RESOURCES_PROSPECT'
                    ELSE 'MIXED_CASE_OIL_RECOVERABLE_RESOURCES_DISCOVERY'
                END,
                abs(mixed_output_rec.oil_recoverable_p99),
                abs(mixed_output_rec.oil_recoverable_p90),
                abs(mixed_output_rec.oil_recoverable_mode),
                abs(mixed_output_rec.oil_recoverable_p50),
                abs(mixed_output_rec.oil_recoverable_mean),
                abs(mixed_output_rec.oil_recoverable_p10),
                abs(mixed_output_rec.oil_recoverable_p01),
                mixed_output_rec.oil_recoverable_comment
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
                comments
            ) VALUES (
                gen_random_uuid(),
                mixed_output_rec.sd_id,
                custom_dist_type,
                CASE
                    WHEN mixed_output_rec.resource_type = 'PROSPECT' THEN 'MIXED_CASE_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT'
                    ELSE 'MIXED_CASE_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT'
                END,
                abs(mixed_output_rec.assgas_recoverable_p99),
                abs(mixed_output_rec.assgas_recoverable_p90),
                abs(mixed_output_rec.assgas_recoverable_mode),
                abs(mixed_output_rec.assgas_recoverable_p50),
                abs(mixed_output_rec.assgas_recoverable_mean),
                abs(mixed_output_rec.assgas_recoverable_p10),
                abs(mixed_output_rec.assgas_recoverable_p01),
                mixed_output_rec.assgas_recoverable_comment
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
                comments
            ) VALUES (
                gen_random_uuid(),
                mixed_output_rec.sd_id,
                custom_dist_type,
                CASE
                    WHEN mixed_output_rec.resource_type = 'PROSPECT' THEN 'MIXED_CASE_GAS_IN_PLACE_PROSPECT'
                    ELSE 'MIXED_CASE_GAS_IN_PLACE_DISCOVERY'
                END,
                abs(mixed_output_rec.gas_inplace_p99),
                abs(mixed_output_rec.gas_inplace_p90),
                abs(mixed_output_rec.gas_inplace_mode),
                abs(mixed_output_rec.gas_inplace_p50),
                abs(mixed_output_rec.gas_inplace_mean),
                abs(mixed_output_rec.gas_inplace_p10),
                abs(mixed_output_rec.gas_inplace_p01),
                mixed_output_rec.gas_inplace_comment
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
                comments
            ) VALUES (
                gen_random_uuid(),
                mixed_output_rec.sd_id,
                custom_dist_type,
                CASE
                    WHEN mixed_output_rec.resource_type = 'PROSPECT' THEN 'MIXED_CASE_GAS_RECOVERABLE_RESOURCES_PROSPECT'
                    ELSE 'MIXED_CASE_GAS_RECOVERABLE_RESOURCES_DISCOVERY'
                END,
                abs(mixed_output_rec.gas_recoverable_p99),
                abs(mixed_output_rec.gas_recoverable_p90),
                abs(mixed_output_rec.gas_recoverable_mode),
                abs(mixed_output_rec.gas_recoverable_p50),
                abs(mixed_output_rec.gas_recoverable_mean),
                abs(mixed_output_rec.gas_recoverable_p10),
                abs(mixed_output_rec.gas_recoverable_p01),
                mixed_output_rec.gas_recoverable_comment
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
                comments
            ) VALUES (
                gen_random_uuid(),
                mixed_output_rec.sd_id,
                custom_dist_type,
                CASE
                    WHEN mixed_output_rec.resource_type = 'PROSPECT' THEN 'MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT'
                    ELSE 'MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY'
                END,
                abs(mixed_output_rec.asscon_recoverable_p99),
                abs(mixed_output_rec.asscon_recoverable_p90),
                abs(mixed_output_rec.asscon_recoverable_mode),
                abs(mixed_output_rec.asscon_recoverable_p50),
                abs(mixed_output_rec.asscon_recoverable_mean),
                abs(mixed_output_rec.asscon_recoverable_p10),
                abs(mixed_output_rec.asscon_recoverable_p01),
                mixed_output_rec.asscon_recoverable_comment
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
                comments
            ) VALUES (
                gen_random_uuid(),
                mixed_output_rec.sd_id,
                custom_dist_type,
                CASE
                    WHEN mixed_output_rec.resource_type = 'PROSPECT' THEN 'MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT'
                    ELSE 'MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY'
                END,
                abs(mixed_output_rec.oilequiv_recoverable_p99),
                abs(mixed_output_rec.oilequiv_recoverable_p90),
                abs(mixed_output_rec.oilequiv_recoverable_mode),
                abs(mixed_output_rec.oilequiv_recoverable_p50),
                abs(mixed_output_rec.oilequiv_recoverable_mean),
                abs(mixed_output_rec.oilequiv_recoverable_p10),
                abs(mixed_output_rec.oilequiv_recoverable_p01),
                mixed_output_rec.oilequiv_recoverable_comment
            );

        END IF;

    END LOOP;

END;

$$;