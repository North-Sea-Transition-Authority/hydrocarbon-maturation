DO $$
DECLARE

    output_rec RECORD;
    custom_dist_type TEXT := 'CUSTOM';

BEGIN

    CREATE OR REPLACE FUNCTION calculate_summed_equiv(oil_val NUMERIC, cond_val NUMERIC, gas_val NUMERIC, ass_gas_val NUMERIC)
        RETURNS NUMERIC AS $calculate_summed_equiv$
    DECLARE
        summed_equiv NUMERIC;
    BEGIN
        oil_val := COALESCE(oil_val, 0);
        cond_val := COALESCE(cond_val, 0);
        gas_val := COALESCE(gas_val, 0);
        ass_gas_val := COALESCE(ass_gas_val, 0);
        summed_equiv := ROUND(oil_val + cond_val + ((gas_val + ass_gas_val)/5.8), 2);
        IF summed_equiv = 0 THEN
            RETURN NULL;
        ELSE
            RETURN summed_equiv;
        END IF;
    END;
    $calculate_summed_equiv$ LANGUAGE plpgsql;

    -- Get oil and gas outputs and apply to either oil or gas hydrocarbon case
    -- as we can't rely on the PaDI hydrocarbon case to be accurate for the information supplied
    FOR output_rec IN (
        SELECT
            prdvo.*,
            LOWER(prdh.hc_case) hydrocarbon_case,
            rsd.submission_detail_id sd_id,
            rsd.id rsd_id
        FROM remi.padi_res_data_vol_outputs prdvo
        JOIN remi.padi_res_data_header prdh ON prdh.resdat_id = prdvo.resdat_id
        JOIN remi.padi_reservoirs pr ON pr.legacy_res_data_id = prdh.resdat_id
        JOIN remi.reservoir_submission_details rsd ON rsd.reservoir = pr.remi_reservoir_id
        WHERE prdh.opportunity_type = 'Discovery'
    )
    LOOP

        IF output_rec.hydrocarbon_case IN ('gas','condensate') THEN
            -- gas case outputs
            INSERT INTO remi.gas_case_outputs (
                id,
                reservoir_submission_detail_id,
                has_associated_condensate_recoverable_resources,
                use_industry_standard_conversion
            ) VALUES (
                gen_random_uuid(),
                output_rec.rsd_id,
                FALSE, -- updated later if required,
                FALSE -- updated later if required
             );
        ELSIF output_rec.hydrocarbon_case = 'oil' THEN

            INSERT INTO remi.oil_case_outputs (
                id,
                reservoir_submission_detail_id,
                has_associated_gas_recoverable_resources,
                use_industry_standard_conversion
            ) VALUES (
                 gen_random_uuid(),
                 output_rec.rsd_id,
                 FALSE, -- updated later if required,
                 FALSE -- updated later if required
             );

        ELSIF output_rec.hydrocarbon_case = 'mixed oil and gas' THEN
            -- mixed case outputs
            INSERT INTO remi.mixed_case_outputs (
                id,
                reservoir_submission_detail_id,
                has_associated_condensate_recoverable_resources,
                use_industry_standard_conversion
            ) VALUES (
                gen_random_uuid(),
                output_rec.rsd_id,
                FALSE, -- updated later if required,
                FALSE -- updated later if required
            );

        END IF;

        -- oil in place
        IF COALESCE(
            output_rec.oil_inplace_p99,
            output_rec.oil_inplace_p90,
            output_rec.oil_inplace_mode,
            output_rec.oil_inplace_p50,
            output_rec.oil_inplace_mean,
            output_rec.oil_inplace_p10,
            output_rec.oil_inplace_p01
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
                output_rec.sd_id,
                custom_dist_type,
                'OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY',
                abs(output_rec.oil_inplace_p99),
                abs(output_rec.oil_inplace_p90),
                abs(output_rec.oil_inplace_mode),
                abs(output_rec.oil_inplace_p50),
                abs(output_rec.oil_inplace_mean),
                abs(output_rec.oil_inplace_p10),
                abs(output_rec.oil_inplace_p01),
                output_rec.oil_inplace_comment,
                output_rec.rsd_id
            );

        END IF;

        -- oil recoverable resources
        IF COALESCE(
            output_rec.oil_recoverable_p99,
            output_rec.oil_recoverable_p90,
            output_rec.oil_recoverable_mode,
            output_rec.oil_recoverable_p50,
            output_rec.oil_recoverable_mean,
            output_rec.oil_recoverable_p10,
            output_rec.oil_recoverable_p01
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
                output_rec.sd_id,
                custom_dist_type,
                'OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY',
                abs(output_rec.oil_recoverable_p99),
                abs(output_rec.oil_recoverable_p90),
                abs(output_rec.oil_recoverable_mode),
                abs(output_rec.oil_recoverable_p50),
                abs(output_rec.oil_recoverable_mean),
                abs(output_rec.oil_recoverable_p10),
                abs(output_rec.oil_recoverable_p01),
                output_rec.oil_recoverable_comment,
                output_rec.rsd_id
            );

        END IF;

        -- associated gas recoverable resources
        IF COALESCE(
            output_rec.assgas_recoverable_p99,
            output_rec.assgas_recoverable_p90,
            output_rec.assgas_recoverable_mode,
            output_rec.assgas_recoverable_p50,
            output_rec.assgas_recoverable_mean,
            output_rec.assgas_recoverable_p10,
            output_rec.assgas_recoverable_p01
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
                output_rec.sd_id,
                custom_dist_type,
                'OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY',
                abs(output_rec.assgas_recoverable_p99),
                abs(output_rec.assgas_recoverable_p90),
                abs(output_rec.assgas_recoverable_mode),
                abs(output_rec.assgas_recoverable_p50),
                abs(output_rec.assgas_recoverable_mean),
                abs(output_rec.assgas_recoverable_p10),
                abs(output_rec.assgas_recoverable_p01),
                output_rec.assgas_recoverable_comment,
                output_rec.rsd_id
            );

        END IF;

        -- gas in place
        IF COALESCE(
            output_rec.gas_inplace_p99,
            output_rec.gas_inplace_p90,
            output_rec.gas_inplace_mode,
            output_rec.gas_inplace_p50,
            output_rec.gas_inplace_mean,
            output_rec.gas_inplace_p10,
            output_rec.gas_inplace_p01
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
                output_rec.sd_id,
                custom_dist_type,
                'GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY',
                abs(output_rec.gas_inplace_p99),
                abs(output_rec.gas_inplace_p90),
                abs(output_rec.gas_inplace_mode),
                abs(output_rec.gas_inplace_p50),
                abs(output_rec.gas_inplace_mean),
                abs(output_rec.gas_inplace_p10),
                abs(output_rec.gas_inplace_p01),
                output_rec.gas_inplace_comment,
                output_rec.rsd_id
            );

        END IF;

        -- gas recoverable resources
        IF COALESCE(
            output_rec.gas_recoverable_p99,
            output_rec.gas_recoverable_p90,
            output_rec.gas_recoverable_mode,
            output_rec.gas_recoverable_p50,
            output_rec.gas_recoverable_mean,
            output_rec.gas_recoverable_p10,
            output_rec.gas_recoverable_p01
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
                output_rec.sd_id,
                custom_dist_type,
                'GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY',
                abs(output_rec.gas_recoverable_p99),
                abs(output_rec.gas_recoverable_p90),
                abs(output_rec.gas_recoverable_mode),
                abs(output_rec.gas_recoverable_p50),
                abs(output_rec.gas_recoverable_mean),
                abs(output_rec.gas_recoverable_p10),
                abs(output_rec.gas_recoverable_p01),
                output_rec.gas_recoverable_comment,
                output_rec.rsd_id
            );

        END IF;

        -- associated condensate recoverable resources
        IF COALESCE(
            output_rec.asscon_recoverable_p99,
            output_rec.asscon_recoverable_p90,
            output_rec.asscon_recoverable_mode,
            output_rec.asscon_recoverable_p50,
            output_rec.asscon_recoverable_mean,
            output_rec.asscon_recoverable_p10,
            output_rec.asscon_recoverable_p01
        ) IS NOT NULL THEN

            IF output_rec.hydrocarbon_case IN ('gas','condensate') THEN

                UPDATE remi.gas_case_outputs
                SET has_associated_condensate_recoverable_resources = TRUE
                WHERE reservoir_submission_detail_id = output_rec.rsd_id;

            ELSIF output_rec.hydrocarbon_case = 'mixed oil and gas' THEN

                UPDATE remi.mixed_case_outputs
                SET has_associated_condensate_recoverable_resources = TRUE
                WHERE reservoir_submission_detail_id = output_rec.rsd_id;

            END IF;

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
                output_rec.sd_id,
                custom_dist_type,
                'OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY',
                abs(output_rec.asscon_recoverable_p99),
                abs(output_rec.asscon_recoverable_p90),
                abs(output_rec.asscon_recoverable_mode),
                abs(output_rec.asscon_recoverable_p50),
                abs(output_rec.asscon_recoverable_mean),
                abs(output_rec.asscon_recoverable_p10),
                abs(output_rec.asscon_recoverable_p01),
                output_rec.asscon_recoverable_comment,
                output_rec.rsd_id
            );

        END IF;

        -- oil equivalent recoverable resources
        IF COALESCE(
                output_rec.oilequiv_recoverable_p99,
                output_rec.oilequiv_recoverable_p90,
                output_rec.oilequiv_recoverable_mode,
                output_rec.oilequiv_recoverable_p50,
                output_rec.oilequiv_recoverable_mean,
                output_rec.oilequiv_recoverable_p10,
                output_rec.oilequiv_recoverable_p01
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
                 output_rec.sd_id,
                 custom_dist_type,
                 'OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY',
                 abs(output_rec.oilequiv_recoverable_p99),
                 abs(output_rec.oilequiv_recoverable_p90),
                 abs(output_rec.oilequiv_recoverable_mode),
                 abs(output_rec.oilequiv_recoverable_p50),
                 abs(output_rec.oilequiv_recoverable_mean),
                 abs(output_rec.oilequiv_recoverable_p10),
                 abs(output_rec.oilequiv_recoverable_p01),
                 output_rec.oilequiv_recoverable_comment,
                 output_rec.rsd_id
             );

        ELSIF COALESCE(
            output_rec.oilequiv_recoverable_calc_p99,
            output_rec.oilequiv_recoverable_calc_p90,
            output_rec.oilequiv_recoverable_calc_mode,
            output_rec.oilequiv_recoverable_calc_p50,
            output_rec.oilequiv_recoverable_calc_mean,
            output_rec.oilequiv_recoverable_calc_p10,
            output_rec.oilequiv_recoverable_calc_p01
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
                 output_rec.sd_id,
                 custom_dist_type,
                 'OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY',
                 abs(output_rec.oilequiv_recoverable_calc_p99),
                 abs(output_rec.oilequiv_recoverable_calc_p90),
                 abs(output_rec.oilequiv_recoverable_calc_mode),
                 abs(output_rec.oilequiv_recoverable_calc_p50),
                 abs(output_rec.oilequiv_recoverable_calc_mean),
                 abs(output_rec.oilequiv_recoverable_calc_p10),
                 abs(output_rec.oilequiv_recoverable_calc_p01),
                 output_rec.oilequiv_recoverable_calc_comment,
                 output_rec.rsd_id
             );

        ELSIF COALESCE(
                output_rec.oil_recoverable_p99,
                output_rec.oil_recoverable_p90,
                output_rec.oil_recoverable_mode,
                output_rec.oil_recoverable_p50,
                output_rec.oil_recoverable_mean,
                output_rec.oil_recoverable_p10,
                output_rec.oil_recoverable_p01,
                output_rec.assgas_recoverable_p99,
                output_rec.assgas_recoverable_p90,
                output_rec.assgas_recoverable_mode,
                output_rec.assgas_recoverable_p50,
                output_rec.assgas_recoverable_mean,
                output_rec.assgas_recoverable_p10,
                output_rec.assgas_recoverable_p01,
                output_rec.gas_recoverable_p99,
                output_rec.gas_recoverable_p90,
                output_rec.gas_recoverable_mode,
                output_rec.gas_recoverable_p50,
                output_rec.gas_recoverable_mean,
                output_rec.gas_recoverable_p10,
                output_rec.gas_recoverable_p01,
                output_rec.asscon_recoverable_p99,
                output_rec.asscon_recoverable_p90,
                output_rec.asscon_recoverable_mode,
                output_rec.asscon_recoverable_p50,
                output_rec.asscon_recoverable_mean,
                output_rec.asscon_recoverable_p10,
                output_rec.asscon_recoverable_p01
            ) IS NOT NULL THEN

            IF output_rec.hydrocarbon_case IN ('gas','condensate') THEN

                UPDATE remi.gas_case_outputs
                SET use_industry_standard_conversion = TRUE
                WHERE reservoir_submission_detail_id = output_rec.rsd_id;

            ELSIF output_rec.hydrocarbon_case = 'oil' THEN

                UPDATE remi.oil_case_outputs
                SET use_industry_standard_conversion = TRUE
                WHERE reservoir_submission_detail_id = output_rec.rsd_id;

            ELSIF output_rec.hydrocarbon_case = 'mixed oil and gas' THEN

                UPDATE remi.mixed_case_outputs
                SET use_industry_standard_conversion = TRUE
                WHERE reservoir_submission_detail_id = output_rec.rsd_id;

            END IF;

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
                 output_rec.sd_id,
                 custom_dist_type,
                 'OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_INDUSTRY_STANDARD_DISCOVERY',
                 calculate_summed_equiv(abs(output_rec.oil_recoverable_p99), abs(output_rec.asscon_recoverable_p99), abs(output_rec.gas_recoverable_p99), abs(output_rec.assgas_recoverable_p99)),
                 calculate_summed_equiv(abs(output_rec.oil_recoverable_p90), abs(output_rec.asscon_recoverable_p90), abs(output_rec.gas_recoverable_p90), abs(output_rec.assgas_recoverable_p90)),
                 calculate_summed_equiv(abs(output_rec.oil_recoverable_mode), abs(output_rec.asscon_recoverable_mode), abs(output_rec.gas_recoverable_mode), abs(output_rec.assgas_recoverable_mode)),
                 calculate_summed_equiv(abs(output_rec.oil_recoverable_p50), abs(output_rec.asscon_recoverable_p50), abs(output_rec.gas_recoverable_p50), abs(output_rec.assgas_recoverable_p50)),
                 calculate_summed_equiv(abs(output_rec.oil_recoverable_mean), abs(output_rec.asscon_recoverable_mean), abs(output_rec.gas_recoverable_mean), abs(output_rec.assgas_recoverable_mean)),
                 calculate_summed_equiv(abs(output_rec.oil_recoverable_p10), abs(output_rec.asscon_recoverable_p10), abs(output_rec.gas_recoverable_p10), abs(output_rec.assgas_recoverable_p10)),
                 calculate_summed_equiv(abs(output_rec.oil_recoverable_p01), abs(output_rec.asscon_recoverable_p01), abs(output_rec.gas_recoverable_p01), abs(output_rec.assgas_recoverable_p01)),
                 NULL,
                 output_rec.rsd_id
             );

        END IF;

    END LOOP;

END;

$$;