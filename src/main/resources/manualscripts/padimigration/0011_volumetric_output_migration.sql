DO $$
DECLARE

    oil_output_rec RECORD;
    gas_output_rec RECORD;
    mixed_output_rec RECORD;
    custom_dist_type TEXT := 'CUSTOM';

BEGIN

    FOR oil_output_rec IN (
        SELECT prdvo.*, sd.id sd_id
        FROM remi.padi_res_data_vol_outputs prdvo
        JOIN remi.submission_details sd ON sd.legacy_res_data_id = prdvo.resdat_id
        JOIN remi.padi_res_data_header prdh ON prdh.resdat_id = sd.legacy_res_data_id
        WHERE sd.submission_type != 'POST_DRILL_DRY_HOLE'
        AND LOWER(prdh.hc_case) = 'oil'
    )
    LOOP

        INSERT INTO remi.oil_case_outputs (
            id,
            submission_detail_id,
            risked_oil_equivalent_recoverable_mean,
            has_associated_gas_recoverable_resources
        ) VALUES (
            gen_random_uuid(),
            oil_output_rec.sd_id,
            NULL, -- is this in PaDI?
            FALSE -- updated later if required
        );

        -- oil in place
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
            p1
        ) VALUES (
            gen_random_uuid(),
            oil_output_rec.sd_id,
            custom_dist_type,
            'OIL_OUTPUTS_OIL_IN_PLACE',
            abs(oil_output_rec.oil_inplace_p99),
            abs(oil_output_rec.oil_inplace_p90),
            abs(oil_output_rec.oil_inplace_mode),
            abs(oil_output_rec.oil_inplace_p50),
            abs(oil_output_rec.oil_inplace_mean),
            abs(oil_output_rec.oil_inplace_p10),
            abs(oil_output_rec.oil_inplace_p01)
        );

        -- oil recoverable resources
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
            p1
        ) VALUES (
            gen_random_uuid(),
            oil_output_rec.sd_id,
            custom_dist_type,
            'OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES',
            abs(oil_output_rec.oil_recoverable_p99),
            abs(oil_output_rec.oil_recoverable_p90),
            abs(oil_output_rec.oil_recoverable_mode),
            abs(oil_output_rec.oil_recoverable_p50),
            abs(oil_output_rec.oil_recoverable_mean),
            abs(oil_output_rec.oil_recoverable_p10),
            abs(oil_output_rec.oil_recoverable_p01)
        );

        IF (COALESCE(
            oil_output_rec.assgas_recoverable_p99,
            oil_output_rec.assgas_recoverable_p90,
            oil_output_rec.assgas_recoverable_mode,
            oil_output_rec.assgas_recoverable_p50,
            oil_output_rec.assgas_recoverable_mean,
            oil_output_rec.assgas_recoverable_p10,
            oil_output_rec.assgas_recoverable_p01
            ) IS NOT NULL)
        THEN

            UPDATE remi.oil_case_outputs
            SET has_associated_gas_recoverable_resources = TRUE
            WHERE submission_detail_id = oil_output_rec.sd_id;

            -- associated gas recoverable resources
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
                p1
            ) VALUES (
                gen_random_uuid(),
                oil_output_rec.sd_id,
                custom_dist_type,
                'OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES',
                abs(oil_output_rec.assgas_recoverable_p99),
                abs(oil_output_rec.assgas_recoverable_p90),
                abs(oil_output_rec.assgas_recoverable_mode),
                abs(oil_output_rec.assgas_recoverable_p50),
                abs(oil_output_rec.assgas_recoverable_mean),
                abs(oil_output_rec.assgas_recoverable_p10),
                abs(oil_output_rec.assgas_recoverable_p01)
            );

        END IF;

        -- oil equivalent recoverable resources
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
            p1
        ) VALUES (
            gen_random_uuid(),
            oil_output_rec.sd_id,
            custom_dist_type,
            'OIL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES',
            abs(oil_output_rec.oilequiv_recoverable_p99),
            abs(oil_output_rec.oilequiv_recoverable_p90),
            abs(oil_output_rec.oilequiv_recoverable_mode),
            abs(oil_output_rec.oilequiv_recoverable_p50),
            abs(oil_output_rec.oilequiv_recoverable_mean),
            abs(oil_output_rec.oilequiv_recoverable_p10),
            abs(oil_output_rec.oilequiv_recoverable_p01)
        );

    END LOOP;

    FOR gas_output_rec IN (
        SELECT prdvo.*, sd.id sd_id
        FROM remi.padi_res_data_vol_outputs prdvo
        JOIN remi.submission_details sd ON sd.legacy_res_data_id = prdvo.resdat_id
        JOIN remi.padi_res_data_header prdh ON prdh.resdat_id = sd.legacy_res_data_id
        WHERE sd.submission_type != 'POST_DRILL_DRY_HOLE'
        AND LOWER(prdh.hc_case) IN ('gas','condensate')
    )
    LOOP

        INSERT INTO remi.gas_case_outputs (
            id,
            submission_detail_id,
            has_associated_condensate_recoverable_resources
        ) VALUES (
            gen_random_uuid(),
            gas_output_rec.sd_id,
            FALSE -- updated later if required
        );

        -- gas in place
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
            p1
        ) VALUES (
            gen_random_uuid(),
            gas_output_rec.sd_id,
            custom_dist_type,
            'GAS_OUTPUTS_GAS_IN_PLACE',
            abs(gas_output_rec.gas_inplace_p99),
            abs(gas_output_rec.gas_inplace_p90),
            abs(gas_output_rec.gas_inplace_mode),
            abs(gas_output_rec.gas_inplace_p50),
            abs(gas_output_rec.gas_inplace_mean),
            abs(gas_output_rec.gas_inplace_p10),
            abs(gas_output_rec.gas_inplace_p01)
        );

        -- gas recoverable resources
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
            p1
        ) VALUES (
            gen_random_uuid(),
            gas_output_rec.sd_id,
            custom_dist_type,
            'GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES',
            abs(gas_output_rec.gas_recoverable_p99),
            abs(gas_output_rec.gas_recoverable_p90),
            abs(gas_output_rec.gas_recoverable_mode),
            abs(gas_output_rec.gas_recoverable_p50),
            abs(gas_output_rec.gas_recoverable_mean),
            abs(gas_output_rec.gas_recoverable_p10),
            abs(gas_output_rec.gas_recoverable_p01)
        );

        IF (COALESCE(
            gas_output_rec.asscon_recoverable_p99,
            gas_output_rec.asscon_recoverable_p90,
            gas_output_rec.asscon_recoverable_mode,
            gas_output_rec.asscon_recoverable_p50,
            gas_output_rec.asscon_recoverable_mean,
            gas_output_rec.asscon_recoverable_p10,
            gas_output_rec.asscon_recoverable_p01
        ) IS NOT NULL)
        THEN

            UPDATE remi.gas_case_outputs
            SET has_associated_condensate_recoverable_resources = TRUE
            WHERE submission_detail_id = gas_output_rec.sd_id;

            -- associated condensate recoverable resources
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
                p1
            ) VALUES (
                gen_random_uuid(),
                gas_output_rec.sd_id,
                custom_dist_type,
                'GAS_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES',
                abs(gas_output_rec.asscon_recoverable_p99),
                abs(gas_output_rec.asscon_recoverable_p90),
                abs(gas_output_rec.asscon_recoverable_mode),
                abs(gas_output_rec.asscon_recoverable_p50),
                abs(gas_output_rec.asscon_recoverable_mean),
                abs(gas_output_rec.asscon_recoverable_p10),
                abs(gas_output_rec.asscon_recoverable_p01)
            );

        END IF;

        -- oil equivalent recoverable resources
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
            p1
        ) VALUES (
            gen_random_uuid(),
            gas_output_rec.sd_id,
            custom_dist_type,
            'GAS_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES',
            abs(gas_output_rec.oilequiv_recoverable_p99),
            abs(gas_output_rec.oilequiv_recoverable_p90),
            abs(gas_output_rec.oilequiv_recoverable_mode),
            abs(gas_output_rec.oilequiv_recoverable_p50),
            abs(gas_output_rec.oilequiv_recoverable_mean),
            abs(gas_output_rec.oilequiv_recoverable_p10),
            abs(gas_output_rec.oilequiv_recoverable_p01)
        );

    END LOOP;

    FOR mixed_output_rec IN (
        SELECT prdvo.*, sd.id sd_id
        FROM remi.padi_res_data_vol_outputs prdvo
        JOIN remi.submission_details sd ON sd.legacy_res_data_id = prdvo.resdat_id
        JOIN remi.padi_res_data_header prdh ON prdh.resdat_id = sd.legacy_res_data_id
        WHERE sd.submission_type != 'POST_DRILL_DRY_HOLE'
        AND LOWER(prdh.hc_case) = 'mixed oil and gas'
    )
    LOOP

        INSERT INTO remi.mixed_case_outputs (
            id,
            submission_detail_id,
            risked_oil_equivalent_recoverable_mean,
            has_associated_gas_recoverable_resources
        ) VALUES (
             gen_random_uuid(),
             mixed_output_rec.sd_id,
             NULL, -- is this in PaDI?
             FALSE -- updated later if required
         );

        -- oil in place
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
            p1
        ) VALUES (
            gen_random_uuid(),
            mixed_output_rec.sd_id,
            custom_dist_type,
            'MIXED_CASE_OIL_IN_PLACE',
            abs(mixed_output_rec.oil_inplace_p99),
            abs(mixed_output_rec.oil_inplace_p90),
            abs(mixed_output_rec.oil_inplace_mode),
            abs(mixed_output_rec.oil_inplace_p50),
            abs(mixed_output_rec.oil_inplace_mean),
            abs(mixed_output_rec.oil_inplace_p10),
            abs(mixed_output_rec.oil_inplace_p01)
        );

        -- oil recoverable resources
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
            p1
        ) VALUES (
            gen_random_uuid(),
            mixed_output_rec.sd_id,
            custom_dist_type,
            'MIXED_CASE_OIL_RECOVERABLE_RESOURCES',
            abs(mixed_output_rec.oil_recoverable_p99),
            abs(mixed_output_rec.oil_recoverable_p90),
            abs(mixed_output_rec.oil_recoverable_mode),
            abs(mixed_output_rec.oil_recoverable_p50),
            abs(mixed_output_rec.oil_recoverable_mean),
            abs(mixed_output_rec.oil_recoverable_p10),
            abs(mixed_output_rec.oil_recoverable_p01)
        );

        IF (COALESCE(
            mixed_output_rec.assgas_recoverable_p99,
            mixed_output_rec.assgas_recoverable_p90,
            mixed_output_rec.assgas_recoverable_mode,
            mixed_output_rec.assgas_recoverable_p50,
            mixed_output_rec.assgas_recoverable_mean,
            mixed_output_rec.assgas_recoverable_p10,
            mixed_output_rec.assgas_recoverable_p01
        ) IS NOT NULL)
        THEN

            UPDATE remi.mixed_case_outputs
            SET has_associated_gas_recoverable_resources = TRUE
            WHERE submission_detail_id = mixed_output_rec.sd_id;

            -- associated gas recoverable resources
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
                p1
            ) VALUES (
                gen_random_uuid(),
                mixed_output_rec.sd_id,
                custom_dist_type,
                'MIXED_CASE_ASSOCIATED_GAS_RECOVERABLE_RESOURCES',
                abs(mixed_output_rec.assgas_recoverable_p99),
                abs(mixed_output_rec.assgas_recoverable_p90),
                abs(mixed_output_rec.assgas_recoverable_mode),
                abs(mixed_output_rec.assgas_recoverable_p50),
                abs(mixed_output_rec.assgas_recoverable_mean),
                abs(mixed_output_rec.assgas_recoverable_p10),
                abs(mixed_output_rec.assgas_recoverable_p01)
            );

        END IF;

        -- oil equivalent recoverable resources
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
            p1
        ) VALUES (
            gen_random_uuid(),
            mixed_output_rec.sd_id,
            custom_dist_type,
            'MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES',
            abs(mixed_output_rec.oilequiv_recoverable_p99),
            abs(mixed_output_rec.oilequiv_recoverable_p90),
            abs(mixed_output_rec.oilequiv_recoverable_mode),
            abs(mixed_output_rec.oilequiv_recoverable_p50),
            abs(mixed_output_rec.oilequiv_recoverable_mean),
            abs(mixed_output_rec.oilequiv_recoverable_p10),
            abs(mixed_output_rec.oilequiv_recoverable_p01)
        );

        -- gas in place
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
            p1
        ) VALUES (
            gen_random_uuid(),
            mixed_output_rec.sd_id,
            custom_dist_type,
            'MIXED_CASE_GAS_IN_PLACE',
            abs(mixed_output_rec.gas_inplace_p99),
            abs(mixed_output_rec.gas_inplace_p90),
            abs(mixed_output_rec.gas_inplace_mode),
            abs(mixed_output_rec.gas_inplace_p50),
            abs(mixed_output_rec.gas_inplace_mean),
            abs(mixed_output_rec.gas_inplace_p10),
            abs(mixed_output_rec.gas_inplace_p01)
        );

        -- gas recoverable resources
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
            p1
        ) VALUES (
            gen_random_uuid(),
            mixed_output_rec.sd_id,
            custom_dist_type,
            'MIXED_CASE_GAS_RECOVERABLE_RESOURCES',
            abs(mixed_output_rec.gas_recoverable_p99),
            abs(mixed_output_rec.gas_recoverable_p90),
            abs(mixed_output_rec.gas_recoverable_mode),
            abs(mixed_output_rec.gas_recoverable_p50),
            abs(mixed_output_rec.gas_recoverable_mean),
            abs(mixed_output_rec.gas_recoverable_p10),
            abs(mixed_output_rec.gas_recoverable_p01)
        );

        -- associated condensate recoverable resources
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
            p1
        ) VALUES (
            gen_random_uuid(),
            mixed_output_rec.sd_id,
            custom_dist_type,
            'MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES',
            abs(mixed_output_rec.asscon_recoverable_p99),
            abs(mixed_output_rec.asscon_recoverable_p90),
            abs(mixed_output_rec.asscon_recoverable_mode),
            abs(mixed_output_rec.asscon_recoverable_p50),
            abs(mixed_output_rec.asscon_recoverable_mean),
            abs(mixed_output_rec.asscon_recoverable_p10),
            abs(mixed_output_rec.asscon_recoverable_p01)
        );

        -- oil equivalent recoverable resources
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
            p1
        ) VALUES (
            gen_random_uuid(),
            mixed_output_rec.sd_id,
            custom_dist_type,
            'MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES',
            abs(mixed_output_rec.oilequiv_recoverable_p99),
            abs(mixed_output_rec.oilequiv_recoverable_p90),
            abs(mixed_output_rec.oilequiv_recoverable_mode),
            abs(mixed_output_rec.oilequiv_recoverable_p50),
            abs(mixed_output_rec.oilequiv_recoverable_mean),
            abs(mixed_output_rec.oilequiv_recoverable_p10),
            abs(mixed_output_rec.oilequiv_recoverable_p01)
        );

    END LOOP;

END;

$$;